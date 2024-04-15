package net.dogcraft.dogcraftmessagesync;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

public class PluginMessage implements PluginMessageListener {
  private final Dogcraft_MessageSync plugin;
  private final String channelName = "BungeeCord";
  private final String subChannel = "DCMsgSync";

  public PluginMessage(Dogcraft_MessageSync plugin) {
    this.plugin = plugin;
    plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, channelName);
    plugin.getServer().getMessenger().registerIncomingPluginChannel(plugin, channelName, this);
  }

  @Override
  public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, @NotNull byte[] bytes) {
    if (!channel.equals(channelName)) {
      // Channel does not match our channel we are using
      return;
    }

    ByteArrayDataInput in = ByteStreams.newDataInput(bytes);
    String sub = in.readUTF();

    if (!sub.equals(subChannel)) {
      // Sub channel does not match the one we are using
      return;
    }

    String message = "";

    short len = in.readShort();
    byte[] msgbytes = new byte[len];
    in.readFully(msgbytes);

    DataInputStream msgIN = new DataInputStream(new ByteArrayInputStream(msgbytes));

    try {
      message = msgIN.readUTF();
    }catch (IOException e) {
      plugin.log(e.getMessage());
    }

    if (!message.isEmpty()) {
      plugin.getServer().broadcast(JSONComponentSerializer.json().deserialize(message));
    }
  }

  public void sendPluginMessage(Player player, String message) {
    ByteArrayDataOutput out = ByteStreams.newDataOutput();
    out.writeUTF("Forward");
    out.writeUTF("ALL");
    out.writeUTF(subChannel);

    ByteArrayOutputStream msgBytes = new ByteArrayOutputStream();
    DataOutputStream msgOut = new DataOutputStream(msgBytes);

    try {
      msgOut.writeUTF(message);
    }catch (IOException e) {
      plugin.log(e.getMessage());
    }

    out.writeShort(msgBytes.toByteArray().length);
    out.write(msgBytes.toByteArray());
    player.sendPluginMessage(plugin, channelName, out.toByteArray());
  }
}
