package com.github.estuaryoss.libs.fluentdlogger.env;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

public class Environment {
    private static final Logger log = LoggerFactory.getLogger(Environment.class);
    private static final String EXT_ENV_VAR_PATH = "environment.properties";
    private final ImmutableMap<String, String> environment = ImmutableMap.copyOf(System.getenv());
    private final Map<String, String> virtualEnvironment = new LinkedHashMap<>();

    private final int VIRTUAL_ENVIRONMENT_MAX_SIZE = 100;

    public Environment() {
        this.setExtraEnvVarsFromFile();
    }

    private void setExtraEnvVarsFromFile() {

        try (InputStream fileInputStream = new FileInputStream(Paths.get(".", EXT_ENV_VAR_PATH).toFile())) {
            Properties properties = new Properties();
            properties.load(fileInputStream);
            virtualEnvironment.putAll(properties.entrySet()
                    .stream()
                    .filter(elem -> !environment.containsKey(elem.getKey()))
                    .collect(Collectors.toMap(elem -> elem.getKey().toString(),
                            elem -> elem.getValue().toString())));
        } catch (Exception e) {
            log.debug(ExceptionUtils.getStackTrace(e));
        }

        log.debug("External env vars read from file '" + EXT_ENV_VAR_PATH + "' are: " + new JSONObject(virtualEnvironment).toString());
    }

    public void setExternalEnvVar(String key, String value) {
        if (!environment.containsKey(key) && virtualEnvironment.size() <= VIRTUAL_ENVIRONMENT_MAX_SIZE)
            virtualEnvironment.put(key, value);
    }

    public Map<String, String> setExternalEnvVars(Map<String, String> envVars) {
        Map<String, String> addedEnvVars = new LinkedHashMap<>();

        for (Map.Entry<String, String> entry : envVars.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (environment.containsKey(key)) continue;

            if (virtualEnvironment.containsKey(key)) {
                virtualEnvironment.put(key, value); //can override
                addedEnvVars.put(key, value);
                continue;
            }
            if (virtualEnvironment.size() <= VIRTUAL_ENVIRONMENT_MAX_SIZE) {
                virtualEnvironment.put(key, value);
                addedEnvVars.put(key, value);
            }

        }
        return addedEnvVars;
    }

    /**
     * Gets the immutable environment variables from the System
     *
     * @return Map containing initial immutable env vars plus virtual env vars set by the user
     */
    public Map<String, String> getEnvAndVirtualEnv() {
        Map<String, String> systemAndExternalEnvVars = new LinkedHashMap<>();
        systemAndExternalEnvVars.putAll(environment);

        virtualEnvironment.forEach((key, value) -> {
            if (!systemAndExternalEnvVars.containsKey(key)) systemAndExternalEnvVars.put(key, value);
        });

        return systemAndExternalEnvVars;
    }

    /**
     * Gets the immutable environment variables from the System
     *
     * @return Map containing initial immutable env vars
     */
    public Map<String, String> getEnv() {
        return environment;
    }

    /**
     * Gets the virtual environment variables
     *
     * @return Map containing mutable env vars set by the user
     */
    public Map<String, String> getVirtualEnv() {
        return virtualEnvironment;
    }
}
