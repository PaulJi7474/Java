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

/**
 * Loads a .trek file and provides helpers for constructing model objects from it.
 */
public class GameLoader {

  private final String path;
  private final ArrayList<String> lines = new ArrayList<>();
  private Boolean success = false;

  public GameLoader(String path) {
    this.path = path;
  }

  public void load() {
    success = false;
    lines.clear();
    try {
      Path filePath = Paths.get(path);
      lines.addAll(Files.readAllLines(filePath, StandardCharsets.UTF_8));
      if (enterpriseLine().isEmpty()) {
        throw new IOException("Missing enterprise line");
      }
      if (galaxyLines().isEmpty()) {
        throw new IOException("Missing galaxy data");
      }
      buildEnterprise();
      buildGalaxy();
      success = true;
    } catch (IOException | IllegalStateException e) {
      success = false;
    }
  }

  public String enterpriseLine() {
    for (String line : lines) {
      if (line != null && line.trim().startsWith("[e]")) {
        return line.trim();
      }
    }
    return "";
  }

  public ArrayList<String> galaxyLines() {
    ArrayList<String> result = new ArrayList<>();
    for (String line : lines) {
      if (line != null && line.trim().startsWith("[q]")) {
        result.add(line.trim());
      }
    }
    return result;
  }

  @Override
  public String toString() {
    return "GameLoader{"
        + "path='" + path + '\''
        + ", success=" + success
        + ", enterpriseLine='" + enterpriseLine() + '\''
        + ", galaxyLines=" + galaxyLines()
        + '}';
  }

  public Galaxy buildGalaxy() {
    ArrayList<String> quadrantLines = galaxyLines();
    if (quadrantLines.isEmpty()) {
      throw new IllegalStateException("No galaxy data loaded");
    }
    ArrayList<Quadrant> quadrants = new ArrayList<>();
    for (String line : quadrantLines) {
      try {
        int x = parseLineForX(line);
        int y = parseLineForY(line);
        HashMap<String, Integer> counts = parseLineForQuadrantSymbol(line);
        quadrants.add(new Quadrant(
            x,
            y,
            counts.get("starbases"),
            counts.get("klingons"),
            counts.get("stars")));
      } catch (IOException e) {
        throw new IllegalStateException("Unable to parse galaxy line: " + line, e);
      }
    }
    return new Galaxy(quadrants);
  }

  public Enterprise buildEnterprise() {
    String line = enterpriseLine();
    if (line.isEmpty()) {
      throw new IllegalStateException("No enterprise data loaded");
    }
    try {
      int x = parseLineForX(line);
      int y = parseLineForY(line);
      int energy = parseLineForEnergy(line);
      int shields = parseLineForShields(line);
      int torpedoes = parseLineForTorpedoes(line);
      return new Enterprise(x, y, energy, shields, torpedoes);
    } catch (IOException e) {
      throw new IllegalStateException("Unable to parse enterprise line: " + line, e);
    }
  }

  public int parseLineForX(String line) throws IOException {
    if (line == null) {
      throw new IOException("Line is null");
    }
    int index = line.indexOf("x:");
    if (index < 0) {
      throw new IOException("Missing x value");
    }
    int start = index + 2;
    while (start < line.length() && Character.isWhitespace(line.charAt(start))) {
      start += 1;
    }
    int end = start;
    while (end < line.length() && Character.isDigit(line.charAt(end))) {
      end += 1;
    }
    if (start == end) {
      throw new IOException("Missing x digits");
    }
    try {
      return Integer.parseInt(line.substring(start, end));
    } catch (NumberFormatException e) {
      throw new IOException("Invalid x value", e);
    }
  }

