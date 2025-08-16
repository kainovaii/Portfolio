package fr.kainovaii.dashbot;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import spark.Spark;

import java.awt.*;

public class UptimeKumaWebhook {
    private final JDA jda;
    private final long channelId;

    public UptimeKumaWebhook(JDA jda, long channelId) {
        this.jda = jda;
        this.channelId = channelId;
        setupServer();
    }

    private void setupServer() {
        Spark.port(8080); // port pour recevoir le webhook
        Spark.get("/uptime", (req, res) -> "Le serveur fonctionne !");

        Spark.post("/uptime", (req, res) -> {
            try {
                JsonObject json = JsonParser.parseString(req.body()).getAsJsonObject();
                String content = json.has("content") ? json.get("content").getAsString() : "";
                JsonObject embedJson = json.getAsJsonArray("embeds").get(0).getAsJsonObject();
                String title = embedJson.has("title") ? embedJson.get("title").getAsString() : "Titre inconnu";
                int color = embedJson.has("color") ? embedJson.get("color").getAsInt() : 0xFFFFFF;
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

                System.out.println(json);

                TextChannel channel = jda.getTextChannelById(channelId);
                if (channel != null) {
                    EmbedBuilder embed = new EmbedBuilder();
                    embed.setTitle(title);
                    embed.setDescription("Service: " + serviceName + "\nURL: " + serviceUrl + "\nHeure: " + time + "\nPing: " + ping);
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

