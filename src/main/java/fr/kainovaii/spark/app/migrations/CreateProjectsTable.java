package fr.kainovaii.spark.app.migrations;

import fr.kainovaii.core.database.Migration;

public class CreateProjectsTable extends Migration
{
    @Override
    public void up()
    {
        createTable("projects", table -> {
            table.id();
            table.string("icon").notNull();
            table.string("title").notNull();
            table.text("description").notNull();
            table.string("link").notNull();
            table.string("tools").notNull();
        });
    }

    @Override
    public void down() {
        dropTable("projects");
    }
}
