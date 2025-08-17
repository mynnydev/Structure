package dev.klayses.structure.api;

import dev.klayses.structure.function.GetCommand;
import org.bukkit.Location;
import org.bukkit.structure.Structure;
import org.bukkit.block.structure.Mirror;
import org.bukkit.block.structure.StructureRotation;
import java.util.Collection;
import java.util.Collections;

public class StructureAPIImpl extends StructureAPI {

    private final GetCommand getCommand;

    public StructureAPIImpl(GetCommand getCommand) {
        this.getCommand = getCommand;
    }

    @Override
    public Collection<String> getLoadedStructureNames() {
        return Collections.unmodifiableSet(getCommand.getLoadedStructures().keySet());
    }

    @Override
    public boolean structureExists(String name) {
        return getCommand.getLoadedStructures().containsKey(name.toLowerCase());
    }

    @Override
    public Structure getStructure(String name) {
        return getCommand.getLoadedStructures().get(name.toLowerCase());
    }

    @Override
    public boolean spawnStructure(String name, Location location) {
        Structure structure = getStructure(name);
        if (structure == null || location == null) return false;
        structure.place(location,
                true,
                StructureRotation.NONE,
                Mirror.NONE,
                -1,
                1.0f,
                null);
        return true;
    }
}
