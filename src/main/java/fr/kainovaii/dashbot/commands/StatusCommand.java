package fr.kainovaii.dashbot.commands;

import fr.kainovaii.dashbot.webhook.UptimeKumaWebhook;
import fr.kainovaii.dashbot.models.ServiceStatus;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.awt.*;

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

        if (status != null) {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(Color.MAGENTA);
            embed.setTitle("Statut du service " + serviceName);
            embed.addField("Status", status.getStatusMessage(), true);
            embed.addField("URL", status.getServiceUrl(), true);
            embed.addField("Ping", status.getPing() + " ms", true);
            embed.addField("Heure", status.getTime(), true);

            event.replyEmbeds(embed.build()).queue();
        } else {
            event.reply("Service inconnu ou aucune info disponible").setEphemeral(true).queue();
        }
    }
}
