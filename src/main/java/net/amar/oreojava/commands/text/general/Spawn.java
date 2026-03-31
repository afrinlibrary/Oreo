package net.amar.oreojava.commands.text.general;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.amar.oreojava.commands.Categories;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;

public class Spawn extends Command {

    // mob name -> { spawn conditions, drops, health (hearts), notes }
    private static final Map<String, String[]> MOBS = new HashMap<>();

    static {
        // Hostile
        MOBS.put("zombie",        new String[]{"Overworld at light level 0, underground or night",   "0–2 Rotten Flesh, Rare: Iron Ingot, Carrot, Potato",        "10 ❤️",  "Can pick up items, Burns in daylight, Calls reinforcements"});
        MOBS.put("skeleton",      new String[]{"Overworld at light level 0, underground or night",   "0–2 Bones, 0–2 Arrows, Rare: Bow",                          "10 ❤️",  "Burns in daylight, Runs from wolves"});
        MOBS.put("creeper",       new String[]{"Overworld at light level 0, underground or night",   "0–2 Gunpowder, Rare: Music Disc (when killed by Skeleton)",  "10 ❤️",  "Silent, explodes when near player, Fears cats"});
        MOBS.put("spider",        new String[]{"Overworld at light level ≤7 (night), caves",         "0–2 String, 0–1 Spider Eye",                                 "8 ❤️",   "Can climb walls, Neutral in daylight"});
        MOBS.put("cave spider",   new String[]{"Spawner only (abandoned mineshafts)",                "0–2 String, 0–1 Spider Eye",                                 "6 ❤️",   "Smaller than spider, Inflicts Poison"});
        MOBS.put("enderman",      new String[]{"All dimensions at light level 0; Nether; End",       "0–1 Ender Pearl",                                            "20 ❤️",  "Teleports, Picks up blocks, Neutral unless looked at"});
        MOBS.put("witch",         new String[]{"Overworld at light level 0; Swamp huts",             "Glass Bottles, Sticks, Redstone, Glowstone Dust, Sugar, Spider Eye, Gunpowder", "13 ❤️", "Throws potions, Immune to potions"});
        MOBS.put("blaze",         new String[]{"Nether Fortresses, via spawner",                     "0–1 Blaze Rod",                                              "10 ❤️",  "Shoots fireballs, Required to brew potions"});
        MOBS.put("ghast",         new String[]{"Nether (Soul Sand Valley, Nether Wastes, Basalt Deltas)", "0–1 Gunpowder, 0–1 Ghast Tear",                        "5 ❤️",   "Shoots explosive fireballs, Deflect fireballs with a hit"});
        MOBS.put("wither skeleton",new String[]{"Nether Fortresses",                                 "0–2 Bones, Rare: Wither Skeleton Skull, Coal",               "20 ❤️",  "Inflicts Wither effect, Skulls needed to summon the Wither"});
        MOBS.put("piglin",        new String[]{"Nether (Crimson Forest, Nether Wastes, Basalt Deltas)", "Various loot via bartering with Gold Ingot",              "8 ❤️",   "Hostile without gold armor, Becomes Zombified in Overworld"});
        MOBS.put("hoglin",        new String[]{"Nether (Crimson Forest)",                            "2–4 Raw Porkchop, 0–1 Leather",                             "20 ❤️",  "Becomes Zoglin in Overworld, Afraid of Warped Fungi"});
        MOBS.put("warden",        new String[]{"Ancient Cities, triggered by vibrations",            "Sculk Catalyst",                                             "250 ❤️", "Strongest mob, Uses sonar, Sonic Shriek attack"});
        MOBS.put("ender dragon",  new String[]{"The End (boss, respawns with End Crystals)",         "12,000 XP, Dragon Egg (first kill), unlocks End gateway",   "100 ❤️", "Immune to fire, Healed by End Crystals, Flies over End pillars"});
        MOBS.put("wither",        new String[]{"Summoned: 3 Wither Skulls + 4 Soul Sand/Soil",       "1 Nether Star",                                             "150 ❤️ (Java) / 300 ❤️ (Bedrock)", "Flies, Shoots explosive skulls, Summons Wither Skeletons"});
        MOBS.put("elder guardian",new String[]{"Ocean Monuments (3 per monument)",                  "Wet Sponge, Prismarine Shard/Crystal, Fish, Rare: Sponge",  "40 ❤️",  "Inflicts Mining Fatigue III, Shoots laser beam"});
        MOBS.put("shulker",       new String[]{"End Cities (after defeating Ender Dragon)",          "0–1 Shulker Shell",                                         "15 ❤️",  "Inflicts Levitation, Teleports, Camouflages as purpur block"});

        // Passive
        MOBS.put("cow",           new String[]{"Grass blocks in plains, forests and most biomes",   "1–3 Raw Beef (1–3 Steak if on fire), 0–2 Leather",          "5 ❤️",   "Can be milked with a bucket"});
        MOBS.put("pig",           new String[]{"Grass blocks, most biomes",                         "1–3 Raw Porkchop (1–3 Cooked if on fire)",                  "5 ❤️",   "Can be ridden with a saddle + carrot on a stick"});
        MOBS.put("sheep",         new String[]{"Grass blocks, most biomes",                         "1 White Wool (or dyed color), 1 Raw Mutton",                "4 ❤️",   "Can be sheared for wool, Regrows wool by eating grass"});
        MOBS.put("chicken",       new String[]{"Grass blocks, most biomes",                         "0–2 Feathers, 1 Raw Chicken, Egg every 5–10 minutes",       "2 ❤️",   "Lays eggs, Falls slowly, Baby chickens hatch from eggs"});
        MOBS.put("horse",         new String[]{"Plains and Savanna biomes",                         "1–3 Leather, 1–3 Raw Beef",                                 "7.5–15 ❤️","Tamed with empty hand, Wears saddle + armor, Breeds with apples"});
        MOBS.put("wolf",          new String[]{"Forests, Taigas; various biomes depending on variant", "Nothing",                                                "4 ❤️ (wild) / 10 ❤️ (tamed)", "Tamed with Bones, Attacks skeletons automatically when tamed"});
        MOBS.put("cat",           new String[]{"Villages (1 per 4 beds), Swamp Huts",               "String, Ribbon, Banner Pattern",                            "5 ❤️",   "Tamed with Raw Fish, Scares Creepers and Phantoms"});
        MOBS.put("villager",      new String[]{"Villages across biomes",                             "Various trade goods",                                       "10 ❤️",  "Has professions, Trades with players, Zombifiable"});
        MOBS.put("bee",           new String[]{"Flower Forests, Plains near Bee Nests",              "Nothing directly, honeycombs/honey from hive",             "5 ❤️",   "Pollinates crops, Stings once then dies, Aggressive when hive attacked"});
        MOBS.put("axolotl",       new String[]{"Lush Caves (underground water near clay)",           "Nothing",                                                   "7 ❤️",   "Fights aquatic mobs, Can be bucketed, Plays dead"});
        MOBS.put("dolphin",       new String[]{"Ocean biomes",                                       "0–1 Raw Cod",                                               "5 ❤️",   "Leads players to treasure, Gives Speed buff, Cannot be tamed"});

        // Neutral
        MOBS.put("iron golem",    new String[]{"Villages (10 villagers + 21 houses), or player-built", "Iron Ingots, Poppies",                                   "50 ❤️",  "Protects villagers, Built with 4 Iron Blocks + Pumpkin"});
        MOBS.put("snow golem",    new String[]{"Player-built",                                       "0–15 Snowballs",                                           "2 ❤️",   "Leaves snow trail, Built with 2 Snow Blocks + Pumpkin, Dies in rain/heat"});
        MOBS.put("llama",         new String[]{"Savanna, Mountains, Windswept Hills",                "1–3 Leather",                                               "7.5–15 ❤️","Spits at threats, Can carry chest, Follows lead"});
    }

