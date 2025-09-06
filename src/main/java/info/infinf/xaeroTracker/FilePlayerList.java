package info.infinf.xaeroTracker;

import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FilePlayerList {
    private static final @NotNull Yaml yaml;

    private final Logger LOGGER;
    private final @NotNull Set<@NotNull String> playerList;
    public File file;

    static {
        var dumperOptions = new DumperOptions();
        dumperOptions.setPrettyFlow(true);
        yaml = new Yaml(dumperOptions);
    }

    public FilePlayerList(XaeroTracker plugin, File file) {
        Set<@NotNull String> tmpPlayerList;
        this.file = file;
        LOGGER = plugin.getLogger();
        try (var fis = new FileInputStream(file)) {
            tmpPlayerList = yaml.<ConcurrentHashMap.KeySetView<String, Boolean>>load(fis);
        } catch (FileNotFoundException e) {
            tmpPlayerList = ConcurrentHashMap.newKeySet();
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Cannot load file " + file.toPath().toAbsolutePath(), e);
            tmpPlayerList = ConcurrentHashMap.newKeySet();
        }
        playerList = tmpPlayerList == null ? ConcurrentHashMap.newKeySet() : tmpPlayerList;
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
        try (var fw = new FileWriter(file)) {
            yaml.dump(playerList, fw);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}