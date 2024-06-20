package com.qe.commoncore.utils;

import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Random;


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

}