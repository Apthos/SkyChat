package net.apthos.skychat;

import net.apthos.guilds.Guilds;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.*;

public final class SkyChat extends JavaPlugin implements CommandExecutor {

    private static SkyChat instance;

    private Set<Rank> Ranks = new HashSet<>();
    private Set<Profile> Profiles = new HashSet<>();

    private Guilds guildsPlugin;

    @Override
    public void onEnable() {
        guildsPlugin = Guilds.getInstance();
        this.instance = this;
        this.getServer().getPluginManager().registerEvents(new Listener(), this);
        saveResource("ranks.yml", false);
        saveResource("settings.yml", false);
        YamlConfiguration YAML = YamlConfiguration.loadConfiguration
                (new File(getDataFolder() + "/ranks.yml"));

        for (String rank : YAML.getConfigurationSection("ranks").getKeys(false)) {
            Ranks.add(new Rank(rank));
        }
        profilePlayers();
    }

    public void profilePlayers() {
        Profiles.clear();
        for (Player player : Bukkit.getOnlinePlayers()) {
            Profile profile = new Profile(player);
            if (profile.hasDominantRank()) {
                addProfile(profile);
                continue;
            }
            for (Rank rank : getRanks()) {
                if (player.hasPermission(rank.getPermission())) {
                    if (profile.hasDominantRank()) {
                        if (profile.getDominantRank().getPriority() > rank.getPriority())
                            continue;
                    }
                    profile.setDominantRank(rank);
                }
            }
            addProfile(profile);
        }
    }

    public static SkyChat getInstance() {
        return instance;
    }

    public Set<Rank> getRanks() {
        return this.Ranks;
    }

//    public Guild getGuild(Player player){
//        this.guildsPlugin.
//    }

    public boolean hasRank(String RankName) {
        for (Rank rank : getRanks()) {
            if (rank.getRankName().equalsIgnoreCase(RankName))
                return true;
        }
        return false;
    }

    public Rank getRank(String RankName) {
        for (Rank rank : getRanks()) {
            if (rank.getRankName().equalsIgnoreCase(RankName)) {
                return rank;
            }
        }
        return null;
    }

