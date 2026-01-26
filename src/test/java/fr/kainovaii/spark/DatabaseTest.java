package fr.kainovaii.spark;

import fr.kainovaii.core.database.DB;
import fr.kainovaii.spark.app.models.Setting;
import fr.kainovaii.spark.app.repository.SettingRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DatabaseTest
{
    private static final Logger logger = Logger.getLogger("DatabaseTest");
    private final SettingRepository settingRepository = new SettingRepository();

    @BeforeAll
    public static void setup()
    {
        DB.initSQLite("Spark/data.db", logger);
    }

    @Test
    public void testDatabaseConnection()
    {
        Boolean hasSettings = DB.withConnection(() -> {
            List<Setting> settings = settingRepository.getAll().stream().toList();
            return !settings.isEmpty();
        });

        assertFalse(hasSettings == null);
        assertTrue(hasSettings, "Database should contain settings");
    }

    @Test
    public void testDatabaseTransaction()
    {
        DB.withTransaction(() -> {
            List<Setting> settings = settingRepository.getAll().stream().toList();
            assertTrue(!settings.isEmpty(), "Settings should exist in transaction");
            return null;
        });
    }
}