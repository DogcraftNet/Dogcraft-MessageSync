package net.dogcraft.dogcraftmessagesync;

import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.plugin.java.JavaPlugin;

public final class Dogcraft_MessageSync extends JavaPlugin implements Listener {
    private static Dogcraft_MessageSync instance;

    private Logger logger;
    public PluginMessage pluginMessage;
    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        logger = instance.getLogger();
        pluginMessage = new PluginMessage(instance);

        instance.getServer().getPluginManager().registerEvents(this, instance);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public void log(String message) {
        logger.log(Level.INFO, message);
    }

    @EventHandler
    public void playerDeath(PlayerDeathEvent event) {
        String message = PlainTextComponentSerializer.plainText().serialize(Objects.requireNonNull(event.deathMessage()));

        pluginMessage.sendPluginMessage(event.getPlayer(), message);
    }

    @EventHandler
    public void playerAdvancement(PlayerAdvancementDoneEvent event) {
        if (event.message() == null) {
            return;
        }

        String message = PlainTextComponentSerializer.plainText().serialize(Objects.requireNonNull(event.message()));

        pluginMessage.sendPluginMessage(event.getPlayer(), message);
    }
}
