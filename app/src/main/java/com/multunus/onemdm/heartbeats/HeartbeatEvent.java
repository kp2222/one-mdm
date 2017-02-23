package com.multunus.onemdm.heartbeats;

import org.json.JSONException;
import org.json.JSONObject;


public class HeartbeatEvent {
    private String accesstoken;
    public HeartbeatEvent(String accessToken){
        this.accesstoken = accessToken;
    }

    public JSONObject getEvent() throws JSONException {
        JSONObject event = new JSONObject();
        event.put("access_token", this.accesstoken);
        JSONObject payload = new JSONObject();
        event.put("payload",payload);
        return event;
    }
}
