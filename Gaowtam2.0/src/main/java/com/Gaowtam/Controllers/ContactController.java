package com.Gaowtam.Controllers;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.annotation.RequestScope;

import com.Gaowtam.entities.Contact;
import com.Gaowtam.entities.User;
import com.Gaowtam.forms.ContactForm;
import com.Gaowtam.forms.ContactSearchForm;
import com.Gaowtam.helpers.AppConstats;
import com.Gaowtam.helpers.Message;
import com.Gaowtam.helpers.MessageType;
import com.Gaowtam.helpers.helper;
import com.Gaowtam.services.ContactService;
import com.Gaowtam.services.ImageService;
import com.Gaowtam.services.UserService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/user/contacts")
public class ContactController {

    private Logger logger = org.slf4j.LoggerFactory.getLogger(ContactController.class);

    @Autowired
    private ContactService contactService;

    @Autowired
    private ImageService imageService;

    @Autowired
    private UserService userService;

    @RequestMapping("/add")
    // add contact page:handler
    public String addContactView(Model model) {
        ContactForm contactForm = new ContactForm();
        // contactForm.setName("Nirob");
        // contactForm.setFavorite(true);
        model.addAttribute("contactForm", contactForm);
        return "user/add_contact";
    }

    // jokhon form action er kaz korbe
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String saveContact(@Valid @ModelAttribute ContactForm contactForm, BindingResult result,
            Authentication authentication, HttpSession session) {
        // process the form data

        // validate form
        if (result.hasErrors()) {

            result.getAllErrors().forEach(error -> logger.info(error.toString()));

            // session.setAttribute("message","Please correct the following errors");
            session.setAttribute("message", Message.builder()
                    .content("Please correct the following errors")
                    .type(MessageType.red)
                    .build());
            return "user/add_contact";
        }

        String username = helper.getEmailOfLoggedinUser(authentication);

        // conver form -->contact

        User user = userService.getUserByEmail(username);

        Contact contact = new Contact();

        // process the contact picture

        logger.info("file information : {}", contactForm.getContactImage().getOriginalFilename());

        /// image upload er code
        String filename = UUID.randomUUID().toString();// databae er moddhe public id save kora lagbe ty aikhan thake
                                                       // uploadImage er moddhe pathano hocce

        String fileURL = imageService.uploadImage(contactForm.getContactImage(), filename);

        contact.setName(contactForm.getName());
        contact.setFavortie(contactForm.isFavorite());
        contact.setEmail(contactForm.getEmail());
        contact.setPhoneNumber(contactForm.getPhoneNumber());
        contact.setAddress(contactForm.getAddress());
        contact.setDescription(contactForm.getDescription());
        contact.setUser(user);
        contact.setLinkedInLink(contactForm.getLlinedInLink());
        contact.setWebsiteLink(contactForm.getWebsiteLink());
        contact.setPicture(fileURL);
        contact.setCloudinaryImagePublicId(filename);

        contactService.save(contact);

        System.out.println(contactForm);

        // set the contact picture url

        // set messsage to be displayed on the view
        session.setAttribute("message",
                Message.builder()
                        .content("You have succesfully added a new contact")
                        .type(MessageType.green)
                        .build());

        return "redirect:/user/contacts/add";
    }

    // view contatcts
    @RequestMapping
    public String viewContacts(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = AppConstats.PAGE_SIZE + "") int size,
            // @RequestParam(value = "size",defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "name") String sortBy,
            @RequestParam(value = "direction", defaultValue = "asc") String direction, Model model,
            Authentication authentication) {
        // load all the user contacts

        String username = helper.getEmailOfLoggedinUser(authentication);

        User user = userService.getUserByEmail(username);

        // List<Contact> contacts = contactService.getByUser(user);

        Page<Contact> pageContact = contactService.getByUser(user, page, size, sortBy, direction);

        model.addAttribute("pageContact", pageContact);
        model.addAttribute("pageSize", AppConstats.PAGE_SIZE);

        model.addAttribute("contactSearchForm",new ContactSearchForm());
        return "user/contacts";
    }

    // search handler

    @RequestMapping("/search")
    public String sarchHandler(
            // @RequestParam("field") String field,
            // @RequestParam("keyword") String value,
            @ModelAttribute ContactSearchForm contactSearchForm,//ContactSearchForm class er data search.html er moddhe pathano hoyece
            @RequestParam(value = "size", defaultValue = AppConstats.PAGE_SIZE + "") int size,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "sortBy", defaultValue = "name") String sortBy,
            @RequestParam(value = "derection", defaultValue = "asc") String direction,
            Model model,
            Authentication authentication) {

        // logger.info("field {} keyword {}", field, value);
        logger.info("field {} keyword {}",contactSearchForm.getField(), contactSearchForm.getValue());

        var user = userService.getUserByEmail(helper.getEmailOfLoggedinUser(authentication));

        Page<Contact> pageContact = null;



        // if (field.equalsIgnoreCase("name")) {
        //     System.out.println(value + " ," + sortBy);
        //     pageContact = contactService.searchByName(value, size, page, sortBy, direction, user);
        // }

        // else if (field.equalsIgnoreCase("email")) {
        //     pageContact = contactService.searchByEamil(value, size, page, sortBy, direction, user);
        // } else if (field.equalsIgnoreCase("phone")) {
        //     pageContact = contactService.searchByPhone(value, size, page, sortBy, direction, user);
        // } else {
        //     pageContact = contactService.searchByPhone("Select Field", size, page, sortBy, direction, user);
        // }



        if (contactSearchForm.getField().equalsIgnoreCase("name")) {
            System.out.println(contactSearchForm.getValue() + " ," + sortBy);
            pageContact = contactService.searchByName(contactSearchForm.getValue(), size, page, sortBy, direction, user);
        }
        else if (contactSearchForm.getField().equalsIgnoreCase("email")) {
            pageContact = contactService.searchByEamil(contactSearchForm.getValue(), size, page, sortBy, direction, user);
        } else if (contactSearchForm.getField().equalsIgnoreCase("phone")) {
            pageContact = contactService.searchByPhone(contactSearchForm.getValue(), size, page, sortBy, direction, user);
        } else {
            pageContact = contactService.searchByPhone("Select Field", size, page, sortBy, direction, user);
        }





        logger.info("pageContact {}", pageContact);

        model.addAttribute("contactSearchForm", contactSearchForm);

        model.addAttribute("pageContact", pageContact);

        model.addAttribute("pageSize", AppConstats.PAGE_SIZE);

        return "user/search";
    }

    @RequestMapping("/delete/{contactid}")
    public String deleteContact(@PathVariable("contactid") String contactid)
    {
        contactService.delete(contactid);

        logger.info("contactiId{} deleted ",contactid);

        return "redirect:/user/contacts";
    }
}
