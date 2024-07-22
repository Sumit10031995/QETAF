package com.qe.commoncore.model;

import java.util.List;

import lombok.Data;
@Data
public class InfoDetailsDTO {
	private Fields fields;
	@Data
	public class Project {
		private String id;
		private String projectKey;
	}
	@Data
	public class Component {
		private String name;
	}
	@Data
	public class Fields {
		private Project project;
		private List<Component> components;
	}
}
