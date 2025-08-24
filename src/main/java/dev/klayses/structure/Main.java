package dev.klayses.structure;

import dev.klayses.structure.function.GetCommand;
import dev.klayses.structure.function.GetlistCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class Main extends JavaPlugin {

    public static final Component MAIN_PREFIX_CO;
    public static final String MAIN_PREIFX;
    public static final String NO_PLAYER;
    public static final String NO_PERMISSION;
    public static Permission perm;

    public void onEnable() {
        GetCommand getCommand = new GetCommand(this);
        this.getCommand("get").setExecutor(new GetCommand(this));
        this.getCommand("getlist").setExecutor(new GetlistCommand(this, getCommand.getLoadedStructures()));
        File structureFolder = new File(this.getDataFolder(), "structures");
        if (!structureFolder.exists()) {
            structureFolder.mkdirs();
        }

    }

    public void onDisable() {
    }

    public void onLoad() {
        for(Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission(perm)) {
                String var10001 = MAIN_PREIFX;
                player.sendMessage(var10001 + String.valueOf(ChatColor.WHITE) + "All structures have been loaded!");
            }
        }

    }

    static {
        MAIN_PREFIX_CO = ((TextComponent.Builder)((TextComponent.Builder)((TextComponent.Builder)Component.text().append(Component.text("[", NamedTextColor.WHITE))).append(Component.text("Structure", TextColor.fromHexString("#fdeca6")))).append(Component.text("] ", NamedTextColor.WHITE))).build();
        String var10000 = String.valueOf(ChatColor.WHITE);
        MAIN_PREIFX = var10000 + "[" + String.valueOf(ChatColor.of("#fdeca6")) + "Structure" + String.valueOf(ChatColor.WHITE) + "] ";
        var10000 = MAIN_PREIFX;
        NO_PLAYER = var10000 + String.valueOf(ChatColor.WHITE) + "This command can only be executed by the player!";
        var10000 = MAIN_PREIFX;
        NO_PERMISSION = var10000 + String.valueOf(ChatColor.WHITE) + "You do not have permission to execute this command!";
        perm = new Permission("structure.use");
    }
}