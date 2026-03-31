package net.amar.oreojava.commands.text.staff;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.amar.oreojava.commands.Categories;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.InteractionContextType;

import java.util.List;

public class Purge extends Command {

    public Purge() {
        this.name = "purge";
        this.help = "bulk delete messages from the current channel (max 100)";
        this.category = Categories.staff;
        this.aliases = new String[]{"clear", "nuke", "prune"};
        this.arguments = "<amount>";
        this.contexts = new InteractionContextType[]{
                InteractionContextType.GUILD
        };
        this.userPermissions = new Permission[]{
                Permission.MESSAGE_MANAGE
        };
        this.botPermissions = new Permission[]{
                Permission.MESSAGE_MANAGE
        };
    }

    @Override
    protected void execute(CommandEvent event) {
        String arg = event.getArgs().trim();

        if (arg.isBlank()) {
            event.replyError("Please provide an amount ``%s``".formatted(this.arguments));
            return;
        }

        int amount;
        try {
            amount = Integer.parseInt(arg);
        } catch (NumberFormatException e) {
            event.replyError("That doesn't look like a number.");
            return;
        }

        if (amount < 1 || amount > 100) {
            event.replyError("Amount must be between **1** and **100**.");
            return;
        }

        // Delete the invoking command message first, then retrieve and purge
        event.getMessage().delete().queue(ignored ->
                event.getChannel().getHistory().retrievePast(amount).queue(
                        (List messages) -> {
                            if (messages.isEmpty()) {
                                event.replyError("No messages found to delete.");
                                return;
                            }
                            event.getChannel().asTextChannel().purgeMessages(messages);
                            event.getChannel().sendMessage(
                                    "✅ Purged **%d** message(s).".formatted(messages.size())
                            ).queue(reply -> reply.delete().queueAfter(5, java.util.concurrent.TimeUnit.SECONDS));
                        },
                        failure -> event.replyError("Failed to retrieve messages.\n[%s]".formatted(failure.getMessage()))
                )
        );
    }
}
