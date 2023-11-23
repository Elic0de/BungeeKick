package net.japanpvpserver.bungeekick;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.mrpowergamerbr.temmiewebhook.DiscordEmbed;
import com.mrpowergamerbr.temmiewebhook.DiscordMessage;
import com.mrpowergamerbr.temmiewebhook.TemmieWebhook;
import com.mrpowergamerbr.temmiewebhook.embed.ThumbnailEmbed;
import lombok.Getter;
import me.leoko.advancedban.Universal;
import me.leoko.advancedban.bungee.BungeeMain;
import me.leoko.advancedban.bungee.event.PunishmentEvent;
import me.leoko.advancedban.manager.PunishmentManager;
import me.leoko.advancedban.manager.TimeManager;
import me.leoko.advancedban.manager.UUIDManager;
import me.leoko.advancedban.utils.Punishment;
import me.leoko.advancedban.utils.PunishmentType;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.ServerKickEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.scheduler.TaskScheduler;
import net.md_5.bungee.event.EventHandler;
import net.william278.annotaml.Annotaml;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public final class BungeeKick extends Plugin implements Listener {

    @Getter
    private static BungeeKick instance;

    @Getter
    private BungeeKickSettings settings;

    @Getter
    private TemmieWebhook temmie;


    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        loadConfig();

        if (ProxyServer.getInstance().getPluginManager().getPlugin("AdvancedBan") == null) {
            throw new RuntimeException("not loaded AdvancedBan plugin");
        }
        temmie = new TemmieWebhook(settings.getUrl());

        getProxy().getPluginManager().registerListener(this, this);
        getProxy().getPluginManager().registerListener(this, new PunishmentListener());
        getProxy().getPluginManager().registerCommand(this, new BungeeCommand());
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

    public void resisterListener() {
        ProxyServer.getInstance().getPluginManager().registerListener(this, new AutoRestartListener());
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

    private void connectSection(ProxiedPlayer player, String name) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF(name);

        player.sendData("playerbalancer:main", out.toByteArray());
    }
}
