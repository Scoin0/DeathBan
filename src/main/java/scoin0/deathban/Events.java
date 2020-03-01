package scoin0.deathban;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class Events implements Listener {

    FileConfiguration config = DeathBan.plugin.getConfig();

    String configDayBanned = "day-banned-in-ms";
    String configDayUnbanned = "day-unbanned-in-ms";

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity().getPlayer();
        ConfigurationSection section = config.getConfigurationSection(player.getUniqueId().toString());
        addBan(player.getUniqueId().toString());
        player.kickPlayer(ChatColor.RED + "You are dead.\n You can return after:\n" + ChatColor.GOLD + convertMillisToTime(getTimeDifference(section.getLong(configDayUnbanned))));
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        ConfigurationSection section = config.getConfigurationSection(player.getUniqueId().toString());
        if (config.contains(player.getUniqueId().toString())){
            if (checkIfStillBanned(player.getUniqueId().toString())){
                player.kickPlayer(ChatColor.RED + "You are dead\n"
                + "You can return after:\n"
                + ChatColor.GOLD + convertMillisToTime(getTimeDifference(section.getLong(configDayUnbanned))));
            } else {
                removeBan(player.getUniqueId().toString());
                player.setHealth(20);
            }
        } else {
            config.createSection(player.getUniqueId().toString());
            DeathBan.plugin.saveConfig();
        }
    }

    private void addBan(String player) {
        ConfigurationSection section = config.getConfigurationSection(player);
        Calendar cal = Calendar.getInstance();
        section.set(configDayBanned, cal.getTimeInMillis());
        cal.add(Calendar.DAY_OF_WEEK, config.getInt("banLength"));
        section.set(configDayUnbanned, cal.getTimeInMillis());
        DeathBan.plugin.saveConfig();
    }

    private void removeBan(String p) {
        ConfigurationSection section = config.getConfigurationSection(p);
        section.set(configDayBanned, 0);
        section.set(configDayUnbanned, 0);
        DeathBan.plugin.saveConfig();
    }

    private boolean checkIfStillBanned(String player) {
        ConfigurationSection section = config.getConfigurationSection(player);
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(section.getLong(configDayUnbanned));
        Calendar today = Calendar.getInstance();
        if (cal.after(today)){
            return true;
        }
        removeBan(player);
        return false;
    }

    private long getTimeDifference(long dayUnbanned) {
        Calendar cal = Calendar.getInstance();
        long difference = dayUnbanned - cal.getTimeInMillis();
        return difference;
    }

    private String convertMillisToTime(long millis) {
        long days = TimeUnit.MILLISECONDS.toDays(millis);
        long hours = TimeUnit.MILLISECONDS.toHours(millis) - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(millis));
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis));
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis));

        return (days != 0 ? days + " days, " : "") + (hours != 0 ? hours + " hours, " : "") + (minutes != 0 ? minutes + " minutes, " : "") + (seconds != 0 ? seconds + " seconds. " : "");

    }
}
