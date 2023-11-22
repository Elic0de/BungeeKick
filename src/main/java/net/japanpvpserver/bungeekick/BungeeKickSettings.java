package net.japanpvpserver.bungeekick;

import lombok.Getter;
import net.william278.annotaml.YamlComment;
import net.william278.annotaml.YamlFile;
import net.william278.annotaml.YamlKey;

@Getter
@YamlFile
public class BungeeKickSettings {

    @YamlKey("kickToServer")
    @YamlComment("キックされた際に転送するサーバー \nセクションも扱えます")
    private final String serverName = "lobby";

    @YamlKey("showKickReason")
    private final boolean showKickReason = true;

    @YamlKey("webhook")
    private String url;

    public BungeeKickSettings() {
    }
}
