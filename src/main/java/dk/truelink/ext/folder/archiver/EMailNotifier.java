package dk.truelink.ext.folder.archiver;

import java.util.Properties;

import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;

public class EMailNotifier {
	private MailSender mailSender;
	
	public EMailNotifier(String host,int port, String username, String password) {
		JavaMailSenderImpl mailSenderImpl = new JavaMailSenderImpl();
		mailSenderImpl.setHost(host);
		mailSenderImpl.setPort(port);
		mailSenderImpl.setUsername(username);
		mailSenderImpl.setPassword(password);
		
		Properties javaMailProperties = new Properties();
		javaMailProperties.put("mail.smtp.auth", "true");
		javaMailProperties.put("mail.smtp.starttls.enable", "true");
		mailSenderImpl.setJavaMailProperties(javaMailProperties);
		
		
		mailSender = mailSenderImpl;
	}

	public void sendMail(String to, String subject, String msg) {

		SimpleMailMessage message = new SimpleMailMessage();
		
		message.setTo(to);
		message.setSubject(subject);
		message.setText(msg);
		mailSender.send(message);
	}
}
