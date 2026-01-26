package fr.kainovaii.spark.app.migrations;

import fr.kainovaii.core.database.Migration;

public class CreateSettingsTable extends Migration
{
    @Override
    public void up()
    {
        createTable("settings", table -> {
            table.id();
            table.integer("admin_exist").notNull().unique();
        });
    }

    @Override
    public void down() {
        dropTable("settings");
    }
}