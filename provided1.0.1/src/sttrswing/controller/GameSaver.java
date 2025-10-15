package sttrswing.controller;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * Persists a stringified representation of game state to disk.
 */
public class GameSaver {

    private final String gameData;
    private final Path targetPath;
    private Boolean succeeded = null; // null = not run yet

    /**
     * Construct a new GameSaver instance with the data and target location we wish to save it to.
     *
     * @param gameData stringified representation of game state we wish to save
     * @param path     filepath for where we want to save the given data
     */
    public GameSaver(String gameData, String path) {
        this.gameData = gameData;
        this.targetPath = Path.of(path);
    }

    /**
     * Run the saving code, attempt to write to the file location with our stringified game data.
     */
    public void save() {
        try {
            // Ensure parent directory exists (if any)
            Path parent = targetPath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }

            // Null-safe: treat null data as empty file (or you could throw if preferred)
            byte[] bytes = (gameData == null ? "" : gameData).getBytes(StandardCharsets.UTF_8);

            // Write atomically-ish: create or truncate existing file
            Files.write(
                    targetPath,
                    bytes,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING,
                    StandardOpenOption.WRITE
            );

            succeeded = true;
        } catch (IOException e) {
            // You could log e here
            succeeded = false;
        }
    }

    /**
     * Return if the GameSaver succeeded.
     *
     * @return if the GameSaver succeeded (true/false), or null if save() has not been called yet.
     */
    public Boolean success() {
        return succeeded;
    }
}
