package utils;

import model.User;

public class Session {
    private static User currentUser;

    public static void setUser(User u) {
        currentUser = u;
    }

    public static User getUser() {
        return currentUser;
    }

    public static void clear() {
        currentUser = null;
    }
}
