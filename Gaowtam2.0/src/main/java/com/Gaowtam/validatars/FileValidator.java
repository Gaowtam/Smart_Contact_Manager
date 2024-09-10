package com.Gaowtam.validatars;

import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.*;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class FileValidator implements ConstraintValidator<ValidFile,MultipartFile>{

    private static final long MAX_FILE_SIZE= 1024 * 1024 * 2 ;//2 MB file size
    //type

    //height

    //width

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
        
        if(file==null || file.isEmpty())
        {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("File cannot be empty").addConstraintViolation();
            return false;
        }
        if(file.getSize()>MAX_FILE_SIZE)
        {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("File size should be less than 2MB").addConstraintViolation();
            return false;
        }
        //resolution
        
        // try {
        //      BufferedImage bufferedImage = ImageIO.read(file.getInputStream());

        //      if(bufferedImage.getHeight()||bufferedImage.getWidth())
        //      {
        //         // image er hight width kmne check kora hoi dekhano hoyece
        //      }
        // } catch (IOException e) {
        //     // TODO Auto-generated catch block
        //     e.printStackTrace();
        // }
        return true;
    }

}
