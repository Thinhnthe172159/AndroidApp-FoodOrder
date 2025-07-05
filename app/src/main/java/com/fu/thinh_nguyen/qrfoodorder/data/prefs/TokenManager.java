package com.fu.thinh_nguyen.qrfoodorder.data.prefs;

import android.content.Context;
import android.content.SharedPreferences;

public class TokenManager {
    private static final String KEY = "jwt_token";
    private SharedPreferences prefs;

    public TokenManager(Context context) {
        prefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE);
    }

    public void save(String token) {
        prefs.edit().putString(KEY, token).apply();
    }

    public String get() {
        return prefs.getString(KEY, null);
    }

    public void clear() {
        prefs.edit().remove(KEY).apply();
    }
}
