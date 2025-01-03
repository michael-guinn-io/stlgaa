package org.stlgaa.personalization;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import org.stlgaa.SLGForm;

@Named
@ViewScoped
public class RegistrationForm extends SLGForm {

    @EJB
    private User userService;

    private User user;
    private String password;

    @PostConstruct
    public void init() {
        user = new User();
    }

    public String register() {



        return "index";
    }

    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
}
