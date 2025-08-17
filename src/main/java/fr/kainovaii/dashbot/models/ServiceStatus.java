package fr.kainovaii.dashbot.models;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.time.temporal.TemporalAccessor;

public class ServiceStatus
{
    private final String name;
    private final String url;
    private final String status;
    private final String time;
    private final int ping;
    private final String title;
    private final int color;

    public ServiceStatus(String name, String url, String status, String time, int ping, String title, int color) {
        this.name = name;
        this.url = url;
        this.status = status;
        this.time = time;
        this.ping = ping;
        this.title = title;
        this.color = color;
    }

    public static ServiceStatus fromJson(String body)
    {
        JsonObject json = JsonParser.parseString(body).getAsJsonObject();
        JsonObject embedJson = json.getAsJsonArray("embeds").get(0).getAsJsonObject();

        String title = embedJson.has("title") ? embedJson.get("title").getAsString() : "Titre inconnu";
        int color = embedJson.has("color") ? embedJson.get("color").getAsInt() : 0xFFFFFF;
        String status = title.toLowerCase().contains("up") ? "✅ Up" : "⚠️ Down";

        String name = "Inconnu";
        String url = "Inconnu";
        String time = "Inconnu";
        int pingValue = -1;

        for (var fieldElem : embedJson.getAsJsonArray("fields")) {
            JsonObject field = fieldElem.getAsJsonObject();
            String fieldName = field.get("name").getAsString();
            String value = field.get("value").getAsString();

            switch (fieldName) {
                case "Service Name" -> name = value;
                case "Service URL" -> url = value;
                case "Time (Europe/Paris)" -> time = value;
                case "Ping" -> {
                    try {
                        pingValue = Integer.parseInt(value.replaceAll("\\D", ""));
                    } catch (NumberFormatException ignored) {}
                }
            }
        }

        return new ServiceStatus(name, url, status, time, pingValue, title, color);
    }

    public String getName() { return name; }
    public String getUrl() { return url; }
    public String getStatus() { return status; }
    public String getTime() { return time; }
    public int getPing() { return ping; }
    public String getTitle() { return title; }
    public int getColor() { return color; }
}
