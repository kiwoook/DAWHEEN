package com.study.dahween.config;

import com.study.dahween.organization.entity.Organization;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;

import java.io.Serializable;


public class ClientPermissionExpression implements PermissionEvaluator {

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        if (targetDomainObject instanceof Organization org) {

            if (("DELETE".equals(permission) || "PUT".equals(permission)) && org.getUsers().stream().anyMatch(user -> user.getEmail().equals(authentication.getName()))) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        return false;
    }
}
