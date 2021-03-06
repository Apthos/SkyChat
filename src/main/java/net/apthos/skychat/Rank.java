package net.apthos.skychat;

import com.google.common.collect.ImmutableSet;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.yaml.snakeyaml.Yaml;

import javax.xml.soap.Text;
import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;


public class Rank {

    private final Set<String> Tags =
            ImmutableSet.of("{INFO}", "{PREFIX}", "{USERNAME}", "{MESSAGE}",
                    "{RAW_USERNAME}", "{RAW_MESSAGE}", "{GUILD}");

    private final Set<Character> ColorChars =
            ImmutableSet.of('a', 'b', 'c', 'd', 'e', 'f', '0', '1', '2', '3', '4', '5', '6'
                    , '7', '8', '9', 'k', 'l', 'n', 'm', 'o', 'r');

    public String getRankName() {
        return rankName;
    }

    private String rankName;
    private String format;
    private String username;
    private String prefix;
    private String info;
    private String message;

    private String TubeLink;

    public String getPermission() {
        return permission;
    }

    private String permission;

    public int getPriority() {
        return priority;
    }

    private int priority;

    public Rank(String name) {
        this.rankName = name;
        YamlConfiguration YAML = YamlConfiguration.loadConfiguration
                (new File(SkyChat.getInstance().getDataFolder() + "/ranks.yml"));
        String dir = "ranks." + name + ".";
        format = YAML.getString(dir + "format");
        username = YAML.getString(dir + "username");
        prefix = YAML.getString(dir + "prefix");
        info = YAML.getString(dir + "info");
        message = YAML.getString(dir + "message");
        permission = YAML.getString(dir + "permission");
        priority = YAML.getInt(dir + "priority");
    }

