package dev.wux.survivaldreams.handler.enchant;

import net.minecraft.world.item.enchantment.Enchantments;

import java.util.List;

import static dev.wux.survivaldreams.handler.enchant.EnchantSlot.group;
import static dev.wux.survivaldreams.handler.enchant.EnchantSlot.solo;
import static dev.wux.survivaldreams.handler.enchant.EnchantSpec.of;

public final class EnchantmentPools {

    private EnchantmentPools() {}

    private static EnchantSlot protectionGroup() {
        return group(
                of(Enchantments.PROTECTION, 4, 100),
                of(Enchantments.PROJECTILE_PROTECTION, 4, 70),
                of(Enchantments.FIRE_PROTECTION, 4, 50),
                of(Enchantments.BLAST_PROTECTION, 4, 50)
        );
    }

    private static EnchantSlot meleeDamageGroup() {
        return group(
                of(Enchantments.SHARPNESS, 5, 100),
                of(Enchantments.SMITE, 5, 45),
                of(Enchantments.BANE_OF_ARTHROPODS, 5, 45)
        );
    }

    public static final List<EnchantSlot> HELMET = List.of(
            solo(of(Enchantments.MENDING, 1, 12)),
            solo(of(Enchantments.UNBREAKING, 3, 100)),
            solo(of(Enchantments.THORNS, 3, 35)),
            solo(of(Enchantments.RESPIRATION, 3, 55)),
            solo(of(Enchantments.AQUA_AFFINITY, 1, 20)),
            protectionGroup()
    );

    public static final List<EnchantSlot> TURTLE_SHELL = HELMET;

    public static final List<EnchantSlot> CHESTPLATE = List.of(
            solo(of(Enchantments.MENDING, 1, 12)),
            solo(of(Enchantments.UNBREAKING, 3, 100)),
            solo(of(Enchantments.THORNS, 3, 35)),
            protectionGroup()
    );

    public static final List<EnchantSlot> LEGGINGS = List.of(
            solo(of(Enchantments.MENDING, 1, 12)),
            solo(of(Enchantments.UNBREAKING, 3, 100)),
            solo(of(Enchantments.THORNS, 3, 35)),
            solo(of(Enchantments.SWIFT_SNEAK, 3, 20)),
            protectionGroup()
    );

    public static final List<EnchantSlot> BOOTS = List.of(
            solo(of(Enchantments.MENDING, 1, 12)),
            solo(of(Enchantments.UNBREAKING, 3, 100)),
            solo(of(Enchantments.THORNS, 3, 35)),
            solo(of(Enchantments.FEATHER_FALLING, 4, 90)),
            solo(of(Enchantments.SOUL_SPEED, 3, 25)),
            group(
                    of(Enchantments.DEPTH_STRIDER, 3, 60),
                    of(Enchantments.FROST_WALKER, 2, 40)
            ),
            protectionGroup()
    );

    public static final List<EnchantSlot> SWORD = List.of(
            solo(of(Enchantments.MENDING, 1, 12)),
            solo(of(Enchantments.UNBREAKING, 3, 90)),
            solo(of(Enchantments.FIRE_ASPECT, 2, 45)),
            solo(of(Enchantments.LOOTING, 3, 55)),
            solo(of(Enchantments.KNOCKBACK, 2, 55)),
            solo(of(Enchantments.SWEEPING_EDGE, 3, 55)),
            meleeDamageGroup()
    );

    public static final List<EnchantSlot> AXE = List.of(
            solo(of(Enchantments.MENDING, 1, 12)),
            solo(of(Enchantments.UNBREAKING, 3, 90)),
            solo(of(Enchantments.EFFICIENCY, 5, 60)),
            group(
                    of(Enchantments.FORTUNE, 3, 55),
                    of(Enchantments.SILK_TOUCH, 1, 25)
            ),
            meleeDamageGroup()
    );

    public static final List<EnchantSlot> TRIDENT = List.of(
            solo(of(Enchantments.MENDING, 1, 12)),
            solo(of(Enchantments.UNBREAKING, 3, 90)),
            solo(of(Enchantments.IMPALING, 5, 55)),
            solo(of(Enchantments.CHANNELING, 1, 15)),
            group(
                    of(Enchantments.LOYALTY, 3, 55),
                    of(Enchantments.RIPTIDE, 3, 35)
            )
    );

    public static final List<EnchantSlot> BOW = List.of(
            solo(of(Enchantments.UNBREAKING, 3, 90)),
            solo(of(Enchantments.POWER, 5, 100)),
            solo(of(Enchantments.PUNCH, 2, 45)),
            solo(of(Enchantments.FLAME, 1, 30)),
            group(
                    of(Enchantments.INFINITY, 1, 25),
                    of(Enchantments.MENDING, 1, 12)
            )
    );

    public static final List<EnchantSlot> CROSSBOW = List.of(
            solo(of(Enchantments.MENDING, 1, 12)),
            solo(of(Enchantments.UNBREAKING, 3, 90)),
            solo(of(Enchantments.QUICK_CHARGE, 3, 55)),
            group(
                    of(Enchantments.PIERCING, 4, 60),
                    of(Enchantments.MULTISHOT, 1, 35)
            )
    );

    public static final List<EnchantSlot> PICKAXE = List.of(
            solo(of(Enchantments.MENDING, 1, 12)),
            solo(of(Enchantments.UNBREAKING, 3, 90)),
            solo(of(Enchantments.EFFICIENCY, 5, 60)),
            group(
                    of(Enchantments.FORTUNE, 3, 55),
                    of(Enchantments.SILK_TOUCH, 1, 25)
            )
    );

    public static final List<EnchantSlot> SHOVEL = PICKAXE;

    public static final List<EnchantSlot> HOE = PICKAXE;

    public static final List<EnchantSlot> FISHING_ROD = List.of(
            solo(of(Enchantments.MENDING, 1, 12)),
            solo(of(Enchantments.UNBREAKING, 3, 90)),
            solo(of(Enchantments.LURE, 3, 55)),
            solo(of(Enchantments.LUCK_OF_THE_SEA, 3, 45))
    );

    public static final List<EnchantSlot> SHEARS = List.of(
            solo(of(Enchantments.MENDING, 1, 12)),
            solo(of(Enchantments.UNBREAKING, 3, 90)),
            solo(of(Enchantments.EFFICIENCY, 5, 60))
    );
}