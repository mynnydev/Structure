package dev.klayses.structure;

import dev.klayses.structure.api.StructureAPI;
import dev.klayses.structure.api.StructureAPIImpl;
import dev.klayses.structure.function.GetCommand;
import dev.klayses.structure.function.GetlistCommand;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        GetCommand getCommand = new GetCommand(this);

        // register Commands
        this.getCommand("get").setExecutor(new GetCommand(this));
        this.getCommand("getlist").setExecutor(new GetlistCommand(this, getCommand.getLoadedStructures()));
        // register Commands

        // create file
        File structureFolder = new File(getDataFolder(), "structures");
        if (!structureFolder.exists()) {
            structureFolder.mkdirs();
        }
        // create file

        // API
        StructureAPI api = new StructureAPIImpl(getCommand);
        getServer().getServicesManager().register(StructureAPI.class, api, this, org.bukkit.plugin.ServicePriority.Normal);
        // API

    }

    @Override
    public void onDisable() {
    }

    @Override
    public void onLoad() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission("structure.get")) {
                player.sendMessage(Main.MAIN_PREIFX + ChatColor.WHITE + "All structures have been loaded!");
            }
        }
    }

    public static final String MAIN_PREIFX = ChatColor.WHITE + "[" + ChatColor.of("#fdeca6") + "Structure" + ChatColor.WHITE + "] ";
    public static final String NO_PLAYER = MAIN_PREIFX + ChatColor.WHITE + "This command can only be executed by the player!";
    public static final String NO_PERMISSION = MAIN_PREIFX + ChatColor.WHITE + "You do not have permission to execute this command!";
}
