package com.github.estuaryoss.libs.zephyruploader.service;

import com.github.estuaryoss.libs.zephyruploader.component.ZephyrConfig;
import com.github.estuaryoss.libs.zephyruploader.model.ZephyrMetaInfo;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;
import io.restassured.specification.RequestSpecification;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static io.restassured.RestAssured.preemptive;
import static org.assertj.core.api.Assertions.assertThat;

@Service
@Slf4j
public class ZephyrService {
    private ZephyrConfig zephyrConfig;
    private final RequestSpecification requestSpecification;

    @Autowired
    public ZephyrService(ZephyrConfig zephyrConfig) {
        this.zephyrConfig = zephyrConfig;
        this.requestSpecification = new RequestSpecBuilder()
                .setBaseUri(zephyrConfig.getJiraUrl())
                .setRelaxedHTTPSValidation()
                .setAuth(preemptive().basic(zephyrConfig.getUsername(), zephyrConfig.getPassword()))
//                .addFilter(new RequestLoggingFilter())
                .addFilter(new ResponseLoggingFilter())
                .build();
    }

    public String getProjectByKey(String projectKey) {
        Response httpResponse = RestAssured.given()
                .spec(this.requestSpecification)
                .log()
                .uri()
                .get("/rest/api/2/project/" + projectKey);

        assertThat(httpResponse.getStatusCode())
                .withFailMessage(String.format("Failed to get project described by projectKey=%s, responseBody=%s", projectKey,
                        httpResponse.getBody().asString()))
                .isEqualTo(HttpStatus.SC_OK);

        return httpResponse.jsonPath().getString("id");
    }

    public String getIssueByKey(String issueKey) {
        Response httpResponse = RestAssured.given()
                .spec(this.requestSpecification)
                .log()
                .uri()
                .get("/rest/api/2/issue/" + issueKey);

        assertThat(httpResponse.getStatusCode())
                .withFailMessage(String.format("Failed to get issue described by key =%s, responseBody=%s", issueKey,
                        httpResponse.getBody().asString()))
                .isEqualTo(HttpStatus.SC_OK);

        return httpResponse.jsonPath().getString("id");
    }

    public int getFolderForCycleId(String folderName, String cycleId, String projectId, String versionId) {
        Response httpResponse = RestAssured.given()
                .spec(this.requestSpecification)
                .queryParam("versionId", versionId)
                .queryParam("projectId", projectId)
                .log()
                .uri()
                .get(String.format("/rest/zapi/latest/cycle/%s/folders", cycleId));

        ResponseBody responseBody = httpResponse.getBody();

        assertThat(httpResponse.getStatusCode())
                .withFailMessage(String.format("Failed to get folders for cycleId=%s, versionId=%s, projectId=%s, responseBody=%s",
                        cycleId, versionId, projectId, responseBody.asString()))
                .isEqualTo(HttpStatus.SC_OK);

        List folderInfo = responseBody.as(List.class);

        HashMap<String, Integer> folderIDs = new HashMap<>();
        IntStream.range(0, folderInfo.size()).forEach(i -> {
            String key = (String) ((Map) folderInfo.get(i)).get("folderName");
            int value = (int) ((Map) folderInfo.get(i)).get("folderId");
            folderIDs.put(key, value);
        });

        if (folderIDs.get(folderName) != null) {
            int folderId = folderIDs.get(folderName);
            log.info(String.format("Folder name %s exists, id=%s", folderName, folderId));

            return folderId;
        }

        return 0;
    }

    public String getCycleId(String cycleName, String projectId, String versionId) {
        Response httpResponse = RestAssured.given()
                .spec(this.requestSpecification)
                .queryParam("versionId", versionId)
                .queryParam("projectId", projectId)
                .log()
                .uri()
                .get("/rest/zapi/latest/cycle");

        ResponseBody responseBody = httpResponse.getBody();

        assertThat(httpResponse.getStatusCode())
                .withFailMessage(String.format("Failed to get cycleId for cycleName=%s, versionId=%s, projectId=%s, responseBody=%s",
                        cycleName, versionId, projectId, responseBody.asString()))
                .isEqualTo(HttpStatus.SC_OK);

        String cycleId = "";
        List<Object> elements = Arrays.stream(responseBody.as(Map.class).keySet().toArray())
                .filter(elem -> responseBody.as(Map.class).get(elem) instanceof Map)
                .collect(Collectors.toList());

        for (Object element : elements) {
            String name = ((Map) responseBody.as(Map.class).get(element.toString())).get("name").toString();
            if (name.equals(cycleName)) {
                cycleId = element.toString();
                break;
            }
        }

        return cycleId;
    }

    public String getFoldersForCycleId(String cycleId, String projectId, String versionId) {
        Response httpResponse = RestAssured.given()
                .spec(this.requestSpecification)
                .queryParam("versionId", versionId)
                .queryParam("projectId", projectId)
                .log()
                .uri()
                .get(String.format("/rest/zapi/latest/cycle/%s/folders", cycleId));

        String responseBody = httpResponse.getBody().asString();

        assertThat(httpResponse.getStatusCode())
                .withFailMessage(String.format("Failed to get folders for cycleId=%s, versionId=%s, projectId=%s, responseBody=%s",
                        cycleId, versionId, projectId, responseBody))
                .isEqualTo(HttpStatus.SC_OK);

        return responseBody;
    }

