package com.qe.commoncore.model;

import lombok.Data;

@Data
public class CreateIssueDTO {

	public Fields fields;

	@Data
	public static class Fields {
		public Project project;
		public String summary;
		public String description;
		public IssueType issuetype;

		@Data
		public static class Project {
			public String key;

		}

		@Data
		public static class IssueType {
			public String name;

		}
	}
}
