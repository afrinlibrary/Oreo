package net.amar.oreojava.commands.text.general;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.amar.oreojava.commands.Categories;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Enchants extends Command {

    // item category -> list of "Name (max level) — description"
    private static final Map<String, List<String>> ENCHANTMENTS = new HashMap<>();

    static {
        ENCHANTMENTS.put("sword", List.of(
                "Sharpness (V) — increased melee damage",
                "Smite (V) — extra damage to undead",
                "Bane of Arthropods (V) — extra damage to arthropods",
                "Fire Aspect (II) — sets target on fire",
                "Looting (III) — increases mob drops",
                "Knockback (II) — knocks back hit targets",
                "Sweeping Edge (III) — increases sweep damage [Java only]",
                "Unbreaking (III) — reduces durability loss",
                "Mending (I) — repairs with XP",
                "Curse of Vanishing (I) — item vanishes on death"
        ));
        ENCHANTMENTS.put("pickaxe", List.of(
                "Efficiency (V) — faster mining speed",
                "Fortune (III) — increases block drops",
                "Silk Touch (I) — drops the block itself",
                "Unbreaking (III) — reduces durability loss",
                "Mending (I) — repairs with XP",
                "Curse of Vanishing (I) — item vanishes on death"
        ));
        ENCHANTMENTS.put("axe", List.of(
                "Efficiency (V) — faster chopping",
                "Fortune (III) — increases drops",
                "Silk Touch (I) — drops blocks as-is",
                "Sharpness (V) — increased melee damage [Bedrock: all levels; Java: anvil only]",
                "Smite (V) — extra damage to undead [anvil only on Java]",
                "Bane of Arthropods (V) — extra damage to arthropods [anvil only on Java]",
                "Unbreaking (III) — reduces durability loss",
                "Mending (I) — repairs with XP",
                "Curse of Vanishing (I) — item vanishes on death"
        ));
        ENCHANTMENTS.put("shovel", List.of(
                "Efficiency (V) — faster digging",
                "Fortune (III) — increases drops",
                "Silk Touch (I) — drops blocks as-is",
                "Unbreaking (III) — reduces durability loss",
                "Mending (I) — repairs with XP",
                "Curse of Vanishing (I) — item vanishes on death"
        ));
        ENCHANTMENTS.put("hoe", List.of(
                "Efficiency (V) — faster tilling",
                "Fortune (III) — increases drops",
                "Silk Touch (I) — drops blocks as-is",
                "Unbreaking (III) — reduces durability loss",
                "Mending (I) — repairs with XP",
                "Curse of Vanishing (I) — item vanishes on death"
        ));
        ENCHANTMENTS.put("bow", List.of(
                "Power (V) — increased arrow damage",
                "Punch (II) — knockback on arrow hit",
                "Flame (I) — arrows set targets on fire",
                "Infinity (I) — fire without consuming arrows (not compatible with Mending)",
                "Unbreaking (III) — reduces durability loss",
                "Mending (I) — repairs with XP (not compatible with Infinity)",
                "Curse of Vanishing (I) — item vanishes on death"
        ));
        ENCHANTMENTS.put("crossbow", List.of(
                "Quick Charge (III) — faster reloading",
                "Multishot (I) — fires 3 arrows at once",
                "Piercing (IV) — arrows pierce through entities",
                "Unbreaking (III) — reduces durability loss",
                "Mending (I) — repairs with XP",
                "Curse of Vanishing (I) — item vanishes on death"
        ));
        ENCHANTMENTS.put("trident", List.of(
                "Loyalty (III) — trident returns after thrown",
                "Riptide (III) — launches player when thrown in water/rain",
                "Channeling (I) — calls lightning on hit during thunderstorm",
                "Impaling (V) — extra damage to aquatic mobs",
                "Unbreaking (III) — reduces durability loss",
                "Mending (I) — repairs with XP",
                "Curse of Vanishing (I) — item vanishes on death"
        ));
        ENCHANTMENTS.put("helmet", List.of(
                "Protection (IV) — general damage reduction",
                "Blast Protection (IV) — explosion damage reduction",
                "Fire Protection (IV) — fire damage reduction",
                "Projectile Protection (IV) — projectile damage reduction",
                "Respiration (III) — extends underwater breathing",
                "Aqua Affinity (I) — normal mining speed underwater",
                "Thorns (III) — damages attackers",
                "Unbreaking (III) — reduces durability loss",
                "Mending (I) — repairs with XP",
                "Curse of Binding (I) — cannot be removed until death",
                "Curse of Vanishing (I) — item vanishes on death"
        ));
        ENCHANTMENTS.put("chestplate", List.of(
                "Protection (IV) — general damage reduction",
                "Blast Protection (IV) — explosion damage reduction",
                "Fire Protection (IV) — fire damage reduction",
                "Projectile Protection (IV) — projectile damage reduction",
                "Thorns (III) — damages attackers",
                "Unbreaking (III) — reduces durability loss",
                "Mending (I) — repairs with XP",
                "Curse of Binding (I) — cannot be removed until death",
                "Curse of Vanishing (I) — item vanishes on death"
        ));
        ENCHANTMENTS.put("leggings", List.of(
                "Protection (IV) — general damage reduction",
                "Blast Protection (IV) — explosion damage reduction",
                "Fire Protection (IV) — fire damage reduction",
                "Projectile Protection (IV) — projectile damage reduction",
                "Swift Sneak (III) — faster crouching speed [Java only]",
                "Thorns (III) — damages attackers",
                "Unbreaking (III) — reduces durability loss",
                "Mending (I) — repairs with XP",
                "Curse of Binding (I) — cannot be removed until death",
                "Curse of Vanishing (I) — item vanishes on death"
        ));
        ENCHANTMENTS.put("boots", List.of(
                "Protection (IV) — general damage reduction",
                "Blast Protection (IV) — explosion damage reduction",
                "Fire Protection (IV) — fire damage reduction",
                "Projectile Protection (IV) — projectile damage reduction",
                "Feather Falling (IV) — reduces fall damage",
                "Depth Strider (III) — faster underwater movement",
                "Frost Walker (II) — walk on water by freezing it",
                "Soul Speed (III) — faster movement on soul sand/soil",
                "Thorns (III) — damages attackers",
                "Unbreaking (III) — reduces durability loss",
                "Mending (I) — repairs with XP",
                "Curse of Binding (I) — cannot be removed until death",
                "Curse of Vanishing (I) — item vanishes on death"
        ));
        ENCHANTMENTS.put("fishing rod", List.of(
                "Luck of the Sea (III) — better loot while fishing",
                "Lure (III) — faster fish bites",
                "Unbreaking (III) — reduces durability loss",
                "Mending (I) — repairs with XP",
                "Curse of Vanishing (I) — item vanishes on death"
        ));
        ENCHANTMENTS.put("shield", List.of(
                "Unbreaking (III) — reduces durability loss",
                "Mending (I) — repairs with XP",
                "Curse of Vanishing (I) — item vanishes on death"
        ));
    }

    public Enchants() {
        this.name = "enchants";
        this.help = "list all valid enchantments for a given item type";
        this.category = Categories.general;
        this.aliases = new String[]{"enchantments", "ench"};
        this.arguments = "<item type (e.g. sword, pickaxe, boots)>";
    }

    @Override
    protected void execute(CommandEvent event) {
        String query = event.getArgs().trim().toLowerCase();

        if (query.isBlank()) {
            String keys = String.join(", ", ENCHANTMENTS.keySet());
            event.replyError("Please provide an item type. Available: ``%s``".formatted(keys));
            return;
        }

        List<String> enchants = ENCHANTMENTS.get(query);

        // Aliases / shorthands
        if (enchants == null) {
            String mapped = switch (query) {
                case "diamond sword", "iron sword", "netherite sword" -> "sword";
                case "diamond pickaxe", "iron pickaxe", "stone pickaxe" -> "pickaxe";
                case "diamond axe", "iron axe" -> "axe";
                case "diamond shovel", "iron shovel" -> "shovel";
                case "diamond boots", "iron boots", "netherite boots" -> "boots";
                case "diamond helmet", "iron helmet", "netherite helmet" -> "helmet";
                case "diamond chestplate", "iron chestplate", "netherite chestplate" -> "chestplate";
                case "diamond leggings", "iron leggings", "netherite leggings" -> "leggings";
                case "armor" -> "chestplate";
                default -> null;
            };
            if (mapped != null) enchants = ENCHANTMENTS.get(mapped);
        }

        if (enchants == null) {
            String keys = String.join(", ", ENCHANTMENTS.keySet());
            event.replyError("No enchantment data found for **%s**.\nSupported types: ``%s``".formatted(event.getArgs().trim(), keys));
            return;
        }

        StringBuilder sb = new StringBuilder();
        for (String e : enchants) sb.append("• ").append(e).append("\n");

        EmbedBuilder em = new EmbedBuilder()
                .setTitle("✨ Enchantments — " + capitalize(query))
                .setDescription(sb.toString())
                .setColor(Color.MAGENTA)
                .setFooter("Incompatible enchants cannot be combined on the same item")
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
