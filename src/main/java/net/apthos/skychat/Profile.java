package net.apthos.skychat;

import net.apthos.guilds.guild.Guild;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;

public class Profile {

    public Rank getDominantRank() {
        return DominantRank;
    }

    public void setDominantRank(Rank dominantRank) {
        DominantRank = dominantRank;
    }

    public boolean hasDominantRank() {
        if (DominantRank == null) {
            return false;
        }
        return true;
    }

    Rank DominantRank = null;

    public Player getPlayer() {
        return player;
    }

    private Player player;

    public String getNick() {
        if (! hasNick()) {
            return player.getName();
        }
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    private boolean hasNick() {
        return false;
    }

    private String nick = "$empty$";
    private String ULink = "$empty$";

    public Guild getGuild(){
        return SkyChat.getInstance().getGuildsPlugin().getProfile(player).getGuild();
    }

    public Profile(Player player) {
        this.player = player;
        File file = new File(SkyChat.getInstance().getDataFolder() + "/Profiles/"
                + player.getUniqueId().toString() + ".yml");
        if (! file.exists()) {
            return;
        }
        YamlConfiguration YAML = YamlConfiguration.loadConfiguration(file);
        if (! YAML.getString("rank").equalsIgnoreCase("$empty$")) {
            setDominantRank(SkyChat.getInstance().getRank(YAML.getString("rank")));
        }
        if (! YAML.getString("nick").equalsIgnoreCase("$empty$")) {
            setNick(YAML.getString("nick"));
        }
        if (! YAML.getString("channel").equalsIgnoreCase("$empty$")) {
            ULink = YAML.getString("youtube");
        }
    }

    public void setDominantSelection(Rank rank) {
        setDominantRank(rank);
        YamlConfiguration yaml = null;
        if (hasFile())
            yaml = YamlConfiguration.loadConfiguration(new File(SkyChat.getInstance()
                    .getDataFolder() + "/Profiles/"
                    + player.getUniqueId().toString() + ".yml"));
        else {
            createFile(rank.getRankName(), nick, ULink);
            return;
        }
        yaml.set("rank", rank.getRankName());
    }

    public void destroyDominantSelection() {
        if (! hasFile()) return;
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(new File(SkyChat
                .getInstance().getDataFolder() + "/Profiles/"
                + player.getUniqueId().toString() + ".yml"));
        yaml.set("rank", "$empty$");
    }

    public void createFile(String rank, String nick, String channel) {
        File file = new File(SkyChat.getInstance().getDataFolder() + "/Profiles/"
                + player.getUniqueId().toString() + ".yml");
        YamlConfiguration YAML = new YamlConfiguration();
        YAML.createSection("rank"); YAML.set("rank", rank);
        YAML.createSection("nick"); YAML.set("nick", nick);
        YAML.createSection("channel"); YAML.set("channel", channel);
        try {
            YAML.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean hasFile() {
        File file = new File(SkyChat.getInstance().getDataFolder() + "/Profiles/"
                + player.getUniqueId().toString() + ".yml");
        return file.exists();
    }

}