    public TextComponent getProcessedComponenet(Profile profile, String message) {
        LinkedList<TextComponent> comps = new LinkedList<>();
        net.md_5.bungee.api.ChatColor CC = net.md_5.bungee.api.ChatColor.WHITE;
        String format = this.format;
        boolean bold = false, italic = false, obfuscated = false, underlined = false,
                strike = false;
        int SI = -1, p = 0, count = 0;
        HashMap<String, Integer> tags = new HashMap<>();
        for (String tag : this.Tags) {
            tags.put(tag, 0);
        }

        while (!format.isEmpty()) {
            if (format.charAt(0) == '{') {
                String FTag = "null";
                boolean FTagF = false;
                for (String tag : Tags) {
                    if (tag.length() > format.length()) continue;
                    if (subString(format, 0, tag.length()-1).equalsIgnoreCase(tag)) {
                        FTag = tag;
                        FTagF = true;
                        break;
                    }
                }

                if (FTagF) {
                    format = cut(format, 0, FTag.length()-1);
                    if (FTag.equalsIgnoreCase("{RAW_USERNAME}") ) {
                        format = insert(format, profile.getNick(), 0);
                        tags.put(FTag, profile.getNick().length());
                    }else if (FTag.equalsIgnoreCase("{RAW_MESSAGE}")){
                        format = insert(format, message, 0);
                        tags.put(FTag, message.length());
                    } else if (FTag.equalsIgnoreCase("{GUILD}")) {
                        format = insert(format, profile.getGuild().getName(), 0);
                        tags.put(FTag, profile.getGuild().getName().length());
                    } else {
                        format = insert(format, getFormatTag(FTag), 0);
                        tags.put(FTag, ChatColor.stripColor(getFormatTag(FTag)).length());
                    }
                    ;
                }

            }

            TextComponent fragment = new TextComponent(String.valueOf(format.charAt(0)));

            if (tags.get("{INFO}") > 0) {
                fragment.setHoverEvent(getInfoFragment(profile));
                tags.put("{INFO}", tags.get("{INFO}") - 1);
            } else if (tags.get("{RAW_USERNAME}") > 0){
                fragment.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        new ComponentBuilder(
                                ChatColor.translateAlternateColorCodes('&', "" +
                                        "&aClick to send &c&l" +
                                        profile.getPlayer().getName() + " &aa message!"
                                )).create()));
                fragment.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,
                        "/msg " + profile.getPlayer().getName() + " "));
                tags.put("{RAW_USERNAME}", tags.get("{RAW_USERNAME}") - 1);
            } else if (tags.get("{GUILD}") > 0){
                fragment.setHoverEvent(getGuildHoverEvent(profile));
                tags.put("{GUILD}", tags.get("{GUILD}") - 1);
            }

            if (format.charAt(0) == ' '){
                SI = count;
            }

            if (format.charAt(0) == '&' && ! (format.toCharArray().length <= 1)
                    && ColorChars.contains(format.toCharArray()[1])) {
                char color = format.toCharArray()[1];
                if (color == 'k') {
                    obfuscated = true;
                } else if (color == 'l') {
                    bold = true;
                } else if (color == 'n') {
                    underlined = true;
                } else if (color == 'm') {
                    strike = true;
                } else if (color == 'o') {
                    italic = true;
                } else if (color == 'r') {
                    obfuscated = false;
                    bold = false;
                    underlined = false;
                    strike = false;
                    italic = false;
                    CC = net.md_5.bungee.api.ChatColor.WHITE;
                } else {
                    CC = net.md_5.bungee.api.ChatColor.getByChar(color);
                    obfuscated = false;
                    bold = false;
                    underlined = false;
                    strike = false;
                    italic = false;
                }
                format = cut(format, 0, 1);
                if (tags.get("{INFO}") > 0) {
                    tags.put("{INFO}", tags.get("{INFO}") - 1);
                } else if (tags.get("{USERNAME}") > 0){
                    tags.put("{RAW_USERNAME}", tags.get("{RAW_USERNAME}") - 1);
                }
                continue;
            }

            fragment.setObfuscated(obfuscated);
            fragment.setBold(bold);
            fragment.setUnderlined(underlined);
            fragment.setStrikethrough(strike);
            fragment.setItalic(italic);
            fragment.setColor(CC);

            p += Pixel.getPixelWidth(format.charAt(0), bold);

            if (p >= Pixel.DISPLAY_WIDTH + 15){
                comps.add(SI,new TextComponent("\n"));
                p = 0;
                for (int y = SI; y < comps.size(); y++){
                    p += Pixel.getPixelWidth(comps.get(y).getText().charAt(0),
                            comps.get(y).isBold());
                }
                SI = -1;
            }

            format = removeFirstChar(format);
            comps.add(fragment);
            count++;
        }
        TextComponent finale = new TextComponent();
        for (TextComponent text : comps){
            finale.addExtra(text);
        }
        return finale;
    }

    private String insert(String base, String insert, int index){
        String R = "";
        for (int x = 0; x < base.length(); x++){
            if (x == index){
                R = R + insert;
            }
            R = R + base.charAt(x);
        }
        if (R == ""){
            R = insert;
        }
        return R;
    }

    private String cut(String base, int start, int end) {
        String R = "";
        for (int x = 0; x < base.length(); x++) {
            if (x >= start && x <= end)
                continue;
            R = R + base.charAt(x);
        }
        return R;
    }

    private String subString(String base, int start, int end){
        String R = "";
        for (int x = start; x <= end; x++){
            R = R + base.charAt(x);
        }
        return R;
    }

    private String removeFirstChar(String base){
        String R = "";
        for (int x = 0; x < base.length(); x++){
            if ( x != 0 )
                R = R + base.charAt(x);
        }
        return R;
    }

    private String getFormatTag(String Format){
        switch (Format.toUpperCase()){
            case "{INFO}":
                return info;
            case "{PREFIX}":
                return prefix;
            case "{USERNAME}":
                return username;
            case "{MESSAGE}":
                return message;
        }
        return "null";
    }


    public HoverEvent getInfoFragment(Profile profile) {
        return (new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new ComponentBuilder("Player Information \n" + "will go here!").create()));
    }

    public HoverEvent getGuildHoverEvent(Profile profile){

    }


}
