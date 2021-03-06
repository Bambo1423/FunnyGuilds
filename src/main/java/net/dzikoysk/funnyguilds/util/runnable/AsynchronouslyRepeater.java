package net.dzikoysk.funnyguilds.util.runnable;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.data.Settings;
import net.dzikoysk.funnyguilds.system.ban.BanSystem;
import net.dzikoysk.funnyguilds.system.validity.ValiditySystem;
import net.dzikoysk.funnyguilds.util.element.tablist.AbstractTablist;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

public class AsynchronouslyRepeater implements Runnable {

    private static AsynchronouslyRepeater instance;
    private final FunnyGuilds plugin;
    private volatile BukkitTask repeater;
    private int player_list;
    private int ban_system;
    private int validity_system;
    /*private int funnyguilds_stats;*/
    private int player_list_time;

    public AsynchronouslyRepeater(FunnyGuilds plugin) {
        this.plugin = plugin;
        instance = this;
        player_list_time = Settings.getConfig().playerlistInterval;
    }

    public static AsynchronouslyRepeater getInstance() {
        try {
            if (instance == null) {
                throw new UnsupportedOperationException("AsynchronouslyRepeater is not setup!");
            }
            return instance;
        } catch (Exception ex) {
            if (FunnyGuilds.exception(ex.getCause())) {
                ex.printStackTrace();
            }
            return null;
        }
    }

    public void start() {
        if (this.repeater != null) {
            return;
        }
        this.repeater = this.plugin.getServer().getScheduler().runTaskTimerAsynchronously(this.plugin, this, 100, 20);
    }

    @Override
    public void run() {
        player_list++;
        ban_system++;
        validity_system++;
        /*funnyguilds_stats++;*/

        if (validity_system >= 10) {
            validitySystem();
        }
        if (ban_system >= 7) {
            banSystem();
        }

        if (Settings.getConfig().playerlistEnable) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (!AbstractTablist.hasTablist(player)) {
                    AbstractTablist.createTablist(Settings.getConfig().playerList, Settings.getConfig().playerListHeader, Settings.getConfig().playerListFooter, Settings.getConfig().playerListPing, player);
                }
                final AbstractTablist tablist = AbstractTablist.getTablist(player);
                tablist.send();
            }
        }

        /*if (funnyguilds_stats >= 10) {
            funnyguildsStats();
        }*/
    }

    private void validitySystem() {
        ValiditySystem.getInstance().run();
        validity_system = 0;
    }

    /*private void funnyguildsStats() {
        MetricsCollector.getMetrics();
        funnyguilds_stats = 0;
    }*/

    private void banSystem() {
        BanSystem.getInstance().run();
        ban_system = 0;
    }

    public void reload() {
        player_list_time = Settings.getConfig().playerlistInterval;
    }

    public void stop() {
        if (repeater != null) {
            repeater.cancel();
            repeater = null;
        }
    }

}