    public Integer createFolderForCycle(String projectId, String versionId, String cycleId, String folderName) {
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("cycleId", cycleId);
        jsonBody.put("name", folderName);
        jsonBody.put("projectId", projectId);
        jsonBody.put("versionId", versionId);

        Response httpResponse = RestAssured.given()
                .spec(this.requestSpecification)
                .header("Content-Type", ContentType.JSON)
                .body(jsonBody)
                .log()
                .uri()
                .post("/rest/zapi/latest/folder/create");

        ResponseBody responseBody = httpResponse.getBody();
        Integer id = (Integer) responseBody.as(Map.class).get("id");

        assertThat(httpResponse.getStatusCode())
                .withFailMessage(String.format("Failed to create folder=%s for cycleId=%s, requestBody=%s, responseBody=%s",
                        folderName, cycleId, jsonBody.toString(), responseBody.asString()))
                .isEqualTo(HttpStatus.SC_OK);

        return id;
    }

    public void deleteFolderFromCycle(double folderId, String projectId, String versionId, String cycleId) {
        RequestSpecification request = RestAssured.given().log().uri()
                .queryParam("versionId", versionId)
                .queryParam("projectId", projectId)
                .queryParam("cycleId", cycleId);

        Response httpResponse = request.delete(String.format("/rest/zapi/latest/folder/%s", (int) folderId));
        ResponseBody responseBody = httpResponse.getBody();

        assertThat(httpResponse.getStatusCode())
                .withFailMessage(String.format("### Failed to delete folderId=%s for versionId=%s, projectId=%s, cycled=%s, responseBody=%s ###",
                        folderId, versionId, projectId, cycleId, responseBody))
                .isEqualTo(HttpStatus.SC_OK);
    }

    public String createNewExecution(String issueId, ZephyrMetaInfo zephyrDetails, ZephyrConfig zephyrConfig) {
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("cycleId", zephyrDetails.getCycleId());
        jsonBody.put("projectId", zephyrDetails.getProjectId());
        jsonBody.put("versionId", zephyrDetails.getVersionId());
        jsonBody.put("assigneeType", "assignee");
        jsonBody.put("assignee", zephyrConfig.getUsername());
        jsonBody.put("folderId", zephyrDetails.getFolderId());
        jsonBody.put("issueId", issueId);

        Response httpResponse = RestAssured.given()
                .spec(this.requestSpecification)
                .header("Content-Type", ContentType.JSON)
                .body(jsonBody)
                .log()
                .uri()
                .post("/rest/zapi/latest/execution");

        ResponseBody responseBody = httpResponse.getBody();

        String id = (String) responseBody.as(Map.class).keySet().toArray()[0];
        assertThat(httpResponse.getStatusCode())
                .withFailMessage(String.format("Failed to create new execution for issueId=%s, requestBody=%s, responseBody=%s",
                        issueId, jsonBody, responseBody))
                .isEqualTo(HttpStatus.SC_OK);

        return id;
    }

    public void updateExecutionId(String executionId, int status, String comment) {
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("status", status);
        jsonBody.put("comment", comment);

        Response httpResponse = RestAssured.given()
                .spec(this.requestSpecification)
                .header("Content-Type", ContentType.JSON)
                .body(jsonBody)
                .log()
                .uri()
                .put(String.format("/rest/zapi/latest/execution/%s/execute", executionId));

        ResponseBody responseBody = httpResponse.getBody();

        assertThat(httpResponse.getStatusCode())
                .withFailMessage(String.format("Failed to update execution id=%s, requestBody=%s, responseBody=%s",
                        executionId, jsonBody.toJSONString(), responseBody))
                .isEqualTo(HttpStatus.SC_OK);
    }

    public String getVersionForProjectId(String versionName, String projectId) {
        Response httpResponse = RestAssured.given()
                .spec(this.requestSpecification)
                .queryParam("projectId", projectId)
                .log()
                .uri()
                .get("/rest/zapi/latest/util/versionBoard-list");

        ResponseBody responseBody = httpResponse.getBody();
        assertThat(httpResponse.getStatusCode())
                .withFailMessage(String.format("Failed to get version for versionName=%s and projectId=%s, responseBody=%s",
                        versionName, projectId, responseBody))
                .isEqualTo(HttpStatus.SC_OK);

        log.info("### Got version response=%s " + responseBody.asString());

        Map json = responseBody.as(Map.class);

        List unreleasedVersions = (ArrayList) json.get("unreleasedVersions");
        String version = getVersion(versionName, unreleasedVersions);

        if (version == null) {
            List releasedVersions = (ArrayList) json.get("releasedVersions");
            version = getVersion(versionName, releasedVersions);
        }

        return version;
    }

    public ZephyrConfig getZephyrConfig() {
        return zephyrConfig;
    }

    public void setZephyrConfig(ZephyrConfig zephyrConfig) {
        this.zephyrConfig = zephyrConfig;
    }

    private String getVersion(String versionName, List versionList) {
        String version = null;

        for (int i = 0; i < versionList.size(); i++) {
            if (((Map) versionList.get(i)).containsKey("label")) {
                if (((Map) versionList.get(i)).get("label").equals(versionName)) {
                    version = ((Map) versionList.get(i)).get("value").toString();
                }
            }
        }

        return version;
    }
}
