/* Copyright (c) 2024 Jericho Crosby <jericho.crosby227@gmail.com>. Licensed under GNU General Public License v3.0.
   See the LICENSE file or visit https://www.gnu.org/licenses/gpl-3.0.en.html for details. */
package com.chalwk.commands;

import com.chalwk.CommandManager.CommandCooldownManager;
import com.chalwk.CommandManager.CommandInterface;
import com.chalwk.game.GameManager;
import com.chalwk.game.HangmanLayout;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.ArrayList;
import java.util.List;

public class invite implements CommandInterface {

    /**
     * The cooldown manager for the command.
     */
    private static final CommandCooldownManager COOLDOWN_MANAGER = new CommandCooldownManager();

    /**
     * The game manager for managing game operations.
     */
    private final GameManager gameManager;

    /**
     * Initializes the invite command instance with the provided GameManager.
     *
     * @param gameManager the GameManager managing game operations
     */
    public invite(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    public String getName() {
        return "invite";
    }

    @Override
    public String getDescription() {
        return "Invite a player to a game";
    }

    @Override
    public List<OptionData> getOptions() {

        List<OptionData> options = new ArrayList<>();
        options.add(new OptionData(OptionType.USER, "opponent", "The user to invite", true));
        OptionData option = new OptionData(OptionType.INTEGER, "layout", "The hangman layout you want to use", true);

        option.addChoice("Gallows", 0);
        option.addChoice("Exercise", 1);

        options.add(option);
        return options;
    }

    /**
     * Executes the invite command when called.
     *
     * @param event the event associated with the command execution
     */
    @Override
    public void execute(SlashCommandInteractionEvent event) {
        if (COOLDOWN_MANAGER.isOnCooldown(event)) return;

        OptionMapping layoutOption = event.getOption("layout");
        User userToInvite = event.getOption("opponent").getAsUser();
        User invitingPlayer = event.getUser();

        int layout;
        int layoutIndex = layoutOption.getAsInt();
        layout = switch (layoutIndex) {
            case 0 -> 0;
            case 1 -> 1;
            default -> 0;
        };

        gameManager.invitePlayer(invitingPlayer, userToInvite, layout, event);

        COOLDOWN_MANAGER.setCooldown("invite", event.getUser());
    }
}