package net.amar.oreojava.commands.text.staff;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.amar.oreojava.Oreo;
import net.amar.oreojava.commands.Categories;
import net.amar.oreojava.db.DBGetter;
import net.amar.oreojava.db.tables.Case;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.InteractionContextType;

import java.awt.*;
import java.time.OffsetDateTime;
import java.util.Map;

public class GetWarns extends Command {

    public GetWarns() {
        this.name = "warns";
        this.help = "view all warnings for a user";
        this.category = Categories.staff;
        this.aliases = new String[]{"warnings", "infractions"};
        this.arguments = "<@user>";
        this.contexts = new InteractionContextType[]{
                InteractionContextType.GUILD
        };
        this.userPermissions = new Permission[]{
                Permission.MODERATE_MEMBERS
        };
    }

    @Override
    protected void execute(CommandEvent event) {
        String[] args = event.getArgs().split("\\s+", 1);

        if (args[0].isBlank()) {
            event.replyError("Please provide a user ``%s``".formatted(this.arguments));
            return;
        }

        String uid = args[0].replaceAll("\\D", "");

        event.getGuild().retrieveMemberById(uid).queue(member -> {
            Map<Integer, Case> allCases = DBGetter.getUserCases(uid, Oreo.getConnection());

            if (allCases == null || allCases.isEmpty()) {
                event.replySuccess("**%s** has no cases on record.".formatted(member.getEffectiveName()));
                return;
            }

            EmbedBuilder em = new EmbedBuilder()
                    .setTitle("Warnings for " + member.getEffectiveName())
                    .setThumbnail(member.getUser().getAvatarUrl())
                    .setColor(Color.ORANGE)
                    .setTimestamp(OffsetDateTime.now());

            int warnCount = 0;
            for (Map.Entry<Integer, Case> entry : allCases.entrySet()) {
                Case c = entry.getValue();
                if (c.getType().equals("WARN")) {
                    em.addField(
                            "Case #%d".formatted(entry.getKey()),
                            "**Reason:** %s\n**Mod:** %s".formatted(c.getReason(), c.getModName()),
                            false
                    );
                    warnCount++;
                }
            }

            if (warnCount == 0) {
                event.replySuccess("**%s** has no warnings on record.".formatted(member.getEffectiveName()));
                return;
            }

            em.setFooter("Total warnings: " + warnCount);
            event.getChannel().sendMessageEmbeds(em.build()).queue();

        }, failure -> event.replyError("User not found in this server."));
    }
}