  public int parseLineForY(String line) throws IOException {
    if (line == null) {
      throw new IOException("Line is null");
    }
    int index = line.indexOf("y:");
    if (index < 0) {
      throw new IOException("Missing y value");
    }
    int start = index + 2;
    while (start < line.length() && Character.isWhitespace(line.charAt(start))) {
      start += 1;
    }
    int end = start;
    while (end < line.length() && Character.isDigit(line.charAt(end))) {
      end += 1;
    }
    if (start == end) {
      throw new IOException("Missing y digits");
    }
    try {
      return Integer.parseInt(line.substring(start, end));
    } catch (NumberFormatException e) {
      throw new IOException("Invalid y value", e);
    }
  }

  public int parseLineForShields(String line) throws IOException {
    if (line == null) {
      throw new IOException("Line is null");
    }
    int index = line.indexOf("s:");
    if (index < 0) {
      throw new IOException("Missing shields value");
    }
    int start = index + 2;
    while (start < line.length() && Character.isWhitespace(line.charAt(start))) {
      start += 1;
    }
    int end = start;
    while (end < line.length() && Character.isDigit(line.charAt(end))) {
      end += 1;
    }
    if (start == end) {
      throw new IOException("Missing shields digits");
    }
    try {
      return Integer.parseInt(line.substring(start, end));
    } catch (NumberFormatException e) {
      throw new IOException("Invalid shields value", e);
    }
  }

  public int parseLineForEnergy(String line) throws IOException {
    if (line == null) {
      throw new IOException("Line is null");
    }
    int index = line.indexOf("e:");
    if (index < 0) {
      throw new IOException("Missing energy value");
    }
    int start = index + 2;
    while (start < line.length() && Character.isWhitespace(line.charAt(start))) {
      start += 1;
    }
    int end = start;
    while (end < line.length() && Character.isDigit(line.charAt(end))) {
      end += 1;
    }
    if (start == end) {
      throw new IOException("Missing energy digits");
    }
    try {
      return Integer.parseInt(line.substring(start, end));
    } catch (NumberFormatException e) {
      throw new IOException("Invalid energy value", e);
    }
  }

  public int parseLineForTorpedoes(String line) throws IOException {
    if (line == null) {
      throw new IOException("Line is null");
    }
    int index = line.indexOf("t:");
    if (index < 0) {
      throw new IOException("Missing torpedoes value");
    }
    int start = index + 2;
    while (start < line.length() && Character.isWhitespace(line.charAt(start))) {
      start += 1;
    }
    int end = start;
    while (end < line.length() && Character.isDigit(line.charAt(end))) {
      end += 1;
    }
    if (start == end) {
      throw new IOException("Missing torpedoes digits");
    }
    try {
      return Integer.parseInt(line.substring(start, end));
    } catch (NumberFormatException e) {
      throw new IOException("Invalid torpedoes value", e);
    }
  }

  public HashMap<String, Integer> parseLineForQuadrantSymbol(String line) throws IOException {
    if (line == null) {
      throw new IOException("Line is null");
    }
    int index = line.indexOf("s:");
    if (index < 0) {
      throw new IOException("Missing quadrant symbol");
    }
    int start = index + 2;
    while (start < line.length() && Character.isWhitespace(line.charAt(start))) {
      start += 1;
    }
    int end = start;
    while (end < line.length() && Character.isDigit(line.charAt(end))) {
      end += 1;
    }
    if (end - start != 3) {
      throw new IOException("Quadrant symbol must be three digits");
    }
    String digits = line.substring(start, end);
    int stars = Character.digit(digits.charAt(0), 10);
    int starbases = Character.digit(digits.charAt(1), 10);
    int klingons = Character.digit(digits.charAt(2), 10);
    if (stars < 0 || starbases < 0 || klingons < 0) {
      throw new IOException("Quadrant symbol contains invalid digits");
    }
    HashMap<String, Integer> counts = new HashMap<>();
    counts.put("stars", stars);
    counts.put("starbases", starbases);
    counts.put("klingons", klingons);
    return counts;
  }

  public Boolean success() {
    return success;
  }
}