    public Spawn() {
        this.name = "spawn";
        this.help = "show spawn conditions, drops, and info for a Minecraft mob";
        this.category = Categories.general;
        this.aliases = new String[]{"mob", "mcmob", "drops"};
        this.arguments = "<mob name>";
    }

    @Override
    protected void execute(CommandEvent event) {
        String query = event.getArgs().trim().toLowerCase();

        if (query.isBlank()) {
            event.replyError("Please provide a mob name ``%s``".formatted(this.arguments));
            return;
        }

        String[] data = MOBS.get(query);

        // Try partial match
        if (data == null) {
            String finalQuery = query;
            String partial = MOBS.keySet().stream()
                    .filter(k -> k.contains(finalQuery) || finalQuery.contains(k))
                    .findFirst()
                    .orElse(null);
            if (partial != null) {
                data = MOBS.get(partial);
                query = partial;
            }
        }

        if (data == null) {
            event.replyError("No mob data found for **%s**. Check the spelling and try again.".formatted(event.getArgs().trim()));
            return;
        }

        EmbedBuilder em = new EmbedBuilder()
                .setTitle("🧟 " + capitalize(query))
                .setColor(Color.DARK_GRAY)
                .addField("Spawn", data[0], false)
                .addField("Drops", data[1], false)
                .addField("Health", data[2], true)
                .addField("Notes", data[3], false)
                .setTimestamp(OffsetDateTime.now());

        event.getChannel().sendMessageEmbeds(em.build()).queue();
    }

    private String capitalize(String s) {
        if (s == null || s.isBlank()) return s;
        String[] words = s.split(" ");
        StringBuilder sb = new StringBuilder();
        for (String w : words)
            sb.append(Character.toUpperCase(w.charAt(0))).append(w.substring(1)).append(" ");
        return sb.toString().trim();
    }
}
