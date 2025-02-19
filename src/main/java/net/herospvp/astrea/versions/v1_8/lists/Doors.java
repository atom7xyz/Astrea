package net.herospvp.astrea.versions.v1_8.lists;

import lombok.Getter;
import org.bukkit.Material;

@Getter
public enum Doors {

    WOODEN_DOOR(Material.WOODEN_DOOR),
    SPRUCE_DOOR(Material.SPRUCE_DOOR),
    ACACIA_DOOR(Material.ACACIA_DOOR),
    BIRCH_DOOR(Material.BIRCH_DOOR),
    JUNGLE_DOOR(Material.JUNGLE_DOOR),
    DARK_OAK_DOOR(Material.DARK_OAK_DOOR),
    TRAP_DOOR(Material.TRAP_DOOR);

    private final Material material;

    Doors(Material material) {
        this.material = material;
    }

}
