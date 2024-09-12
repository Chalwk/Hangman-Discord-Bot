/* Copyright (c) 2024 Jericho Crosby <jericho.crosby227@gmail.com>. Licensed under GNU General Public License v3.0.
   See the LICENSE file or visit https://www.gnu.org/licenses/gpl-3.0.en.html for details. */

package com.chalwk.bot;

import com.chalwk.CommandManager.CommandListener;
import com.chalwk.Listeners.EventListeners;
import com.chalwk.commands.*;
import com.chalwk.game.GameManager;
import com.chalwk.util.authentication;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class BotInitializer {

    private static final Logger logger = LoggerFactory.getLogger(BotInitializer.class);
    private static final String GAME_ACTIVITY = "GAME";
    private static final OnlineStatus BOT_STATUS = OnlineStatus.ONLINE;

    public static ShardManager shardManager;
    public static GameManager gameManager;

    private final String token;

    public BotInitializer() throws IOException {
        this.token = authentication.getToken();
    }

    public static GameManager getGameManager() {
        return gameManager;
    }

    public static ShardManager getShardManager() {
        return shardManager;
    }

    public void initializeBot() {
        try {
            gameManager = new GameManager();
            shardManager = createShardManager();
            shardManager.addEventListener(new EventListeners());
            registerCommands(shardManager);
        } catch (Exception e) {
            logger.error("Failed to initialize bot", e);
        }
    }

    private ShardManager createShardManager() {
        DefaultShardManagerBuilder builder = DefaultShardManagerBuilder.createDefault(this.token)
                .setStatus(BOT_STATUS)
                .setActivity(Activity.playing(GAME_ACTIVITY))
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .enableIntents(GatewayIntent.GUILD_MEMBERS,
                        GatewayIntent.GUILD_MESSAGES,
                        GatewayIntent.GUILD_PRESENCES,
                        GatewayIntent.MESSAGE_CONTENT);

        return builder.build();
    }

    private void registerCommands(ShardManager shardManager) {
        CommandListener commands = new CommandListener();
        commands.add(new invite(gameManager));
        commands.add(new accept(gameManager));
        commands.add(new decline(gameManager));
        commands.add(new channel(gameManager));
        commands.add(new cancel(gameManager));
        shardManager.addEventListener(commands);
    }
}