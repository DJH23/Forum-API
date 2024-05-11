package com.LessonLab.forum.Services;

import java.util.List;

import com.LessonLab.forum.Models.Content;
import com.LessonLab.forum.Models.User;
import com.LessonLab.forum.Models.Enums.Role;
import com.LessonLab.forum.Repositories.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

 //   @Autowired
  //  private MailSender mailSender;  // This assumes you configure a MailSender in your application context

    /**
     * Send a notification to users, e.g., admins and moderators, via email.
     * @param users List of users to notify.
     * @param messageBody the body of the notification message.
     */
   // public void notifyUsers(List<User> users, String messageBody) {
     //   for (User user : users) {
       //     if (user.getEmail() != null && !user.getEmail().isEmpty()) {
         //       sendEmail(user.getEmail(), "Notification", messageBody);
           // }
       // }
 
 //   }

    /**
     * Send an email to a recipient.
     * @param recipientEmail the email address of the recipient.
     * @param subject the subject line of the email.
     * @param messageBody the body text of the email.
     */
  /*  private void sendEmail(String recipientEmail, String subject, String messageBody) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(recipientEmail);
        message.setSubject(subject);
        message.setText(messageBody);*/ 
      //  mailSender.send(message);
    }
//}
