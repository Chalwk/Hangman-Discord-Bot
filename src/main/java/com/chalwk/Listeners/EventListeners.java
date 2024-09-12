/* Copyright (c) 2024 Jericho Crosby <jericho.crosby227@gmail.com>. Licensed under GNU General Public License v3.0.
   See the LICENSE file or visit https://www.gnu.org/licenses/gpl-3.0.en.html for details. */

package com.chalwk.Listeners;

import com.chalwk.game.Game;
import com.chalwk.game.GameManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.chalwk.bot.BotInitializer.getGameManager;
import static com.chalwk.game.Game.createGameEmbed;
import static com.chalwk.game.Guess.formatGuessBox;
import static com.chalwk.game.Guess.getGuess;

public class EventListeners extends ListenerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(EventListeners.class);
    private static final String GAME_READY_MESSAGE = """
            __________________________________________________________
             _    _                                            ┌─────┐
            | |  | |                                           │     │
            | |__| | __ _ _ __   __ _ _ __ ___   __ _ _ __     │     O
            |  __  |/ _` | '_ \\ / _` | '_ ` _ \\ / _` | '_      │    /|\\
            | |  | | (_| | | | | (_| | | | | | | (_| | | | |   │    / \\
            |_|  |_|\\__,_|_| |_|\\__, |_| |_| |_|\\__,_|_| |_|   │
                                 __/ |                         └─────┘
                                |___/
            __________________________________________________________""";

    @Override
    public void onGuildReady(@NotNull GuildReadyEvent event) {
        logger.info(GAME_READY_MESSAGE);
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        User player = event.getAuthor();
        if (player.isBot()) return;

        GameManager gameManager = getGameManager();
        if (!gameManager.isInGame(player)) return;

        Game game = gameManager.getGame(player);
        if (!game.isPlayer(player)) return;
        if (notYourTurn(event, game, player)) return;

        handlePlayerInput(event, game, player);
    }

    private void handlePlayerInput(@NotNull MessageReceivedEvent event, Game game, User player) {
        String word = game.getWordToGuess();
        String input = event.getMessage().getContentRaw().toLowerCase();

        if (input.length() > 1) {
            if (input.contentEquals(word)) {
                game.endGame(player, null);
                return;
            } else {
                game.mistakes++;
            }
        } else if (!getGuess(input, game)) {
            game.mistakes++;
        }

        if (game.mistakes >= game.getMaxMistakes()) {
            event.getMessage().delete().queue();
            game.endGame(player, "Nobody. You lost! The word was: " + word);
            return;
        }

        String guessBox = formatGuessBox(game);
        if (word.length() == game.correctGuesses) {
            event.getMessage().delete().queue();
            game.endGame(player, null);
            return;
        }

        game.setWhosTurn();
        updateEmbed(game, event, guessBox);
    }

    private void updateEmbed(Game game, MessageReceivedEvent event, String guesses) {
        event.getMessage().delete().queue();
        EmbedBuilder embed = createGameEmbed(game, guesses);
        event.getChannel()
                .retrieveMessageById(game.getEmbedID())
                .queue(
                        message -> message.editMessageEmbeds(embed.build()).queue(),
                        throwable -> logger.error("Failed to update embed", throwable)
                );
    }

    private boolean notYourTurn(@NotNull MessageReceivedEvent event, Game game, User player) {
        User whosTurn = game.getWhosTurn();
        if (!player.equals(whosTurn)) {
            event.getMessage().delete().queue();
            return true;
        }
        return false;
    }
}