package com.github.estuaryoss.libs.fluentdlogger.message;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.estuaryoss.libs.fluentdlogger.About;

import java.util.Map;

public class EnrichedMessage {
    public String name = About.NAME;
    public String version = About.VERSION;
    public String[] uname;
    public String java;

    @JsonProperty("level_code")
    public String levelCode;

    public Map<String, Object> msg;
    public String timestamp;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String[] getUname() {
        return uname;
    }

    public void setUname(String[] uname) {
        this.uname = uname;
    }

    public String getJava() {
        return java;
    }

    public void setJava(String java) {
        this.java = java;
    }

    public String getLevelCode() {
        return levelCode;
    }

    public void setLevelCode(String levelCode) {
        this.levelCode = levelCode;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public Map<String, Object> getMsg() {
        return msg;
    }

    public void setMsg(Map<String, Object> msg) {
        this.msg = msg;
    }
}
