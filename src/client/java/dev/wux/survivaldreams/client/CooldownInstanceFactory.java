package dev.wux.survivaldreams.client;

import net.minecraft.world.item.ItemCooldowns;

import java.lang.reflect.Constructor;

final class CooldownInstanceFactory {

    private static volatile Constructor<?> cachedConstructor;

    private CooldownInstanceFactory() {}

    static Object create(int startTime, int endTime) {
        try {
            return constructor().newInstance(startTime, endTime);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("No se pudo crear CooldownInstance por reflection", e);
        }
    }

    private static Constructor<?> constructor() throws ReflectiveOperationException {
        Constructor<?> local = cachedConstructor;
        if (local == null) {
            synchronized (CooldownInstanceFactory.class) {
                local = cachedConstructor;
                if (local == null) {
                    Class<?> instanceClass = Class.forName(
                            ItemCooldowns.class.getName() + "$CooldownInstance");
                    local = instanceClass.getDeclaredConstructor(int.class, int.class);
                    local.setAccessible(true);
                    cachedConstructor = local;
                }
            }
        }
        return local;
    }
}