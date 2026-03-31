package net.amar.oreojava.commands.text.general;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.amar.oreojava.commands.Categories;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;

public class Recipe extends Command {

    // item name (lowercase) -> { grid description, result }
    private static final Map<String, String[]> RECIPES = new HashMap<>();

    static {
        // Tools
        RECIPES.put("wooden pickaxe",      new String[]{"WWW\n_S_\n_S_",    "1 Wooden Pickaxe",     "W = Planks, S = Stick"});
        RECIPES.put("stone pickaxe",       new String[]{"CCC\n_S_\n_S_",    "1 Stone Pickaxe",      "C = Cobblestone, S = Stick"});
        RECIPES.put("iron pickaxe",        new String[]{"III\n_S_\n_S_",    "1 Iron Pickaxe",       "I = Iron Ingot, S = Stick"});
        RECIPES.put("golden pickaxe",      new String[]{"GGG\n_S_\n_S_",    "1 Golden Pickaxe",     "G = Gold Ingot, S = Stick"});
        RECIPES.put("diamond pickaxe",     new String[]{"DDD\n_S_\n_S_",    "1 Diamond Pickaxe",    "D = Diamond, S = Stick"});
        RECIPES.put("netherite pickaxe",   new String[]{"Upgrade Smithing Table",   "1 Netherite Pickaxe",  "Diamond Pickaxe + Netherite Ingot + Netherite Upgrade Smithing Template"});

        // Swords
        RECIPES.put("wooden sword",        new String[]{"_W_\n_W_\n_S_",    "1 Wooden Sword",       "W = Planks, S = Stick"});
        RECIPES.put("stone sword",         new String[]{"_C_\n_C_\n_S_",    "1 Stone Sword",        "C = Cobblestone, S = Stick"});
        RECIPES.put("iron sword",          new String[]{"_I_\n_I_\n_S_",    "1 Iron Sword",         "I = Iron Ingot, S = Stick"});
        RECIPES.put("golden sword",        new String[]{"_G_\n_G_\n_S_",    "1 Golden Sword",       "G = Gold Ingot, S = Stick"});
        RECIPES.put("diamond sword",       new String[]{"_D_\n_D_\n_S_",    "1 Diamond Sword",      "D = Diamond, S = Stick"});

        // Common blocks and items
        RECIPES.put("crafting table",      new String[]{"WW\nWW",           "1 Crafting Table",     "W = Planks (any)"});
        RECIPES.put("furnace",             new String[]{"CCC\nC_C\nCCC",    "1 Furnace",            "C = Cobblestone"});
        RECIPES.put("chest",              new String[]{"WWW\nW_W\nWWW",    "1 Chest",              "W = Planks (any)"});
        RECIPES.put("torch",              new String[]{"_C_\n_S_",         "4 Torches",            "C = Coal/Charcoal, S = Stick"});
        RECIPES.put("stick",              new String[]{"_W_\n_W_",         "4 Sticks",             "W = Planks"});
        RECIPES.put("planks",             new String[]{"_L_",              "4 Planks",             "L = Any Log"});
        RECIPES.put("bread",              new String[]{"WWW",              "1 Bread",              "W = Wheat"});
        RECIPES.put("bow",                new String[]{"_SC\n__C\n_SC",    "Wait — correct pattern below", "S = Stick, C = String\nPattern: _SC / S_C / _SC"});
        RECIPES.put("arrow",             new String[]{"_F_\n_S_\n_Fe_",   "4 Arrows",             "F = Flint, S = Stick, Fe = Feather"});
        RECIPES.put("iron ingot",        new String[]{"Smelt in Furnace",  "1 Iron Ingot",         "Iron Ore / Raw Iron + Fuel"});
        RECIPES.put("gold ingot",        new String[]{"Smelt in Furnace",  "1 Gold Ingot",         "Gold Ore / Raw Gold + Fuel"});
        RECIPES.put("glass",             new String[]{"Smelt in Furnace",  "1 Glass",              "Sand + Fuel"});
        RECIPES.put("charcoal",          new String[]{"Smelt in Furnace",  "1 Charcoal",           "Any Log + Fuel"});
        RECIPES.put("book",              new String[]{"_P_\nPLP\n_P_",    "1 Book",               "P = Paper, L = Leather\n(shapeless)"});
        RECIPES.put("bookshelf",         new String[]{"PPP\nBBB\nPPP",    "1 Bookshelf",          "P = Planks, B = Book"});
        RECIPES.put("enchanting table",  new String[]{"_B_\nDOD\nOOO",    "1 Enchanting Table",   "B = Book, D = Diamond, O = Obsidian"});
        RECIPES.put("anvil",             new String[]{"III\n_i_\nIII",    "1 Anvil",              "I = Iron Block, i = Iron Ingot"});
        RECIPES.put("bucket",            new String[]{"I_I\n_I_",         "1 Bucket",             "I = Iron Ingot"});
        RECIPES.put("compass",           new String[]{"_I_\nIRI\n_I_",    "1 Compass",            "I = Iron Ingot, R = Redstone"});
        RECIPES.put("clock",             new String[]{"_G_\nGRI\n_G_",    "Wait, correct below",  "G = Gold Ingot, R = Redstone\n_G_ / GRG / _G_"});
        RECIPES.put("tnt",               new String[]{"GSG\nSGS\nGSG",    "1 TNT",                "G = Gunpowder, S = Sand"});
        RECIPES.put("bed",               new String[]{"WWW\nPPP",         "1 Bed",                "W = Wool (any, same color), P = Planks (any)"});
        RECIPES.put("iron armor",        new String[]{"See specific piece", "Iron Armor",          "Try: iron helmet, iron chestplate, iron leggings, iron boots"});
        RECIPES.put("iron helmet",       new String[]{"III\nI_I",         "1 Iron Helmet",        "I = Iron Ingot"});
        RECIPES.put("iron chestplate",   new String[]{"I_I\nIII\nIII",    "1 Iron Chestplate",    "I = Iron Ingot"});
        RECIPES.put("iron leggings",     new String[]{"III\nI_I\nI_I",    "1 Iron Leggings",      "I = Iron Ingot"});
        RECIPES.put("iron boots",        new String[]{"I_I\nI_I",         "1 Iron Boots",         "I = Iron Ingot"});
        RECIPES.put("diamond helmet",    new String[]{"DDD\nD_D",         "1 Diamond Helmet",     "D = Diamond"});
        RECIPES.put("diamond chestplate",new String[]{"D_D\nDDD\nDDD",    "1 Diamond Chestplate", "D = Diamond"});
        RECIPES.put("diamond leggings",  new String[]{"DDD\nD_D\nD_D",    "1 Diamond Leggings",   "D = Diamond"});
        RECIPES.put("diamond boots",     new String[]{"D_D\nD_D",         "1 Diamond Boots",      "D = Diamond"});
    }

