package me.alexdevs.solstice.commands.moderation;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.command.argument.GameProfileArgumentType;
import net.minecraft.server.command.ServerCommandSource;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.regex.Pattern;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class TempBanCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        var rootCommand = literal("tempban")
                .requires(Permissions.require("solstice.command.tempban", 3))
                .then(argument("targets", GameProfileArgumentType.gameProfile())
                        .then(argument("duration", StringArgumentType.string())
                                .executes(context -> execute(context, GameProfileArgumentType.getProfileArgument(context, "targets"), null, StringArgumentType.getString(context, "duration")))
                                .then(argument("reason", StringArgumentType.greedyString())
                                        .executes(context -> execute(context, GameProfileArgumentType.getProfileArgument(context, "targets"), StringArgumentType.getString(context, "reason"), StringArgumentType.getString(context, "duration"))))));

        dispatcher.register(rootCommand);
    }

    private static int execute(CommandContext<ServerCommandSource> context, Collection<GameProfile> targets, String reason, String duration) throws CommandSyntaxException {
        var totalSeconds = parseTime(duration);
        var expiryDate = getDateFromNow(totalSeconds);

        return BanCommand.execute(context, targets, reason, expiryDate);
    }

    private static Date getDateFromNow(int seconds) {
        var now = new Date();
        var c = Calendar.getInstance();
        c.setTime(now);
        c.add(Calendar.SECOND, seconds);
        return c.getTime();
    }

    private static int parseTime(String time) {
        var totalSeconds = 0;

        var weeks = match(time, "(\\d+)\\s*w");
        var days = match(time, "(\\d+)\\s*d");
        var hours = match(time, "(\\d+)\\s*h");
        var minutes = match(time, "(\\d+)\\s*m");
        var seconds = match(time, "(\\d+)\\s*s");

        if (weeks.success)
            totalSeconds += weeks.value * 604_800;
        if (days.success)
            totalSeconds += days.value * 86_400;
        if (hours.success)
            totalSeconds += hours.value * 3_600;
        if (minutes.success)
            totalSeconds += minutes.value * 60;
        if (seconds.success)
            totalSeconds += seconds.value;

        return totalSeconds;
    }

    private static Match match(String value, String regex) {
        var pattern = Pattern.compile(regex);
        var matcher = pattern.matcher(value);
        if (matcher.find()) {
            try {
                var val = Integer.parseInt(matcher.group(1));
                return new Match(true, val);
            } catch (NumberFormatException e) {
                return new Match(false, 0);
            }
        }
        return new Match(false, 0);
    }

    private record Match(boolean success, int value) {
    }
}
