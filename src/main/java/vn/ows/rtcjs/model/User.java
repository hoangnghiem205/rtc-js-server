/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.ows.rtcjs.model;

import java.util.Random;

/**
 *
 * @author hoangnm
 */
public class User {
    private String id;
    private String username;
    private String displayName;
    private String email;

    public User() {
    }

    public User(String id, String username) {
        this.id = id;
        this.username = username;
    }
    
    

    public User(String username) {
        this.id = "_" + new Random().nextLong();
        this.username = username;
        this.displayName = username;
        this.email = username + "@mail.com";
    }

    public User(String id, String username, String displayName, String email) {
        this.id = id;
        this.username = username;
        this.displayName = displayName;
        this.email = email;
    }

    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public boolean equals(Object obj) {
        return this.id.equals(((User) obj).getId());
    }
    
    
}
