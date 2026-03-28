package net.amar.oreojava.handlers;

import com.jagrosh.jdautilities.command.CommandEvent;
import net.amar.oreojava.Oreo;
import net.amar.oreojava.commands.Categories;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;

import java.awt.*;
import java.time.OffsetDateTime;
import java.util.concurrent.TimeUnit;

public class Help {

    static StringBuilder generalCmd;
    static StringBuilder staffCmd;
    static StringBuilder ownerCmd;

    private static StringSelectMenu menu() {
        return StringSelectMenu.create("help")
                .addOption("General","general")
                .addOption("Staff", "staff")
                .addOption("Owner", "owner")
                .build();
    }

    private static MessageEmbed embed(String category, String cmd) {
        return new EmbedBuilder()
                .setTitle("Available Text Commands under %s category".formatted(category))
                .setDescription(cmd)
                .setColor(Color.CYAN)
                .setTimestamp(OffsetDateTime.now())
                .build();
    }

    private static void waitForMenu(CommandEvent help, Message message, String general, String staff, String owner) {
        Oreo.getWaiter().waitForEvent(
                StringSelectInteractionEvent.class,
                event ->
                        event.getUser().getIdLong() == help.getEvent().getAuthor().getIdLong() &&
                                event.getMessageIdLong() == message.getIdLong(),

                event -> {
                    String selected = event.getValues().get(0);

                    if (selected.equals("general")) {
                        message.editMessageEmbeds(embed(selected, general))
                                .queue();
                    } else if (selected.equals("staff")) {
                        message.editMessageEmbeds(embed(selected, staff))
                                .queue();
                    } else {
                        message.editMessageEmbeds(embed(selected, owner))
                                .queue();
                    }
                    event.reply("You selected "+selected).setEphemeral(true).queue();
                    waitForMenu(help, message, general, staff, owner);
                },
                1, TimeUnit.MINUTES,
                () -> message.editMessage("This command expired")
                        .setComponents()
                        .queue()
        );
    }

    public static void helpCmdReply(CommandEvent help) {

        generalCmd = new StringBuilder();
        staffCmd = new StringBuilder();
        ownerCmd = new StringBuilder();

        help.getClient().getCommands().forEach(c -> {

            String aliases = "empty";

            if (c.getAliases() != null) {
                aliases = String.join(", ", c.getAliases());
            }

            if (c.getCategory()== Categories.owner) {
                ownerCmd.append("\n")
                        .append("**%s** - ".formatted(c.getName()))
                        .append("%s".formatted(c.getHelp()))
                        .append("\n-# Aliases: [%s]".formatted(aliases))
                        .append("\n");
            }

            if (c.getCategory()==Categories.staff) {
                staffCmd.append("\n")
                        .append("**%s** - ".formatted(c.getName()))
                        .append("%s".formatted(c.getHelp()))
                        .append("\n-# Aliases: [%s]".formatted(aliases))
                        .append("\n");
            }

            if (c.getCategory()==Categories.general) {
                generalCmd.append("\n")
                        .append("**%s** - ".formatted(c.getName()))
                        .append("%s".formatted(c.getHelp()))
                        .append("\n-# Aliases: [%s]".formatted(aliases))
                        .append("\n");
            }
        });

        help.getEvent().getMessage().replyEmbeds(embed("general", generalCmd.toString()))
                .addComponents(ActionRow.of(menu()))
                .queue((m) -> waitForMenu(help, m, generalCmd.toString(), staffCmd.toString(), ownerCmd.toString()));
    }
}
