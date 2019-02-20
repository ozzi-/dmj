package mail;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.mail.MessagingException;

import model.Mail;
import model.MailAccount;
import model.Settings;

public class GetMail {
	public static void main(String[] args) throws MessagingException, IOException {

		if (args.length < 4) {
			System.err.println("Usage:");
			System.err.println(
					"{/path/to/config.json} {accountName} {emailSubjectContains} {/path/for/dump} {deleteAfterDump}");
			System.exit(1);
		}

		String accountName = args[1];
		String emailSubjectContains = args[2];
		String dumpPath = args[3];
		boolean deleteAfterDump = args.length == 5;
		ArrayList<MailAccount> mal = JSON.loadConfig(args[0]);
		ArrayList<String> dumpedMailSubjects = new ArrayList<String>();
		System.out.println("\r\nReceiving Mails for '" + accountName + "'");
		boolean foundAccount = false;
		boolean mailsFound = false;

		ArrayList<Mail> mailsReceived = new ArrayList<Mail>();
		for (MailAccount mailAccount : mal) {
			if (mailAccount.getAccountName().equals(accountName)) {
				foundAccount = true;
				mailsReceived.addAll(Mailer.getMessages(mailAccount));
				System.out.println("");
				System.out.println("Looking for Mails containing subject '" + emailSubjectContains + "'");
				for (Mail mail : mailsReceived) {
					if (mail.getSubject().contains(emailSubjectContains)) {
						mailsFound = true;
						String path = dumpPath + "/" + mail.getFilenameString() + ".mail";
						System.out.println(Settings.indentMarker + " '" + mail.getSubject() + "' writing to " + path);
						File file = new File(path);
						file.getParentFile().mkdirs();
						PrintWriter writer = new PrintWriter(file, "UTF-8");
						writer.println("FROM: " + mail.getFrom());
						writer.println("TO: " + mail.getTo());
						writer.println("SUBJECT: " + mail.getSubject());
						writer.println("SENT: " + mail.getSentDate());
						writer.println("CONTENT:");
						writer.println(mail.getContent());
						writer.println("CONTENT HTML:");
						writer.print(mail.getContentHTML());
						writer.close();
						dumpedMailSubjects.add(mail.getSubject());
					}
				}
				if (deleteAfterDump) {
					Mailer.deleteMessagesBySubjectIMAP(mailAccount, dumpedMailSubjects);
				}
			}
		}
		if (!foundAccount){
			System.out.println("Could not find account in config json");
			System.exit(1);
		}
		if (!mailsFound) {
			System.out.println("No mails with containing keyword in subject found");
			System.exit(2);
		}
		System.exit(0);
	}
}
