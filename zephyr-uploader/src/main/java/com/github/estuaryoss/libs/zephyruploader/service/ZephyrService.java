package com.github.estuaryoss.libs.zephyruploader.service;

import com.github.estuaryoss.libs.zephyruploader.component.ZephyrConfig;
import com.github.estuaryoss.libs.zephyruploader.model.ZephyrMetaInfo;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;
import io.restassured.specification.RequestSpecification;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.preemptive;
import static org.assertj.core.api.Assertions.assertThat;

@Service
public class ZephyrService {
    private static final Logger log = LoggerFactory.getLogger(ZephyrService.class);
    private final int HTTP_STATUS_OK = 200;
    private ZephyrConfig zephyrConfig;

    @Autowired
    public ZephyrService(ZephyrConfig zephyrConfig) {
        this.zephyrConfig = zephyrConfig;
        RestAssured.baseURI = zephyrConfig.getJiraUrl();
        RestAssured.authentication = preemptive().basic(zephyrConfig.getUsername(), zephyrConfig.getPassword());
    }

    public String getProjectByKey(String projectKey) {
        RequestSpecification request = RestAssured.given().log().uri();
        Response httpResponse = request.get("api/2/project/" + projectKey);

        assertThat(httpResponse.getStatusCode())
                .withFailMessage(String.format("Failed to get project described by projectKey=%s, responseBody=%s", projectKey,
                        httpResponse.getBody().asString()))
                .isEqualTo(HTTP_STATUS_OK);

        return httpResponse.jsonPath().getString("id");
    }

    public String getIssueByKey(String issueKey) {
        RequestSpecification request = RestAssured.given().log().uri();
        Response httpResponse = request.get("api/2/issue/" + issueKey);

        assertThat(httpResponse.getStatusCode())
                .withFailMessage(String.format("Failed to get issue described by key =%s, responseBody=%s", issueKey,
                        httpResponse.getBody().asString()))
                .isEqualTo(HTTP_STATUS_OK);

        return httpResponse.jsonPath().getString("id");
    }

    public int getFolderForCycleId(String folderName, String cycleId, String projectId, String versionId) {
        RequestSpecification request = RestAssured.given().log().uri()
                .queryParam("versionId", versionId)
                .queryParam("projectId", projectId);
        Response httpResponse = request.get(String.format("zapi/latest/cycle/%s/folders", cycleId));
        ResponseBody responseBody = httpResponse.getBody();
        assertThat(httpResponse.getStatusCode())
                .withFailMessage(String.format("Failed to get folders for cycleId=%s, versionId=%s, projectId=%s, responseBody=%s",
                        cycleId, versionId, projectId, responseBody.asString()))
                .isEqualTo(HTTP_STATUS_OK);

        JSONArray folderInfo = responseBody.as(JSONArray.class);
        int folderId = 0;
        HashMap<String, Integer> folderIDs = new HashMap<>();
        for (int i = 0; i < folderInfo.size(); i++) {
            String key = (String) ((Map) folderInfo.get(i)).get("folderName");
            int value = (int) ((Map) folderInfo.get(i)).get("folderId");

            folderIDs.put(key, value);
        }

        if (folderIDs.get(folderName) != null) {
            folderId = folderIDs.get(folderName);
            log.info(String.format("Folder name %s exists, id=%s", folderName, folderId));
        }

        return folderId;
    }

    public String getCycleId(String cycleName, String projectId, String versionId) {
        RequestSpecification request = RestAssured.given().log().uri()
                .queryParam("versionId", versionId)
                .queryParam("projectId", projectId);

        Response httpResponse = request.get("zapi/latest/cycle");
        ResponseBody responseBody = httpResponse.getBody();
        assertThat(httpResponse.getStatusCode())
                .withFailMessage(String.format("Failed to get cycleId for cycleName=%s, versionId=%s, projectId=%s, responseBody=%s",
                        cycleName, versionId, projectId, responseBody.asString()))
                .isEqualTo(HTTP_STATUS_OK);

        String cycleId = "";
        List<Object> elements = Arrays.stream(responseBody.as(JSONObject.class).keySet().toArray())
                .filter(elem -> responseBody.as(JSONObject.class).get(elem) instanceof Map)
                .collect(Collectors.toList());

        for (Object element : elements) {
            String name = ((Map) responseBody.as(JSONObject.class).get(element.toString())).get("name").toString();
            if (name.equals(cycleName)) {
                cycleId = element.toString();
                break;
            }
        }

        return cycleId;
    }

    public String getFoldersForCycleId(String cycleId, String projectId, String versionId) {
        RequestSpecification request = RestAssured.given().log().uri()
                .queryParam("versionId", versionId)
                .queryParam("projectId", projectId);

        Response httpResponse = request.get(String.format("zapi/latest/cycle/%s/folders", cycleId));
        String responseBody = httpResponse.getBody().asString();
        assertThat(httpResponse.getStatusCode())
                .withFailMessage(String.format("Failed to get folders for cycleId=%s, versionId=%s, projectId=%s, responseBody=%s",
                        cycleId, versionId, projectId, responseBody))
                .isEqualTo(HTTP_STATUS_OK);

        return responseBody;
    }

