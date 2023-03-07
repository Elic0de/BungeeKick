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
    private String serverName = "lobby";

    @YamlKey("showKickReason")
    private boolean showKickReason = true;
}
