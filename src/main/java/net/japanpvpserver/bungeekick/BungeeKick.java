package net.japanpvpserver.bungeekick;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerKickEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;
import net.william278.annotaml.Annotaml;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;

public final class BungeeKick extends Plugin implements Listener {

    @Getter
    private BungeeKickSettings settings;

    @Override
    public void onEnable() {
        // Plugin startup logic
        loadConfig();
        getProxy().getPluginManager().registerListener(this, this);
    }

    private void loadConfig() throws RuntimeException {
        try {
            this.settings = Annotaml.create(new File(getDataFolder(), "config.yml"), BungeeKickSettings.class).get();
        } catch (IOException | InvocationTargetException | InstantiationException |
                 IllegalAccessException e) {
            getLogger().log(Level.SEVERE, "Failed to load configuration files", e);
            throw new RuntimeException(e);
        }
    }

    @EventHandler
    public void onServerKick(ServerKickEvent event) {
        final ProxiedPlayer player = event.getPlayer();
        final String name = settings.getServerName();
        final ServerInfo server = getProxy().getServerInfo(name);

        event.setCancelled(true);
        if (settings.isShowKickReason()) player.sendMessage(new ComponentBuilder().color(ChatColor.RED).append(event.getKickReasonComponent()).create());
        if (server != null) {
            if (server == event.getKickedFrom()) return;
            event.setCancelServer(server);
            return;
        }
        connectSection(player, name);
    }

    private void connectSection(ProxiedPlayer player, String name) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF(name);

        player.sendData("playerbalancer:main", out.toByteArray());
    }
}
