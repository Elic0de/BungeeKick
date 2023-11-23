package net.japanpvpserver.bungeekick;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class AutoRestartListener implements Listener {

    @EventHandler
    private void onDisconnect(PlayerDisconnectEvent event) {
        final ProxyServer proxyServer = ProxyServer.getInstance();
        if (proxyServer.getPlayers().size() != 0) return;
        proxyServer.stop("AutoRestart");
    }
}
