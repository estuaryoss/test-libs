package com.github.estuaryoss.libs.fluentdlogger.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.estuaryoss.libs.fluentdlogger.constants.EnvConstants;
import com.github.estuaryoss.libs.fluentdlogger.env.Environment;
import com.github.estuaryoss.libs.fluentdlogger.message.EnrichedMessage;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.fluentd.logger.FluentLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;


public class FluentdService {
    private static final Logger log = LoggerFactory.getLogger(FluentdService.class);
    private final Environment env = new Environment();
    private FluentLogger fluentLogger;
    private String fluentIpPort;

    public FluentdService(String tag, String fluentdIpPort) {
        this.fluentIpPort = fluentdIpPort;
        this.setFluentdLogger(tag);
    }

    /**
     * Sends the log to the FluentD service. If FluentD is not enabled then it will only print the message back to the user.
     *
     * @param tag With which tag to log the message
     * @param msg The message to be sent into Fluentd
     * @return A response which can be printed in console to be sure that it was sent to the FluentD service.
     * For success, the return value should be: 'emit: true'.
     */
    public LinkedHashMap emit(String tag, LinkedHashMap<String, Object> msg) {
        LinkedHashMap map = new LinkedHashMap();
        EnrichedMessage message = this.enrichLog("INFO", msg);

        map.put(FinalConsoleMessage.EMIT.getField(), this.emit(tag, message));
        map.put(FinalConsoleMessage.MESSAGE.getField(), message);

        try {
            log.info(new ObjectMapper().writeValueAsString(map));
        } catch (JsonProcessingException e) {
            log.info(ExceptionUtils.getStackTrace(e));
        }

        return map;
    }

    private EnrichedMessage enrichLog(String levelCode, LinkedHashMap<String, Object> parentMessage) {
        DateTimeFormatter customFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS");
        EnrichedMessage enrichedMessage = new EnrichedMessage();
        enrichedMessage.setMsg(parentMessage);
        enrichedMessage.setLevelCode(levelCode);
        enrichedMessage.setUname(new String[]{System.getProperty("os.name")});
        enrichedMessage.setJava(System.getProperty("java.vm.vendor") + " " + System.getProperty("java.runtime.version"));
        enrichedMessage.setTimestamp(LocalDateTime.now().format(customFormatter));

        return enrichedMessage;
    }

    private String emit(String tag, EnrichedMessage message) {
        ObjectMapper objectMapper = new ObjectMapper();

        if (env.getEnvAndVirtualEnv().get(EnvConstants.FLUENTD_IP_PORT) == null) {
            return String.format("Fluentd logging not enabled", EnvConstants.FLUENTD_IP_PORT);
        }

        return String.valueOf(this.fluentLogger.log(tag, objectMapper.convertValue(message, LinkedHashMap.class)));
    }

    private enum FinalConsoleMessage {
        EMIT("emit"),
        MESSAGE("message");

        private final String field;

        FinalConsoleMessage(String field) {
            this.field = field;
        }

        public String getField() {
            return this.field;
        }
    }

    private void setFluentdLogger(String tag) {
        if (fluentIpPort != null)
            this.fluentLogger = FluentLogger.getLogger(tag,
                    env.getEnvAndVirtualEnv().get(EnvConstants.FLUENTD_IP_PORT).split(":")[0],
                    Integer.parseInt(env.getEnvAndVirtualEnv().get(EnvConstants.FLUENTD_IP_PORT).split(":")[1]));
    }
}
