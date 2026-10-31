package mk.mdt.budgetapi.entity;

import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    ROLE_USER,
    ROLE_ADMINISTRATOR;

    @Override
    public String getAuthority() {
        return name();
    }
}