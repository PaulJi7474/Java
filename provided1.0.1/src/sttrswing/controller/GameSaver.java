package sttrswing.controller;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * Saves the stringified game state to a target file.
 * Javadoc: GameSaver(String gameData, String path), save(), success()
 */
public class GameSaver {
  private final String gameData;
  private final String path;
  private Boolean success = false;

  public GameSaver(String gameData, String path) {
    this.gameData = gameData;
    this.path = path;
  }

  /** Run the saving code, attempt to write to the file location with our stringified game data. */
  public void save() {
    try {
      Path p = Paths.get(path);
      Path parent = p.getParent();
      if (parent != null) {
        Files.createDirectories(parent);
      }
      try (BufferedWriter writer = Files.newBufferedWriter(
          p,
          StandardCharsets.UTF_8,
          StandardOpenOption.CREATE,
          StandardOpenOption.TRUNCATE_EXISTING,
          StandardOpenOption.WRITE)) {
        writer.write(gameData == null ? "" : gameData);
      }
      success = true;
    } catch (IOException e) {
      success = false;
    }
  }

  /** Return if the GameSaver succeeded. */
  public Boolean success() {
    return success;
  }
}
