package mail;

import java.io.IOException;
import java.util.ArrayList;

import javax.mail.MessagingException;

import model.Mail;
import model.MailAccount;
import model.Settings;

public class GetMail {
	public static void main(String[] args) throws MessagingException, IOException {

		Settings.checkCommandLineArgs(args);

		ArrayList<MailAccount> mal = JSON.loadConfig(args[0]);
		String accountName = args[1];
		String emailSubjectContains = args[2];
		String dumpPath = args[3];
		boolean deleteAfterDump = (args.length == 5);
		
		boolean mailsFound = false;
		boolean foundAccount = false;

		System.out.println("\r\nReceiving Mails for '" + accountName + "'");
		ArrayList<Mail> mailsReceived = new ArrayList<Mail>();
		for (MailAccount mailAccount : mal) {
			if (mailAccount.getAccountName().equals(accountName)) {
				foundAccount = true;
				mailsReceived.addAll(Mailer.getMessages(mailAccount));
				System.out.println("\r\nLooking for Mails containing subject '" + emailSubjectContains + "'");
				mailsFound = Mailer.handleMailsReceived(emailSubjectContains, dumpPath, mailsReceived, deleteAfterDump, mailAccount);
			}
		}
		Settings.handleExitCode(mailsFound, foundAccount);
	}
}
