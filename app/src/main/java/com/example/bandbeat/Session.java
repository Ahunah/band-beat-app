package com.example.bandbeat;

import android.content.Context;
import android.content.SharedPreferences;

/** Simple session store for role + userId */
public class Session {
    private static final String PREF = "bandbeat_session";
    private static final String KEY_ROLE = "role";
    private static final String KEY_USER_ID = "user_id";
    private final SharedPreferences sp;

    public Session(Context ctx) { sp = ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE); }

    public void login(String role, int userId) {
        sp.edit().putString(KEY_ROLE, role).putInt(KEY_USER_ID, userId).apply();
    }
    public void logout() { sp.edit().clear().apply(); }
    public String getRole() { return sp.getString(KEY_ROLE, null); }
    public int getUserId() { return sp.getInt(KEY_USER_ID, -1); }
    public boolean isAdmin() { return "ADMIN".equals(getRole()); }
    public boolean isLoggedIn() { return getRole() != null && getUserId() > 0; }
}