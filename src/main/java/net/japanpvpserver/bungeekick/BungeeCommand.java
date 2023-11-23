package net.japanpvpserver.bungeekick;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.plugin.Command;

public class BungeeCommand extends Command {


    public BungeeCommand() {
        super("autorestart", "autorestart");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length != 0) return;
        if (ProxyServer.getInstance().getPlayers().size() == 0) {
            ProxyServer.getInstance().stop("AutoRestart");
            sender.sendMessage(new ComponentBuilder("再起動します。").create());
            return;
        }

        BungeeKick.getInstance().resisterListener();
        sender.sendMessage(new ComponentBuilder("オンラインプレイヤーが0プレイヤーの場合、再起動します。").create());
    }
}
