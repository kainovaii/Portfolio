package fr.kainovaii.dashbot.commands;

import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class PingCommand implements Command {

    @Override
    public String getName() {
        return "ping";
    }

    @Override
    public net.dv8tion.jda.api.interactions.commands.build.SlashCommandData getCommandData() {
        return Commands.slash(getName(), "Répond avec Pong!");
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.reply("Pong!").queue();
    }
}