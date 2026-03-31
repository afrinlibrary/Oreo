package net.amar.oreojava.commands.text.general;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.amar.oreojava.Log;
import net.amar.oreojava.commands.Categories;
import net.dv8tion.jda.api.EmbedBuilder;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.awt.*;
import java.io.IOException;
import java.time.OffsetDateTime;

public class McVersion extends Command {

    private static final OkHttpClient client = new OkHttpClient();

    public McVersion() {
        this.name = "mcversion";
        this.help = "show the latest Minecraft Java and Bedrock release versions";
        this.category = Categories.general;
        this.aliases = new String[]{"version", "mcver"};
    }

    @Override
    protected void execute(CommandEvent event) {
        // Mojang version manifest for Java Edition
        String javaUrl = "https://launchermeta.mojang.com/mc/game/version_manifest_v2.json";
        Request request = new Request.Builder().url(javaUrl).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.error("McVersion request failed", e);
                event.getChannel().sendMessage("❌ Failed to reach the Mojang API.").queue();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    event.getChannel().sendMessage("❌ Mojang API error: " + response.code()).queue();
                    return;
                }

                JSONObject body = new JSONObject(response.body().string());
                JSONObject latest = body.optJSONObject("latest");

                String latestRelease  = latest != null ? latest.optString("release",  "Unknown") : "Unknown";
                String latestSnapshot = latest != null ? latest.optString("snapshot", "Unknown") : "Unknown";

                EmbedBuilder em = new EmbedBuilder()
                        .setTitle("⛏️ Minecraft Versions")
                        .setColor(Color.GREEN)
                        .addField("Java — Latest Release",  "`" + latestRelease  + "`", true)
                        .addField("Java — Latest Snapshot", "`" + latestSnapshot + "`", true)
                        .addField("Changelog", "[View on minecraft.net](https://www.minecraft.net/en-us/article/minecraft-java-edition-" + latestRelease.replace(".", "-") + ")", false)
                        .setTimestamp(OffsetDateTime.now());

                event.getChannel().sendMessageEmbeds(em.build()).queue();
            }
        });
    }
}
