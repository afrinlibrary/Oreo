package net.amar.oreojava.commands.slash.staff;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import net.amar.oreojava.Log;
import net.amar.oreojava.Oreo;
import net.amar.oreojava.commands.Categories;
import net.amar.oreojava.db.tables.Case;
import net.amar.oreojava.handlers.Verdict;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.interactions.InteractionContextType;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class BanSlash extends SlashCommand {

    public BanSlash() {
        this.name = "ban";
        this.help = "ban a bad user";
        this.category = Categories.staff;
        this.contexts = new InteractionContextType[]{
                InteractionContextType.GUILD
        };
        this.userPermissions = new Permission[] {
                Permission.BAN_MEMBERS
        };

        List<OptionData> options = new ArrayList<>();
        options.add(new OptionData(OptionType.USER, "user", "user to ban", true));
        options.add(new OptionData(OptionType.STRING, "reason", "why ban this user", true));
        options.add(new OptionData(OptionType.BOOLEAN, "appealable", "can they appeal their ban?", false));
        options.add(new OptionData(OptionType.ATTACHMENT, "proof", "optional proof", false));
        options.add(new OptionData(OptionType.INTEGER, "delete-days", "message delete days", false));
        this.options = options;
    }
    @Override
    protected void execute(@NotNull SlashCommandEvent event) {
        Member moderator = event.getMember();
        Member user = event.getOption("user").getAsMember();
        User u = user.getUser(); 
        String reason = event.getOption("reason").getAsString();
        Message.Attachment image = null;
        int deleteDays = 0;
        boolean appeal = true;

        if (event.getOption("proof")!=null)
            image = event.getOption("proof").getAsAttachment();
        if (event.getOption("delete-days")!=null)
            deleteDays = event.getOption("delete-days").getAsInt();
        if (event.getOption("appealable")!=null)
             appeal = event.getOption("appealable").getAsBoolean();


        assert user != null;
        assert moderator != null;

        if (!moderator.canInteract(user)) {
            event.reply("You can't punish a person with a higher role").queue();
            return;
        }

        Case modCase = new Case(
                user.getUser().getId(),
                user.getUser().getName(),
                moderator.getId(),
                moderator.getUser().getName(),
                "BAN",
                reason,
                "",
                appeal
        );

        try {
           if (image==null)
               Verdict.buildVerdict(modCase, Oreo.getVerdictChannel(), user.getUser(), null);
           else
               Verdict.buildVerdict(modCase, Oreo.getVerdictChannel(), user.getUser(), image.getUrl());

           event.getGuild().ban(u, deleteDays, TimeUnit.DAYS).reason(reason).queue();
           event.replyFormat("Banned **%s** for Reason: **%s**", user.getUser().getName(), reason).queue();
        } catch (Exception e) {
            Log.error("Something went wrong while executing /ban command",e);
            event.replyFormat("*[%s]*",e.getMessage()).setEphemeral(true).queue();
        }
    }
}
