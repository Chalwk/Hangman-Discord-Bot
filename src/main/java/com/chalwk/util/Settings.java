/* Copyright (c) 2024 Jericho Crosby <jericho.crosby227@gmail.com>. Licensed under GNU General Public License v3.0.
   See the LICENSE file or visit https://www.gnu.org/licenses/gpl-3.0.en.html for details. */

package com.chalwk.util;

import com.chalwk.game.GameManager;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class Settings {

    public static final int DEFAULT_TIME_LIMIT = 300;
    private static final String SETUP_MESSAGE = """
            # Hangman is not set up.
            Please set the channel for Hangman to use first.
            Ask an admin to use the `/setchannel` command.
            """;
    private static final String CHANNEL_NOT_AVAILABLE_MESSAGE = "The required channel is not available";

    public static int getDefaultTimeLimit() {
        return DEFAULT_TIME_LIMIT;
    }

    public static boolean notCorrectChannel(SlashCommandInteractionEvent event) {
        String thisChannel = event.getChannel().getId();
        String requiredChannel = GameManager.getChannelID();

        if (requiredChannel.isEmpty()) {
            sendEphemeralReply(event, SETUP_MESSAGE);
            return true;
        } else if (!thisChannel.equals(requiredChannel)) {
            handleIncorrectChannel(event, requiredChannel);
            return true;
        }
        return false;
    }

    private static void sendEphemeralReply(SlashCommandInteractionEvent event, String message) {
        event.reply(message).setEphemeral(true).queue();
    }

    private static void handleIncorrectChannel(SlashCommandInteractionEvent event, String requiredChannel) {
        Channel channel = event.getGuild().getTextChannelById(requiredChannel);

        if (channel != null) {
            sendEphemeralReply(event, "Hangman only works in " + channel.getAsMention());
        } else {
            sendEphemeralReply(event, CHANNEL_NOT_AVAILABLE_MESSAGE);
        }
    }
}
