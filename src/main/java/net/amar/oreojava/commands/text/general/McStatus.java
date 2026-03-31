package net.amar.oreojava.commands.text.general;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.amar.oreojava.Log;
import net.amar.oreojava.commands.Categories;
import net.dv8tion.jda.api.EmbedBuilder;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.*;
import java.io.IOException;
import java.time.OffsetDateTime;

public class McStatus extends Command {

    private static final OkHttpClient client = new OkHttpClient();

    public McStatus() {
        this.name = "mcstatus";
        this.help = "check if a Minecraft server is online";
        this.category = Categories.general;
        this.aliases = new String[]{"serverstatus", "mcs"};
        this.arguments = "<server address>";
    }

    @Override
    protected void execute(CommandEvent event) {
        String address = event.getArgs().trim();

        if (address.isBlank()) {
            event.replyError("Please provide a server address ``%s``".formatted(this.arguments));
            return;
        }

        String url = "https://api.mcsrvstat.us/3/" + address;
        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.error("McStatus request failed", e);
                event.getChannel().sendMessage("❌ Failed to reach the status API.").queue();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    event.getChannel().sendMessage("❌ API returned an error: " + response.code()).queue();
                    return;
                }

                JSONObject body = new JSONObject(response.body().string());
                boolean online = body.optBoolean("online", false);

                EmbedBuilder em = new EmbedBuilder()
                        .setTitle("🖥️ Server Status — " + address)
                        .setTimestamp(OffsetDateTime.now());

                if (!online) {
                    em.setColor(Color.RED)
                            .setDescription("**Offline** or unreachable.");
                } else {
                    String version = body.optString("version", "Unknown");
                    int playersOnline = 0;
                    int playersMax = 0;

                    JSONObject players = body.optJSONObject("players");
                    if (players != null) {
                        playersOnline = players.optInt("online", 0);
                        playersMax = players.optInt("max", 0);
                    }

                    String motd = "No MOTD";
                    JSONObject motdObj = body.optJSONObject("motd");
                    if (motdObj != null) {
                        JSONArray clean = motdObj.optJSONArray("clean");
                        if (clean != null && !clean.isEmpty())
                            motd = clean.getString(0);
                    }

                    em.setColor(Color.GREEN)
                            .setDescription("**Online** ✅")
                            .addField("MOTD", motd, false)
                            .addField("Version", version, true)
                            .addField("Players", "%d / %d".formatted(playersOnline, playersMax), true);
                }

                event.getChannel().sendMessageEmbeds(em.build()).queue();
            }
        });
    }
}
