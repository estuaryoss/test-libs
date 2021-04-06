package com.github.estuaryoss.libs.zephyruploader.model;

public class ZephyrMetaInfo {
    int folderId;
    String cycleId;
    String projectId;
    String versionId;

    public ZephyrMetaInfo folderId(int folderId) {
        this.folderId = folderId;
        return this;
    }

    public ZephyrMetaInfo cycleId(String cycleId) {
        this.cycleId = cycleId;
        return this;
    }

    public ZephyrMetaInfo projectId(String projectId) {
        this.projectId = projectId;
        return this;
    }

    public ZephyrMetaInfo versionId(String versionId) {
        this.versionId = versionId;
        return this;
    }

    public int getFolderId() {
        return folderId;
    }

    public String getCycleId() {
        return cycleId;
    }

    public String getProjectId() {
        return projectId;
    }

    public String getVersionId() {
        return versionId;
    }
}
