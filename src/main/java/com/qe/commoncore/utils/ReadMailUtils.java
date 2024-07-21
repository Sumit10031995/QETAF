package com.qe.commoncore.utils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import com.aventstack.extentreports.Status;
import com.google.gson.Gson;
import com.qe.apicore.impl.APIResponse;
import com.qe.apicore.impl.ApiDriver;
import com.qe.commoncore.model.EmailResponse;
import com.qe.commoncore.model.EmailResponse.Inbox;
/**
 * @author s0n06hp
 * Maildrop is a free disposable email address to use any time.
 * This class is use to read mails from Maildrop (https://maildrop.cc/)
 */
public class ReadMailUtils {
	ReportingUtil reporter = ReportingUtil.getInstance();
	private final String baseURL;
	private final String endPoint;
	private static ReadMailUtils email = null;

	/**
	 * Singleton
	 */
	
	/**
	 * Private constructor
	 */
	private ReadMailUtils() {
		this.baseURL = "https://api.maildrop.cc";
		this.endPoint = "graphql";
	}

	/**
	 * initializes EmailUtil object if it was not created earlier
	 */
	public static synchronized ReadMailUtils getInstance() {
		if (email == null) {
			email = new ReadMailUtils();
		}
		return email;
	}
	/**
	 *
	 * Enum to set feature type(either GetInbox details or GetMessage message details from mail)
	 */
	private enum Email {
		GETINBOX("GetInbox"), 
		GETMESSAGE("GetMessage"),
		DELETEMESSAGE("DeleteMessage");
        
		private String name;

		Email(String name) {
			this.name = name;
		}

		public String getName() {
			return this.name;
		}
	}
	
	/**
	 * 
	 * @param PayloadBody
	 * @return APIResponse
	 * use to do API POST request
	 * @throws Exception 
	 */
	private APIResponse postTbAddStatus(JSONObject PayloadBody) throws Exception {
		ApiDriver apiDriver = new ApiDriver(baseURL, endPoint);
		apiDriver.setRequestBody(PayloadBody.toString());
		return apiDriver.POST();
	}

	/**
	 * 
	 * @param mailBox
	 * @return EmailResponse
	 * This method is use to get In-box details from Maildrop
	 * @throws Exception 
	 */
	private EmailResponse getInboxDetails(String mailBox) throws Exception {
		Gson gson = new Gson();
		EmailResponse emailResponse = new EmailResponse();
		HashMap variableMap=new HashMap();
		variableMap.put("mailbox", mailBox);
		JSONObject obj = new JSONObject();

		obj.put("operationName", Email.GETINBOX.getName());
		obj.put("variables", variableMap);
		obj.put("query",
				"query GetInbox($mailbox: String\u0021) {\n  ping(message: \"Test\")\n  inbox(mailbox: $mailbox) {\n    id\n    subject\n    date\n    headerfrom\n    __typename\n  }\n  altinbox(mailbox: $mailbox)\n}\n");
		APIResponse response = postTbAddStatus(obj);
		if(response.response.statusCode()==200)
			emailResponse=gson.fromJson(response.getResposneBody(), EmailResponse.class);
		else
			reporter.logTestStepDetails(Status.FAIL,"Not getting success response for 'GETINBOX' API call, Please check request specification");
		return emailResponse;
	}

	/**
	 * 
	 * @param mailBox
	 * @param messageID
	 * @return EmailResponse
	 * This method is use to get message details from Maildrop
	 * @throws Exception 
	 */
	private EmailResponse getMessageDetails(String mailBox, String messageID) throws Exception {
		Gson gson = new Gson();
		EmailResponse emailResponse = new EmailResponse();
		HashMap variableMap=new HashMap();
		variableMap.put("mailbox", mailBox);
		variableMap.put("id", messageID);
		JSONObject obj = new JSONObject();
		
		obj.put("operationName", Email.GETMESSAGE.getName());
		obj.put("variables", variableMap);
		obj.put("query",
				"query GetMessage($mailbox: String!, $id: String!) {\n  message(mailbox: $mailbox, id: $id) {\n    id\n    subject\n    date\n    headerfrom\n    data\n    html\n    __typename\n  }\n}\n");
		APIResponse response = postTbAddStatus(obj);
		if(response.response.statusCode()==200)
			emailResponse=gson.fromJson(response.getResposneBody(), EmailResponse.class);
		else
			reporter.logTestStepDetails(Status.FAIL,"Not getting success response for 'GETMESSAGE' API call, Please check request specification");
		return emailResponse;
	}
	
