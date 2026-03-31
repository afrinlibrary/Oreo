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

public class McPlayer extends Command {

    private static final OkHttpClient client = new OkHttpClient();

    public McPlayer() {
        this.name = "mcplayer";
        this.help = "look up a Minecraft player's UUID and profile";
        this.category = Categories.general;
        this.aliases = new String[]{"player", "uuid"};
        this.arguments = "<username>";
    }

    @Override
    protected void execute(CommandEvent event) {
        String username = event.getArgs().trim();

        if (username.isBlank()) {
            event.replyError("Please provide a username ``%s``".formatted(this.arguments));
            return;
        }

        String url = "https://api.mojang.com/users/profiles/minecraft/" + username;
        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.error("McPlayer request failed", e);
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

                // Format UUID: xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx
                String uuid = rawUuid.length() == 32
                        ? "%s-%s-%s-%s-%s".formatted(
                            rawUuid.substring(0, 8),
                            rawUuid.substring(8, 12),
                            rawUuid.substring(12, 16),
                            rawUuid.substring(16, 20),
                            rawUuid.substring(20))
                        : rawUuid;

                String avatarUrl = "https://crafatar.com/avatars/" + rawUuid + "?overlay";
                String bodyUrl = "https://crafatar.com/renders/body/" + rawUuid + "?overlay";
                String nameHistoryUrl = "https://namemc.com/profile/" + rawUuid;

                EmbedBuilder em = new EmbedBuilder()
                        .setTitle("🎮 " + name)
                        .setThumbnail(avatarUrl)
                        .setColor(Color.CYAN)
                        .addField("Username", "`" + name + "`", true)
                        .addField("UUID", "`" + uuid + "`", false)
                        .addField("Profile", "[View on NameMC](" + nameHistoryUrl + ")", true)
                        .setImage(bodyUrl)
                        .setTimestamp(OffsetDateTime.now());

                event.getChannel().sendMessageEmbeds(em.build()).queue();
            }
        });
    }
}
