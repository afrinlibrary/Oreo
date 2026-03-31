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

public class McSkin extends Command {

    private static final OkHttpClient client = new OkHttpClient();

    public McSkin() {
        this.name = "mcskin";
        this.help = "display a Minecraft player's full skin render";
        this.category = Categories.general;
        this.aliases = new String[]{"skin"};
        this.arguments = "<username>";
    }

    @Override
    protected void execute(CommandEvent event) {
        String username = event.getArgs().trim();

        if (username.isBlank()) {
            event.replyError("Please provide a username ``%s``".formatted(this.arguments));
            return;
        }

        String mojangUrl = "https://api.mojang.com/users/profiles/minecraft/" + username;
        Request request = new Request.Builder().url(mojangUrl).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.error("McSkin request failed", e);
                event.getChannel().sendMessage("❌ Failed to reach the Mojang API.").queue();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.code() == 404) {
                    event.getChannel().sendMessage("❌ Player **%s** was not found.".formatted(username)).queue();
                    return;
                }
                if (!response.isSuccessful()) {
                    event.getChannel().sendMessage("❌ Mojang API error: " + response.code()).queue();
                    return;
                }

                JSONObject body = new JSONObject(response.body().string());
                String name = body.optString("name", username);
                String rawUuid = body.optString("id", "unknown");

                String fullBodyUrl = "https://crafatar.com/renders/body/" + rawUuid + "?overlay&scale=10";
                String avatarUrl   = "https://crafatar.com/avatars/" + rawUuid + "?overlay";
                String rawSkinUrl  = "https://crafatar.com/skins/" + rawUuid;

                EmbedBuilder em = new EmbedBuilder()
                        .setTitle("🧍 " + name + "'s Skin")
                        .setThumbnail(avatarUrl)
                        .setColor(Color.GREEN)
                        .addField("Raw Skin File", "[Download](" + rawSkinUrl + ")", true)
                        .setImage(fullBodyUrl)
                        .setTimestamp(OffsetDateTime.now());

                event.getChannel().sendMessageEmbeds(em.build()).queue();
            }
        });
    }
}
