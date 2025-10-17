package sttrswing.controller;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 * Loads a .trek file and exposes enterpriseLine() and galaxyLines() as per Javadoc.
 * Javadoc: GameLoader(String path), load(), enterpriseLine(), galaxyLines(), success(), toString()
 */
public class GameLoader {
  private final String path;
  private Boolean success = false;
  private final ArrayList<String> lines = new ArrayList<>();

  public GameLoader(String path) {
    this.path = path;
  }

  /** Attempt to load the file at the given path, track if it succeeds. */
  public void load() {
    lines.clear();
    try {
      Path p = Paths.get(path);
      if (!Files.exists(p)) {
        success = false;
        return;
      }
      lines.addAll(Files.readAllLines(p, StandardCharsets.UTF_8));
      success = true;
    } catch (IOException e) {
      success = false;
    }
  }

  /** Extract the line related to the enterprise. */
  public String enterpriseLine() {
    for (String line : lines) {
      if (line != null && line.startsWith("[e]")) {
        return line.trim();
      }
    }
    return "";
  }

  /** Extract each line that relates to the galaxy. */
  public ArrayList<String> galaxyLines() {
    ArrayList<String> result = new ArrayList<>();
    for (String line : lines) {
      if (line != null && line.startsWith("[q]")) {
        result.add(line.trim());
      }
    }
    return result;
  }

  /** Returns if the GameLoader has been toggled to successful. */
  public Boolean success() {
    return success;
  }

  /** Returns a string representation of the loaded file content. */
  @Override
  public String toString() {
    return String.join(System.lineSeparator(), lines);
  }
}
