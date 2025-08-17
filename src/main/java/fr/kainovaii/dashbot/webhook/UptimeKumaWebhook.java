package fr.kainovaii.dashbot.webhook;

import fr.kainovaii.dashbot.models.ServiceStatus;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import spark.Spark;

import java.util.concurrent.ConcurrentHashMap;

public class UptimeKumaWebhook {

    private final JDA jda;
    private final long channelId;

    public static final ConcurrentHashMap<String, ServiceStatus> statuses = new ConcurrentHashMap<>();

    public UptimeKumaWebhook(JDA jda, long channelId)
    {
        this.jda = jda;
        this.channelId = channelId;
        setupServer();
    }

    private void setupServer()
    {
        Spark.port(8282);

        Spark.post("/uptime", (req, res) -> {
            try {
                JsonObject json = JsonParser.parseString(req.body()).getAsJsonObject();
                JsonObject embedJson = json.getAsJsonArray("embeds").get(0).getAsJsonObject();

                String title = embedJson.has("title") ? embedJson.get("title").getAsString() : "Titre inconnu";
                int color = embedJson.has("color") ? embedJson.get("color").getAsInt() : 0xFFFFFF;

                String serviceStatus = title.toLowerCase().contains("up") ? "✅ Up" : "⚠️ Down";
                String serviceName = "Inconnu";
                String serviceUrl = "Inconnu";
                String time = "Inconnu";
                String ping = "-1 ms";

                for (var fieldElem : embedJson.getAsJsonArray("fields")) {
                    JsonObject field = fieldElem.getAsJsonObject();
                    String name = field.get("name").getAsString();
                    String value = field.get("value").getAsString();

                    switch (name) {
                        case "Service Name" -> serviceName = value;
                        case "Service URL" -> serviceUrl = value;
                        case "Time (Europe/Paris)" -> time = value;
                        case "Ping" -> ping = value;
                    }
                }

                int pingValue = -1;
                try {
                    pingValue = Integer.parseInt(ping.replaceAll("\\D", ""));
                } catch (NumberFormatException ignored) {}

                ServiceStatus status = new ServiceStatus(
                        serviceName,
                        serviceUrl,
                        serviceStatus,
                        time,
                        pingValue
                );
                statuses.put(serviceName, status);

                TextChannel channel = jda.getTextChannelById(channelId);
                if (channel != null) {
                    EmbedBuilder embed = new EmbedBuilder();
                    embed.setTitle(title);
                    embed.setDescription(
                            "Service: " + serviceName + "\n" +
                            "Service: " + serviceName + "\n" +
                                    "URL: " + serviceUrl + "\n" +
                                    "Heure: " + time + "\n" +
                                    "Ping: " + ping
                    );
                    embed.setColor(color);
                    channel.sendMessageEmbeds(embed.build()).queue();
                }

                res.status(200);
                return "OK";

            } catch (Exception e) {
                e.printStackTrace();
                res.status(500);
                return "Erreur";
            }
        });
    }
}
