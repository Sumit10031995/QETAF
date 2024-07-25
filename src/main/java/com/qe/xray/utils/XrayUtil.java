package com.qe.xray.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Stack;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.qe.api.enums.JiraIssueType.IssueType;
import com.qe.commoncore.config.XrayConfig;
import com.qe.commoncore.constants.AuthorizationConstants;
import com.qe.commoncore.constants.ContextConstant;
import com.qe.commoncore.constants.XrayIntegration;
import com.qe.commoncore.model.CreateIssueDTO;
import com.qe.commoncore.model.InfoDetailsDTO;
import com.qe.commoncore.model.InfoDetailsDTO.Component;
import com.qe.commoncore.model.InfoDetailsDTO.Fields;
import com.qe.commoncore.model.InfoDetailsDTO.Project;
import com.qe.commoncore.model.TestCaseStatus;
import com.qe.commoncore.utils.Configurator;
import com.qe.commoncore.utils.FileUtil;
import com.qe.commoncore.utils.JavaUtils;
import com.qe.commoncore.utils.JsonUtil;
import com.qe.exception.XrayException;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class XrayUtil {
	private static final Logger log = LoggerFactory.getLogger(XrayUtil.class);
	private static String executionKey = Configurator.getInstance().getParameter(ContextConstant.JIRA_TESTEXECUTION_KEY);
	private static XrayConfig xrayConfig;
	private static XrayUtil xray;
	private static boolean isExtnKeyexist;
	private CreateIssueDTO createJiraIssue;
	private CreateIssueDTO.Fields fields;
	private CreateIssueDTO.Fields.Project project;
	private CreateIssueDTO.Fields.IssueType issueType;
    private static String testExecutionKey;
    private static boolean flag=true;

	private XrayUtil() {
		this.createJiraIssue = new CreateIssueDTO();
		this.fields = new CreateIssueDTO.Fields();
		this.project = new CreateIssueDTO.Fields.Project();
		this.issueType = new CreateIssueDTO.Fields.IssueType();
		xrayConfig = new XrayConfig();
	}

	public static synchronized XrayUtil getInstance() {
		if (xray == null) {
			xray = new XrayUtil();
		}
		return xray;
	}

	
	public boolean addJiraIssue(Stack<TestCaseStatus> testCases) {
		if (testCases.isEmpty()) {
			return true;
		}

		if(flag) {
		createTestExecutationKey(testCases);
		flag=false;
		}
		
		TestCaseStatus testResult = testCases.pop();
		Response response = null;

		try {
			RequestSpecification request = getRestSpecification();
			request.baseUri(xrayConfig.getXRAYBaseURL());
			request.log().all().body(getRequestBodyForCreateXrayTestIssue(testResult));
			response = request.post(xrayConfig.getCreateIssueURL());

			String tesKey = null;
			String testID= null;

			if (Objects.nonNull(response) && response.getStatusCode() == 201) {
				tesKey = JsonUtil.getValue(response.asString(), "key").toString();
				testID = JsonUtil.getValue(response.asString(), "id").toString();

				if (Objects.nonNull(tesKey) && !StringUtils.isEmpty(tesKey)) {
					mapTestToTestExecutationTicket(testExecutionKey,tesKey);
					//updateTestStatus(testResult,testID,tesKey);
					addJiraIssue(testCases); // Recursive call
				}
			} else {
				log.info(response.getBody().prettyPrint());
			}
		} catch (Exception e) {
			throw new XrayException("Unable to update the Test status: " + e.getMessage());
		}

		return Objects.nonNull(response) && response.getStatusCode() == 200 && testCases.isEmpty();
	}
    
//	public void updateTestStatus(TestCaseStatus obj, String id, String key) {
//		Response response = null;
//		JSONObject requestBody = new JSONObject();
//		JSONObject fields = new JSONObject();
//		fields.put("customfield_" + id, obj.getStatus());
//		requestBody.put("fields", fields);
//
//		try {
//			RequestSpecification request = getRestSpecification();
//			response = request.log().all().pathParam("key", key).body(requestBody.toString())
//					.patch(xrayConfig.getUpdateStatusURL());
//			if(response.statusCode()==200) {
//				System.out.println("Test case successsfully added");
//			}else {
//				System.out.println("Unable to add testcase");
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//	}
	public boolean createTestExecutationKey(Stack<TestCaseStatus> testCases) {
		Response response = null;
		try {
			isExtnKeyexist= isTestKeyExist();
			if(!isExtnKeyexist) {
			RequestSpecification request = getRestSpecification();
			request.baseUri(xrayConfig.getXRAYBaseURL());
			request.log().all().body(getRequestBodyForCreateXrayExecutationIssue(testCases.peek()));
			response = request.post(xrayConfig.getCreateIssueURL());
			
			if (Objects.nonNull(response) && response.getStatusCode() == 201) {
				if (!StringUtils.isEmpty(testExecutionKey=JsonUtil.getValue(response.asString(), "key").toString())) {
					   executionKey=testExecutionKey;
					   updateTestExecution(testExecutionKey);
					}
					log.info("Response Test Execution Key: {}", testExecutionKey);
				}

			} else {
				testExecutionKey=executionKey;
			}
		} catch (Exception e) {
			throw new XrayException("Unable to update the Test status" + e.getMessage());
		}
		
		return Objects.nonNull(response) && response.getStatusCode() == 200;
	}
	

	private String getRequestBodyForCreateXrayExecutationIssue(TestCaseStatus testCase) {
		Gson gson=new Gson();
		try {
				fields.setSummary(testCase.getXmlSuiteName() + "::"+ JavaUtils.getDateTime("yyyy-MM-dd HH:mm:ss.SSS"));
				issueType.setName(IssueType.TESTEXECUTION.get());
				project.setKey(xrayConfig.getJiraProjectKey());

				fields.setProject(project);
				fields.setIssuetype(issueType);
				createJiraIssue.setFields(fields);
			return gson.toJson(createJiraIssue);
		} catch (Exception e) {
			throw new XrayException("Unable to create the request body for creating a Jira issue");
		}
	}
	
	private String getRequestBodyForCreateXrayTestIssue(TestCaseStatus testCase) {
		Gson gson=new Gson();
		try {
			fields.setSummary(testCase.getMethodDescription());
			issueType.setName(IssueType.BUG.get());
			project.setKey(xrayConfig.getJiraProjectKey());

			fields.setProject(project);
			fields.setIssuetype(issueType);
			createJiraIssue.setFields(fields);
			return gson.toJson(createJiraIssue);
		} catch (Exception e) {
			throw new XrayException("Unable to create the request body for creating a Jira issue");
		}
	}

	private boolean isTestKeyExist() {
		boolean isKeyExist = false;
		RequestSpecification request = getRestSpecification();
		request.baseUri(xrayConfig.getXRAYBaseURL());
		request.log().all().baseUri(xrayConfig.getXRAYBaseURL())
		.pathParam("key", executionKey);
		Response response = request.get(xrayConfig.getUpdateStatusURL());

		try {
			if (JsonUtil.getValue(response.asString(), "key") != null) {
				isKeyExist = true;
			}
		} catch (Exception e) {
			isKeyExist = false;
		}
		return (response.statusCode() == 200 && isKeyExist) ? true : false;
	}

	private RequestSpecification getRestSpecification() {
		RequestSpecification request = RestAssured.given().auth().basic(xrayConfig.getUserName(),
				xrayConfig.getXrayPass());
		request.header(AuthorizationConstants.AUTHORIZATION, getBasicAuth());
		request.header(AuthorizationConstants.CONTENT_TYPE, AuthorizationConstants.APPLICATION_JSON);
		request.header(AuthorizationConstants.ACCEPT, AuthorizationConstants.APPLICATION_JSON);
		return request;
	}

	private String getBasicAuth() {
		String userCredentials = xrayConfig.getUserName() + ":" + xrayConfig.getXrayPass();
		return "Basic " + new String(new Base64().encode(userCredentials.getBytes()));
	}
	
	/**
	 * @param filePath
	 * @return This method performs a HTTP POST request to update the JIRA
	 *         components details
	 */
	private boolean updateTestExecution(String testExecIssueKey) {
		Response response = null;
		String componentSet = "";
		ArrayList<String> validComponents = new ArrayList();
		ArrayList<String> invalidComponents = new ArrayList();

		try {
			String resultJson = createResultJsonFile(testExecIssueKey);
			List<String> inputComponents = Configurator.getInstance().getJiraComponents();
			for (String component : inputComponents) {
				componentSet += (inputComponents.indexOf(component) > 0 && !componentSet.isEmpty()) ? "," + component
						: component;
				String infoJson = createInfoJsonFile(Arrays.asList(componentSet.split(",")));
				RestAssured.baseURI = xrayConfig.getXRAYBaseURL();
				RequestSpecification request = RestAssured.given().auth().basic(xrayConfig.getUserName(),
						xrayConfig.getXrayPass());
				request.header(AuthorizationConstants.AUTHORIZATION, getBasicAuth());
				request.header(AuthorizationConstants.CONTENT_TYPE, AuthorizationConstants.MULTIPART_FORM_DATA);
				request.multiPart(XrayIntegration.INFO_KEY, new File(infoJson), infoJson);
				request.multiPart(XrayIntegration.RESULT_KEY, new File(resultJson), resultJson);
				response = request.post(xrayConfig.getTestExecutionUpdateURI());
				if (response.getStatusCode() == 200) {
					validComponents.add(component);
				} else if (response.getStatusCode() == 400) {
					componentSet = resetComponentSet(componentSet);
					invalidComponents.add(component);
				}
			}
			if (validComponents.size() > 0)
				log.info("Component list '" + validComponents + " successfully set to executation ticket:- "
						+ testExecIssueKey);
			if (invalidComponents.size() > 0)
				log.info("Invalid Component :-'" + invalidComponents);
			return Objects.nonNull(response) && response.getStatusCode() == 200;
		} catch (Exception e) {
			log.error("Unable to update the Test status " + e.getMessage());
			throw new XrayException("Unable to update the Test status" + e.getMessage());
		}
	}

	/**
	 * @param testExecutionKey
	 * @return
	 * @throws Exception Creates a JSON file named "Result.json" and writes the
	 *                   provided TestExecutionKey to it.
	 */
	private String createResultJsonFile(String testExecutionKey) throws Exception {
		String fileath = "test-output/Result.json";
		log.info("Creates a \"Result.json\" file with the specified test execution key: {}", testExecutionKey);
		JsonObject obj = new JsonObject();
		obj.addProperty(XrayIntegration.TEST_EXECUTION_KEY, testExecutionKey);
		// Write testExecutionKey into Result json file
		FileUtil.createFile(fileath);
		try (FileWriter file = new FileWriter(fileath)) {
			file.write(obj.toString());
			file.flush();
			log.info("Successfully wrote a JSON object to Result.json file...!!");
		} catch (IOException e) {
			e.printStackTrace();
			log.error("Unable to write the testExecutionKey to Result.json file: {}", e.getMessage());
		}
		return fileath;
	}

	/**
	 * @param components
	 * @return Creates a JSON file named "Info.json" and writes the provided
	 *         component details to it.
	 */
	private String createInfoJsonFile(List<String> inputComponents) {
		String fileath = "test-output/Info.json";
		List<Component> components = new ArrayList();

		InfoDetailsDTO info = new InfoDetailsDTO();
		Project project = new InfoDetailsDTO().new Project();
		project.setId(xrayConfig.getJiraID());
		project.setProjectKey(xrayConfig.getJiraProjectKey());

		Fields field = new InfoDetailsDTO().new Fields();
		for (String component : inputComponents) {
			Component compnt = new InfoDetailsDTO().new Component();
			compnt.setName(component);
			components.add(compnt);
		}
		field.setProject(project);
		field.setComponents(components);
		info.setFields(field);
		try {
			FileUtil.createFile(fileath);
			FileWriter file = new FileWriter(fileath);
			ObjectMapper objectMapper = new ObjectMapper();
			file.write(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(info));
			file.flush();
			log.info("Successfully wrote component '" + inputComponents + "' to Info.json file...!!");
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Unable to write the testExecutionKey to Info.json file: {}", e.getMessage());
		}
		return fileath;
	}

	private String resetComponentSet(String componentSet) {
		String componentArray[] = componentSet.split(",");
		String topElement = componentArray[componentArray.length - 1];
		return (componentArray.length == 1) ? componentSet.replaceAll(topElement, "")
				: componentSet.replaceAll("," + topElement, "");
	}
	
	/**
	 * 
	 * @param testExnKey
	 * @param testKey
	 * @return
	 * This API use to link a issue to a jira ticket
	 */
    public Response mapTestToTestExecutationTicket(String testExnKey,String testKey) {
		RequestSpecification request = getRestSpecification();
		request.baseUri(xrayConfig.getXRAYBaseURL());
		request.body(getRequestBodyToMapTestToTestExecutation(testKey).toString())
		.pathParam("key", testExnKey).log().all();
		Response response = request.put(xrayConfig.getUpdateStatusURL());

		if(response.statusCode()==204) {
			System.out.println("test successfully linked to the jira ticket");
		}else
			System.out.println("Unabel to link the jira ticket");
        return response;
    }
    
    public static JSONObject getRequestBodyToMapTestToTestExecutation(String relatedIssueKey) {
        JSONObject requestBody = new JSONObject();
        JSONObject update = new JSONObject();
        JSONArray issueLinks = new JSONArray();
        JSONObject issueLink = new JSONObject();
        JSONObject add = new JSONObject();
        JSONObject linkType = new JSONObject();
        
        linkType.put("name", "Relates");
        linkType.put("inward", "is related to");
        linkType.put("outward", "relates to");

        JSONObject outwardIssue = new JSONObject();
        outwardIssue.put("key", relatedIssueKey);

        add.put("type", linkType);
        add.put("outwardIssue", outwardIssue);

        issueLink.put("add", add);

        issueLinks.put(issueLink);
        update.put("issuelinks", issueLinks);

        requestBody.put("update", update);
        return requestBody;
    }
	
    /**
     * <host>/rest/raven/1.0/api/testexec/{TestExecutationID}/status?status=PASS
     * need to implement this API to add Overall Execution Status. Currently this API is not working
     * Throwing 404 error
     */
}
