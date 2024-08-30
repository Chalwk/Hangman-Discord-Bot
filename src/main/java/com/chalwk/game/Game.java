/* Copyright (c) 2024 Jericho Crosby <jericho.crosby227@gmail.com>. Licensed under GNU General Public License v3.0.
   See the LICENSE file or visit https://www.gnu.org/licenses/gpl-3.0.en.html for details. */
package com.chalwk.game;

import com.chalwk.util.WordList;
import com.chalwk.util.settings;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.awt.*;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import static com.chalwk.game.HangmanLayout.*;

/**
 * Represents a game between two players, managing game-related operations such as starting a game and scheduling game end tasks.
 */
public class Game {

    /**
     * The user who initiated the game.
     */
    private final User invitingPlayer;
    /**
     * The user who was invited to join the game.
     */
    private final User invitedPlayer;
    /**
     * The start time of the game.
     */
    private Date startTime;

    private final SlashCommandInteractionEvent event;

    private final int hangmanLayout;

    private int mistakes = 0;

    private final String wordToGuess;
    private WordList wordList;

    /**
     * Creates a new Game instance for the specified players and assigns a GameManager.
     *
     * @param invitingPlayer the user who initiated the game
     * @param invitedPlayer  the user who was invited to join the game
     * @param event          the event that triggered the game creation
     * @param layout         the layout of the hangman game
     */
    public Game(User invitingPlayer, User invitedPlayer, SlashCommandInteractionEvent event, int layout) {
        this.event = event;
        this.invitingPlayer = invitingPlayer;
        this.invitedPlayer = invitedPlayer;
        this.hangmanLayout = layout;

        wordList = new WordList();

        this.wordToGuess = WordList.getRandomWord();

        startGame(event);

//        HangmanLayout currentLayout = getCurrentLayout();
//        String layoutGraphic = currentLayout.getLayout();
//        System.out.println(layoutGraphic);
    }

    /**
     * Starts the game, sends a notification to both players, and schedules the game end task.
     * @param event the event that triggered the game start
     */
    public void startGame(SlashCommandInteractionEvent event) {
        this.startTime = new Date();
        scheduleGameEndTask();

        String stage = getCurrentLayout().getLayout();
        event.replyEmbeds(new EmbedBuilder()
                .setTitle("Hangman Game")
                .addField("Players: ", invitingPlayer.getAsMention() + " VS " + invitedPlayer.getAsMention(), true)
                .setFooter("Guess a letter or the word: " + wordToGuess.length() + " characters")
                .addField("Stage: ", "```" + stage + "```", false)
                .addField("Characters:", "```" + "〔 〕".repeat(wordToGuess.length()) + "```", false).build()).queue();
    }

    private HangmanLayout getCurrentLayout() {
        return switch (mistakes) {
            case 0 -> hangmanLayout == 0 ? HangmanLayout.GALLOWS_8 : HangmanLayout.EXERCISE_6;
            case 1 -> hangmanLayout == 0 ? HangmanLayout.GALLOWS_7 : HangmanLayout.EXERCISE_5;
            case 2 -> hangmanLayout == 0 ? HangmanLayout.GALLOWS_6 : HangmanLayout.EXERCISE_4;
            case 3 -> hangmanLayout == 0 ? HangmanLayout.GALLOWS_5 : HangmanLayout.EXERCISE_3;
            case 4 -> hangmanLayout == 0 ? HangmanLayout.GALLOWS_4 : HangmanLayout.EXERCISE_2;
            case 5 -> hangmanLayout == 0 ? HangmanLayout.GALLOWS_3 : HangmanLayout.EXERCISE_1;
            case 6 -> HangmanLayout.GALLOWS_2;
            case 7 -> HangmanLayout.GALLOWS_1;
            default -> throw new IllegalStateException("Unsupported layout stage [" + mistakes + "]");
        };
    }

    /**
     * Schedules a task to end the game when the default time limit is reached.
     */
    private void scheduleGameEndTask() {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (isTimeUp()) {
                    this.cancel();
                    event.replyEmbeds(new EmbedBuilder()
                            .setTitle("Times up!")
                            .setDescription("Game between " + invitingPlayer.getName() + " and " + invitedPlayer.getName() + " has ended!")
                            .setColor(Color.GREEN).build()).queue();
                }
                // todo: Create checkGameState() (or checkForWinner()) method to check if a player has won the game
            }
        }, 0, 1000);
    }

    /**
     * Checks if the default time limit for the game has been exceeded.
     *
     * @return true if the time limit has been exceeded, false otherwise
     */
    private boolean isTimeUp() {
        long elapsedTime = System.currentTimeMillis() - startTime.getTime();
        return elapsedTime > settings.getDefaultTimeLimit() * 1000L;
    }

    public String getWordToGuess() {
        return wordToGuess;
    }
}