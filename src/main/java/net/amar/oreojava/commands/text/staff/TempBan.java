package net.amar.oreojava.commands.text.staff;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.amar.oreojava.Log;
import net.amar.oreojava.Oreo;
import net.amar.oreojava.commands.Categories;
import net.amar.oreojava.db.tables.Case;
import net.amar.oreojava.handlers.Verdict;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.interactions.InteractionContextType;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TempBan extends Command {

    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

    public TempBan() {
        this.name = "tempban";
        this.help = "temporarily ban a user (auto-unbans after duration). Note: pending unbans are lost on restart.";
        this.category = Categories.staff;
        this.aliases = new String[]{"tb", "tban"};
        this.arguments = "<@user> <duration (e.g. 1h, 2d, 30m)> [reason]";
        this.contexts = new InteractionContextType[]{
                InteractionContextType.GUILD
        };
        this.userPermissions = new Permission[]{
                Permission.BAN_MEMBERS
        };
    }

    @Override
    protected void execute(CommandEvent event) {
        String[] args = event.getArgs().split("\\s+", 3);

        if (args.length < 3) {
            event.replyError("Please provide all the arguments ``%s``".formatted(this.arguments));
            return;
        }

        String uid = args[0].replaceAll("\\D", "");
        String durationStr = args[1];
        String reason = args[2];
        String proof = event.getMessage().getAttachments().isEmpty()
                ? null
                : event.getMessage().getAttachments().get(0).getUrl();
        Member mod = event.getMember();

        long durationMs;
        try {
            durationMs = parseDurationMs(durationStr);
        } catch (IllegalArgumentException e) {
            event.replyError("Invalid duration format. Use something like ``1h``, ``2d``, ``30m``, ``1w``.");
            return;
        }

        event.getGuild().retrieveMemberById(uid).queue(member -> {
            if (!mod.canInteract(member)) {
                event.replyError("You can't punish a member higher than you.");
                return;
            }

            event.getGuild().ban(member, 0, TimeUnit.DAYS).reason("[TEMPBAN] " + reason).queue(
                    success -> {
                        Case c = new Case(
                                member.getUser().getId(),
                                member.getUser().getName(),
                                mod.getUser().getId(),
                                mod.getUser().getName(),
                                "TEMPBAN",
                                reason,
                                durationStr,
                                false
                        );
                        Verdict.buildVerdict(c, Oreo.getVerdictChannel(), member.getUser(), proof);
                        event.replySuccess("Temporarily banned **%s** for **%s** — *%s*".formatted(member.getEffectiveName(), durationStr, reason));

                        // Schedule auto-unban
                        String userId = member.getUser().getId();
                        String userName = member.getUser().getName();
                        scheduler.schedule(() -> {
                            try {
                                event.getGuild().unban(event.getJDA().retrieveUserById(userId).complete())
                                        .reason("TempBan expired after " + durationStr)
                                        .queue(
                                                s -> Log.info("Auto-unbanned " + userName + " after " + durationStr),
                                                f -> Log.error("Failed to auto-unban " + userName, new Throwable(f.getMessage()))
                                        );
                            } catch (Exception e) {
                                Log.error("Failed to auto-unban " + userName, e);
                            }
                        }, durationMs, TimeUnit.MILLISECONDS);
                    },
                    failure -> event.replyError("Failed to ban.\n[%s]".formatted(failure.getMessage()))
            );
        }, failure -> event.replyError("Something went wrong\n[%s]".formatted(failure.getMessage())));
    }

    private long parseDurationMs(String duration) {
        String unit = duration.replaceAll("\\d+", "").toLowerCase().trim();
        int amount;
        try {
            amount = Integer.parseInt(duration.replaceAll("\\D+", ""));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid amount in duration: " + duration);
        }

        return switch (unit) {
            case "s", "sec", "second", "seconds" -> amount * 1000L;
            case "m", "min", "minute", "minutes" -> amount * 60_000L;
            case "h", "hour", "hours" -> amount * 3_600_000L;
            case "d", "day", "days" -> amount * 86_400_000L;
            case "w", "week", "weeks" -> amount * 604_800_000L;
            default -> throw new IllegalArgumentException("Unknown time unit: " + unit);
        };
    }
}
