/* Copyright (c) 2024 Jericho Crosby <jericho.crosby227@gmail.com>. Licensed under GNU General Public License v3.0.
   See the LICENSE file or visit https://www.gnu.org/licenses/gpl-3.0.en.html for details. */

package com.chalwk.util;

import com.chalwk.game.GameManager;
import com.chalwk.util.Logging.Logger;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

public class FileIO {

    private static final String CONFIG_FILE = "/config.txt";

    public static String loadChannelID() {
        try (Stream<String> lines = Files.lines(getConfigFilePath())) {
            return lines.filter(line -> !line.trim().isEmpty())
                    .findFirst()
                    .map(String::trim)
                    .orElse("");
        } catch (IOException | URISyntaxException e) {
            Logger.info("Failed to load Channel ID: " + e.getMessage());
            return null;
        }
    }

    public static void saveChannelID(String channelID, boolean isAddOperation, SlashCommandInteractionEvent event, GameManager gameManager) {
        try {
            Path filePath = getConfigFilePath();
            List<String> lines = Files.readAllLines(filePath);

            if (isAddOperation) {
                lines.add(channelID);
                event.reply("## Channel ID saved!").setEphemeral(true).queue();
                gameManager.setChannelID(channelID);
            } else {
                lines.removeIf(line -> line.trim().equals(channelID));
                event.reply("## Channel ID removed!").setEphemeral(true).queue();
                gameManager.setChannelID("");
            }

            Files.writeString(filePath, String.join("\n", lines));
        } catch (URISyntaxException | IOException e) {
            Logger.info("Failed to save Channel ID: " + e.getMessage());
            event.reply("## Failed to save channel ID!").setEphemeral(true).queue();
        }
    }

    public static boolean isChannelIdConfigured(String channelID) {
        if (channelID == null) {
            return false;
        } else if (!GameManager.getChannelID().isEmpty()) {
            return true;
        }

        try (Stream<String> lines = Files.lines(getConfigFilePath())) {
            return lines.anyMatch(line -> line.trim().equals(channelID));
        } catch (IOException | URISyntaxException e) {
            Logger.info("Failed to read data: " + e.getMessage());
            return false;
        }
    }

    private static Path getConfigFilePath() throws URISyntaxException {
        URI fileUri = FileIO.class.getResource(CONFIG_FILE).toURI();
        return Paths.get(fileUri);
    }
}
