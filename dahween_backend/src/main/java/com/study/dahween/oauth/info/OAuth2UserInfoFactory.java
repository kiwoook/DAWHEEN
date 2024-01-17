package com.study.dahween.oauth.info;


import com.study.dahween.oauth.entity.ProviderType;
import com.study.dahween.oauth.info.impl.KakaoOAuth2UserInfo;
import com.study.dahween.oauth.info.impl.NaverOAuth2UserInfo;

import java.util.Map;

public class OAuth2UserInfoFactory {
    public static OAuth2UserInfo getOAuth2UserInfo(ProviderType providerType, Map<String, Object> attributes) {
        return switch (providerType) {
            case NAVER -> new NaverOAuth2UserInfo(attributes);
            case KAKAO -> new KakaoOAuth2UserInfo(attributes);
        };
    }
}