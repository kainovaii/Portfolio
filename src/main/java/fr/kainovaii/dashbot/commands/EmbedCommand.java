package fr.kainovaii.dashbot.commands;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.awt.Color;

public class EmbedCommand implements Command
{
    @Override
    public String getName()
    {
        return "embed";
    }

    @Override
    public SlashCommandData getCommandData()
    {
        return Commands.slash(getName(), "Dit bonjour à l'utilisateur");
    }

    @Override
    public void execute(SlashCommandInteractionEvent event)
    {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("Dedit haec vel adfore quae.");
        embed.setThumbnail("https://avatars.githubusercontent.com/u/52110617?v=4");
        embed.setDescription("Manibus usque ad rogati instituta Spoletium inminuto artuum nobilium vigore sunt ad manibus usque inpigre cum aurum vigore observentur ubi.");
        embed.setColor(Color.MAGENTA);
        embed.setFooter("Footer text");

        event.replyEmbeds(embed.build())
            .setActionRow(
                    Button.primary("myButton", "Test")
            )
        .queue();
    }
}