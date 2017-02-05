package utility;

import java.io.File;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;

public class SendMailSSL {

    String to = "";//change accordingly

    public SendMailSSL(String to) {
        this.to = to;
    }

    public void sendEmail(String filename) {

//Get the session object
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class",
                "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");

        Session session = Session.getDefaultInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication("bill.ipearl@gmail.com", "billipearl");//change accordingly
                    }
                });

//compose message
        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress("iPearl"));//change accordingly
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setSubject("iPearl");

            // Create the message part
            BodyPart messageBodyPart = new MimeBodyPart();

            // Now set the actual message
            messageBodyPart.setText("Please find attachment of your bill copy.");
            // Create a multipar message
            Multipart multipart = new MimeMultipart();
            // Set text message part
            multipart.addBodyPart(messageBodyPart);

            // Part two is attachment
            messageBodyPart = new MimeBodyPart();
            DataSource source = new FileDataSource(filename);
            messageBodyPart.setDataHandler(new DataHandler(source));
            messageBodyPart.setFileName(filename);
            multipart.addBodyPart(messageBodyPart);

            // Send the complete message parts
            message.setContent(multipart);

            //send message
            Transport.send(message);

            System.out.println("message sent successfully");

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }

    }

    public void sendEmailContent(String content) {

//Get the session object
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class",
                "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");

        Session session = Session.getDefaultInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication("bill.ipearl@gmail.com", "billipearl");//change accordingly
                    }
                });

//compose message
        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress("iPearl"));//change accordingly
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setSubject("iPearl");
            message.setContent(content,"text/html");

            // Create the message part
//            BodyPart messageBodyPart = new MimeBodyPart();
//
//            // Now set the actual message
//            messageBodyPart.setText(content);
//            messageBodyPart.setContent("");
//            // Create a multipar message
//            Multipart multipart = new MimeMultipart();
//            // Set text message part
//            multipart.addBodyPart(messageBodyPart);
//
//            // Part two is attachment
//            messageBodyPart = new MimeBodyPart();
//            DataSource source = new FileDataSource(System.getProperty("user.dir") + File.separatorChar + "Reports" + File.separatorChar + "BankBook.jrxml");
//            messageBodyPart.setDataHandler(new DataHandler(source));
//            messageBodyPart.setFileName(System.getProperty("user.dir") + File.pathSeparator + "Reports" + File.separatorChar + "BankBook.jrxml");
//            multipart.addBodyPart(messageBodyPart);
//
//            // Send the complete message parts
//            message.setContent(multipart);

            //send message
            Transport.send(message);

            System.out.println("message sent successfully");

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }

    }
}
