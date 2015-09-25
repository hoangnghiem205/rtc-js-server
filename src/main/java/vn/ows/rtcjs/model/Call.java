/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.ows.rtcjs.model;

import java.util.Random;
import javax.websocket.Session;

/**
 *
 * @author hoangnm
 */
public class Call {
    
    private String id;
    private Session caller;
    private Session receiver;

    public Call() {
    }
    public Call(Session caller, Session receiver) {
        this.id = "_" + new Random().nextLong();
        this.caller = caller;
        this.receiver = receiver;
    }
    

    public Call(String id, Session caller, Session receiver) {
        this.id = id;
        this.caller = caller;
        this.receiver = receiver;
    }

    public Session getCaller() {
        return caller;
    }

    public void setCaller(Session caller) {
        this.caller = caller;
    }

    public Session getReceiver() {
        return receiver;
    }

    public void setReceiver(Session receiver) {
        this.receiver = receiver;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    
    @Override
    public boolean equals(Object obj) {
        return this.id.equals(((Call)obj).getId());
    }    
}
