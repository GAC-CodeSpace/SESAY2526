package io.SesProject.model;

/**
 * Rappresenta solo il PROFILO dell'utente (Identità).
 */
public class User {

    private String username;

    public User(String username) {
        this.username = username;
    }

    public String getUsername() { return username; }
}
