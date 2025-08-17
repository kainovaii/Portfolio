package fr.kainovaii.dashbot.commands;

import fr.kainovaii.dashbot.webhook.UptimeKumaWebhook;
import fr.kainovaii.dashbot.models.ServiceStatus;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.awt.*;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class StatusCommand implements Command
{
    @Override
    public String getName() {
        return "status";
    }

    @Override
    public SlashCommandData getCommandData()
    {
        return Commands.slash(getName(), "Affiche le statut d'un service").addOption(net.dv8tion.jda.api.interactions.commands.OptionType.STRING,"service","Nom du service",true);
    }

    @Override
    public void execute(SlashCommandInteractionEvent event)
    {
        String serviceName = event.getOption("service").getAsString();
        ServiceStatus status = UptimeKumaWebhook.statuses.get(serviceName);

        ZonedDateTime timestamp;
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.of("Europe/Paris"));
            timestamp = ZonedDateTime.parse(status.getTime(), formatter);
        } catch (Exception e) {
            timestamp = ZonedDateTime.now(ZoneId.of("Europe/Paris"));
        }

        if (status != null) {
            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle(status.getTitle())
                    .setThumbnail("https://i.ibb.co/5XMvf6qn/Frame-2.png")
                    .addField("Service:", status.getName(), true)
                    .addField("Statut:", status.getStatus(), true)
                    .addField("\u200B", "\u200B", true)
                    .addField("Ping:", status.getPing() + "ms", true)
                    .setTimestamp(timestamp)
                    .setColor(status.getColor())
                    .setFooter("UptimeKuma Webhook");
    
            event.replyEmbeds(embed.build()).queue();
        } else {
            event.reply("Service inconnu ou aucune info disponible").setEphemeral(true).queue();
        }
    }
}
