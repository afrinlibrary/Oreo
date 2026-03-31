package net.amar.oreojava.commands.text.general;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.amar.oreojava.commands.Categories;
import net.amar.oreojava.handlers.UrlRequest;

public class McBugReport extends Command {

    public McBugReport() {
        this.name = "mcbug";
        this.help = "look up a specific Minecraft bug by ID (accepts MC-12345 or just 12345)";
        this.category = Categories.general;
        this.aliases = new String[]{"bug", "mcissue", "mojira"};
        this.arguments = "<MC-XXXXX or XXXXX>";
    }

    @Override
    protected void execute(CommandEvent event) {
        String raw = event.getArgs().trim();

        if (raw.isBlank()) {
            event.replyError("Please provide a bug ID ``%s``".formatted(this.arguments));
            return;
        }

        // Accept both "MC-12345" and "12345"
        String key = raw.toUpperCase().startsWith("MC-")
                ? raw.substring(3).replaceAll("\\D", "")
                : raw.replaceAll("\\D", "");

        if (key.isBlank()) {
            event.replyError("That doesn't look like a valid bug ID.");
            return;
        }

        UrlRequest.fetchBug(event.getMessage(), key);
    }
}
