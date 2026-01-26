package fr.kainovaii.spark.app.migrations;

import fr.kainovaii.core.database.Migration;

public class CreateSkillsTable extends Migration
{
    @Override
    public void up()
    {
        createTable("skills", table -> {
            table.id();
            table.string("icon").notNull();
            table.string("title").notNull();
            table.string("tools").notNull();
        });
    }

    @Override
    public void down() {
        dropTable("skills");
    }
}