	/**
	 * 
	 * @param mailBox
	 * @param messageID
	 * @return EmailResponse
	 * This method is use to delete message from Maildrop
	 * @throws Exception 
	 */
	private EmailResponse deleteMail(String mailBox, String messageID) throws Exception {
		Gson gson = new Gson();
		EmailResponse emailResponse = new EmailResponse();
		HashMap variableMap=new HashMap();
		variableMap.put("mailbox", mailBox);
		variableMap.put("id", messageID);
		JSONObject obj = new JSONObject();
		
		obj.put("operationName", Email.DELETEMESSAGE.getName());
		obj.put("variables", variableMap);
		obj.put("query",
				"mutation DeleteMessage($mailbox: String!, $id: String!) {\n  delete(mailbox: $mailbox, id: $id)\n}\n");
		APIResponse response = postTbAddStatus(obj);
		if(response.response.statusCode()==200)
			emailResponse=gson.fromJson(response.getResposneBody(), EmailResponse.class);
		else
			reporter.logTestStepDetails(Status.FAIL,"Not getting success response for 'DELETEMESSAGE' API call, Please check request specification");
		return emailResponse;
	}


	/**
	 * 
	 * @param mailBox
	 * @return Inbox object
	 * This method use to fetch latest mail received in the in-box ('<userInput>@ maildrop.cc')
	 * @throws Exception 
	 */
	public Inbox getLatestMail(String mailBox) throws Exception {
		Inbox email=getInboxDetails(mailBox).getData().getInbox().get(0);
		reporter.logTestStepDetails(Status.PASS,email.toString());
		return email;
	}

	/**
	 * 
	 * @param mailBox
	 * @param timeStamp
	 * @return List<Inbox>
	 * This method use to fetch list of mails received after specified time stamp
	 * @throws Exception 
	 */
	public List<Inbox> getLatestMailAfter(String mailBox, String timeStamp) throws Exception {
		List<Inbox> inboxList = new ArrayList();
		String zoneName = "UTC";
		LocalDateTime expDAteTime = JavaUtils.getLocalTime(timeStamp, zoneName);
		ArrayList<Inbox> emailList=getInboxDetails(mailBox).getData().getInbox();
		for (Inbox obj : emailList) {
			LocalDateTime dateTimeFromResponse = JavaUtils.getLocalTime(obj.getDate(), zoneName);

			if (dateTimeFromResponse.isAfter(expDAteTime)) {
				inboxList.add(obj);
			}
		}
		reporter.logTestStepDetails(Status.PASS,emailList.toString());
		return inboxList;
	}
	
    /**
     * 
     * @param mailBox
     * @param messageID
     * @return String
     * This method use to get the mail details
     * @throws Exception 
     */
	public String getMessage(String mailBox, String messageID) throws Exception {
		String email=getMessageDetails(mailBox, messageID).getData().getMessage().getHtml()
				.replaceAll("<div dir=\\\"ltr\\\">", "").replaceAll("</div>\\r\\n", "");
		reporter.logTestStepDetails(Status.PASS,email);
		return email;

	}
	
	 /**
     * 
     * @param mailBox
     * @param messageID
     * @return String
     * This method use to delete a mail
	 * @throws Exception 
     */
	public boolean deleteMessage(String mailBox, String messageID) throws Exception {
		boolean isDelete=deleteMail(mailBox, messageID).getData().isDelete();
		if(isDelete)
		reporter.logTestStepDetails(Status.PASS,"isDelete= true");
		return isDelete;

	}

}
