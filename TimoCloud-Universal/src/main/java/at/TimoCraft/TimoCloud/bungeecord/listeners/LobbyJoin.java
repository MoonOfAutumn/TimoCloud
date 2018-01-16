package at.TimoCraft.TimoCloud.bungeecord.listeners;

import at.TimoCraft.TimoCloud.bungeecord.TimoCloud;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.event.ServerKickEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LobbyJoin implements Listener {
    private Map<UUID, Boolean> pending;

    public LobbyJoin() {
        pending = new HashMap<>();
    }

    private boolean isPending(UUID uuid) {
        pending.putIfAbsent(uuid, false);
        return pending.get(uuid);
    }

    @EventHandler
    public void onPlayerConnect(PostLoginEvent event) {
        if (! TimoCloud.getInstance().getFileManager().getConfig().getBoolean("useFallback")) {
            return;
        }
        pending.put(event.getPlayer().getUniqueId(), true);
    }

    @EventHandler
    public void onServerChange(ServerConnectEvent event) {
        if (! isPending(event.getPlayer().getUniqueId())) {
            return;
        }
        ProxiedPlayer player = event.getPlayer();
        ServerInfo info = TimoCloud.getInstance().getLobbyManager().getFreeLobby(player.getUniqueId());
        if (info == null) {
            TimoCloud.severe("No lobby server found.");
            return;
        }
        event.setTarget(info);
        pending.put(player.getUniqueId(), false);
    }

    @EventHandler
    public void onServerKick(ServerKickEvent event) {
        event.setCancelled(true);
        event.setCancelServer(TimoCloud.getInstance().getLobbyManager().getFreeLobby(event.getPlayer().getUniqueId()));
    }
}