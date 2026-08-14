package info.infinf.xaeroTracker;

import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FilePlayerList {
    private static final @NotNull Yaml yaml;

    private final Logger LOGGER;
    private final @NotNull Set<@NotNull String> playerList;
    private final File file;

    static {
        var dumperOptions = new DumperOptions();
        dumperOptions.setPrettyFlow(true);
        yaml = new Yaml(dumperOptions);
    }

    public FilePlayerList(@NotNull XaeroTracker plugin, File file) {
        this.file = file;
        LOGGER = plugin.getLogger();

        Set<@NotNull String> tmpPlayerList = ConcurrentHashMap.newKeySet();
        try (var fis = new FileInputStream(file)) {
            var loaded = yaml.load(fis);
            if (loaded instanceof Collection<?> loadedCollection) {
                for (var entry : loadedCollection) {
                    if (entry instanceof String playerName) {
                        tmpPlayerList.add(playerName);
                    } else {
                        LOGGER.warning("Ignoring a non-string entry in " + file.toPath().toAbsolutePath());
                    }
                }
            } else if (loaded != null) {
                LOGGER.warning("Ignoring invalid player list in " + file.toPath().toAbsolutePath());
            }
        } catch (FileNotFoundException ignored) {
            // The list is empty until the first toggle creates the file.
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Cannot load file " + file.toPath().toAbsolutePath(), e);
        }
        playerList = tmpPlayerList;
    }

    public boolean toggle(String name) {
        if (playerList.contains(name)) {
            playerList.remove(name);
            save();
            return false;
        } else {
            playerList.add(name);
            save();
            return true;
        }
    }

    public boolean contains(String name) {
        return playerList.contains(name);
    }

    protected void save() {
        try (var writer = Files.newBufferedWriter(file.toPath(), StandardCharsets.UTF_8)) {
            yaml.dump(playerList, writer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
