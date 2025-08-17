package dev.klayses.structure.api;

import org.bukkit.Location;
import org.bukkit.structure.Structure;

import java.util.Collection;

public abstract class StructureAPI {
    public abstract Collection<String> getLoadedStructureNames();
    public abstract boolean structureExists(String name);
    public abstract Structure getStructure(String name);
    public abstract boolean spawnStructure(String name, Location location);
}
