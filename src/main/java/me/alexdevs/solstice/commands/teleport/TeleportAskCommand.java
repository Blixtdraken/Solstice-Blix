package me.alexdevs.solstice.commands.teleport;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.util.Format;
import me.alexdevs.solstice.core.TeleportTracker;
import me.alexdevs.solstice.util.Components;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import eu.pb4.placeholders.api.PlaceholderContext;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.Map;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class TeleportAskCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        var requirement = Permissions.require("solstice.command.tpa", true);
        var node = dispatcher.register(literal("tpa")
                .requires(requirement)
                .then(argument("player", StringArgumentType.word())
                        .suggests((context, builder) -> {
                            var playerManager = context.getSource().getServer().getPlayerManager();
                            return CommandSource.suggestMatching(
                                    playerManager.getPlayerNames(),
                                    builder);
                        })
                        .executes(context -> {
                            execute(context);
                            return 1;
                        })));

        dispatcher.register(literal("tpask").requires(requirement).redirect(node));
    }

    private static void execute(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        var source = context.getSource();
        var player = context.getSource().getPlayerOrThrow();

        var server = source.getServer();
        var targetName = StringArgumentType.getString(context, "player");
        var playerManager = server.getPlayerManager();
        var target = playerManager.getPlayer(targetName);
        var playerContext = PlaceholderContext.of(player);
        if (target == null) {
            var placeholders = Map.of(
                    "targetPlayer", Text.of(targetName)
            );
            source.sendFeedback(() -> Format.parse(
                    Solstice.locale().commands.teleportRequest.playerNotFound,
                    playerContext,
                    placeholders
            ), false);
            return;
        }

        // TODO: make tpa and tpahere respect ignore list of target

        var request = new TeleportTracker.TeleportRequest(player.getUuid(), target.getUuid());
        var targetRequests = TeleportTracker.teleportRequests.get(target.getUuid());
        targetRequests.addLast(request);
        var targetContext = PlaceholderContext.of(target);
        var placeholders = Map.of(
                "requesterPlayer", player.getDisplayName(),
                "acceptButton", Components.button(
                        Solstice.locale().commands.common.accept,
                        Solstice.locale().commands.teleportRequest.hoverAccept,
                        "/tpaccept " + request.requestId),
                "refuseButton", Components.button(
                        Solstice.locale().commands.common.refuse,
                        Solstice.locale().commands.teleportRequest.hoverRefuse,
                        "/tpdeny " + request.requestId)
        );

        target.sendMessage(Format.parse(
                Solstice.locale().commands.teleportRequest.pendingTeleport,
                targetContext,
                placeholders
        ));

        source.sendFeedback(() -> Format.parse(
                Solstice.locale().commands.teleportRequest.requestSent,
                playerContext
        ), false);
    }

}
