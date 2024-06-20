package com.qe.commoncore.utils;

public class XrayUtil
 {
//     private static final Logger log =
//             LoggerFactory.getLogger(XrayUtil.class);
//     private final XrayConfig config;
//
//     public XrayUtil(){
//         this.config = new XrayConfig();
//     }
//
//     /*
//     * Get the Test Results details based on given test plan
//     * @param: testPlan
//     * */
//
//     public List<TestCaseResult> getTestResults(String testPlan){
//         List result = null;
//         try {
//             RestAssured.baseURI = config.getXRAYGetURl(testPlan);
//             RequestSpecification httpRequest = getRestSpecification();
//             Response response = httpRequest.request(Method.GET);
//             if (Objects.nonNull(response) && response.getStatusCode() == 200) {
//                 result = response.getBody().as(List.class);
//             }
//         }catch(Exception e){
//             throw new XrayException("Unable to fetch the Tests "+ e.getMessage());
//         }
//         return result;
//     }
//     /**
//      * @param testPlan
//      * @return boolean
//      * Updates test execution details in Jira for a specified test execution key. If a user provides a test execution key,
//      * the method adds test status details to that existing test execution key. If no test execution key is provided,
//      * the API response generates a new test execution key, and the method appends the test details to this newly created
//      * test execution key.
//      */
//     public boolean updateTestPlan(TestPlan testPlan){
//         Response response = null;
//         try {
//             RestAssured.baseURI = config.getXRAYBaseURL();
//             RequestSpecification request = getRestSpecification();
//             request.body(testPlan);
//             response = request.post(config.getXRAYUpdatePathURI());
//             if(Objects.nonNull(response) && response.getStatusCode() == HttpStatus.SC_OK && StringUtils.isEmpty(testPlan.getTestExecutionKey())){
//                 Map testExecution = response.jsonPath().getMap("testExecIssue");
//                 if(Objects.nonNull(testExecution)){
//                     String testExecIssueKey = (String)testExecution.get("key");
//                     //Write Test Plan Execution Key into Json file
//                     log.info("Response Test Execution Key: {}", testExecIssueKey);
//                     updateTestExecution(testExecIssueKey);
//                 }
//
//             }else {
//            	 log.info(response.getBody().prettyPrint());
//             }
//         }catch(Exception e){
//             throw new XrayException("Unable to update the Test status"+ e.getMessage());
//         }
//         return Objects.nonNull(response) && response.getStatusCode() == 200;
//     }
//
//     private RequestSpecification getRestSpecification(){
//         RequestSpecification request = RestAssured.given().auth().basic(config.getUserName(),  config.getXrayPass());
//         request.header(AuthorizationConstants.AUTHORIZATION, getBasicAuth());
//         request.header(AuthorizationConstants.CONTENT_TYPE, AuthorizationConstants.APPLICATION_JSON);
//         request.header(AuthorizationConstants.ACCEPT, AuthorizationConstants.APPLICATION_JSON);
//         return request;
//     }
//
//     private String getBasicAuth(){
//         String userCredentials = config.getUserName() + ":" + config.getXrayPass();
//         return "Basic " + new String(new Base64().encode(userCredentials.getBytes()));
//     }
//
//     /**
//      * @param testExecutionKey
//      * @return
//      * @throws Exception
//      * Creates a JSON file named "Result.json" and writes the provided TestExecutionKey to it.
//      */
//     private String createResultJsonFile(String testExecutionKey) throws Exception {
//    	 String fileath="test-output/Result.json";
//         log.info("Creates a \"Result.json\" file with the specified test execution key: {}", testExecutionKey);
//         JsonObject obj = new JsonObject();
//         obj.addProperty(XrayIntegration.TEST_EXECUTION_KEY, testExecutionKey);
//         //Write testExecutionKey into Result json file  
//         FileUtil.createFile(fileath);
//         try (FileWriter file=new FileWriter(fileath)) {
//             file.write(obj.toString());
//             file.flush();
//             log.info("Successfully wrote a JSON object to Result.json file...!!");
//         } catch (IOException e) {
//        	 e.printStackTrace();
//            log.error("Unable to write the testExecutionKey to Result.json file: {}", e.getMessage());
//         }
//         return fileath;
//     }
//     
//		/**
//		 * @param components
//		 * @return Creates a JSON file named "Info.json" and writes the provided
//		 *         component details to it.
//		 */
//		private String createInfoJsonFile(List<String> inputComponents) {
//			String fileath = "test-output/Info.json";
//			List<Component> components = new ArrayList();
//
//			InfoDetailsDTO info = new InfoDetailsDTO();
//			Project project = new InfoDetailsDTO().new Project();
//			project.setId(config.getJiraID());
//			project.setProjectKey(config.getJiraProjectKey());
//
//			Fields field = new InfoDetailsDTO().new Fields();
//			for (String component : inputComponents) {
//				Component compnt = new InfoDetailsDTO().new Component();
//				compnt.setName(component);
//				components.add(compnt);
//			}
//			field.setProject(project);
//			field.setComponents(components);
//			info.setFields(field);
//			try {
//				FileUtil.createFile(fileath);
//				FileWriter file = new FileWriter(fileath);
//		        ObjectMapper objectMapper = new ObjectMapper();
//				file.write(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(info));
//				file.flush();
//				log.info("Successfully wrote component '"+inputComponents+"' to Info.json file...!!");
//			} catch (Exception e) {
//				e.printStackTrace();
//				log.error("Unable to write the testExecutionKey to Info.json file: {}", e.getMessage());
//			}
//			return fileath;
//		}
//
//    /**
//     * @param filePath
//     * @return
//     * This method performs a HTTP POST request to update the JIRA components details
//     */  
//	private boolean updateTestExecution(String testExecIssueKey) {
//		Response response = null;
//		String componentSet="";
//		ArrayList<String> validComponents=new ArrayList();
//		ArrayList<String> invalidComponents=new ArrayList();
//
//		try {
//			String resultJson = createResultJsonFile(testExecIssueKey);
//			List<String> inputComponents =Configurator.getInstance().getJiraComponents();
//			for (String component : inputComponents) {
//				componentSet+=(inputComponents.indexOf(component)>0 && !componentSet.isEmpty())?","+component:component;
//				String infoJson = createInfoJsonFile(Arrays.asList(componentSet.split(",")));
//				RestAssured.baseURI = config.getXRAYBaseURL();
//				RequestSpecification request = RestAssured.given().auth().basic(config.getUserName(),
//						config.getXrayPass());
//				request.header(AuthorizationConstants.AUTHORIZATION, getBasicAuth());
//				request.header(AuthorizationConstants.CONTENT_TYPE, AuthorizationConstants.MULTIPART_FORM_DATA);
//				request.multiPart(XrayIntegration.INFO_KEY, new File(infoJson), infoJson);
//				request.multiPart(XrayIntegration.RESULT_KEY, new File(resultJson), resultJson);
//				response = request.post(config.getTestExecutionUpdateURI());
//				if (response.getStatusCode() == 200) {
//					validComponents.add(component);
//				} else if (response.getStatusCode() == 400) {
//					componentSet=resetComponentSet(componentSet);
//					invalidComponents.add(component);
//				}
//			}
//			if(validComponents.size()>0)
//			log.info("Component list '" + validComponents +" successfully set to executation ticket:- "+ testExecIssueKey);
//			if(invalidComponents.size()>0)
//			log.info("Invalid Component :-'" + invalidComponents);
//			return Objects.nonNull(response) && response.getStatusCode() == 200;
//		} catch (Exception e) {
//			log.error("Unable to update the Test status " + e.getMessage());
//			throw new XrayException("Unable to update the Test status" + e.getMessage());
//		}
//	}
	
//	private String resetComponentSet(String componentSet) {
//	String componentArray[] = componentSet.split(",");
//	String topElement = componentArray[componentArray.length - 1];
//	return (componentArray.length == 1) ? componentSet.replaceAll(topElement, "")
//			: componentSet.replaceAll("," + topElement, "");
//}
     
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
	  		return "<a href = 'https://jira.walmart.com/browse/"+jiraKey+"'>"+jiraKey+"</a>";	
	  	}
	
	  }

 }
