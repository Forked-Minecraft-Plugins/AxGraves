package com.artillexstudios.axgraves.commands.subcommands;

import com.artillexstudios.axapi.utils.PaperUtils;
import com.artillexstudios.axgraves.grave.Grave;
import com.artillexstudios.axgraves.grave.SpawnedGraves;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import static com.artillexstudios.axgraves.AxGraves.MESSAGEUTILS;


public enum SubCommandRestore {
    INSTANCE;

    public void subCommand(@NotNull Player sender, int graveID) {

        Optional<Grave> foundGrave = SpawnedGraves.getGraves().stream()
                .filter(grave -> grave.getGraveID() == graveID)
                .findFirst();

        if(!foundGrave.isPresent())
        {
            MESSAGEUTILS.sendLang(sender, "grave-restore.grave-not-found");
            return;
        }

        Grave grave = foundGrave.get();
        Player target = (grave.getPlayerName().equals(sender.getName())) ? sender : Bukkit.getPlayer(grave.getPlayerName());

        if(!target.isOnline())
        {
            MESSAGEUTILS.sendLang(sender, "grave-restore.user-offline");
            return;
        }

        grave.restore(target, null, true);
        MESSAGEUTILS.sendLang(sender, "grave-restore.restore-success");

    }
}
