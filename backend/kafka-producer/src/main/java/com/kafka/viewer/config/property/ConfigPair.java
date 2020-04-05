package com.kafka.viewer.config.property;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConfigPair {

    private String envName;
    private String defaultValue;

    private static final Pattern pattern;

    static {
        pattern = Pattern.compile("^\\$\\{(\\w*)\\:?(\\S*)\\}$");
    }

    public String getEnvName() {
        return envName;
    }

    public void setEnvName(String envName) {
        this.envName = envName;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public static ConfigPair parsePair(String value) {
        Matcher matcher = pattern.matcher(value);

        if (!matcher.find()) {
            return null;
        }

        if (matcher.groupCount() < 2) {
            return null;
        }

        ConfigPair configPair = new ConfigPair();

        String envGroup = matcher.group(1);
        if (!StringUtils.isEmpty(envGroup)) {
            configPair.setEnvName(envGroup);
        }


        String defaultValGroup = matcher.group(2);
        if (!StringUtils.isEmpty(defaultValGroup)) {
            configPair.setDefaultValue(defaultValGroup);
        }

        return configPair;
    }

}
