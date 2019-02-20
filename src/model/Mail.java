package model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import mail.Mailer;

public class Mail {
	private String from;
	private String to;
	private String subject;
	private String content;
	private String contentHTML;
	private Date sentDate;

	public Mail(String from, String to, String subject, String content, String contentHTML, Date sentDate) {
		this.setFrom(from);
		this.setTo(to);
		this.setSubject(subject);
		this.setContent(content);
		this.setContentHTML(contentHTML);
		this.setSentDate(sentDate);
	}

	public void dumpMail(String dumpPath) throws FileNotFoundException, UnsupportedEncodingException {
		String path = dumpPath + "/" + this.getFilenameString() + ".mail";
		System.out.println(Settings.indentMarker + " '" + this.getSubject() + "' writing to " + path);
		File file = new File(path);
		file.getParentFile().mkdirs();
		PrintWriter writer = new PrintWriter(file, "UTF-8");
		writer.println("FROM: " + this.getFrom());
		writer.println("TO: " + this.getTo());
		writer.println("SUBJECT: " + this.getSubject());
		writer.println("SENT: " + this.getSentDate());
		writer.println("CONTENT:");
		writer.println(this.getContent());
		writer.println("CONTENT HTML:");
		writer.print(this.getContentHTML());
		writer.close();
	}

	public String getFilenameString() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_hh-mm-ss");
		return dateFormat.format(sentDate);
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Date getSentDate() {
		return sentDate;
	}

	public void setSentDate(Date sentDate) {
		this.sentDate = sentDate;
	}

	public String getContentHTML() {
		return contentHTML;
	}

	public void setContentHTML(String contentHTML) {
		this.contentHTML = contentHTML;
	}
}