    public Player getPlayer(String playerName) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (playerName.equalsIgnoreCase(player.getName()))
                return player;
        }
        return null;
    }

    public Profile getProfile(Player player) {
        for (Profile profile : Profiles) {
            if (profile.getPlayer().getUniqueId().equals(player.getUniqueId()))
                return profile;
        }
        return null;
    }

    public Set<Profile> getAllProfiles(){
        return this.Profiles;
    }

    public void addProfile(Profile profile) {
        Profiles.add(profile);
    }

    public void removeProfile(Player player) {
        for (Profile profile : Profiles) {
            if (profile.getPlayer().getName().equals(player.getName()))
                Profiles.remove(profile);
        }
    }

    public Guilds getGuildsPlugin(){
        return this.guildsPlugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd,
                             String desc, String[] args) {

        if (cmd.getName().equalsIgnoreCase("nick")){
            if (sender instanceof Player){
                if (args.length == 0){
                    sender.sendMessage(ChatColor.RED + "Usage: /Nick [NICK] {PLAYER}");
                    return true;
                }
                if (args.length == 1){
                    Profile profile = getProfile((Player) sender);
                    profile.setNick(args[0]);
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "" +
                            "&a&lSkyChat&a: &c" + args[0] + " &ahas been set as your nick!"
                    ));
                    return true;
                }
            } else {

            }
        }

        if (cmd.getName().equalsIgnoreCase("Discord")) {
            if (sender instanceof Player) {
                TextComponent component = new TextComponent();
                component.addExtra(new TextComponent
                        (ChatColor.BLUE + ChatColor.BOLD.toString() + " ✔ Join our " +
                                "discord! "));
                TextComponent linkComponent = new TextComponent("[Click Me]");
                linkComponent.setColor(net.md_5.bungee.api.ChatColor.RED);
                linkComponent.setBold(true);
                linkComponent.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL,
                        "https://discord.gg/8hKTasu"));
                component.addExtra(linkComponent);
                Player player = (Player) sender;
                player.spigot().sendMessage(component);
                return true;
            }
            sender.sendMessage(ChatColor.RED + "This is an In-Game Command!");
            return true;
        }

        if (cmd.getName().equalsIgnoreCase("RankList")) {
            LinkedList<Rank> SortedRanks = sortRanks();
            sender.sendMessage(ChatColor.RED + "<==== " + ChatColor.GREEN + "Rank List "
                    + ChatColor.RED + "================>");
            for (Rank rank : SortedRanks) {
                sender.sendMessage("   " + ChatColor.GREEN + String.valueOf(rank
                        .getPriority()) + " : " + ChatColor.RED + rank.getRankName());
            }
            sender.sendMessage(ChatColor.RED + "<==== " + ChatColor.GREEN + "Rank List "
                    + ChatColor.RED + "================>");
            return true;
        }

        if (cmd.getName().equalsIgnoreCase("Rank")) {
            if (args.length == 0) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "" +
                        "&c<==== &aRank Command &c=============>\n" +
                        "&eSub-Commands:\n" +
                        "  &6Select\n" +
                        "  &6Create\n" +
                        "  &6Edit\n" +
                        "  &6Delete\n" +
                        "&c<==== &aRank Command &c=============>"));
                return true;
            }

            if (args[0].equalsIgnoreCase("Select")) {
                if (! sender.hasPermission("SkyChat.OP")) {
                    sender.sendMessage(ChatColor.RED + "Insufficient Permissions!");
                    return true;
                }
                if (args.length == 1) {
                    sender.sendMessage
                            (ChatColor.RED + "Usage: /Rank select [Rank] {Player}");
                    return true;
                }
                if (! hasRank(args[1]) && ! args[1].equalsIgnoreCase("-")) {
                    sender.sendMessage(ChatColor.RED + "Error: Rank does not exist!");
                    return true;
                }
                Rank rank = getRank(args[1]);
                if (args.length == 2) {
                    if (sender instanceof Player) {
                        Profile profile = getProfile((Player) sender);
                        if (args[1].equalsIgnoreCase("-")) {
                            profile.destroyDominantSelection();
                            profilePlayers();
                        }
                        profile.setDominantSelection(rank);
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "" +
                                "&c&l" + rank.getRankName()
                                + " &ahas been set as your Dominant Rank!"));
                        return true;
                    }
                }
                if (args.length >= 3) {
                    if (getPlayer(args[2]) != null) {
                        Player player = getPlayer(args[2]);
                        Profile profile = getProfile(player);
                        if (args[1].equalsIgnoreCase("-")) {
                            player.sendMessage(
                                    ChatColor.translateAlternateColorCodes('&', "&c&l" +
                                            sender + " &ahas has un-set your Dominant" +
                                            " Selection"));
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                    "" +
                                    "&c&l" + player.getName() + "&a's Dominant Selection " +
                                            "has been unset!"));
                            profile.destroyDominantSelection();
                            profilePlayers();
                            return true;
                        }
                        player.sendMessage(
                                ChatColor.translateAlternateColorCodes('&', "&c&l" +
                                        rank.getRankName() + " &ahas been set as your " +
                                        "Dominant Rank"
                                        + " by &c&l" + sender.getName()));
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "" +
                                "&c&l" + rank.getRankName() + "&a has been set as &c&l" +
                                player.getName() + "&a's Dominant Rank!"));
                        profile.setDominantSelection(rank);
                        return true;
                    }
                    sender.sendMessage(ChatColor.RED + "Error: player not found!");
                    return true;
                }
            } // -- END OF SELECTION BLOCK -- //

            if (args[0].equalsIgnoreCase("Create")) ;

        }


        return true;
    }

    private LinkedList<Rank> sortRanks() {
        LinkedList<Rank> SortedRanks = new LinkedList<>();
        Set<Rank> CopiedSet = new HashSet<>();
        for (Rank rank : getRanks()) {
            CopiedSet.add(rank);
        }
        while (! CopiedSet.isEmpty()) {
            Rank Hrank = null;
            for (Rank rank : CopiedSet) {
                if (Hrank == null) Hrank = rank;
                if (rank.getPriority() > Hrank.getPriority()) {
                    Hrank = rank;
                }
            }
            CopiedSet.remove(Hrank);
            SortedRanks.add(Hrank);
        }
        return SortedRanks;
    }

}
