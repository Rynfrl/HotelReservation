package service;

import dao.UserDAO;
import model.User;

public class LoginService {
    private UserDAO userDAO = new UserDAO();

    public User authenticate(String username, String password) {
        if (username == null || username.isEmpty()) return null;
        if (password == null || password.isEmpty()) return null;
        User u = userDAO.findByUsername(username);
        if (u != null) {
            String dbPass = u.getPassword();
            if (dbPass.length() < 64) {
                // Migrate plain text to SHA-256
                if (dbPass.equals(password)) {
                    u.setPassword(utils.PasswordUtil.hashSHA256(password));
                    userDAO.update(u);
                    return u;
                }
            } else {
                // Verify against SHA-256 hash
                if (dbPass.equals(utils.PasswordUtil.hashSHA256(password))) {
                    return u;
                }
            }
        }
        return null;
    }
}
