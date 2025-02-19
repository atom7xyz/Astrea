package net.herospvp.astrea.versions.v1_8.lists;

import lombok.Getter;
import org.bukkit.Material;

@Getter
public enum OneHitMaterials {

    LONG_GRASS(Material.LONG_GRASS),
    DOUBLE_PLANT(Material.DOUBLE_PLANT),
    CARROT(Material.CARROT),
    POTATO(Material.POTATO),
    WHEAT(Material.WHEAT),
    DEAD_BUSH(Material.DEAD_BUSH),
    FIRE(Material.FIRE),
    RED_MUSHROOM(Material.RED_MUSHROOM),
    BROWN_MUSHROOM(Material.BROWN_MUSHROOM),
    NETHER_WARTS(Material.NETHER_WARTS),
    SAPLING(Material.SAPLING),
    REDSTONE_COMPARATOR_ON(Material.REDSTONE_COMPARATOR_ON),
    REDSTONE_COMPARATOR_OFF(Material.REDSTONE_COMPARATOR_OFF),
    REDSTONE_WIRE(Material.REDSTONE_WIRE),
    REDSTONE_TORCH_OFF(Material.REDSTONE_TORCH_OFF),
    REDSTONE_TORCH_ON(Material.REDSTONE_TORCH_ON),
    WATER_LILY(Material.WATER_LILY),
    TRIPWIRE(Material.TRIPWIRE),
    TRIPWIRE_HOOK(Material.TRIPWIRE_HOOK),
    TORCH(Material.TORCH),
    TNT(Material.TNT),
    SLIME_BLOCK(Material.SLIME_BLOCK),
    PUMPKIN_STEM(Material.PUMPKIN_STEM),
    MELON_STEM(Material.MELON_STEM),
    SUGAR_CANE_BLOCK(Material.SUGAR_CANE_BLOCK);

    private final Material material;

    OneHitMaterials(Material material) {
        this.material = material;
    }

}
