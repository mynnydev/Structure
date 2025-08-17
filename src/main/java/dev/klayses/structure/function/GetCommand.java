package dev.klayses.structure.function;

import dev.klayses.structure.Main;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
// RICHTIG:
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.structure.Structure;
import org.bukkit.block.structure.Mirror;
import org.bukkit.block.structure.StructureRotation;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.*;


public class GetCommand implements CommandExecutor, TabCompleter {

    private final JavaPlugin plugin;
    private final Map<String, Structure> loadedStructures = new HashMap<>();

    public Map<String, Structure> getLoadedStructures() {
        return loadedStructures;
    }

    public GetCommand(JavaPlugin plugin) {
        this.plugin = plugin;
        loadStructures();
    }

    // load structure
    private void loadStructures() {
        File structureFolder = new File(plugin.getDataFolder(), "structures");
        if (!structureFolder.exists()) {
            structureFolder.mkdirs();
        }

        File[] files = structureFolder.listFiles((dir, name) -> name.endsWith(".nbt"));
        if (files == null) return;

        for (File file : files) {
            try {
                String name = file.getName().replace(".nbt", "");
                NamespacedKey key = NamespacedKey.fromString(name);
                Structure structure = Bukkit.getStructureManager().loadStructure(file);
                if (structure != null) {
                    loadedStructures.put(name.toLowerCase(), structure);
                    plugin.getLogger().info("Struktur geladen: " + name);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    // load structure

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender.hasPermission("structure.get")) {

            if (args.length != 5) { // jetzt 5 Argumente: structure, world, x, y, z
                sender.sendMessage(Main.MAIN_PREIFX + ChatColor.WHITE + "Usage: /get <structure> <world> <x> <y> <z>");
                return true;
            }

            String structureName = args[0].toLowerCase();
            String worldName = args[1];
            int x, y, z;

            try {
                x = Integer.parseInt(args[2]);
                y = Integer.parseInt(args[3]);
                z = Integer.parseInt(args[4]);
            } catch (NumberFormatException e) {
                sender.sendMessage(Main.MAIN_PREIFX + ChatColor.WHITE + "Coordinates must be numbers!");
                return true;
            }

            Structure structure = loadedStructures.get(structureName);
            if (structure == null) {
                sender.sendMessage(Main.MAIN_PREIFX + ChatColor.WHITE + "Structure not found: " + ChatColor.of("#ff4400") + structureName);
                return true;
            }

            if (Bukkit.getWorld(worldName) == null) {
                sender.sendMessage(Main.MAIN_PREIFX + ChatColor.WHITE + "World not found: " + ChatColor.of("#ff4400") + worldName);
                return true;
            }

            Location loc = new Location(Bukkit.getWorld(worldName), x, y, z);
            structure.place(loc, true, StructureRotation.NONE, Mirror.NONE, -1, 1.0f, new Random());
            sender.sendMessage(Main.MAIN_PREIFX + ChatColor.WHITE + "Structure " + ChatColor.of("#00fc88") +
                    structureName + ChatColor.WHITE + " has been spawned!");
            return true;

        } else {
            sender.sendMessage(Main.NO_PERMISSION);
        }
        return false;
    }


    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) return Collections.emptyList();
        if (cmd.getName().equalsIgnoreCase("get")) {
            switch (args.length) {
                case 1:
                    String typed = args[0].toLowerCase();
                    List<String> options = new ArrayList<>();
                    for (String name : loadedStructures.keySet()) {
                        if (name.startsWith(typed)) {
                            options.add(name);
                        }
                    }
                    Collections.sort(options);
                    return options;
                case 2:
                    List<String> worlds = new ArrayList<>();
                    for (org.bukkit.World w : Bukkit.getWorlds()) {
                        worlds.add(w.getName());
                    }
                    return worlds;
                case 3:
                    return Collections.singletonList(String.valueOf(player.getLocation().getBlockX()));
                case 4:
                    return Collections.singletonList(String.valueOf(player.getLocation().getBlockY()));
                case 5:
                    return Collections.singletonList(String.valueOf(player.getLocation().getBlockZ()));
            }
        }
        return Collections.emptyList();
    }
}
