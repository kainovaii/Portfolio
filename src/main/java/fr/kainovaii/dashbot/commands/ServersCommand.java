package fr.kainovaii.dashbot.commands;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

public class ServersCommand implements Command {

    @Override
    public String getName() {
        return "servers";
    }

    @Override
    public net.dv8tion.jda.api.interactions.commands.build.SlashCommandData getCommandData() {
        return Commands.slash(getName(), "List des serveurs");
    }

    @Override
    public void execute(SlashCommandInteractionEvent event)
    {
        StringBuilder response = new StringBuilder("📋 Liste des serveurs où je suis :\n");

        for (Guild guild : event.getJDA().getGuilds()) {
            response.append("• ").append(guild.getName())
                    .append(" (ID: ").append(guild.getId()).append(")\n");
        }

        event.reply(response.toString()).setEphemeral(true).queue();
    }
}