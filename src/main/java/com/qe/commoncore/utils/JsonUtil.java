package com.qe.commoncore.utils;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectWriter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import io.restassured.path.json.JsonPath;


/**
 * The ReadJson Utility class has static methods for extracting values of a
 * particular Json Object from the json response dynamically.
 */

public class JsonUtil {

	static Object obj;

	/**
	 * This method returns the value of a JsonObject in Object return type
	 * 
	 * @param jsonResponse as String
	 * @param jsonPath     as String
	 * @return object present at jsonPath in the jsonResponse
	 * @exception throws nullPointerException if jsonpath is not found
	 * 
	 */

	public static synchronized Object getValue(String jsonResponse, String jsonPath) throws Exception {
		
		JsonPath jpath = new JsonPath(jsonResponse);
		obj = jpath.get(jsonPath);
		return obj;
		
	}

	/**
	 * This method returns list of all values of a JsonObject present inside
	 * jsonresponse
	 * 
	 * @param jsonResponse as String
	 * @param jsonPath     as String
	 * @return List of Object present at jsonPath in the jsonResponse
	 * @exception throws nullPointerException if jsonpath is not found
	 * Collections.singletonList-->The list created by Collections.singletonList is immutable, meaning its size and content cannot change. This ensures thread safety and avoids unintended modifications.
	 */

	public static synchronized List<Object> getValues(String jsonResponse, String jsonPath) throws Exception {

		JsonPath jpath = new JsonPath(jsonResponse);
		obj = jpath.get(jsonPath);
		if (obj == null) {
			throw new Exception("No jsonpath found for " + jsonPath);
		}

		List<Object> list = (List<Object>) Collections.singletonList(obj).get(0);
		return list;
	}

	/**
	 * This method returns size of list of all values of a JsonObject present inside
	 * jsonresponse
	 * 
	 * @param jsonResponse as String
	 * @param jsonPath     as String
	 * @return size of List of Object present at jsonPath in the jsonResponse
	 * @exception throws nullPointerException if jsonpath is not found
	 */
	public static synchronized int getCount(String jsonResponse, String jsonPath) throws Exception {

		JsonPath jpath = new JsonPath(jsonResponse);
		obj = jpath.get(jsonPath);
		if (obj == null) {
			throw new Exception("No jsonpath found for " + jsonPath);
		}

		List<Object> list = (List<Object>) Collections.singletonList(obj).get(0);
		return list.size();

	}


	public static  String prettyPrint(String jsonString) {
		String prettyJsonString = null;
		try {
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			JsonParser parser = new JsonParser();
			JsonElement jsonElement = parser.parse(jsonString);
			prettyJsonString = gson.toJson(jsonElement);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return prettyJsonString;

	}

	
	public static String hashMapToJSONCoverter(Map<String, String> map) throws Exception {
		String input = null;
		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.enable(SerializationFeature.INDENT_OUTPUT);
			input = mapper.writeValueAsString(map);
			System.out.println(input);
		} catch (Exception e) {
			throw new Exception(
					" Conversion of Map Object to JSON String Method Failed . \n " + e.getLocalizedMessage());

		}
		return input;
	}

	/**
	 * getObjectFromJsonString method converts json string to corresponding java object.
	 *
	 * @param jsonString json string
	 * @param dto        of any Type
	 * @throws JsonProcessingException 
	 */
	public static synchronized <T> T getObjectFromJsonString(String jsonString, Class<T> dto) throws JsonProcessingException  {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			return objectMapper.readValue(jsonString, dto);
		} catch (JsonProcessingException jsonProcessingException) {
			throw jsonProcessingException;
		}
	}

	/**
	 * getJsonStringFromObject method converts java object to json string.
	 *
	 * @param object java Object
	 * @throws JsonProcessingException 
	 */
	public static String getJsonStringFromObject(Object object) throws JsonProcessingException {
		try {
			ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
			return ow.writeValueAsString(object);
		} catch (JsonProcessingException jsonProcessingException) {
			throw jsonProcessingException;
		}
	}
}
