package org.stlgaa.security.authentication;

import jakarta.persistence.*;
import org.stlgaa.SLGEntity;
import org.stlgaa.personalization.User;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class Credentials extends SLGEntity {

    @ManyToOne(targetEntity = User.class)
    private User user;
    public User getUser() {
        return user;
    }
    public void setUser(User profile) {
        this.user = profile;
    }

    @Basic
    private boolean enabled;
    public boolean isEnabled() {
        return enabled;
    }
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
