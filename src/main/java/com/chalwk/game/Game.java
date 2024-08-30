/* Copyright (c) 2024 Jericho Crosby <jericho.crosby227@gmail.com>. Licensed under GNU General Public License v3.0.
   See the LICENSE file or visit https://www.gnu.org/licenses/gpl-3.0.en.html for details. */
package com.chalwk.game;

import com.chalwk.util.WordList;
import com.chalwk.util.settings;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.awt.*;
import java.util.List;
import java.util.*;

import static com.chalwk.game.Guess.showGuesses;

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
    private final SlashCommandInteractionEvent event;
    private final int hangmanLayout;
    private final String wordToGuess;
    public int mistakes = 0;
    public List<Character> guesses = new ArrayList<>();
    private String embedID;
    private User whos_turn;
    private final int maxMistakes;

    /**
     * The start time of the game.
     */
    private Date startTime;

    /**
     * Creates a new Game instance for the specified players, event, and layout.
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
        this.wordToGuess = WordList.getRandomWord();
        this.whos_turn = getStartingPlayer();
        this.maxMistakes = layout == 0 ? 7 : 6;
        startGame(event);
    }

    public static EmbedBuilder createGameEmbed(Game game, String guessBox) {
        String stage = game.getCurrentLayout().getLayout();
        return new EmbedBuilder()
                .setTitle("\uD83D\uDD74 \uD80C\uDF6F Hangman \uD80C\uDF6F \uD83D\uDD74")
                .addField("Players: ", game.getInvitingPlayer().getAsMention() + " VS " + game.getInvitedPlayer().getAsMention(), true)
                .addField("Turn: ", game.getWhosTurn().getAsMention(), false)
                .addField("Stage: ", "```" + stage + "```", false)
                .addField("Characters:", guessBox != null ? guessBox : "```" + "〔 〕".repeat(game.getWordToGuess().length()) + "```", false)
                .addField("Guesses: " + showGuesses(game.guesses), " ", false)
                .setFooter("Guess a letter or the word: " + game.getWordToGuess().length() + " characters")
                .setColor(Color.BLUE);
    }

    public String getEmbedID() {
        return this.embedID;
    }

    public void setWhosTurn() {
        this.whos_turn = this.whos_turn.equals(invitingPlayer) ? invitedPlayer : invitingPlayer;
    }

    private void setEmbedID(String embedID) {
        this.embedID = embedID;
    }

    public User getWhosTurn() {
        return whos_turn;
    }

    /**
     * Starts the game, sends a notification to both players, and schedules the game end task.
     *
     * @param event the event that triggered the game start
     */
    public void startGame(SlashCommandInteractionEvent event) {
        this.startTime = new Date();
        scheduleGameEndTask();
        event.replyEmbeds(createGameEmbed(this, null).build()).queue();
        setMessageID(event);
    }

    public void endGame(User winner) {
        event.replyEmbeds(new EmbedBuilder()
                .setTitle("Game Over!")
                .setDescription("The game between " + invitingPlayer.getName() + " and " + invitedPlayer.getName() + " has ended!")
                .addField("Winner: ", winner.getAsMention(), true)
                .setColor(Color.BLUE).build()).queue();
    }

    public HangmanLayout getCurrentLayout() {
        return switch (this.mistakes) {
            case 0 -> hangmanLayout == 0 ? HangmanLayout.GALLOWS_8 : HangmanLayout.EXERCISE_6;
            case 1 -> hangmanLayout == 0 ? HangmanLayout.GALLOWS_7 : HangmanLayout.EXERCISE_5;
            case 2 -> hangmanLayout == 0 ? HangmanLayout.GALLOWS_6 : HangmanLayout.EXERCISE_4;
            case 3 -> hangmanLayout == 0 ? HangmanLayout.GALLOWS_5 : HangmanLayout.EXERCISE_3;
            case 4 -> hangmanLayout == 0 ? HangmanLayout.GALLOWS_4 : HangmanLayout.EXERCISE_2;
            case 5 -> hangmanLayout == 0 ? HangmanLayout.GALLOWS_3 : HangmanLayout.EXERCISE_1;
            case 6 -> HangmanLayout.GALLOWS_2;
            case 7 -> HangmanLayout.GALLOWS_1;
            default -> throw new IllegalStateException("Unsupported layout stage [" + this.mistakes + "]");
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

    private void setMessageID(SlashCommandInteractionEvent event) {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                setEmbedID(event.getChannel().getLatestMessageId());
            }
        }, 500);
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

    // create getter for inviting player and invited player:
    public User getInvitingPlayer() {
        return invitingPlayer;
    }

    public User getInvitedPlayer() {
        return invitedPlayer;
    }

    public User getStartingPlayer() {
        return new Random().nextBoolean() ? invitingPlayer : invitedPlayer;
    }

    public boolean isPlayer(User player) {
        return player.equals(invitingPlayer) || player.equals(invitedPlayer);
    }

    public int getMaxMistakes() {
        return this.maxMistakes;
    }
}