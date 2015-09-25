/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.ows.rtcjs.server;

import java.io.StringReader;
import java.util.Date;
import javax.json.Json;
import javax.json.JsonObject;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import vn.ows.rtcjs.manager.CallManager;
import vn.ows.rtcjs.manager.UserManager;
import vn.ows.rtcjs.model.Call;
import vn.ows.rtcjs.model.User;

/**
 *
 * @author hoangnm
 */
@ServerEndpoint("/call/{name}")
public class CallServer {

    private static final UserManager userManager = UserManager.getInstance();
    private static final CallManager callManager = CallManager.getInstance();

    @OnOpen
    public void onOpen(Session session, @PathParam("name") String name) {
        System.out.println("1.8");
        System.out.println(session.getId() + " with name " + name + " connected.");
        User u = new User(session.getId(), name);
        userManager.addUser(u);
        session.getUserProperties().put("user", u);
        System.out.println("User online: " + userManager.getUsersOnline().size());
    }

    @OnMessage
    public void onMessage(String message, Session session, @PathParam("name") String name) {
        System.out.println("From " + session.getId() + ": " + message);
        try {
            JsonObject o = Json.createReader(new StringReader(message)).readObject();
            String action = o.getString("signal");

            switch (action) {
                case "call": {
                    String to = o.getJsonObject("message").getString("to");
                    System.out.println("A request call to " + to);
                    for (Session s : session.getOpenSessions()) {
                        User user = (User) s.getUserProperties().get("user");
                        if (s.isOpen() && user.getUsername().equals(to)) {
                            
                            if (callManager.isBusy(user)) {
                                JsonObject jsonMessBody = Json.createObjectBuilder()
                                    .add("time", new Date().toString()).build();
                                JsonObject jsonMess = Json.createObjectBuilder()
                                    .add("action", "busy")
                                    .add("message", jsonMessBody)
                                    .build();
                                session.getBasicRemote().sendText(jsonMess.toString());
                            } else {
                                JsonObject jsonMessBody = Json.createObjectBuilder()
                                    .add("from", name).build();
                                JsonObject jsonMess = Json.createObjectBuilder()
                                    .add("action", "calling")
                                    .add("message", jsonMessBody)
                                    .build();
                                s.getBasicRemote().sendText(jsonMess.toString());
                            }
                            
                            
                        }
                    }
                    break;
                }
                case "accept_call": {
                    String to = o.getJsonObject("message").getString("to");
                    for (Session s : session.getOpenSessions()) {
                        User user = (User) s.getUserProperties().get("user");
                        if (s.isOpen() && user.getUsername().equals(to)) {
                            JsonObject jsonMessBody = Json.createObjectBuilder()
                                    .add("time", new Date().toString()).build();
                            JsonObject jsonMess = Json.createObjectBuilder()
                                    .add("action", "accepted")
                                    .add("message", jsonMessBody)
                                    .build();
                            s.getBasicRemote().sendText(jsonMess.toString());
                            Call call = new Call(s, session);
                            callManager.addCall(call);

                            JsonObject jsonCallBody = Json.createObjectBuilder()
                                    .add("call_id", call.getId())
                                    .add("time", new Date().toString()).build();
                            JsonObject jsonCall = Json.createObjectBuilder()
                                    .add("action", "in_call")
                                    .add("message", jsonCallBody)
                                    .build();

                            s.getBasicRemote().sendText(jsonCall.toString());
                            session.getBasicRemote().sendText(jsonCall.toString());

                        }
                    }
                    break;
                }
                case "reject_call": {
                    String to = o.getJsonObject("message").getString("to");
                    for (Session s : session.getOpenSessions()) {
                        User user = (User) s.getUserProperties().get("user");
                        if (s.isOpen() && user.getUsername().equals(to)) {
                            JsonObject jsonMessBody = Json.createObjectBuilder()
                                    .add("time", new Date().toString()).build();
                            JsonObject jsonMess = Json.createObjectBuilder()
                                    .add("action", "rejected")
                                    .add("message", jsonMessBody)
                                    .build();
                            s.getBasicRemote().sendText(jsonMess.toString());
                        }
                    }
                    break;
                }
                case "end_call": {
                    String callId = o.getJsonObject("message").getString("call_id");
                    Call call = callManager.getCall(callId);

                    JsonObject jsonMessBody = Json.createObjectBuilder()
                            .add("time", new Date().toString()).build();
                    JsonObject jsonMess = Json.createObjectBuilder()
                            .add("action", "ended")
                            .add("message", jsonMessBody)
                            .build();
                    
                    call.getCaller().getBasicRemote().sendText(jsonMess.toString());
                    call.getReceiver().getBasicRemote().sendText(jsonMess.toString());
                    callManager.removeCall(call);
                    break;
                }
                default: {
                    session.getBasicRemote().sendText(message);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @OnClose
    public void onClose(Session session, @PathParam("name") String name) {
        System.out.println(session.getId() + " closed.");
        userManager.removeUser(new User(session.getId(), name));
    }

}
