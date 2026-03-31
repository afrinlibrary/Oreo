package net.amar.oreojava.commands.text.general;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.amar.oreojava.commands.Categories;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;
import java.time.OffsetDateTime;
import java.util.Random;

public class Seed extends Command {

    private static final Random random = new Random();

    // A handful of famous seeds for flavour
    private static final String[][] FAMOUS_SEEDS = {
            {"-12345",         "One of the oldest known seeds"},
            {"404",            "The legendary 404 Challenge seed"},
            {"2151901553968352745", "The original Herobrine seed"},
            {"8091867987493326313", "Minecraft's title screen panorama (Java 1.8)"},
            {"3257840388504953787", "Minecraft's title screen panorama (Java 1.16)"},
    };

    public Seed() {
        this.name = "seed";
        this.help = "generate a random Minecraft seed, or show a random famous one";
        this.category = Categories.general;
        this.aliases = new String[]{"mcseed", "randomseed"};
        this.arguments = "[famous]";
    }

    @Override
    protected void execute(CommandEvent event) {
        String arg = event.getArgs().trim().toLowerCase();

        EmbedBuilder em = new EmbedBuilder()
                .setColor(Color.GREEN)
                .setTimestamp(OffsetDateTime.now());

        if (arg.equals("famous") || arg.equals("f")) {
            String[] pick = FAMOUS_SEEDS[random.nextInt(FAMOUS_SEEDS.length)];
            em.setTitle("🌍 Famous Minecraft Seed")
              .addField("Seed", "`" + pick[0] + "`", true)
              .addField("Notes", pick[1], true)
              .setFooter("Use ?seed for a random seed");
        } else {
            long seed = random.nextLong();
            em.setTitle("🎲 Random Minecraft Seed")
              .setDescription("# `" + seed + "`")
              .setFooter("Use ?seed famous for a well-known seed");
        }

        event.getChannel().sendMessageEmbeds(em.build()).queue();
    }
}
