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
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;

public class McWiki extends Command {

    private static final OkHttpClient client = new OkHttpClient();

    public McWiki() {
        this.name = "mcwiki";
        this.help = "search the Minecraft Wiki";
        this.category = Categories.general;
        this.aliases = new String[]{"wiki", "mwiki"};
        this.arguments = "<search query>";
    }

    @Override
    protected void execute(CommandEvent event) {
        String query = event.getArgs().trim();

        if (query.isBlank()) {
            event.replyError("Please provide a search query ``%s``".formatted(this.arguments));
            return;
        }

        String encoded = URLEncoder.encode(query, StandardCharsets.UTF_8);
        String url = "https://minecraft.wiki/api.php?action=query&list=search&srsearch="
                + encoded + "&srlimit=3&format=json&srprop=snippet";

        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.error("McWiki request failed", e);
                event.getChannel().sendMessage("❌ Failed to reach the Minecraft Wiki API.").queue();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    event.getChannel().sendMessage("❌ Wiki API error: " + response.code()).queue();
                    return;
                }

                JSONObject body = new JSONObject(response.body().string());
                JSONObject queryObj = body.optJSONObject("query");
                if (queryObj == null) {
                    event.getChannel().sendMessage("❌ Unexpected response from Wiki.").queue();
                    return;
                }

                JSONArray results = queryObj.optJSONArray("search");
                if (results == null || results.isEmpty()) {
                    event.getChannel().sendMessage("❌ No results found for **%s**.".formatted(query)).queue();
                    return;
                }

                EmbedBuilder em = new EmbedBuilder()
                        .setTitle("📖 Minecraft Wiki — " + query)
                        .setColor(Color.CYAN)
                        .setTimestamp(OffsetDateTime.now());

                for (int i = 0; i < Math.min(3, results.length()); i++) {
                    JSONObject result = results.getJSONObject(i);
                    String title = result.optString("title", "Unknown");
                    String snippet = result.optString("snippet", "")
                            .replaceAll("<[^>]+>", "") // strip HTML tags
                            .trim();
                    String pageUrl = "https://minecraft.wiki/w/" + URLEncoder.encode(title, StandardCharsets.UTF_8).replace("+", "_");

                    em.addField(
                            (i + 1) + ". " + title,
                            "%s\n[Read more](%s)".formatted(snippet.isBlank() ? "" : snippet + "…", pageUrl),
                            false
                    );
                }

                event.getChannel().sendMessageEmbeds(em.build()).queue();
            }
        });
    }
}
