package fr.kainovaii.dashbot.listeners;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ButtonListener extends ListenerAdapter
{
    @Override
    public void onButtonInteraction(ButtonInteractionEvent event)
    {
        if (event.getComponentId().equals("uptimeButtonSeeMore")) {
            event.reply("Tu as cliqué sur le bouton !").setEphemeral(true).queue();
        }
    }
}