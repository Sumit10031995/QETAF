package com.qe.commoncore.utils;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.Flags.Flag;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.search.SubjectTerm;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.qe.api.commoncore.BaseTest;
import com.qe.commoncore.config.EmailConfig;
import com.qe.commoncore.constants.ContextConstant;

public class EmailUtil {
	private final EmailConfig config;
	private String content;
	private final Pattern pattern;
	private String body = "", totalTestExecutated, passed, failed, skipped,passPercentage;
    private String buildNumber="xx";
    private String subject;
    
	public EmailUtil() {
		this.config = EmailConfig.getInstance();
		this.content = FileUtil.readFile(getClass().getClassLoader().getResourceAsStream("Report_Template.html"));
		this.pattern = Pattern.compile("(\\d+) tests passed (\\d+) tests failed, (\\d+) skipped");
	}

	/**
	 * @return String
	 * @throws Exception This method is use to set email contents
	 */
	private String setContents() throws Exception {
		boolean flag = false;
		DecimalFormat df=new DecimalFormat("#.##");
		Document doc = Jsoup.parse(FileUtil.readFile(BaseTest.reporter.reportPath), "");
		Elements TestNames = doc.select("li[status]");
		String text = doc.getElementsByClass("card-footer").get(0).text();
		Matcher matcher = pattern.matcher(text);
		int count=0;

		if (matcher.find()) {
			totalTestExecutated = String.valueOf(Integer.parseInt(matcher.group(1)) + Integer.parseInt(matcher.group(2))
					+ Integer.parseInt(matcher.group(3)));
			passed = matcher.group(1);
			failed = matcher.group(2);
			skipped = matcher.group(3);
			passPercentage = df.format((Double.parseDouble(passed)/Double.parseDouble(totalTestExecutated))*100)+"%";
		} else {
			throw new Exception(
					"The automation report file does not contain the expected fields(test passed, test failed,skipped).Please ensure you are using the correct file");
		}
		// Extract the failed cases and set it to HTML template
		for (Element el : TestNames) {
			if (el.attr("status").equals("fail")) {
				flag = true;
				body = body + fetchJiraID("<tr><td>"+(++count)+"</td> <td>" + el.getElementsByClass("name").text() + "" + "</td><td>"
						+ el.getElementsByClass("m-t-10 m-l-5").text() + "</td></tr>");
			}

		}
		content = content.replace("xTotalTestExecuted", totalTestExecutated);
		content = content.replace("xPassed", passed);
		content = content.replace("xFailed", failed);
		content = content.replace("xSkipped", skipped);
		content = content.replace("xPassPercentage", passPercentage);
		
		if (System.getenv("buildLink") != null) {
			content = content.replace("LooperBuildURLLink", System.getenv("buildLink"));
			String[] buildURLArr=System.getenv("buildLink").split("/");
			buildNumber=buildURLArr[buildURLArr.length-1];
		}
		content = content.replace("BuildNo", buildNumber);
		if (System.getenv("buildNumber") != null)
			content = content.replace("BuildNo	", System.getenv("buildNumber"));
		if (System.getenv("buildUrl") != null)
			content = content.replace("BuildLink", System.getenv("buildUrl"));
		if (System.getenv("REPOSITORY_URL") != null)
			content = content.replace("TestRepository", System.getenv("REPOSITORY_URL"));
		if (System.getenv("tagName") != null)
			content = content.replace("TestSuite", System.getenv("tagName"));
		if (System.getenv("testEnvironment") != null)
			content = content.replace("EnvironmentName", System.getenv("testEnvironment"));
		content = content.replace("ExecutionStatus", (!flag) ? "PASS" : "FAIL");
		if (System.getenv("branchName") != null)
			content = content.replace("BranchName", System.getenv("branchName"));
		if (System.getenv("testburstReport") != null)
			content = content.replace("TestbrustLink", System.getenv("testburstReport"));
		if (System.getenv("extentReportLink") != null)
			content = content.replace("ExtentReportLink", System.getenv("extentReportLink"));
		content = content.replaceFirst("</tbody>", String.format("%s</tbody>", body));
		return content;
	}

	/**
	 * 
	 * @param html
	 * @return This method use to add JIRAID to a separate column
	 */
	private String fetchJiraID(String input) {
		int index = input.indexOf("::");
		JavaUtils xray=new JavaUtils();
		if (index != -1) {
			String firstPart = input.substring(0, index);
			String secondPart = input.substring(index + 2, input.length());
			return firstPart + "</td><td>" +xray.getJiraKeyasLink(secondPart.replaceAll("</td></tr>", "").trim())+"</td></tr>" ;

		} else {
			return input;
		}
	}

	/**
	 * This method is use to send mail
	 */
	public void sendMail() {
		
		Session session = EmailConfig.getInstance().getSendMailSession();
		try {
			MimeMessage message = new MimeMessage(session);
			if (Configurator.getInstance().getParameter(ContextConstant.MAIL_TO) == null) {
				for (String to : config.getTODetails())
					message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
			} else {
				for (String to : Arrays
						.asList(Configurator.getInstance().getParameter(ContextConstant.MAIL_TO).split(",")))
					message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
			}
			
			if (FileUtil.isFileSizeValid(BaseTest.reporter.reportPath, (15 * 1024 * 1024))) {
				Multipart multipart = new MimeMultipart();
				MimeBodyPart attachmentBodyPart = new MimeBodyPart();
				attachmentBodyPart.attachFile(new File(BaseTest.reporter.reportPath));
				multipart.addBodyPart(attachmentBodyPart);
				message.setContent(multipart);
			} else {
				System.out.println("FileSize exceeds 15 MB, It will not be attached in test results email.");
			}
			
			String automationStatus = (Jsoup.parse(content).getElementById("executionStatus").toString().contains("PASS")) ? "PASS" : "FAIL";
			message.setSubject(
					(subject = ((System.getenv("tagName") != null) ? System.getenv("tagName") : "TestRepository")
							+ " test execution report (Build no:" + buildNumber + "::" + automationStatus + ")"));
			
			message.setText(setContents());
			
			// send message
			Transport.send(message);
			System.out.println("<<============Email Sent=============>>");
		} catch (Exception e) {
			System.out.println("Error sending email: " + e.getMessage());
			e.printStackTrace();
		}

	}
	
	public String readMail(String subject) throws Exception {
		String emailMessage = "";
		EmailConfig config=EmailConfig.getInstance();
		try {
			Session session = EmailConfig.getInstance().getReadMailSession();
			Store store = session.getStore("imaps");
			store.connect(config.getReadMailHost(), config.getFROMDetails(), config.getPassword());
			Folder inbox = store.getFolder("Inbox");
			inbox.open(Folder.READ_WRITE);

			Message[] messages = inbox.search(new SubjectTerm(subject));
			if (messages.length > 0) {
				Message message = messages[0];
				message.setFlag(Flag.SEEN, true);
				emailMessage = message.getContent().toString();
			}
			return emailMessage;
		} catch (Exception e) {
			throw new Exception("Unable to read email");
		}
	}
}
