package fr.kainovaii.porfolio.app.domain.message;

import fr.kainovaii.obsidian.database.Migration;

public class CreateMessageTable extends Migration
{
    @Override
    public void up()
    {
        createTable("messages", table -> {
            table.id();
            table.string("name").notNull();
            table.string("email").notNull();
            table.text("message").notNull();
            table.timestamps();
        });
    }

    @Override
    public void down() {
        dropTable("messages");
    }
}
