package net.japanpvpserver.bungeekick;

import com.mrpowergamerbr.temmiewebhook.DiscordEmbed;
import com.mrpowergamerbr.temmiewebhook.DiscordMessage;
import com.mrpowergamerbr.temmiewebhook.embed.ThumbnailEmbed;
import me.leoko.advancedban.Universal;
import me.leoko.advancedban.bungee.event.PunishmentEvent;
import me.leoko.advancedban.manager.PunishmentManager;
import me.leoko.advancedban.manager.TimeManager;
import me.leoko.advancedban.utils.Punishment;
import me.leoko.advancedban.utils.PunishmentType;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.Objects;
import java.util.Optional;

public class PunishmentListener implements Listener {

    @EventHandler
    public void onLogin(LoginEvent event) {
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
    public void onPunishment(PunishmentEvent event) {
        final Punishment punishment = event.getPunishment();
        final String duration = punishment.getDuration(true).equals("permanent") ? "永久BAN" : "期限付きBAN" + punishment.getDuration(true);
        Optional.of(BungeeKick.getInstance().getTemmie()).ifPresent(temmieWebhook ->
                temmieWebhook.sendMessage(
                        DiscordMessage.builder()
                                .username("処罰通知")
                                .content("")
                                .embed(DiscordEmbed.builder()
                                        .thumbnail(ThumbnailEmbed.builder().url(String.format("https://minotar.net/armor/body/%s/100.png", punishment.getUuid())).build())
                                        .description(String.format("Minecraft \nMCID: %s\nUUID: %s\n期限: %s\n%s", punishment.getName(), punishment.getUuid(), duration, punishment.getReason()))
                                        .color(16744576)
                                        .build())
                                .build()));
    }

}
