package com.qe.commoncore.model;

import java.util.ArrayList;

import com.google.gson.annotations.SerializedName;

@lombok.Data
public class EmailResponse {
	private Data data;

	@lombok.Data
	public class Data {
		private String ping;
		private ArrayList<Inbox> inbox;
		private String altinbox;
		private Message message;
		private boolean delete;
	}

	@lombok.Data
	public class Inbox {
		private String id;
		private String subject;
		private String date;
		private String headerfrom;
		@SerializedName("__typename")
		private String typename;
	}

	@lombok.Data
	public class Message {
		public String id;
		public String subject;
		public String date;
		public String headerfrom;
		public String data;
		public String html;
		@SerializedName("__typename")
		public String typename;
	}
}