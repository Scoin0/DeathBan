package scoin0.deathban;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class DeathBan extends JavaPlugin {

    FileConfiguration config = this.getConfig();
    public static DeathBan plugin;

    @Override
    public void onEnable() {
        plugin = this;
        getServer().getPluginManager().registerEvents(new Events(), this);
        config.options().header("Ban Length in Days");
        config.addDefault("banLength", 3);
        config.options().copyDefaults(true);
        saveConfig();
    }
}
