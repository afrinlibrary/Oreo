package net.amar.oreojava.commands.text.staff;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.amar.oreojava.commands.Categories;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.interactions.InteractionContextType;

import java.util.List;

public class Role extends Command {

    public Role() {
        this.name = "role";
        this.help = "add or remove a role from a member (toggles if they already have it)";
        this.category = Categories.staff;
        this.aliases = new String[]{"giverole", "takerole"};
        this.arguments = "<@user> <role name>";
        this.contexts = new InteractionContextType[]{
                InteractionContextType.GUILD
        };
        this.userPermissions = new Permission[]{
                Permission.MANAGE_ROLES
        };
        this.botPermissions = new Permission[]{
                Permission.MANAGE_ROLES
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
        String roleName = args[1].trim();

        event.getGuild().retrieveMemberById(uid).queue(member -> {

            List<Role> roles = event.getGuild().getRolesByName(roleName, true);

            if (roles.isEmpty()) {
                event.replyError("No role found with name **%s**. Check spelling and try again.".formatted(roleName));
                return;
            }

            Role role = roles.get(0);

            if (!event.getMember().canInteract(role)) {
                event.replyError("You can't manage a role higher than your own.");
                return;
            }

            if (member.getRoles().contains(role)) {
                // Remove
                event.getGuild().removeRoleFromMember(member, role).queue(
                        success -> event.replySuccess("Removed **%s** from **%s**.".formatted(role.getName(), member.getEffectiveName())),
                        failure -> event.replyError("Failed to remove role.\n[%s]".formatted(failure.getMessage()))
                );
            } else {
                // Add
                event.getGuild().addRoleToMember(member, role).queue(
                        success -> event.replySuccess("Gave **%s** to **%s**.".formatted(role.getName(), member.getEffectiveName())),
                        failure -> event.replyError("Failed to add role.\n[%s]".formatted(failure.getMessage()))
                );
            }
        }, failure -> event.replyError("Something went wrong\n[%s]".formatted(failure.getMessage())));
    }
}
