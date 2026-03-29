package com.onhsape.app.onshapeautomationv1.service;

import com.onhsape.app.onshapeautomationv1.entity.AssignmentOrder;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailClass implements MailService {

    private final JavaMailSender mailSender;

    public MailClass(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }
    @Override
    public String sendConfirmationMail(String userEmail, String referralCode) {

        try{
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom("graphicsauto365@gmail.com");
            mailMessage.setTo(userEmail);
            mailMessage.setSubject("Assignment Completion");
            mailMessage.setText("Your Onshape Assignment has been successfully completed. \n " +
                    "For any queries please contact us on " +
                    "\nInstagram GC by clicking on this link https://ig.me/j/AbayJP7UIBiBAkYZ/ \n \n"
                    +"And here is your referral code: " + referralCode);

            mailSender.send(mailMessage);
            return "Mail Sent";

        }catch(Exception e){
            return e.getMessage();
        }
    }
}
