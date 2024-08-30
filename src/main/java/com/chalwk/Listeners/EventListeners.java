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

import static com.chalwk.bot.BotInitializer.getGameManager;
import static com.chalwk.game.Game.createGameEmbed;
import static com.chalwk.game.Guess.getGuess;
import static com.chalwk.game.Guess.guessBox;

public class EventListeners extends ListenerAdapter {

    private static void updateEmbed(StringBuilder word, Game game, MessageReceivedEvent event) {

        event.getMessage().delete().queue();
        String guess_box = guessBox(word, game);

        EmbedBuilder embed = createGameEmbed(game, guess_box);
        event.getChannel()
                .retrieveMessageById(game.getEmbedID())
                .queue(message -> message.editMessageEmbeds(embed.build()).queue());
    }


    @Override
    public void onGuildReady(@NotNull GuildReadyEvent event) {
        System.out.println("""
                __________________________________________________________
                 _    _                                            ┌─────┐
                | |  | |                                           │     │
                | |__| | __ _ _ __   __ _ _ __ ___   __ _ _ __     │     O
                |  __  |/ _` | '_ \\ / _` | '_ ` _ \\ / _` | '_      │    /|\\
                | |  | | (_| | | | | (_| | | | | | | (_| | | | |   │    / \\
                |_|  |_|\\__,_|_| |_|\\__, |_| |_| |_|\\__,_|_| |_|   │
                                     __/ |                         └─────┘
                                    |___/
                __________________________________________________________""");
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {

        User player = event.getAuthor();
        if (player.isBot()) return; // ignore bots

        GameManager gameManager = getGameManager();
        if (!gameManager.isInGame(player)) return; // only players in a game can play

        Game game = gameManager.getGame(player);

        if (!game.isPlayer(player)) return; // only the players in this specific game can play

        User whos_turn = game.getWhosTurn();
        if (!player.equals(whos_turn)) {
            event.getMessage().delete().queue();
            return;
        }

        String word = game.getWordToGuess();
        String input = event.getMessage().getContentRaw().toLowerCase();

        if (input.length() > 1) {
            if (input.contentEquals(word)) {
                game.endGame(player); // guessed the whole word
            } else {
                game.mistakes++;  // incorrect guess
            }
        } else if (!getGuess(input, new StringBuilder(word), game)) {
            game.mistakes++; // incorrect guess
        }

        if (game.mistakes >= game.getMaxMistakes()) {
            game.endGame(game.getWhosTurn());
            return;
        }

        game.setWhosTurn();
        updateEmbed(new StringBuilder(word), game, event);
    }
}