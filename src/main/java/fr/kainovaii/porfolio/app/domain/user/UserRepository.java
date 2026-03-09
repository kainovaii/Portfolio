package fr.kainovaii.porfolio.app.domain.user;

import fr.kainovaii.obsidian.di.annotations.Repository;
import org.javalite.activejdbc.LazyList;

@Repository
public class UserRepository
{
    public LazyList<User> findAll() {
        return User.findAll();
    }

    public User findByUsername(String username) {
        return User.findFirst("username = ?", username);
    }

    public User findById(int id) {
        return User.findById(id);
    }

    public static boolean userExist(String username) {
        return User.findFirst("username = ?", username) != null;
    }

    public boolean delete(String username)
    {
        User user = findByUsername(username);
        return user.delete();
    }

    public Boolean create(String username, String email, String password, String role)
    {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password);
        user.setRole(role);
        return user.saveIt();
    }

    public Boolean update(String usernameCurrent, String username, String email, String password, String role)
    {
        User user = findByUsername(usernameCurrent);
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password);
        user.setRole(role);
        return user.saveIt();
    }
}