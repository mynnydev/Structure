package dev.klayses.structure.function;

import dev.klayses.structure.Main;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.structure.Structure;
import org.bukkit.structure.StructureManager;


import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

public class CasCommand implements CommandExecutor, TabCompleter {

    private final JavaPlugin plugin;

    public CasCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length != 8) {
            sender.sendMessage(Main.MAIN_PREIFX + ChatColor.WHITE + "Usage: /cas <name> <world> <pos1-x> <pos1-y> <pos1-z> <pos2-x> <pos2-y> <pos2-z>");
            return true;
        }
        if(sender.hasPermission(Main.perm)) {
            String name = args[0];
            String worldName = args[1];
            World world = Bukkit.getWorld(worldName);

            if (world == null) {
                sender.sendMessage(Main.MAIN_PREIFX + ChatColor.WHITE + "World not found: " + ChatColor.of("#ff4400") + worldName);
                return true;
            }

            try {
                int pos1x = Integer.parseInt(args[2]);
                int pos1y = Integer.parseInt(args[3]);
                int pos1z = Integer.parseInt(args[4]);
                int pos2x = Integer.parseInt(args[5]);
                int pos2y = Integer.parseInt(args[6]);
                int pos2z = Integer.parseInt(args[7]);

                StructureManager structureManager = Bukkit.getStructureManager();
                Structure structure = structureManager.createStructure();

                Location corner1 = new Location(world, pos1x, pos1y, pos1z);
                Location corner2 = new Location(world, pos2x, pos2y, pos2z);
                structure.fill(corner1, corner2, true);

                NamespacedKey key = new NamespacedKey(plugin, name);
                structureManager.saveStructure(key, structure);

                File sourceFile = new File(plugin.getDataFolder().getParentFile().getParentFile(),
                        "world/generated/structure/structures" + "/" + name + ".nbt");
                File targetDir = new File(plugin.getDataFolder(), "structures");
                if (!targetDir.exists()) {
                    targetDir.mkdirs();
                }
                File targetFile = new File(targetDir, name + ".nbt");
                if (sourceFile.exists()) {
                    Files.move(sourceFile.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    sender.sendMessage(Main.MAIN_PREIFX + ChatColor.WHITE + "Structure " + ChatColor.of("#00fc88") +  name + ChatColor.WHITE + " saved and placed in the structures folder.");
                    this.plugin.getLogger().log(Level.INFO, "Structure " + name + " saved and placed in " + targetFile.getPath() + " folder.");
                } else {
                    sender.sendMessage(Main.MAIN_PREIFX + ChatColor.WHITE + "An error has occurred. For more information, check the console.");
                    this.plugin.getLogger().log(Level.SEVERE, "Could not find the file: ", sourceFile.getPath());
                }

            } catch (NumberFormatException e) {
                sender.sendMessage(Main.MAIN_PREIFX + ChatColor.WHITE + "Coordinates must be numbers!");
            } catch (Exception e) {
                this.plugin.getLogger().log(Level.SEVERE, "Error saving the structure: ", e);
            }
        } else {
            sender.sendMessage(Main.NO_PERMISSION);
        }
        return true;
    }

    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            return Collections.emptyList();
        } else {
            if (sender.hasPermission(Main.perm) && cmd.getName().equalsIgnoreCase("cas")) {
                switch (args.length) {
                    case 1:
                        List<String> options = new ArrayList();
                        options.add("structur");
                        return options;
                    case 2:
                        List<String> worlds = new ArrayList();

                        for(World w : Bukkit.getWorlds()) {
                            worlds.add(w.getName());
                        }

                        return worlds;
                    case 3:
                        return Collections.singletonList(String.valueOf(player.getLocation().getBlockX()));
                    case 4:
                        return Collections.singletonList(String.valueOf(player.getLocation().getBlockY()));
                    case 5:
                        return Collections.singletonList(String.valueOf(player.getLocation().getBlockZ()));

                    case 6:
                        return Collections.singletonList(String.valueOf(player.getLocation().getBlockX() + 10));
                    case 7:
                        return Collections.singletonList(String.valueOf(player.getLocation().getBlockY() + 10));
                    case 8:
                        return Collections.singletonList(String.valueOf(player.getLocation().getBlockZ() + 10));
                }
            }

            return Collections.emptyList();
        }
    }
}
