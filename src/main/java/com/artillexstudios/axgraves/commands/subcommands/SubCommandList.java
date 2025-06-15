package com.artillexstudios.axgraves.commands.subcommands;

import com.artillexstudios.axapi.utils.StringUtils;
import com.artillexstudios.axgraves.grave.Grave;
import com.artillexstudios.axgraves.grave.SpawnedGraves;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static com.artillexstudios.axgraves.AxGraves.CONFIG;
import static com.artillexstudios.axgraves.AxGraves.MESSAGES;
import static com.artillexstudios.axgraves.AxGraves.MESSAGEUTILS;

public enum SubCommandList {
    INSTANCE;

    public void subCommand(@NotNull CommandSender sender) {
        if (SpawnedGraves.getGraves().isEmpty()) {
            MESSAGEUTILS.sendLang(sender, "grave-list.no-graves");
            return;
        }

        MESSAGEUTILS.sendFormatted(sender, MESSAGES.getString("grave-list.header"));

        int dTime = CONFIG.getInt("despawn-time-seconds", 180);
        for (Grave grave : SpawnedGraves.getGraves()) {
            if (!sender.hasPermission("axgraves.list.other") &&
                    sender instanceof Player &&
                    !grave.getPlayer().equals(sender)
            ) continue;

            final Location l = grave.getLocation();
            final String time = StringUtils.formatTime(dTime != -1 ? (dTime * 1_000L - (System.currentTimeMillis() - grave.getSpawned())) : System.currentTimeMillis() - grave.getSpawned());

            final Map<String, String> map = Map.of("%player%", grave.getPlayerName(),
                    "%world%", l.getWorld().getName(),
                    "%x%", "" + l.getBlockX(),
                    "%y%", "" + l.getBlockY(),
                    "%z%", "" + l.getBlockZ(),
                    "%time%", time);



            BaseComponent[] restoreOption = TextComponent.fromLegacyText(StringUtils.formatToString(MESSAGES.getString("grave-list.restore-option"), new HashMap<>()));
            for (BaseComponent component : restoreOption) {
                component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/grave restore " + grave.getGraveID()));
            }

            BaseComponent[] tpOption = TextComponent.fromLegacyText(StringUtils.formatToString(MESSAGES.getString("grave-list.tp-option"), new HashMap<>(map)));
            for (BaseComponent component : tpOption) {
                component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, String.format("/axgraves grave %s %f %f %f", l.getWorld().getName(), l.getX(), l.getY(), l.getZ())));
            }

            String oText = MESSAGES.getString("grave-list.grave");

            if(oText.contains("%player%")) oText = oText.replace("%player%", grave.getPlayerName());
            if(oText.contains("%world%")) oText = oText.replace("%world%", l.getWorld().getName());
            if(oText.contains("%x%")) oText = oText.replace("%x%", String.valueOf(l.getX()));
            if(oText.contains("%y%")) oText = oText.replace("%y%", String.valueOf(l.getY()));
            if(oText.contains("%z%")) oText = oText.replace("%z%", String.valueOf(l.getZ()));
            if(oText.contains("%time%")) oText = oText.replace("%time%", time);

            BaseComponent[] otherText = TextComponent.fromLegacyText(StringUtils.formatToString(oText, new HashMap<>()));

            sender.spigot().sendMessage(Stream.of(restoreOption,tpOption,otherText)
                    .flatMap(Stream::of)
                    .toArray(BaseComponent[]::new));
        }
    }
}
