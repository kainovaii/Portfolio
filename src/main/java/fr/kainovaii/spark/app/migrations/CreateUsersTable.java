package fr.kainovaii.spark.app.migrations;

import fr.kainovaii.core.database.Migration;

public class CreateUsersTable extends Migration
{
    @Override
    public void up()
    {
        createTable("users", table -> {
            table.id();
            table.string("username").notNull().unique();
            table.string("email").notNull().unique();
            table.string("password").notNull();
            table.string("role").notNull();
        });
    }

    @Override
    public void down() {
        dropTable("users");
    }
}