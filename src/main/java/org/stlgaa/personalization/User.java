package org.stlgaa.personalization;

import jakarta.mail.internet.InternetAddress;
import jakarta.persistence.*;
import org.apache.commons.lang3.StringUtils;
import org.stlgaa.SLGEntity;
import org.stlgaa.util.conversion.EmailAttributeConverter;

import java.time.LocalDate;
import java.util.Locale;
import java.util.Set;

@Entity
@Table(name = "Principal")
@NamedQueries(
    @NamedQuery(name = User.GET_BY_EMAIL, query = "SELECT u FROM User u WHERE u.emailAddress = :emailAddress")
)
public class User extends SLGEntity {

    public static final String GET_BY_EMAIL = "User.getByEmail";

    @Basic
    private String firstName;
    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Basic
    private String lastName;
    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Basic
    @Convert(converter = EmailAttributeConverter.class)
    private InternetAddress emailAddress;
    public InternetAddress getEmailAddress() {
        return emailAddress;
    }
    public void setEmailAddress(InternetAddress emailAddress) {
        this.emailAddress = emailAddress;
    }

    @Basic
    private String phoneNumber;
    public String getPhoneNumber() {
        return phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = StringUtils.getDigits(phoneNumber);
    }

    @Basic
    private LocalDate birthday;
    public LocalDate getBirthday() {
        return birthday;
    }
    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    @Basic
    private Locale preferredLocale;
    public Locale getPreferredLocale() {
        return preferredLocale;
    }
    public void setPreferredLocale(Locale preferredLocale) {
        this.preferredLocale = preferredLocale;
    }

    @Basic
    private boolean verified;
    public boolean isVerified() {
        return verified;
    }
    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    private Set<Profile> profiles;
    public Set<Profile> getProfiles() {
        return profiles;
    }
    public void setProfiles(Set<Profile> profiles) {
        this.profiles = profiles;
    }
}
