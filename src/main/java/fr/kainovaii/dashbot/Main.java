package fr.kainovaii.dashbot;

import fr.kainovaii.dashbot.commands.Command;
import fr.kainovaii.dashbot.listeners.ButtonListener;
import fr.kainovaii.dashbot.listeners.SlashCommandListener;
import fr.kainovaii.dashbot.webhook.UptimeKumaWebhook;
import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.*;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.reflections.Reflections;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class Main
{
    private static Dotenv dotenv;

    public static void main(String[] args) throws Exception
    {
        dotenv = loadEnv();

        String token = getEnv("DISCORD_TOKEN");
        long channelId = Long.parseLong(getEnv("CHANNEL_ID"));
        long guildId = Long.parseLong(getEnv("GUILD_ID"));

        List<Command> commands = loadCommands();

        JDA jda = buildJDA(token, commands);

        configurePresence(jda);
        startWebhook(jda, channelId);
        registerGuildCommands(jda, guildId, commands);
    }

    private static List<Command> loadCommands()
    {
        Reflections reflections = new Reflections("fr.kainovaii.dashbot.commands");
        Set<Class<? extends Command>> commandClasses = reflections.getSubTypesOf(Command.class);

        return commandClasses.stream()
                .map(cls -> {
                    try {
                        return cls.getDeclaredConstructor().newInstance();
                    } catch (Exception e) {
                        System.err.println("Impossible d’instancier la commande : " + cls.getName());
                        e.printStackTrace();
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private static JDA buildJDA(String token, List<Command> commands) throws InterruptedException
    {
        JDABuilder builder = JDABuilder.createDefault(token, GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_MEMBERS)
        .addEventListeners(
                new SlashCommandListener(commands),
                new ButtonListener()
        );

        JDA jda = builder.build();
        jda.awaitReady();
        return jda;
    }

    private static void configurePresence(JDA jda)
    {
        jda.getPresence().setStatus(OnlineStatus.DO_NOT_DISTURB);
        jda.getPresence().setActivity(Activity.watching("kainovaii.cloud"));
    }

    private static void startWebhook(JDA jda, long channelId)
    {
        new UptimeKumaWebhook(jda, channelId);
        System.out.println("Serveur webhook Uptime Kuma démarré !");
    }

    private static void registerGuildCommands(JDA jda, long guildId, List<Command> commands)
    {
        Guild guild = jda.getGuildById(guildId);
        if (guild != null) {
            guild.updateCommands()
            .addCommands(commands.stream()
                    .map(Command::getCommandData)
                    .toList())
            .queue(success -> System.out.println("Commandes mises à jour !"),
                    error -> System.err.println("Erreur lors de l’update des commandes : " + error.getMessage()));
        } else {
            System.err.println("La guild avec l'ID " + guildId + " n'a pas été trouvée !");
        }
    }

    private static Dotenv loadEnv() { return Dotenv.configure().directory("./").load(); }

    private static String getEnv(String key)
    {
        String value = dotenv.get(key);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("La variable d'environnement " + key + " est manquante !");
        }
        return value;
    }
}
