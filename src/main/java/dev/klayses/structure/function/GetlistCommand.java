package dev.klayses.structure.function;

import dev.klayses.structure.Main;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.structure.Structure;

import java.util.List;
import java.util.Map;

public class GetlistCommand implements CommandExecutor {
    private final JavaPlugin plugin;
    private final Map<String, Structure> loadedStructures;

    public GetlistCommand(JavaPlugin plugin, Map<String, Structure> loadedStructures) {
        this.plugin = plugin;
        this.loadedStructures = loadedStructures;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission(Main.perm)) {
            sender.sendMessage(Main.NO_PERMISSION);
            return true;
        } else if (this.loadedStructures.isEmpty()) {
            sender.sendMessage(Main.MAIN_PREIFX + String.valueOf(ChatColor.WHITE) + "No structures loaded!");
            return true;
        } else {
            sender.sendMessage(Main.MAIN_PREIFX + String.valueOf(ChatColor.WHITE) + "Loaded Structures:");

            for(String name : this.loadedStructures.keySet()) {
                if (sender instanceof Player) {
                    Player player = (Player)sender;

                    Structure structure = loadedStructures.get(name);
                    var size = structure.getSize();
                    String sizeString = (int)size.getX() + "L " + (int)size.getY() + "H " + (int)size.getZ() + "W";

                    int entityCount = structure.getEntities().size();
                    int paletteCount = structure.getPaletteCount();

                    Component structureComponent = Component.text("- ",
                            NamedTextColor.WHITE).append(((TextComponent)Component.text(name, NamedTextColor.YELLOW).
                            hoverEvent(HoverEvent.showText(Component.text("size: " + ChatColor.YELLOW + sizeString + "\n" +
                                    ChatColor.WHITE + "entities: " + ChatColor.DARK_AQUA + entityCount + "\n" + ChatColor.WHITE +
                                    "palette: " + ChatColor.BLUE + paletteCount + "\n" + ChatColor.WHITE + "\n" + "Click to spawn this structure!")))).
                            clickEvent(ClickEvent.suggestCommand("/get " + name + " NONE NONE " + player.getWorld().getName() + " " +
                                    player.getLocation().getBlockX() + " " + player.getLocation().getBlockY() + " " + player.getLocation().getBlockZ())));

                    sender.sendMessage(structureComponent);
                } else {
                    sender.sendMessage(Main.MAIN_PREIFX + ChatColor.WHITE + "- " + String.valueOf(ChatColor.YELLOW) + name);
                }
            }

            return true;
        }
    }
}

