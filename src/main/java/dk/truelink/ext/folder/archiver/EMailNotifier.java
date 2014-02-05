package dk.truelink.ext.folder.archiver;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;

public class EMailNotifier {

	private Properties props;
	private String username;
	private String password;

	public EMailNotifier(String host, int port, String username, String password) {

		this.username = username;
		this.password = password;
		props = new Properties();
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.port", port);		
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.auth", "true");		
	}

	public void sendMail(String to, String subject, String sendingMessage) {

		Session session = Session.getInstance(props,
				new javax.mail.Authenticator() {
			
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(username, password);
					}
				});

		try {
			MimeMessage message = new MimeMessage(session);			
			message.setRecipients(Message.RecipientType.TO, to);
			message.setSubject(subject);
			message.setText(sendingMessage);
			Transport.send(message);
		} catch (MessagingException mex) {
			System.out.println("send failed, exception: " + mex);
		}
	}
}
