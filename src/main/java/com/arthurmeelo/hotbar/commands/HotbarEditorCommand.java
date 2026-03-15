package com.arthurmeelo.hotbar.commands;

import com.arthurmeelo.hotbar.HotbarManagerPlugin;
import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.CommandPermission;
import org.bukkit.entity.Player;

@CommandAlias("hotbar|hotbareditor|hbed")
@CommandPermission("hotbar.use")
public class HotbarEditorCommand extends BaseCommand {
    private final HotbarManagerPlugin plugin;

    public HotbarEditorCommand(HotbarManagerPlugin plugin) {
        this.plugin = plugin;
    }

    @Default
    public void onCommand(Player player) {
        plugin.getHotbarEditorGui().openMain(player);
    }
}
