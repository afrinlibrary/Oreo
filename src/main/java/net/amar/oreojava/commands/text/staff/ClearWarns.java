package net.amar.oreojava.commands.text.staff;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.amar.oreojava.Log;
import net.amar.oreojava.Oreo;
import net.amar.oreojava.commands.Categories;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.InteractionContextType;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ClearWarns extends Command {

    public ClearWarns() {
        this.name = "clearwarns";
        this.help = "clear all warnings for a user";
        this.category = Categories.staff;
        this.aliases = new String[]{"clearwarnings", "warnreset"};
        this.arguments = "<@user>";
        this.contexts = new InteractionContextType[]{
                InteractionContextType.GUILD
        };
        this.userPermissions = new Permission[]{
                Permission.KICK_MEMBERS
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
            final String sql = "DELETE FROM cases WHERE userId = ? AND type = 'WARN'";
            try (PreparedStatement ps = Oreo.getConnection().prepareStatement(sql)) {
                ps.setString(1, uid);
                int deleted = ps.executeUpdate();
                if (deleted == 0) {
                    event.replySuccess("**%s** had no warnings to clear.".formatted(member.getEffectiveName()));
                } else {
                    event.replySuccess("Cleared **%d** warning(s) for **%s**.".formatted(deleted, member.getEffectiveName()));
                }
            } catch (SQLException e) {
                Log.error("Failed to clear warns for user " + uid, e);
                event.replyError("Something went wrong while clearing warnings.");
            }
        }, failure -> event.replyError("User not found in this server."));
    }
}
