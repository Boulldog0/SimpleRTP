package fr.Boulldogo.SimpleRTP;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.bukkit.plugin.java.JavaPlugin;

public class VersionChecker {

    public static void checkVersion(JavaPlugin plugin) {
        try {
            String currentVersion = plugin.getConfig().getString("version");

            URL url = new URL("https://api.github.com/repos/Boulldog0/SimpleRTP/releases/latest");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            String line;
            StringBuilder content = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }

            reader.close();

            String latestVersion = content.toString().split("\"tag_name\":\"")[1].split("\",")[0];

            if (currentVersion.equals(latestVersion)) {
                System.out.println("Le plugin SimpleRTP utilise la dernière version.");
            } else {
                System.out.println("Une nouvelle version de SimpleRTP (" + latestVersion + ") est disponible. Retrouvez la ici : https://github.com/Boulldog0/SimpleRTP");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
