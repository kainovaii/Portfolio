package fr.kainovaii.dashbot.commands;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public class LeaveServerCommand implements Command
{
    @Override
    public String getName() {
        return "leave";
    }

    @Override
    public SlashCommandData getCommandData() {
        return Commands.slash(getName(), "Fait quitter un serveur au bot")
                .addOption(OptionType.STRING, "id", "ID du serveur", true);
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        String guildId = event.getOption("id").getAsString();
        Guild guild = event.getJDA().getGuildById(guildId);

        if (guild == null) {
            event.reply("❌ Aucun serveur trouvé avec l’ID `" + guildId + "`.").setEphemeral(true).queue();
            return;
        }

        String name = guild.getName();
        guild.leave().queue(
                success -> event.reply("🚪 J’ai quitté le serveur **" + name + "**.").setEphemeral(true).queue(),
                error -> event.reply("⚠️ Erreur en quittant le serveur : " + error.getMessage()).setEphemeral(true).queue()
        );
    }
}
