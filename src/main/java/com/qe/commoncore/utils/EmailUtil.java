package com.qe.commoncore.utils;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.mail.HtmlEmail;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qe.commoncore.BaseTest;
import com.qe.commoncore.Configurator;
import com.qe.commoncore.config.EmailConfig;
import com.qe.commoncore.constants.ContextConstant;

public class EmailUtil {
	private static final Logger log = LoggerFactory.getLogger(EmailUtil.class);
	private final EmailConfig config;
	private String content;
	private final Pattern pattern;
	private String body = "", totalTestExecutated, passed, failed, skipped,passPercentage;
    private String buildNumber="xx";
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
		XrayUtil xray=new XrayUtil();
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
		try {
			String mailContent = setContents();
			HtmlEmail mail = new HtmlEmail();
			mail.setHostName(config.getHost());
			mail.setSmtpPort(config.getPort());
			mail.setFrom(config.getFROMDetails());
			// Add 'To' recipients
			if (Configurator.getInstance().getParameter(ContextConstant.MAIL_TO) == null) {
				for (String to : config.getTODetails())
					mail.addTo(to);
			} else {
				for (String to : Arrays
						.asList(Configurator.getInstance().getParameter(ContextConstant.MAIL_TO).split(",")))
					mail.addTo(to);
			}
			if(FileUtil.isFileSizeValid(BaseTest.reporter.reportPath, (15 * 1024 * 1024))) {
			mail.attach(new File(BaseTest.reporter.reportPath));
			}else {
				System.out.println("FileSize exceeds 15 MB, It will not be attached in test results email.");
			}
			String automationStatus=(Jsoup.parse(content).getElementById("executionStatus").toString().contains("PASS"))?"PASS":"FAIL";
			mail.setSubject(((System.getenv("tagName")!=null)?System.getenv("tagName"):"TestRepository") + " test execution report (Build no:"+buildNumber+"::"+automationStatus+")");
			mail.setHtmlMsg(mailContent);
			mail.send();
			log.info("<<============Email Sent=============>>");
		} catch (Exception e) {
			log.error("Error sending email: " + e.getMessage());
			e.printStackTrace();
		}
	}
}