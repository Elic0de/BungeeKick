package net.japanpvpserver.bungeekick;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.mrpowergamerbr.temmiewebhook.DiscordEmbed;
import lombok.Getter;
import me.leoko.advancedban.Universal;
import me.leoko.advancedban.bungee.event.PunishmentEvent;
import me.leoko.advancedban.manager.PunishmentManager;
import me.leoko.advancedban.manager.TimeManager;
import me.leoko.advancedban.utils.Punishment;
import me.leoko.advancedban.utils.PunishmentType;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.ServerKickEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;
import net.william278.annotaml.Annotaml;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;
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

    @EventHandler
    private void onLogin(LoginEvent event) {
        final Punishment punishment = PunishmentManager.get().getBan(event.getConnection().getUniqueId().toString());
        if (punishment == null) return;
        for (final String ipAddress : Universal.get().getIps().values()) {
            if (Objects.equals(ipAddress, event.getConnection().getAddress().getAddress().getHostAddress())) {
                final PendingConnection connection = event.getConnection();
                final String name = connection.getName();
                final String uuid = connection.getUniqueId().toString();
                final String reason = "Alt Account Automatically detected";
                final String operation = "Console";

                new Punishment(name, uuid, reason, operation, PunishmentType.BAN, TimeManager.getTime(), -1, null, -1).create();
                PunishmentManager.get().discard(name);
            }
        }
    }

    @EventHandler
    private void onPunishment(PunishmentEvent event) {
        final Punishment punishment = event.getPunishment();
        final String duration = punishment.getDuration(true).equals("permanent") ? "永久" : punishment.getDuration(true);
        DiscordEmbed.builder().color(Color.RED.getRGB()).description(String.format("名前: ``%s(%s)``\n理由: %s\n期限: %s", punishment.getName(), punishment.getUuid(), punishment.getReason(), duration));
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
