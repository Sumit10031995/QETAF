package com.qe.commoncore.utils;

import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Random;

import com.qe.api.commoncore.BaseTest;
import com.qe.commoncore.constants.ContextConstant;


public class JavaUtils {
	
	public static int getRandomNumber(int low, int high) {
		Random r = new Random();
		int result = r.nextInt(high-low) + low;
		return result;
	}
	
	public static String getStackTrace(Exception e) {
		StringBuffer result = new StringBuffer();
		for (StackTraceElement element : e.getStackTrace()) {
			result.append(element.toString()).append("\n");
		}
		e.printStackTrace();
		return getAsHTML("Exception Message: "+ e.getMessage(),result.toString());
	}
	
	/**
	 * 
	 * @param key
	 * @param value
	 * @return String
	 *   This method allows for organized presentation with expand and collapse feature in the Extent Report.
	 */
	public static String getAsHTML(String key, String value) {
	    String html = "<div class=\"accordion\">\n" +
	            " <div class=\"card\">\n" +
	            " <div class=\"card-header\">" +
	            key + "<small><mark>Click to expand/collapse</mark></small>" +
	            "</div>" +
	            " <div class=\"collapse\">\n" +
	            " <div class=\"card-body\"><pre>&cardBody&</pre></div>" +
	            " </div></div></div>";
	    return html.replace("&cardBody&", value);
	}
	
	public static String getDateTime(String format) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		return dateFormat.format(new Date());
	}
	
	public static int getDayNumber(String dateString) {
	    LocalDate date = LocalDate.parse(dateString, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
	    DayOfWeek dayOfWeek = date.getDayOfWeek();
	    int dayNumber = (dayOfWeek.getValue() + 6) % 7;
	    return dayNumber;
	}
	
	/**
	 * Fetch the Jira Key as Hyperlink
	 * 
	 * 
	 * @param jiraKey
	 * @return JiraKey as HyperLink
	 */
	  public static String getJiraKeyasLink(String jiraKey) {
	  	if(jiraKey.isEmpty() || jiraKey == null) {
	  		return null;
	  	}else {
	  		return "<a href = 'https://naiksumit7.atlassian.net/browse/"+jiraKey+"'>"+jiraKey+"</a>";	
	  	}
	
	  }
	  
	  /**
	   * @param timeStamp
	   * @param zone
	   * @return
	   * Get local time
	   */
	  public static LocalDateTime getLocalTime(String timeStamp,String zone) {
			Instant instant = Instant.parse(timeStamp);
	    	return LocalDateTime.ofInstant(instant, ZoneId.of(zone));
		}
	  
	  /**
	   * @param obj
	   * @return
	   * This method is used to copy a user-defined number of elements into a new two-dimensional array
	   */
	  public static Object[][] copyArray(Object[][] obj){  
		  String dataProviderLimit = BaseTest.configurator.getParameter(ContextConstant.DATA_PROVIDER_LIMIT);
		  
			int limit = (dataProviderLimit.toLowerCase().equals("max")) ? obj.length - 1
					: (Integer.parseInt(dataProviderLimit) > obj.length - 1 || Integer.parseInt(dataProviderLimit)<=0) ? obj.length - 1
							: Integer.parseInt(dataProviderLimit);

			Object[][] copiedArray = new Object[limit][obj[0].length];

			for (int i = 1; i <= limit; i++) {
				for (int j = 0; j < obj[i].length; j++) {
					copiedArray[i - 1][j] = obj[i][j];
				}
			}
			return copiedArray;
	  }

}