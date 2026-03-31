package net.amar.oreojava.commands.text.staff;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.amar.oreojava.commands.Categories;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.InteractionContextType;

public class Slowmode extends Command {

    public Slowmode() {
        this.name = "slowmode";
        this.help = "set slowmode on the current channel (0 to disable, max 21600)";
        this.category = Categories.staff;
        this.aliases = new String[]{"slow", "sm"};
        this.arguments = "<seconds>";
        this.contexts = new InteractionContextType[]{
                InteractionContextType.GUILD
        };
        this.userPermissions = new Permission[]{
                Permission.MANAGE_CHANNEL
        };
    }

    @Override
    protected void execute(CommandEvent event) {
        String arg = event.getArgs().trim();

        if (arg.isBlank()) {
            event.replyError("Please provide a duration in seconds ``%s``".formatted(this.arguments));
            return;
        }

        int seconds;
        try {
            seconds = Integer.parseInt(arg);
        } catch (NumberFormatException e) {
            event.replyError("That doesn't look like a number.");
            return;
        }

        if (seconds < 0 || seconds > 21600) {
            event.replyError("Slowmode must be between **0** and **21600** seconds.");
            return;
        }

        if (!event.getChannel().getType().isMessage()) {
            event.replyError("This command can only be used in a text channel.");
            return;
        }

        event.getChannel().asTextChannel().getManager().setSlowmode(seconds).queue(
                success -> {
                    if (seconds == 0)
                        event.replySuccess("Slowmode **disabled** in this channel.");
                    else
                        event.replySuccess("Slowmode set to **%ds** in this channel.".formatted(seconds));
                },
                failure -> event.replyError("Failed to set slowmode.\n[%s]".formatted(failure.getMessage()))
        );
    }
}
