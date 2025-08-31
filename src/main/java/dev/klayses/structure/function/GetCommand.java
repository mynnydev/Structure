package dev.klayses.structure.function;

import dev.klayses.structure.Main;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.block.structure.Mirror;
import org.bukkit.block.structure.StructureRotation;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.structure.Structure;

import java.io.File;
import java.util.*;
import java.util.logging.Level;

public class GetCommand implements CommandExecutor, TabCompleter {
    private final JavaPlugin plugin;
    private final Map<String, Structure> loadedStructures = new HashMap();

    public Map<String, Structure> getLoadedStructures() {
        return this.loadedStructures;
    }

    public GetCommand(JavaPlugin plugin) {
        this.plugin = plugin;
        this.loadStructures();
    }

    public void loadStructures() {
        File structureFolder = new File(this.plugin.getDataFolder(), "structures");
        if (!structureFolder.exists()) {
            structureFolder.mkdirs();
        }

        File[] files = structureFolder.listFiles((dir, namex) -> namex.endsWith(".nbt"));
        if (files != null) {
            this.plugin.getLogger().log(Level.INFO, "Loading structures...");

            for(File file : files) {
                try {
                    String name = file.getName().replace(".nbt", "");
                    NamespacedKey key = NamespacedKey.fromString(name);
                    Structure structure = Bukkit.getStructureManager().loadStructure(file);
                    if (structure != null) {
                        this.loadedStructures.put(name.toLowerCase(), structure);
                        this.plugin.getLogger().log(Level.INFO, "Structure loaded: " + name);
                    }
                } catch (Exception e) {
                    this.plugin.getLogger().log(Level.SEVERE, "Error loading the structure: " + file.getName(), e);
                }
            }

        }
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender.hasPermission(Main.perm)) {
            if (args.length != 7) {
                sender.sendMessage(Main.MAIN_PREIFX + String.valueOf(ChatColor.WHITE) + "Usage: /get <structure> <rotation> <mirror> <world> <x> <y> <z>");
                return true;
            } else {
                String structureName = args[0].toLowerCase();
                String rotationName = args[1].toUpperCase();
                String mirrorName = args[2].toUpperCase();
                String worldName = args[3];

                StructureRotation rotation;
                try {
                    rotation = StructureRotation.valueOf(rotationName);
                } catch (IllegalArgumentException var20) {
                    sender.sendMessage(Main.MAIN_PREIFX + String.valueOf(ChatColor.WHITE) + "Invalid rotation! Use: NONE, CLOCKWISE_90, CLOCKWISE_180, COUNTERCLOCKWISE_90");
                    return true;
                }

                Mirror mirror;
                try {
                    mirror = Mirror.valueOf(mirrorName);
                } catch (IllegalArgumentException var19) {
                    sender.sendMessage(Main.MAIN_PREIFX + String.valueOf(ChatColor.WHITE) + "Invalid mirror! Use: NONE, LEFT_RIGHT, FRONT_BACK");
                    return true;
                }

                int x;
                int y;
                int z;
                try {
                    x = Integer.parseInt(args[4]);
                    y = Integer.parseInt(args[5]);
                    z = Integer.parseInt(args[6]);
                } catch (NumberFormatException var18) {
                    sender.sendMessage(Main.MAIN_PREIFX + String.valueOf(ChatColor.WHITE) + "Coordinates must be numbers!");
                    return true;
                }

                Structure structure = (Structure)this.loadedStructures.get(structureName);
                if (structure == null) {
                    sender.sendMessage(Main.MAIN_PREIFX + String.valueOf(ChatColor.WHITE) + "Structure not found: " + String.valueOf(ChatColor.of("#ff4400")) + structureName);
                    return true;
                } else if (Bukkit.getWorld(worldName) == null) {
                    sender.sendMessage(Main.MAIN_PREIFX + String.valueOf(ChatColor.WHITE) + "World not found: " + String.valueOf(ChatColor.of("#ff4400")) + worldName);
                    return true;
                } else {
                    Location loc = new Location(Bukkit.getWorld(worldName), (double)x, (double)y, (double)z);
                    structure.place(loc, true, rotation, mirror, -1, 1.0F, new Random());
                    Component structureComponent = Component.text("").append(Component.text("settings",
                            NamedTextColor.YELLOW).hoverEvent(HoverEvent.showText(((TextComponent)((TextComponent)
                            Component.text("rotation: ").append(Component.text(rotation.name(),
                                    TextColor.fromHexString("#00fc88")))).append(Component.text(", mirror: ",
                            NamedTextColor.WHITE))).append(Component.text(mirror.name(), TextColor.fromHexString("#fdeca6"))))));
                    Component message = ((TextComponent.Builder)((TextComponent.Builder)
                            ((TextComponent.Builder)((TextComponent.Builder)((TextComponent.Builder)
                                    ((TextComponent.Builder)Component.text().append(Main.MAIN_PREFIX_CO)).append(Component.text
                                            ("Structure ", NamedTextColor.WHITE))).append(Component.text(structureName,
                                    TextColor.fromHexString("#00fc88")))).append(Component.text(" has been spawned with these ",
                                    NamedTextColor.WHITE))).append(structureComponent)).append(Component.text("!", NamedTextColor.WHITE))).build();
                    sender.sendMessage(message);
                    this.plugin.getLogger().log(Level.INFO, "Spawning structure " + structureName + ", " + worldName + ", " + x + ", " + y + ", " + z + ", rotation:" + String.valueOf(rotation) + ", mirror:" + String.valueOf(mirror));
                    return true;
                }
            }
        } else {
            sender.sendMessage(Main.NO_PERMISSION);
            return false;
        }
    }

    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            return Collections.emptyList();
        } else {
            if (sender.hasPermission(Main.perm) && cmd.getName().equalsIgnoreCase("get")) {
                switch (args.length) {
                    case 1:
                        String typed = args[0].toLowerCase();
                        List<String> options = new ArrayList();

                        for(String name : this.loadedStructures.keySet()) {
                            if (name.startsWith(typed)) {
                                options.add(name);
                            }
                        }

                        Collections.sort(options);
                        return options;
                    case 2:
                        return Arrays.asList("NONE", "CLOCKWISE_90", "CLOCKWISE_180", "COUNTERCLOCKWISE_90");
                    case 3:
                        return Arrays.asList("NONE", "LEFT_RIGHT", "FRONT_BACK");
                    case 4:
                        List<String> worlds = new ArrayList();

                        for(World w : Bukkit.getWorlds()) {
                            worlds.add(w.getName());
                        }

                        return worlds;
                    case 5:
                        return Collections.singletonList(String.valueOf(player.getLocation().getBlockX()));
                    case 6:
                        return Collections.singletonList(String.valueOf(player.getLocation().getBlockY()));
                    case 7:
                        return Collections.singletonList(String.valueOf(player.getLocation().getBlockZ()));
                }
            }

            return Collections.emptyList();
        }
    }
}
