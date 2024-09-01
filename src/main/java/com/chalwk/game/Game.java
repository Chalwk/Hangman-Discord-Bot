/* Copyright (c) 2024 Jericho Crosby <jericho.crosby227@gmail.com>. Licensed under GNU General Public License v3.0.
   See the LICENSE file or visit https://www.gnu.org/licenses/gpl-3.0.en.html for details. */
package com.chalwk.game;

import com.chalwk.util.WordList;
import com.chalwk.util.settings;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.awt.*;
import java.util.List;
import java.util.*;

import static com.chalwk.bot.BotInitializer.getShardManager;
import static com.chalwk.game.Guess.showGuesses;

/**
 * Represents a game between two players, managing game-related operations such as starting a game and scheduling game end tasks.
 */
public class Game {

    private final User invitingPlayer;
    private final User invitedPlayer;
    private final int hangmanLayout;
    private final String wordToGuess;
    private final int maxMistakes;
    private final GameManager gameManager;
    public int mistakes = 0;
    public int correctGuesses = 0;
    public List<Character> guesses = new ArrayList<>();
    private String embedID;
    private User whos_turn;
    private Date startTime;
    private TimerTask gameEndTask;

    /**
     * Creates a new Game instance for the specified players, event, and layout.
     *
     * @param invitingPlayer the user who initiated the game
     * @param invitedPlayer  the user who was invited to join the game
     * @param event          the event that triggered the game creation
     * @param layout         the layout of the hangman game
     */
    public Game(User invitingPlayer, User invitedPlayer, SlashCommandInteractionEvent event, int layout, GameManager gameManager) {
        this.invitingPlayer = invitingPlayer;
        this.invitedPlayer = invitedPlayer;
        this.hangmanLayout = layout;
        this.wordToGuess = WordList.getRandomWord();
        this.whos_turn = getStartingPlayer();
        this.maxMistakes = layout == 0 ? 7 : 6;
        this.gameManager = gameManager;
        this.correctGuesses = 0;
        startGame(event);
    }

    /**
     * Creates an embed for the game with the specified game and guess box.
     *
     * @param game     the game to create an embed for
     * @param guessBox the guess box to display in the embed
     * @return the embed for the game
     */
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

    /**
     * Gets the ID of the message embed for the game.
     *
     * @return the ID of the message embed for the game
     */
    public String getEmbedID() {
        return this.embedID;
    }

    /**
     * Sets the ID of the message embed for the game.
     *
     * @param embedID the ID of the message embed for the game
     */
    private void setEmbedID(String embedID) {
        this.embedID = embedID;
    }

    /**
     * Sets the player whose turn it is to play.
     */
    public void setWhosTurn() {
        this.whos_turn = this.whos_turn.equals(invitingPlayer) ? invitedPlayer : invitingPlayer;
    }

    /**
     * Gets the player whose turn it is to play.
     *
     * @return the player whose turn it is to play
     */
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

    /**
     * Ends the game, sends a notification to both players, and removes the game from the game manager.
     *
     * @param winner the winner of the game
     */
    public void endGame(User winner, String nobody) {
        String channelID = GameManager.getChannelID();
        TextChannel channel = getShardManager().getTextChannelById(channelID);
        cancelGameEndTask();
        String result = nobody != null ? nobody : winner.getAsMention();

        channel.sendMessageEmbeds(new EmbedBuilder()
                .setTitle("Game Over!")
                .setDescription("The game between " + invitingPlayer.getName() + " and " + invitedPlayer.getName() + " has ended!")
                .addField("Winner: ", result, true)
                .setColor(Color.BLUE).build()).queue();

        gameManager.removeGame(invitingPlayer, invitedPlayer);
    }

    /**
     * Updates the game state based on the number of mistakes made.
     */
    public HangmanLayout getCurrentLayout() {
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
        if (gameEndTask != null) {
            gameEndTask.cancel();
        }
        gameEndTask = new TimerTask() {
            @Override
            public void run() {
                if (isTimeUp()) {
                    this.cancel();
                    String channelID = GameManager.getChannelID();
                    TextChannel channel = getShardManager().getTextChannelById(channelID);
                    channel.sendMessage("Times up! Game between " + invitingPlayer.getAsMention() + " and " + invitedPlayer.getAsMention() + " has ended!").queue();
                }
            }
        };

        Timer gameEndTimer = new Timer();
        gameEndTimer.scheduleAtFixedRate(gameEndTask, 0, 1000);
    }

    /**
     * Sets the ID of the message embed for the game after a delay.
     *
     * @param event the event associated with the command execution
     */
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

    /**
     * Gets the word to guess in the game.
     *
     * @return the word to guess in the game
     */
    public String getWordToGuess() {
        return wordToGuess;
    }

    /**
     * Gets the player who initiated the game.
     *
     * @return the player who initiated the game
     */
    public User getInvitingPlayer() {
        return invitingPlayer;
    }

    /**
     * Gets the player who was invited to join the game.
     *
     * @return the player who was invited to join the game
     */
    public User getInvitedPlayer() {
        return invitedPlayer;
    }

    /**
     * Gets the player who starts the game.
     *
     * @return the player who starts the game
     */
    public User getStartingPlayer() {
        return new Random().nextBoolean() ? invitingPlayer : invitedPlayer;
    }

    /**
     * Checks if the specified player is in the game.
     *
     * @param player the player to check
     * @return true if the player is in the game, false otherwise
     */
    public boolean isPlayer(User player) {
        return player.equals(invitingPlayer) || player.equals(invitedPlayer);
    }

    private void cancelGameEndTask() {
        if (gameEndTask != null) {
            gameEndTask.cancel();
            gameEndTask = null;
        }
    }

    /**
     * Gets the maximum number of mistakes allowed in the game.
     *
     * @return the maximum number of mistakes allowed in the game
     */
    public int getMaxMistakes() {
        return this.maxMistakes;
    }
}