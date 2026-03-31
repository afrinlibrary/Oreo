package net.amar.oreojava.commands.text.staff;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.amar.oreojava.commands.Categories;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.interactions.InteractionContextType;

public class Unlock extends Command {

    public Unlock() {
        this.name = "unlock";
        this.help = "unlock a locked thread";
        this.category = Categories.staff;
        this.contexts = new InteractionContextType[]{
                InteractionContextType.GUILD
        };
        this.userPermissions = new Permission[]{
                Permission.MANAGE_THREADS
        };
    }

    @Override
    protected void execute(CommandEvent event) {
        if (!event.getChannel().getType().isThread()) {
            event.replyError("This command can only be used inside a thread.");
            return;
        }

        ThreadChannel thread = (ThreadChannel) event.getChannel();

        if (!thread.isLocked()) {
            event.replyError("This thread is not locked.");
            return;
        }

        thread.getManager().setLocked(false).queue(
                success -> event.replySuccess("Thread **unlocked**."),
                failure -> event.replyError("Failed to unlock thread.\n[%s]".formatted(failure.getMessage()))
        );
    }
}
