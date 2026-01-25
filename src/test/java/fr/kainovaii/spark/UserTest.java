package fr.kainovaii.spark;

import fr.kainovaii.core.database.DB;
import fr.kainovaii.spark.app.repository.UserRepository;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserTest
{
    private final UserRepository userRepository = new UserRepository();

    @Test
    @Order(1)
    public void testCreateUser()
    {
        Boolean user = userRepository.create("johndoe", "johh@doe.com", "password", "DEFAULT");
        assertEquals(true, user);
    }

    @Test
    @Order(3)
    public void testDeleteUser()
    {
        int user = DB.withConnection(() -> userRepository.deleteByUsername("johndoe"));
        assertEquals(1, user);
    }
}
