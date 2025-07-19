package de.example.landsystem;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;

public class ChatMessageUtils {

    public static TextComponent getAllianceRequestMessage(String fromLand, String toLand) {
        TextComponent msg = new TextComponent("§7[§aAnnehmen§7] ");
        msg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/alliances accept " + fromLand));

        TextComponent decline = new TextComponent("§7[§cAblehnen§7]");
        decline.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/alliances deny " + fromLand));

        msg.addExtra(" ");
        msg.addExtra(decline);

        return msg;
    }
}
