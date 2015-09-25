/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.ows.rtcjs.manager;

import java.util.ArrayList;
import java.util.List;
import vn.ows.rtcjs.model.User;

/**
 *
 * @author hoangnm
 */
public class UserManager {
    private final List<User> users;
    private static final UserManager instance = null;
    
    public static UserManager getInstance() {
        if (instance != null) return instance;
        return new UserManager();
    }

    UserManager() {
        this.users = new ArrayList<>();
    }
    
    public void addUser(User user) {
        this.users.add(user);
    }
    
    public void removeUser(User user) {
        this.users.remove(user);
    }
    
    public List<User> getUsersOnline() {
        return this.users;
    }
}
