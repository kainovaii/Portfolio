package fr.kainovaii.dashbot.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import javax.xml.crypto.Data;
import java.awt.*;
import java.util.Date;

public class InfoCommand implements Command {

    @Override
    public String getName() {
        return "info";
    }

    @Override
    public SlashCommandData getCommandData()
    {
        return Commands.slash(getName(), "Bot information");
    }

    @Override
    public void execute(SlashCommandInteractionEvent event)
    {
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("Information")
                .addField("Version:", "1.0.0", false)
                .addField("Author:", "KainoVaii", false)
                .setColor(Color.MAGENTA);

        event.replyEmbeds(embed.build()).queue();
    }
}