package dev.wux.survivaldreams.handler.enchant;

import java.util.List;

public sealed interface EnchantSlot permits EnchantSlot.Solo, EnchantSlot.Group {

    record Solo(EnchantSpec spec) implements EnchantSlot {}

    record Group(List<EnchantSpec> options) implements EnchantSlot {}

    static EnchantSlot solo(EnchantSpec spec) {
        return new Solo(spec);
    }

    static EnchantSlot group(EnchantSpec... options) {
        return new Group(List.of(options));
    }

    default int weight() {
        if (this instanceof Solo s) return s.spec().pickWeight();
        if (this instanceof Group g) {
            return g.options().stream().mapToInt(EnchantSpec::pickWeight).max().orElse(1);
        }
        return 1;
    }
}