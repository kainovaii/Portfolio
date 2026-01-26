package fr.kainovaii.spark;

import fr.kainovaii.core.database.DB;
import fr.kainovaii.core.database.MigrationManager;
import fr.kainovaii.spark.app.migrations.CreateSettingsTable;
import fr.kainovaii.spark.app.models.Setting;
import fr.kainovaii.spark.app.repository.SettingRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DatabaseTest
{
    private static final Logger logger = Logger.getLogger("DBTest");

    @BeforeAll
    public static void setup()
    {
        File dbFile = new File("Spark/test_database.db");
        if (dbFile.exists()) {
            dbFile.delete();
        }

        DB.initSQLite("Spark/test_database.db", logger);

        MigrationManager migrations = new MigrationManager(DB.getInstance(), logger);
        migrations.add(new CreateSettingsTable());
        migrations.migrate();
    }

    @Test
    public void testDBConnection()
    {
        SettingRepository settingRepository = new SettingRepository();

        Boolean hasSettings = DB.withConnection(() -> {
            List<Setting> settings = settingRepository.getAll().stream().toList();
            return !settings.isEmpty();
        });

        assertFalse(hasSettings == null);
    }

    @Test
    public void testDBTransaction()
    {
        SettingRepository settingRepository = new SettingRepository();

        DB.withTransaction(() -> {
            List<Setting> settings = settingRepository.getAll().stream().toList();
            assertTrue(settings != null, "Settings should exist in transaction");
            return null;
        });
    }
}