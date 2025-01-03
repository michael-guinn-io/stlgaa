package org.stlgaa.personalization;

import jakarta.persistence.*;
import org.stlgaa.Position;
import org.stlgaa.SLGEntity;

@Entity
public class PreferredPosition extends SLGEntity {

    @ManyToOne
    private Profile profile;
    public Profile getProfile() {
        return profile;
    }
    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    @Basic
    @Enumerated(EnumType.STRING)
    private Position position;
    public Position getPosition() {
        return position;
    }
    public void setPosition(Position position) {
        this.position = position;
    }

    @Basic
    private boolean primary;
    public boolean isPrimary() {
        return primary;
    }
    public void setPrimary(boolean primary) {
        this.primary = primary;
    }
}
