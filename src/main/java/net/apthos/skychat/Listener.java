package net.apthos.skychat;

import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import java.util.logging.Level;

public class Listener implements org.bukkit.event.Listener {

    @EventHandler
    public void onJoinEvent(PlayerJoinEvent e){
        Profile profile = new Profile(e.getPlayer());
        if (profile.hasDominantRank()) {
            SkyChat.getInstance().addProfile(profile);
            return;
        }
        for ( Rank rank : SkyChat.getInstance().getRanks() ){
            if (e.getPlayer().hasPermission(rank.getPermission())){
                if (profile.hasDominantRank()){
                    if (profile.getDominantRank().getPriority() > rank.getPriority())
                        continue;
                }
                profile.setDominantRank(rank);
            }
        }
        SkyChat.getInstance().addProfile(profile);
    }

    @EventHandler
    public void onLeaveEvent(PlayerQuitEvent e){
        SkyChat.getInstance().removeProfile(e.getPlayer());
    }

    @EventHandler
    public void OnChat(AsyncPlayerChatEvent e)
    {
        e.setCancelled(true);
        Profile profile = SkyChat.getInstance().getProfile(e.getPlayer());
        TextComponent CHAT_COMPONENT = profile.getDominantRank().getProcessedComponenet
                (profile, e.getMessage());
        for (Player player : Bukkit.getOnlinePlayers()){
            player.spigot().sendMessage(CHAT_COMPONENT.duplicate());
        }
        Bukkit.getLogger().log(Level.INFO, CHAT_COMPONENT.toPlainText());

    }


}
