package net.amar.oreojava.commands.text.general;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.amar.oreojava.Oreo;
import net.amar.oreojava.commands.Categories;
import net.amar.oreojava.db.DBGetter;
import net.amar.oreojava.db.tables.EmbedTag;
import net.dv8tion.jda.api.EmbedBuilder;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.sql.SQLException;
import java.time.OffsetDateTime;

public class CallEmbedTag extends Command {

    public CallEmbedTag() {
        this.name = "embedtag";
        this.help = "call an embed tag";
        this.arguments = "[id]";
        this.aliases = new String[]{"et", "tt"};
        this.category = Categories.general;
    }
    @Override
    protected void execute(@NotNull CommandEvent event) {
        String EmbedTagId = event.getArgs();

        try {
            EmbedTag embedTag = DBGetter.getEmbedTag(EmbedTagId, Oreo.getConnection());
            if (embedTag==null) {
                event.reply("No tag was found");
                return;
            }

            EmbedBuilder em = new EmbedBuilder();
            em.setTitle(embedTag.getTitle());
            em.setDescription(embedTag.getDescription());
            em.setColor(Color.cyan);
            em.setFooter("Requested by: "+event.getAuthor().getName(), event.getJDA().getSelfUser().getAvatarUrl());
            em.setTimestamp(OffsetDateTime.now());

            if (event.getMessage().getReferencedMessage()!=null) {
                event.getMessage().getReferencedMessage().replyEmbeds(em.build()).queue();
            } else event.reply(em.build());
        } catch (SQLException e) {
            event.replyFormatted("Failed to call **%s** with reason [%s]",EmbedTagId ,e.getMessage());
        }
    }
}