    public Integer createFolderForCycle(String projectId, String versionId, String cycleId, String folderName) {
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("cycleId", cycleId);
        jsonBody.put("name", folderName);
        jsonBody.put("projectId", projectId);
        jsonBody.put("versionId", versionId);
        RequestSpecification request = RestAssured.given().log().uri();
        Response httpResponse = request
                .header("Content-Type", ContentType.JSON)
                .body(jsonBody).post("zapi/latest/folder/create");

        ResponseBody responseBody = httpResponse.getBody();
        Integer id = (Integer) responseBody.as(Map.class).get("id");

        assertThat(httpResponse.getStatusCode())
                .withFailMessage(String.format("Failed to create folder=%s for cycleId=%s, requestBody=%s, responseBody=%s",
                        folderName, cycleId, jsonBody.toString(), responseBody.asString()))
                .isEqualTo(HTTP_STATUS_OK);

        return id;
    }

    public void deleteFolderFromCycle(double folderId, String projectId, String versionId, String cycleId) {
        RequestSpecification request = RestAssured.given().log().uri()
                .queryParam("versionId", versionId)
                .queryParam("projectId", projectId)
                .queryParam("cycleId", cycleId);

        Response httpResponse = request.delete(String.format("zapi/latest/folder/%s", (int) folderId));
        ResponseBody responseBody = httpResponse.getBody();

        assertThat(httpResponse.getStatusCode())
                .withFailMessage(String.format("### Failed to delete folderId=%s for versionId=%s, projectId=%s, cycled=%s, responseBody=%s ###",
                        folderId, versionId, projectId, cycleId, responseBody))
                .isEqualTo(HTTP_STATUS_OK);
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

        RequestSpecification request = RestAssured.given().log().uri();
        Response httpResponse = request
                .header("Content-Type", ContentType.JSON)
                .body(jsonBody)
                .post("zapi/latest/execution");
        ResponseBody responseBody = httpResponse.getBody();
        String id = (String) responseBody.as(JSONObject.class).keySet().toArray()[0];

        assertThat(httpResponse.getStatusCode())
                .withFailMessage(String.format("Failed to create new execution for issueId=%s, requestBody=%s, responseBody=%s",
                        issueId, jsonBody.toString(), responseBody))
                .isEqualTo(HTTP_STATUS_OK);

        return id;
    }

    public void updateExecutionId(String executionId, int status, String comment) {
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("status", status);
        jsonBody.put("comment", comment);

        RequestSpecification request = RestAssured.given().log().uri();
        Response httpResponse = request
                .header("Content-Type", ContentType.JSON)
                .body(jsonBody)
                .put(String.format("zapi/latest/execution/%s/execute", executionId));
        ResponseBody responseBody = httpResponse.getBody();

        assertThat(httpResponse.getStatusCode())
                .withFailMessage(String.format("Failed to update execution id=%s, requestBody=%s, responseBody=%s",
                        executionId, jsonBody.toJSONString(), responseBody))
                .isEqualTo(HTTP_STATUS_OK);
    }

    public String getVersionForProjectId(String versionName, String projectId) {
        RequestSpecification request = RestAssured.given().log().uri()
                .queryParam("projectId", projectId);

        Response httpResponse = request.get("zapi/latest/util/versionBoard-list");
        ResponseBody responseBody = httpResponse.getBody();
        assertThat(httpResponse.getStatusCode())
                .withFailMessage(String.format("Failed to get version for versionName=%s and projectId=%s, responseBody=%s",
                        versionName, projectId, responseBody))
                .isEqualTo(HTTP_STATUS_OK);

        log.info("### Got version response=%s " + responseBody.asString());

        JSONObject json = responseBody.as(JSONObject.class);
        ArrayList array = (ArrayList) json.get("unreleasedVersions");
        String version = null;
        for (int i = 0; i < array.size(); i++) {
            if (((Map) array.get(i)).containsKey("label")) {
                if (((Map) array.get(i)).get("label").equals(versionName)) {
                    version = ((Map) array.get(i)).get("value").toString();
                }
            }
        }
        if (version == null) {
            array = (ArrayList) json.get("releasedVersions");
            for (int i = 0; i < array.size(); i++) {
                if (((Map) array.get(i)).containsKey("label")) {
                    if (((Map) array.get(i)).get("label").equals(versionName)) {
                        version = ((Map) array.get(i)).get("value").toString();
                    }
                }
            }
        }

        return version;
    }

    public ZephyrConfig getZephyrConfig() {
        return zephyrConfig;
    }

    public void setZephyrConfig(ZephyrConfig zephyrConfig) {
        this.zephyrConfig = zephyrConfig;
    }
}
