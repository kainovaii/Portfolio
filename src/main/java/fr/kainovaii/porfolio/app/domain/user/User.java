package fr.kainovaii.porfolio.app.domain.user;

import fr.kainovaii.porfolio.app.security.AppUserDetails;
import org.javalite.activejdbc.Model;

import java.sql.Date;

public class User extends Model implements AppUserDetails
{
    // Getters
    public String getUsername() {
        return getString("username");
    }

    public String getEmail() {
        return getString("email");
    }

    public String getPassword() {
        return getString("password");
    }

    public String getRole() {
        return getString("role");
    }

    public Date createdAt() {
        Long timestamp = getLong("created_at");
        return timestamp != null ? new Date(timestamp) : null;
    }

    // Setters
    public void setUsername(String username) {
        set("username", username);
    }

    public void setEmail(String email) {
        set("email", email);
    }

    public void setPassword(String password) {
        set("password", password);
    }

    public void setRole(String role) {
        set("role", role);
    }
}
