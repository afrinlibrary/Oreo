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
import net.dv8tion.jda.api.interactions.InteractionContextType;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.ArrayList;
import java.util.List;

public class KickSlash extends SlashCommand {

    public KickSlash() {
        this.name = "kick";
        this.help = "kick a bad user";
        this.category = Categories.staff;
        this.contexts = new InteractionContextType[]{
                InteractionContextType.GUILD
        };
        this.userPermissions = new Permission[] {
                Permission.KICK_MEMBERS
        };

        List<OptionData> options = new ArrayList<>();
        options.add(new OptionData(OptionType.USER, "user", "user to ban", true));
        options.add(new OptionData(OptionType.STRING, "reason", "why ban this user", true));
        options.add(new OptionData(OptionType.ATTACHMENT, "proof", "optional proof", false));
        this.options = options;
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        Member moderator = event.getMember();
        Member user = event.getOption("user").getAsMember();
        String reason = event.getOption("reason").getAsString();
        String image = null;

        if (event.getOption("proof")!=null)
            image = event.getOption("proof").getAsAttachment().getUrl();
        try {
            assert moderator != null;
            assert user != null;

            if (!moderator.canInteract(user)) {
                event.reply("You can't punish a person with a higher role").queue();
                return;
            }

            Case c = new Case(
                    user.getUser().getId(),
                    user.getUser().getName(),
                    moderator.getUser().getId(),
                    moderator.getUser().getName(),
                    "KICK",
                    reason,
                    "",
                    true
            );
            if (Verdict.buildVerdict(c, Oreo.getVerdictChannel(), user.getUser(), image)) {
                event.getGuild().kick(user).reason(reason).queue();
                event.replyFormat("Kicked **%s** for reason:\n*%s*", user.getUser().getName(), reason).queue();
            }
        } catch (Exception e) {
            Log.error("Failed to kick member",e);
            event.replyFormat("Failed to kick [%s]",e.getMessage()).setEphemeral(true).queue();
        }
    }
}
