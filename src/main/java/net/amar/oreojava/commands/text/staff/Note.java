package net.amar.oreojava.commands.text.staff;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.amar.oreojava.Log;
import net.amar.oreojava.Oreo;
import net.amar.oreojava.commands.Categories;
import net.amar.oreojava.db.DBInserter;
import net.amar.oreojava.db.DBUpdater;
import net.amar.oreojava.db.tables.Case;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.InteractionContextType;

import java.awt.*;
import java.sql.SQLException;
import java.time.OffsetDateTime;

public class Note extends Command {

    public Note() {
        this.name = "note";
        this.help = "add a private staff note to a user (silent — no DM sent to user)";
        this.category = Categories.staff;
        this.aliases = new String[]{"addnote", "staffnote"};
        this.arguments = "<@user> <note>";
        this.contexts = new InteractionContextType[]{
                InteractionContextType.GUILD
        };
        this.userPermissions = new Permission[]{
                Permission.MODERATE_MEMBERS
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
        String note = args[1];

        event.getGuild().retrieveMemberById(uid).queue(member -> {
            Case c = new Case(
                    member.getUser().getId(),
                    member.getUser().getName(),
                    event.getAuthor().getId(),
                    event.getAuthor().getName(),
                    "NOTE",
                    note,
                    "",
                    false
            );

            try {
                long caseId = DBInserter.insert(Oreo.getConnection(), c);

                EmbedBuilder em = new EmbedBuilder()
                        .setTitle("NOTE | #" + caseId)
                        .addField("Moderator:", "%s (%s)".formatted(event.getAuthor().getName(), event.getAuthor().getId()), true)
                        .addField("User:", "%s (%s)".formatted(member.getUser().getName(), member.getUser().getId()), true)
                        .addField("Note:", note, false)
                        .setColor(Color.YELLOW)
                        .setThumbnail(member.getUser().getAvatarUrl())
                        .setTimestamp(OffsetDateTime.now());

                if (Oreo.getVerdictChannel() != null) {
                    Oreo.getVerdictChannel().sendMessageEmbeds(em.build()).queue(
                            msg -> {
                                try {
                                    DBUpdater.updateMessageId(Oreo.getConnection(), caseId, msg.getId());
                                } catch (SQLException e) {
                                    Log.error("Couldn't set message id for note", e);
                                }
                            }
                    );
                }

                event.replySuccess("Note added for **%s** (Case #%d) — user was **not** notified.".formatted(member.getEffectiveName(), caseId));

            } catch (Exception e) {
                Log.error("Failed to save note for user " + uid, e);
                event.replyError("Something went wrong while saving the note.");
            }
        }, failure -> event.replyError("Something went wrong\n[%s]".formatted(failure.getMessage())));
    }
}
