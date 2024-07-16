package com.craftaro.core.locale;

import com.craftaro.core.SongodaCore;
import com.craftaro.core.chat.AdventureUtils;
import com.craftaro.core.chat.MiniMessagePlaceholder;
import com.craftaro.core.compatibility.ServerVersion;
import com.craftaro.core.utils.TextUtils;
import net.kyori.adventure.Adventure;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;

/**
 * The Message object. This holds the message to be sent
 * as well as the plugins prefix so that they can both be
 * easily manipulated then deployed
 */
public class Message {
    private static boolean canActionBar = false;

    static {
        try {
            Class.forName("net.md_5.bungee.api.ChatMessageType");
            Class.forName("net.md_5.bungee.api.chat.TextComponent");
            Player.Spigot.class.getDeclaredMethod("sendMessage", net.md_5.bungee.api.ChatMessageType.class, net.md_5.bungee.api.chat.TextComponent.class);

            canActionBar = true;
        } catch (Exception ignore) {
        }
    }

    private Component prefix = null;
    private Component message;

    /**
     * create a new message
     *
     * @param message the message text
     */
    public Message(String message) {
        this.message = AdventureUtils.formatComponent(message);
    }

    /**
     * Format and send the held message to a player
     *
     * @param player player to send the message to
     */
    public void sendMessage(Player player) {
        sendMessage((CommandSender) player);
    }

    /**
     * Format and send the held message to a player
     *
     * @param sender command sender to send the message to
     */
    public void sendMessage(CommandSender sender) {
        AdventureUtils.sendMessage(SongodaCore.getHijackedPlugin(), this.message, sender);
    }

    /**
     * Format and send the held message to a player as a title messagexc
     *
     * @param sender command sender to send the message to
     */
    public void sendTitle(CommandSender sender) {
        if (sender instanceof Player) {
            if (ServerVersion.isServerVersionAtLeast(ServerVersion.V1_11)) {
                AdventureUtils.sendTitle(SongodaCore.getHijackedPlugin(), AdventureUtils.createTitle(Component.empty(), getMessage(), 10, 30, 10), sender);
                return;
            }

            AdventureUtils.sendTitle(SongodaCore.getHijackedPlugin(), AdventureUtils.createTitle(Component.empty(), getMessage()), sender);

            return;
        }

        AdventureUtils.sendMessage(SongodaCore.getHijackedPlugin(), this.message, sender);
    }

    /**
     * Format and send the held message to a player as an actionbar message
     *
     * @param sender command sender to send the message to
     */
    public void sendActionBar(CommandSender sender) {
        if (!(sender instanceof Player)) {
            AdventureUtils.sendMessage(SongodaCore.getHijackedPlugin(), this.message, sender);
            return;
        }

        if (!canActionBar) {
            sendTitle(sender);
            return;
        }

        AdventureUtils.sendActionBar(SongodaCore.getHijackedPlugin(), getMessage(), sender);
    }

    /**
     * Format and send the held message with the
     * appended plugin prefix to a command sender
     *
     * @param sender command sender to send the message to
     */
    public void sendPrefixedMessage(CommandSender sender) {
        AdventureUtils.sendMessage(SongodaCore.getHijackedPlugin(), this.prefix.append(this.message), sender);
    }

    /**
     * Format the held message and append the plugins
     * prefix
     *
     * @return the prefixed message
     */
    public Component getPrefixedMessage() {
        return this.prefix.append(this.message);
    }

    /**
     * Get and format the held message
     *
     * @return the message
     */
    public Component getMessage() {
        return this.message;
    }

    /**
     * Get and format the held message
     *
     * @return the message
     */
    public List<Component> getMessageLines() {
        //return Arrays.asList(ChatColor.translateAlternateColorCodes('&', this.message.toText()).split("[\n|]"));
        return AdventureUtils.splitComponent(this.message, '\n');
    }

    /**
     * Replace the provided placeholder with the provided object. <br />
     * Interchangeably Supports {@code %value%} and {@code {value}}
     *
     * @param placeholder the placeholder to replace
     * @param replacement the replacement object
     *
     * @return the modified Message
     */
    public Message processPlaceholder(String placeholder, Object replacement) {
        MiniMessagePlaceholder miniMessagePlaceholder = new MiniMessagePlaceholder(placeholder, replacement == null ? "" : replacement.toString());
        this.message = AdventureUtils.formatPlaceholder(this.message, miniMessagePlaceholder);

        return this;
    }

    Message setPrefix(String prefix) {
        this.prefix = AdventureUtils.formatComponent(prefix + " ");
        return this;
    }

    @Override
    public String toString() {
        return AdventureUtils.toLegacy(this.message);
    }

    public String toText() {
        return AdventureUtils.toLegacy(this.message);
    }
}
