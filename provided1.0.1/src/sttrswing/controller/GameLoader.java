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
import java.util.HashMap;
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
    success = false;
    enterprise = null;
    galaxy = null;
    try {
      Path p = Paths.get(path);
      if (!Files.exists(p)) {
        success = false;
        return;
      }
      lines.addAll(Files.readAllLines(p, StandardCharsets.UTF_8));
      enterprise = buildEnterprise();
      galaxy = buildGalaxy();
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

  /** Returns a string representation of the loaded file content. */
  @Override
  public String toString() {
    return String.join(System.lineSeparator(), lines);
  }

  /** Construct a new Galaxy instance using the currently loaded data. */
  public Galaxy buildGalaxy() {
    ArrayList<String> galaxyLines = galaxyLines();
    if (galaxyLines.isEmpty()) {
      throw new IllegalArgumentException("Missing galaxy information in save file.");
    }
    ArrayList<Quadrant> quadrants = new ArrayList<>();
    for (String line : galaxyLines) {
      if (line == null || line.isBlank()) {
        continue;
      }
      try {
        int x = parseLineForX(line);
        int y = parseLineForY(line);
        HashMap<String, Integer> symbol = parseLineForQuadrantSymbol(line);
        quadrants.add(
            new Quadrant(
                x,
                y,
                symbol.getOrDefault("starbases", 0),
                symbol.getOrDefault("klingons", 0),
                symbol.getOrDefault("stars", 0)));
      } catch (IOException e) {
        throw new IllegalArgumentException("Unable to parse galaxy line: " + line, e);
      }
    }
    Galaxy result = new Galaxy(quadrants);
    this.galaxy = result;
    return result;
  }

  /** Construct a new Enterprise instance using the currently loaded data. */
  public Enterprise buildEnterprise() {
    String line = enterpriseLine();
    if (line == null || line.isBlank()) {
      throw new IllegalArgumentException("Missing enterprise information in save file.");
    }
    Matcher matcher = ENTERPRISE_PATTERN.matcher(line.trim());
    if (matcher.matches()) {
      int x = Integer.parseInt(matcher.group(1));
      int y = Integer.parseInt(matcher.group(2));
      int energy = Integer.parseInt(matcher.group(3));
      int shields = Integer.parseInt(matcher.group(4));
      int torpedoes = Integer.parseInt(matcher.group(5));
      Enterprise result = new Enterprise(x, y, energy, shields, torpedoes);
      this.enterprise = result;
      return result;
    }
    try {
      int x = parseLineForX(line);
      int y = parseLineForY(line);
      int energy = parseLineForEnergy(line);
      int shields = parseLineForShields(line);
      int torpedoes = parseLineForTorpedoes(line);
      Enterprise result = new Enterprise(x, y, energy, shields, torpedoes);
      this.enterprise = result;
      return result;
    } catch (IOException e) {
      throw new IllegalArgumentException("Invalid enterprise line: " + line, e);
    }
  }

  /** Extract an x coordinate from the given line. */
  public int parseLineForX(String line) throws IOException {
    return extractNumber(line, "x:");
  }

  /** Extract a y coordinate from the given line. */
  public int parseLineForY(String line) throws IOException {
    return extractNumber(line, "y:");
  }

  /** Extract shield value from the given line. */
  public int parseLineForShields(String line) throws IOException {
    return extractNumber(line, "s:");
  }

  /** Extract energy value from the given line. */
  public int parseLineForEnergy(String line) throws IOException {
    return extractNumber(line, "e:");
  }

  /** Extract torpedo value from the given line. */
  public int parseLineForTorpedoes(String line) throws IOException {
    return extractNumber(line, "t:");
  }

  /** Extract the quadrant symbol metadata from the given line. */
  public HashMap<String, Integer> parseLineForQuadrantSymbol(String line) throws IOException {
    if (line == null) {
      throw new IOException("Unable to parse null line for quadrant symbol.");
    }
    Matcher matcher = QUADRANT_PATTERN.matcher(line.trim());
    if (!matcher.matches()) {
      throw new IOException("Line does not match quadrant pattern: " + line);
    }
    String symbol = matcher.group(3);
    if (symbol.length() < 3) {
      throw new IOException("Quadrant symbol must contain at least three digits: " + symbol);
    }
    int stars = Character.digit(symbol.charAt(0), 10);
    int klingons = Character.digit(symbol.charAt(1), 10);
    int starbases = Character.digit(symbol.charAt(2), 10);
    if (stars < 0 || klingons < 0 || starbases < 0) {
      throw new IOException("Quadrant symbol must contain numeric digits: " + symbol);
    }
    HashMap<String, Integer> result = new HashMap<>();
    result.put("stars", stars);
    result.put("klingons", klingons);
    result.put("starbases", starbases);
    return result;
  }

  private int extractNumber(String line, String token) throws IOException {
    if (line == null) {
      throw new IOException("Unable to parse null line for token " + token);
    }
    int index = line.indexOf(token);
    if (index < 0) {
      throw new IOException("Token '" + token + "' not present in line: " + line);
    }
    int start = index + token.length();
    int end = start;
    while (end < line.length() && Character.isDigit(line.charAt(end))) {
      end++;
    }
    if (start == end) {
      throw new IOException("No numeric value found for token '" + token + "' in line: " + line);
    }
    return Integer.parseInt(line.substring(start, end));
  }
}