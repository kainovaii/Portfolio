package fr.kainovaii.porfolio.app.domain.message;

import org.javalite.activejdbc.Model;

import java.sql.Date;

public class Message extends Model
{
    // Getters
    public String getName() {
        return getString("name");
    }

    public String getEmail() {
        return getString("email");
    }

    public String getMessage() {
        return getString("message");
    }

    public Date createdAt() {
        Long timestamp = getLong("created_at");
        return timestamp != null ? new Date(timestamp) : null;
    }

    // Setters
    public void setName(String name)
    {
        set("name", name);
    }

    public void setEmail(String email)
    {
        set("email", email);
    }

    public void setMessage(String message)
    {
        set("message", message);
    }
}