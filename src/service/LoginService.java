package service;

import dao.UserDAO;
import model.User;

public class LoginService {
    private UserDAO userDAO = new UserDAO();

    public User authenticate(String username, String password) {
        if (username == null || username.isEmpty()) return null;
        if (password == null || password.isEmpty()) return null;
        User u = userDAO.findByUsername(username);
        if (u != null && u.getPassword().equals(password)) {
            return u;
        }
        return null;
    }
}
