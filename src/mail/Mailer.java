package mail;

import java.util.ArrayList;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeMultipart;

import org.jsoup.Jsoup;

import com.sun.mail.imap.IMAPFolder;

import model.Mail;
import model.MailAccount;
import model.Settings;

public class Mailer {

	private static Store connectIMAP(String imapHost, int imapPort, boolean imapSecure, boolean trustAllCertificates,
			String imapUser, String imapPassword) {
		Store store = null;
		Properties props = System.getProperties();
		String protcol = imapSecure ? "imaps" : "imap";
		if (trustAllCertificates) {
			props.put("mail.imaps.ssl.trust", "*");
		}
		props.put("mail.store.protocol", protcol);
		props.put("mail.imap.port", imapPort);
		Session session = Session.getInstance(props);
		session.setDebug(Settings.debugProtocols);
		try {
			store = session.getStore(protcol);
			store.connect(imapHost, imapUser, imapPassword);
		} catch (Exception e) {
			System.err.println("Could not perform IMAP connect to " + imapHost);
			System.err.println(e.getMessage());
			System.exit(4);
		}
		return store;
	}

	public static ArrayList<Mail> listMessagesIMAP(String imapHost, int imapPort, boolean imapSecure, String imapUser,
			String imapPassword, String imapFolderName, boolean trustAllCertificates) {
		Store store = null;
		ArrayList<Mail> messagesR = new ArrayList<Mail>();
		try {
			store = connectIMAP(imapHost, imapPort, imapSecure, trustAllCertificates, imapUser, imapPassword);
			IMAPFolder folder = (IMAPFolder) store.getFolder(imapFolderName);
			folder.open(Folder.READ_ONLY);
			Message[] messages = folder.getMessages();
			for (int i = 0, n = messages.length; i < n; i++) {
				Message message = messages[i];
				String recipients = "";
				Address[] addresses = message.getRecipients(Message.RecipientType.TO);
				for (Address address : addresses) {
					recipients += address + ",";
				}
				Mail mo = new Mail(message.getFrom()[0].toString(), recipients, message.getSubject(), getTextFromMessage(message), getHTMLFromMessage(message), message.getSentDate());
				messagesR.add(mo);
			}
			folder.close(false);
			store.close();
			return messagesR;
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(e.toString());

		} finally {
			try {
				if (store != null) {
					store.close();
				}
			} catch (MessagingException e) {
				e.printStackTrace();
			}
		}
		return messagesR;
	}

	private static void deleteMessagesBySubjectIMAPInternal(String imapHost, int imapPort, boolean imapSecure,
			boolean trustAllCertificates, String imapUser, String imapPassword, String imapFolderName,
			ArrayList<String> subjects) {
		Store store = null;
		try {
			store = connectIMAP(imapHost, imapPort, imapSecure, trustAllCertificates, imapUser, imapPassword);
			IMAPFolder folder = (IMAPFolder) store.getFolder(imapFolderName);
			if (!folder.isOpen()) {
				folder.open(Folder.READ_WRITE);
			}
			long largestUid = folder.getUIDNext() - 1;
			int chunkSize = 500;
			for (long offset = 0; offset < largestUid; offset += chunkSize) {
				long start = Math.max(1, largestUid - offset - chunkSize + 1);
				long end = Math.max(1, largestUid - offset);
				Message[] messages = folder.getMessagesByUID(start, end);
				for (Message message : messages) {
					for (String subject : subjects) {
						if (message.getSubject().equals(subject)) {
							message.setFlag(Flags.Flag.DELETED, true);
							System.out.println(Settings.indentMarker+" Flagged mail with subject '"+subject+"' as deleted");
						}
					}
				}
			}
			folder.close(true);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (store != null) {
					store.close();
				}
			} catch (MessagingException e) {
				e.printStackTrace();
			}
		}
	}

	private static String getTextFromMessage(Message message) throws Exception {
		if (message.isMimeType("text/plain")) {
			return message.getContent().toString();
		} else if (message.isMimeType("multipart/*")) {
			String result = "";
			MimeMultipart mimeMultipart = (MimeMultipart) message.getContent();
			int count = mimeMultipart.getCount();
			for (int i = 0; i < count; i++) {
				BodyPart bodyPart = mimeMultipart.getBodyPart(i);
				if (bodyPart.isMimeType("text/plain")) {
					result = result + "\n" + bodyPart.getContent();
					break; // without break same text appears twice in my tests
				}
			}
			return result;
		}
		return "";
	}
	
	private static String getHTMLFromMessage(Message message) throws Exception {
		if (message.isMimeType("multipart/*")) {
			String result = "";
			MimeMultipart mimeMultipart = (MimeMultipart) message.getContent();
			int count = mimeMultipart.getCount();
			for (int i = 0; i < count; i++) {
				BodyPart bodyPart = mimeMultipart.getBodyPart(i);
				if (bodyPart.isMimeType("text/html")) {
					String html = (String) bodyPart.getContent();
					result = result + "\n" + Jsoup.parse(html);
				}
			}
			return result;
		}
		return "";
	}

	public static void deleteMessagesBySubjectIMAP(MailAccount mailAccount, ArrayList<String> subjects) {
		deleteMessagesBySubjectIMAPInternal(mailAccount.getHostImap(), mailAccount.getPortImap(),
				mailAccount.isSecureImap(), mailAccount.isTrustAllCerts(), mailAccount.getLogin(), mailAccount.getPw(),
				mailAccount.getInboxFolderName(), subjects);
	}

	public static ArrayList<Mail> getMessages(MailAccount mailAccount) {
		ArrayList<Mail> mailsReceived = Mailer.listMessagesIMAP(mailAccount.getHostImap(), mailAccount.getPortImap(),
				mailAccount.isSecureImap(), mailAccount.getLogin(), mailAccount.getPw(),
				mailAccount.getInboxFolderName(), mailAccount.isTrustAllCerts());
		System.out.println(Settings.indentMarker + mailAccount.getAccountName() + " has " + mailsReceived.size()
				+ " mails in its inbox");
		return mailsReceived;
	}
}
