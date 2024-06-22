package com.qe.api.response.dto;

import java.util.List;

import lombok.Data;

@Data
public class GETResponseDTO {
	private List<UserDTO> response;
	@Data
	public class UserDTO{
	private int userId;
	private int id;
	private String title;
	private String body;
	}
}
