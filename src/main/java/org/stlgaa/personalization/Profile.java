package org.stlgaa.personalization;

import jakarta.persistence.*;
import org.stlgaa.Laterality;
import org.stlgaa.SLGEntity;
import org.stlgaa.Sport;

import java.util.Set;

@Entity
public class Profile extends SLGEntity {

    @ManyToOne
    private User user;
    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }

    @Basic
    @Enumerated(EnumType.STRING)
    private Sport sport;
    public Sport getSport() {
        return sport;
    }
    public void setSport(Sport sport) {
        this.sport = sport;
    }

    @OneToMany(mappedBy = "profile", fetch = FetchType.EAGER)
    private Set<PreferredPosition> preferredPositions;
    public Set<PreferredPosition> getPreferredPositions() {
        return preferredPositions;
    }
    public void setPreferredPositions(Set<PreferredPosition> preferredPositions) {
        this.preferredPositions = preferredPositions;
    }

    @Basic
    @Enumerated(EnumType.STRING)
    private Laterality laterality;
    public Laterality getLaterality() {
        return laterality;
    }
    public void setLaterality(Laterality laterality) {
        this.laterality = laterality;
    }
}
