package com.study.dawheen.auth.info;

import com.study.dawheen.user.entity.RoleType;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

@Getter
public class CustomOAuth2User extends DefaultOAuth2User {

    private String email;
    private RoleType role;

    /**
     * Constructs a {@code DefaultOAuth2User} using the provided parameters.
     *
     * @param authorities      the authorities granted to the user
     * @param attributes       the attributes about the user
     * @param nameAttributeKey the key used to access the user's &quot;name&quot; from
     *                         {@link #getAttributes()}
     */
    public CustomOAuth2User(Collection<? extends GrantedAuthority> authorities,
                            Map<String, Object> attributes, String nameAttributeKey,
                            String email, RoleType roleType) {
        super(authorities, attributes, nameAttributeKey);
        this.email = email;
        this.role = roleType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        CustomOAuth2User that = (CustomOAuth2User) o;
        return Objects.equals(getEmail(), that.getEmail()) && getRole() == that.getRole();
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getEmail(), getRole());
    }
}