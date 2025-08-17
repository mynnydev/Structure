package dev.klayses.structure.function;

import dev.klayses.structure.Main;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.structure.Structure;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;

public class GetlistCommand implements CommandExecutor {

    private final JavaPlugin plugin;
    private final Map<String, Structure> loadedStructures;

    public GetlistCommand(JavaPlugin plugin, Map<String, Structure> loadedStructures) {
        this.plugin = plugin;
        this.loadedStructures = loadedStructures;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("structure.get")) {
            sender.sendMessage(Main.NO_PERMISSION);
            return true;
        }

        if (loadedStructures.isEmpty()) {
            sender.sendMessage(Main.MAIN_PREIFX + ChatColor.WHITE + "No structures loaded!");
            return true;
        }

        sender.sendMessage(Main.MAIN_PREIFX + ChatColor.WHITE + "Loaded Structures:");
        for (String name : loadedStructures.keySet()) {
            //sender.sendMessage(ChatColor.WHITE + "- " + ChatColor.of("#00fc88") + name);
            Player player = (Player) sender;

            Component structureComponent = Component.text("- ", NamedTextColor.WHITE)
                    .append(Component.text(name, NamedTextColor.YELLOW)
                            .hoverEvent(HoverEvent.showText(Component.text("Click to spawn this structure!")))
                            .clickEvent(ClickEvent.suggestCommand("/get " + name + " " + player.getWorld().getName() + " " +
                                    player.getLocation().getBlockX() + " " + player.getLocation().getBlockY() + " " +
                                    player.getLocation().getBlockZ())));
            sender.sendMessage(structureComponent);

        }
        return true;
    }

}
