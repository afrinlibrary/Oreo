package net.amar.oreojava.commands.text.staff;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.amar.oreojava.Oreo;
import net.amar.oreojava.commands.Categories;
import net.amar.oreojava.db.tables.Case;
import net.amar.oreojava.handlers.Verdict;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.interactions.InteractionContextType;

import java.util.concurrent.TimeUnit;

public class Softban extends Command {

    public Softban() {
        this.name = "softban";
        this.help = "ban and immediately unban a user to purge their recent messages";
        this.category = Categories.staff;
        this.aliases = new String[]{"sb", "cleanban"};
        this.arguments = "<@user> [reason]";
        this.contexts = new InteractionContextType[]{
                InteractionContextType.GUILD
        };
        this.userPermissions = new Permission[]{
                Permission.BAN_MEMBERS
        };
    }

    @Override
    protected void execute(CommandEvent event) {
        String[] args = event.getArgs().split("\\s+", 2);

        if (args.length < 2) {
            event.replyError("Please provide all the arguments ``%s``".formatted(this.arguments));
            return;
        }

        String uid = args[0].replaceAll("\\D", "");
        String reason = args[1];
        String proof = event.getMessage().getAttachments().isEmpty()
                ? null
                : event.getMessage().getAttachments().get(0).getUrl();
        Member mod = event.getMember();

        event.getGuild().retrieveMemberById(uid).queue(member -> {
            if (!mod.canInteract(member)) {
                event.replyError("You can't punish a member higher than you.");
                return;
            }

            // Ban with 7-day message deletion, then immediately unban
            event.getGuild().ban(member, 7, TimeUnit.DAYS).reason("[SOFTBAN] " + reason).queue(
                    success -> event.getGuild().unban(member.getUser()).reason("Softban - auto unban").queue(
                            unbanSuccess -> {
                                Case c = new Case(
                                        member.getUser().getId(),
                                        member.getUser().getName(),
                                        mod.getUser().getId(),
                                        mod.getUser().getName(),
                                        "SOFTBAN",
                                        reason,
                                        "",
                                        false
                                );
                                Verdict.buildVerdict(c, Oreo.getVerdictChannel(), member.getUser(), proof);
                                event.replySuccess("Softbanned **%s** for *%s* (messages purged).".formatted(member.getEffectiveName(), reason));
                            },
                            unbanFailure -> event.replyError("Banned but failed to unban: %s".formatted(unbanFailure.getMessage()))
                    ),
                    failure -> event.replyError("Failed to softban.\n[%s]".formatted(failure.getMessage()))
            );
        }, failure -> event.replyError("Something went wrong\n[%s]".formatted(failure.getMessage())));
    }
}
