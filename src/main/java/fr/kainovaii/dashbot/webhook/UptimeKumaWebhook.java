package fr.kainovaii.dashbot.webhook;

import fr.kainovaii.dashbot.models.ServiceStatus;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import spark.Spark;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ConcurrentHashMap;

public class UptimeKumaWebhook {

    private final JDA jda;
    private final long channelId;
    public static final ConcurrentHashMap<String, ServiceStatus> statuses = new ConcurrentHashMap<>();

    public UptimeKumaWebhook(JDA jda, long channelId) {
        this.jda = jda;
        this.channelId = channelId;
        setupServer();
    }

    private void setupServer() {
        Spark.port(8282);

        Spark.post("/uptime", (req, res) -> {
            try {
                ServiceStatus status = ServiceStatus.fromJson(req.body());
                statuses.put(status.getName(), status);
                sendStatusToDiscord(status);
                res.status(200);
                return "OK";
            } catch (Exception e) {
                e.printStackTrace();
                res.status(500);
                return "Erreur";
            }
        });
    }

    private void sendStatusToDiscord(ServiceStatus status) {
        TextChannel channel = jda.getTextChannelById(channelId);
        if (channel == null) return;
        ZonedDateTime timestamp;
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.of("Europe/Paris"));
            timestamp = ZonedDateTime.parse(status.getTime(), formatter);
        } catch (Exception e) {
            timestamp = ZonedDateTime.now(ZoneId.of("Europe/Paris"));
        }

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

        channel.sendMessageEmbeds(embed.build()).setActionRow(
                Button.link("https://status.kainovaii.cloud/status/default", "En savoir plus")
        ).queue();
    }
}
