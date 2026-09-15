package dev.wux.survivaldreams.wand;

public final class ClientWandState {

    private static boolean active = false;

    private ClientWandState() {}

    public static void setActive(boolean value) {
        active = value;
    }

    public static boolean isActive() {
        return active;
    }
}