    public Recipe() {
        this.name = "recipe";
        this.help = "show the crafting recipe for a Minecraft item";
        this.category = Categories.general;
        this.aliases = new String[]{"craft", "mcrecipe"};
        this.arguments = "<item name>";
    }

    @Override
    protected void execute(CommandEvent event) {
        String query = event.getArgs().trim().toLowerCase();

        if (query.isBlank()) {
            event.replyError("Please provide an item name ``%s``".formatted(this.arguments));
            return;
        }

        String[] recipe = RECIPES.get(query);

        if (recipe == null) {
            // Try partial match
            String finalQuery = query;
            String partial = RECIPES.keySet().stream()
                    .filter(k -> k.contains(finalQuery) || finalQuery.contains(k))
                    .findFirst()
                    .orElse(null);

            if (partial != null) {
                recipe = RECIPES.get(partial);
                query = partial;
            } else {
                event.replyError("No recipe found for **%s**.\nTry being more specific (e.g. `diamond sword`, `iron pickaxe`, `enchanting table`).".formatted(event.getArgs().trim()));
                return;
            }
        }

        EmbedBuilder em = new EmbedBuilder()
                .setTitle("📦 Recipe — " + capitalize(query))
                .setColor(Color.CYAN)
                .addField("Grid", "```\n" + recipe[0] + "\n```", false)
                .addField("Result", recipe[1], true)
                .addField("Key", recipe[2], true)
                .setFooter("Grid: _ = empty slot")
                .setTimestamp(OffsetDateTime.now());

        event.getChannel().sendMessageEmbeds(em.build()).queue();
    }

    private String capitalize(String s) {
        if (s == null || s.isBlank()) return s;
        String[] words = s.split(" ");
        StringBuilder sb = new StringBuilder();
        for (String word : words)
            sb.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1)).append(" ");
        return sb.toString().trim();
    }
}
