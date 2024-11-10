package com.example.prepics.models;

import java.util.HashMap;
import java.util.Map;

public class ResponseProperties {

    public static Map<String, Object> createResponse(int status, String message, Object payload) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", status);
        response.put("message", message);
        response.put("payload", payload);
        return response;
    }
}
