package com.cookandroid.medication_helper;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class UserRegisterRequest extends StringRequest {
    final static private String URL = "http://medichelper.dothome.co.kr/register3.php";
    private Map<String, String> parameters;

    public UserRegisterRequest(String userID, String userPassword, String userName, String userBirth,
                               String userGender, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);
        parameters = new HashMap<>();
        parameters.put("userID", userID);
        parameters.put("userPassword", userPassword);
        parameters.put("userName", userName);
        parameters.put("userBirth", userBirth);
        parameters.put("userGender", userGender);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return parameters;
    }
}
