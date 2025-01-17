package me.alexdevs.solstice.commands;

import com.mojang.brigadier.CommandDispatcher;
import me.alexdevs.solstice.commands.admin.*;
import me.alexdevs.solstice.commands.fun.*;
import me.alexdevs.solstice.commands.home.*;
import me.alexdevs.solstice.commands.misc.*;
import me.alexdevs.solstice.commands.moderation.*;
import me.alexdevs.solstice.commands.teleport.*;
import me.alexdevs.solstice.commands.spawn.DelSpawnCommand;
import me.alexdevs.solstice.commands.spawn.SetSpawnCommand;
import me.alexdevs.solstice.commands.spawn.SpawnCommand;
import me.alexdevs.solstice.commands.tell.ReplyCommand;
import me.alexdevs.solstice.commands.tell.TellCommand;
import me.alexdevs.solstice.commands.utilities.*;
import me.alexdevs.solstice.commands.warp.DeleteWarpCommand;
import me.alexdevs.solstice.commands.warp.SetWarpCommand;
import me.alexdevs.solstice.commands.warp.WarpCommand;
import me.alexdevs.solstice.commands.warp.WarpsCommand;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.ServerCommandSource;

public class CommandInitializer {
    private static CommandDispatcher<ServerCommandSource> dispatcher;
    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            CommandInitializer.dispatcher = dispatcher;

            SolsticeCommand.register(dispatcher);
            SudoCommand.register(dispatcher);
            BroadcastCommand.register(dispatcher);
            TimeBarCommand.register(dispatcher);
            RestartCommand.register(dispatcher);

            AfkCommand.register(dispatcher);

            TellCommand.register(dispatcher);
            ReplyCommand.register(dispatcher);

            TeleportAskCommand.register(dispatcher);
            TeleportAskHereCommand.register(dispatcher);
            TeleportAcceptCommand.register(dispatcher);
            TeleportDenyCommand.register(dispatcher);
            BackCommand.register(dispatcher);
            TeleportOfflineCommand.register(dispatcher);
            TeleportHereCommand.register(dispatcher);

            FlyCommand.register(dispatcher);
            GodCommand.register(dispatcher);
            SmiteCommand.register(dispatcher);
            NicknameCommand.register(dispatcher);
            HealCommand.register(dispatcher);
            FeedCommand.register(dispatcher);
            HatCommand.register(dispatcher);

            SetSpawnCommand.register(dispatcher);
            DelSpawnCommand.register(dispatcher);
            SpawnCommand.register(dispatcher);

            HomeCommand.register(dispatcher);
            SetHomeCommand.register(dispatcher);
            DeleteHomeCommand.register(dispatcher);
            HomesCommand.register(dispatcher);
            HomeOther.register(dispatcher);

            WarpCommand.register(dispatcher);
            SetWarpCommand.register(dispatcher);
            DeleteWarpCommand.register(dispatcher);
            WarpsCommand.register(dispatcher);

            MuteCommand.register(dispatcher);
            BanCommand.register(dispatcher);
            TempBanCommand.register(dispatcher);
            UnbanCommand.register(dispatcher);
            KickCommand.register(dispatcher);

            IgnoreCommand.register(dispatcher);
            IgnoreListCommand.register(dispatcher);

            NearCommand.register(dispatcher);
            MailCommand.register(dispatcher);
            SeenCommand.register(dispatcher);
            MotdCommand.register(dispatcher);
            InfoCommand.register(dispatcher);
            RulesCommand.register(dispatcher);
            SuicideCommand.register(dispatcher);
            ExtinguishCommand.register(dispatcher);
            IgniteCommand.register(dispatcher);

            AnvilCommand.register(dispatcher);
            CartographyCommand.register(dispatcher);
            EnderchestCommand.register(dispatcher);
            GrindstoneCommand.register(dispatcher);
            InventorySeeCommand.register(dispatcher);
            LoomCommand.register(dispatcher);
            SmithingCommand.register(dispatcher);
            StonecutterCommand.register(dispatcher);
            TrashCommand.register(dispatcher);
            WorkbenchCommand.register(dispatcher);

        });
    }

    public static void removeCommands(String... commandNames) {
        for (String commandName : commandNames) {
            var command = dispatcher.getRoot().getChild(commandName);
            if (command != null) {
                dispatcher.getRoot().getChildren().remove(command);
            }
        }
    }
}
