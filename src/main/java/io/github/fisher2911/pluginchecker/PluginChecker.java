package io.github.fisher2911.pluginchecker;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class PluginChecker extends JavaPlugin {

    private String errorMessage = "";

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        final FileConfiguration config = this.getConfig();
        this.errorMessage = config.getString("not-installed-message");

        final ConfigurationSection pluginSection = config.getConfigurationSection("required-plugins");

        if (pluginSection == null) {
            return;
        }


        final PluginManager pluginManager = this.getServer().getPluginManager();
        final Logger logger = this.getLogger();

        Bukkit.getScheduler().runTaskLater(this,
                () -> {
                    boolean disable = false;
                    for (final String plugin : pluginSection.getKeys(false)) {
                        final Plugin check = pluginManager.getPlugin(plugin);

                        if (check == null) {
                            String downloadLink = pluginSection.getString(plugin + ".download-link");

                            if (downloadLink == null) {
                                downloadLink = "";
                            }

                            logger.severe(errorMessage.replace("%plugin%", plugin).
                                    replace("%link%", downloadLink));
                            disable = disable || pluginSection.getBoolean(plugin + ".stop-server");
                        }
                    }

                    if (disable) {
                        Bukkit.shutdown();
                    }
                },
                20);
    }

    @Override
    public void onDisable() {

    }
}
