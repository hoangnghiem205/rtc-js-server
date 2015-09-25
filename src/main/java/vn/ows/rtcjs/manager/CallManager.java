/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.ows.rtcjs.manager;

import java.util.ArrayList;
import java.util.List;
import vn.ows.rtcjs.model.Call;
import vn.ows.rtcjs.model.User;

/**
 *
 * @author hoangnm
 */
public class CallManager {
 
    private final List<Call> calls;
    private static final CallManager instance = null;

    public static CallManager getInstance() {
        if (instance == null) 
            return new CallManager();
        else 
            return instance;
    }
    
    CallManager() {
        calls = new ArrayList<>();
    }
    
    public void addCall(Call call) {
        this.calls.add(call);
    }
    
    public void removeCall(Call call) {
        this.calls.remove(call);
    }
    
    public Call getCall(String id) {
        for (Call call : calls) {
            if (call.getId().equals(id)) 
                return call;
        }
        return null;
    }
    
    public boolean isBusy(User user) {
        for (Call call : calls) {
            if (checkUserInCall(user, call)) 
                return true;
        }
        return false;
    }
    
    private boolean checkUserInCall(User user, Call call) {
        User caller = (User) call.getCaller().getUserProperties().get("user");
        User receiver = (User) call.getReceiver().getUserProperties().get("user");
        if (caller.equals(user) || receiver.equals(user))
            return true;
        return false;
    }
}
