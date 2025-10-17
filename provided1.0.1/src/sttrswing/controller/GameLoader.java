package sttrswing.controller;

import sttrswing.model.Enterprise;
import sttrswing.model.Galaxy;
import sttrswing.model.Quadrant;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Loads a .trek file and exposes enterpriseLine() and galaxyLines() as per Javadoc.
 * Javadoc: GameLoader(String path), load(), enterpriseLine(), galaxyLines(), success(), toString()
 */
public class GameLoader {
  private static final Pattern ENTERPRISE_PATTERN =
      Pattern.compile(
          "\\[e\\]\\s*x:(\\d+)\\s*y:(\\d+)\\s*e:(\\d+)\\s*s:(\\d+)\\s*t:(\\d+)\\s*(?:\\|)?");
  private static final Pattern QUADRANT_PATTERN =
      Pattern.compile("\\[q\\]\\s*x:(\\d+)\\s*y:(\\d+)\\s*s:(\\d{3})\\s*(?:\\|)?");

  private final String path;
  private Boolean success = false;
  private final ArrayList<String> lines = new ArrayList<>();
  private Enterprise enterprise;
  private Galaxy galaxy;

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
      enterprise = parseEnterpriseLine(enterpriseLine());
      galaxy = parseGalaxyLines(galaxyLines());
      success = true;
    } catch (IOException e) {
      success = false;
    } catch (IllegalArgumentException e) {
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

  /** Returns the reconstructed Enterprise from the loaded file. */
  public Enterprise enterprise() {
    if (!success || enterprise == null) {
      throw new IllegalStateException("Call load() successfully before requesting the enterprise.");
    }
    return enterprise;
  }

  /** Returns the reconstructed Galaxy from the loaded file. */
  public Galaxy galaxy() {
    if (!success || galaxy == null) {
      throw new IllegalStateException("Call load() successfully before requesting the galaxy.");
    }
    return galaxy;
  }

  /** Returns a string representation of the loaded file content. */
  @Override
  public String toString() {
    return String.join(System.lineSeparator(), lines);
  }

  private Enterprise parseEnterpriseLine(String line) {
    if (line == null || line.isBlank()) {
      throw new IllegalArgumentException("Missing enterprise information in save file.");
    }
    Matcher matcher = ENTERPRISE_PATTERN.matcher(line.trim());
    if (!matcher.matches()) {
      throw new IllegalArgumentException("Invalid enterprise line: " + line);
    }
    int x = Integer.parseInt(matcher.group(1));
    int y = Integer.parseInt(matcher.group(2));
    int energy = Integer.parseInt(matcher.group(3));
    int shields = Integer.parseInt(matcher.group(4));
    int torpedoes = Integer.parseInt(matcher.group(5));
    return new Enterprise(x, y, energy, shields, torpedoes);
  }

  private Galaxy parseGalaxyLines(ArrayList<String> lines) {
    if (lines.isEmpty()) {
      throw new IllegalArgumentException("Missing galaxy information in save file.");
    }
    ArrayList<Quadrant> quadrants = new ArrayList<>();
    for (String line : lines) {
      if (line == null || line.isBlank()) {
        continue;
      }
      Matcher matcher = QUADRANT_PATTERN.matcher(line.trim());
      if (!matcher.matches()) {
        throw new IllegalArgumentException("Invalid quadrant line: " + line);
      }
      int x = Integer.parseInt(matcher.group(1));
      int y = Integer.parseInt(matcher.group(2));
      String symbol = matcher.group(3);
      if (symbol.length() != 3) {
        throw new IllegalArgumentException("Unexpected quadrant symbol: " + symbol);
      }
      int stars = Character.digit(symbol.charAt(0), 10);
      int starbases = Character.digit(symbol.charAt(1), 10);
      int klingons = Character.digit(symbol.charAt(2), 10);
      if (stars < 0 || starbases < 0 || klingons < 0) {
        throw new IllegalArgumentException("Invalid counts in quadrant symbol: " + symbol);
      }
      quadrants.add(new Quadrant(x, y, starbases, klingons, stars));
    }
    return new Galaxy(quadrants);
  }
}