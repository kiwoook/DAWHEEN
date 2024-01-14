package com.study.dahween.oauth.info;

import java.util.Map;

public abstract class OAuth2UserInfo {

    protected static final String RESPONSE = "RESPONSE";
    protected Map<String, Object> attributes;

    protected OAuth2UserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public abstract String getId();

    public abstract String getName();

    public abstract String getEmail();


}
