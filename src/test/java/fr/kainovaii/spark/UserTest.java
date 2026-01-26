package fr.kainovaii.spark;

import fr.kainovaii.core.database.DB;
import fr.kainovaii.spark.app.repository.UserRepository;
import org.junit.jupiter.api.*;

import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserTest
{
    private static final Logger logger = Logger.getLogger("UserTest");
    private final UserRepository userRepository = new UserRepository();

    @BeforeAll
    public static void setup()
    {
        DB.initSQLite("Spark/data.db", logger);
    }

    @Test
    @Order(1)
    public void testCreateUser()
    {
        Boolean created = DB.withConnection(() ->
                userRepository.create("johndoe", "john@doe.com", "password", "DEFAULT")
        );

        assertTrue(created, "User should be created successfully");
    }

    @Test
    @Order(3)
    public void testDeleteUser()
    {
        Integer deleted = DB.withConnection(() ->
                userRepository.deleteByUsername("johndoe")
        );
        assertEquals(1, deleted, "One user should be deleted");
    }

}