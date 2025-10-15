package sttrswing.controller;

import sttrswing.model.Galaxy;
import sttrswing.model.Enterprise;
import sttrswing.model.Quadrant;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Loader for .trek save files. Public API strictly matches the spec.
 */
public class GameLoader {

  private final String path;
  private List<String> lines = new ArrayList<>();
  private boolean ok = false;

  // tokens like x:12 y:3 e:2500 s:500 t:10  或 [q] ... s:201
  private static final Pattern X_PATTERN = Pattern.compile("\\bx:(\\d+)\\b");
  private static final Pattern Y_PATTERN = Pattern.compile("\\by:(\\d+)\\b");
  private static final Pattern E_PATTERN = Pattern.compile("\\be:(\\d+)\\b");
  private static final Pattern S_PATTERN = Pattern.compile("\\bs:(\\d+)\\b");
  private static final Pattern T_PATTERN = Pattern.compile("\\bt:(\\d+)\\b");

  /** public GameLoader(String path) */
  public GameLoader(final String path) {
    this.path = Objects.requireNonNull(path, "path must not be null");
  }

  /** public void load() */
  public void load() {
    this.lines = new ArrayList<>();
    this.ok = false;
    try (BufferedReader br = new BufferedReader(new FileReader(this.path))) {
      String line;
      while ((line = br.readLine()) != null) {
        this.lines.add(line.trim());
      }
      // legal: at least one enterprise line + >=1 galaxy line
      this.ok = (enterpriseLine() != null && !galaxyLines().isEmpty());
    } catch (IOException e) {
      this.ok = false;
    }
  }

  /** public String enterpriseLine() */
  public String enterpriseLine() {
    if (this.lines == null) return null;
    for (String l : this.lines) {
      if (l.startsWith("[e]")) return l;
    }
    // Fallback: if the implicit assumption of the first line being the enterprise line fails, take the first non-empty line
    for (String l : this.lines) {
      if (!l.isEmpty()) return l;
    }
    return null;
  }

  /** public ArrayList<String> galaxyLines() */
  public ArrayList<String> galaxyLines() {
    ArrayList<String> out = new ArrayList<>();
    if (this.lines == null) return out;
    for (String l : this.lines) {
      if (l.startsWith("[q]")) out.add(l);
    }
    return out;
  }

  /** public String toString() */
  @Override
  public String toString() {
    return "GameLoader{path='" + this.path + "', success=" + this.ok
        + ", lines=" + (this.lines == null ? 0 : this.lines.size()) + "}";
  }

  /** public Galaxy buildGalaxy() */
  public Galaxy buildGalaxy() {
    ArrayList<Quadrant> quadrants = new ArrayList<>();
    for (String qLine : galaxyLines()) {
      try {
        int gx = parseLineForX(qLine);
        int gy = parseLineForY(qLine);
        HashMap<String, Integer> counts = parseLineForQuadrantSymbol(qLine);
        int stars = counts.getOrDefault("stars", 0);
        int starbases = counts.getOrDefault("starbases", 0);
        int klingons = counts.getOrDefault("klingons", 0);
        // Quadrant(gx, gy, starbases, klingons, stars) —— order by symbol() method
        Quadrant q = new Quadrant(gx, gy, starbases, klingons, stars);
        quadrants.add(q);
      } catch (IOException ignored) { /* skip malformed lines */ }
    }
    return new Galaxy(quadrants);
  }

  /** public Enterprise buildEnterprise() */
  public Enterprise buildEnterprise() {
    String eLine = enterpriseLine();
    if (eLine == null) return new Enterprise(); // Fallback to default Enterprise
    try {
      int x = parseLineForX(eLine);
      int y = parseLineForY(eLine);
      int energy = parseLineForEnergy(eLine);
      int shields = parseLineForShields(eLine);
      int torps = parseLineForTorpedoes(eLine);
      return new Enterprise(x, y, energy, shields, torps);
    } catch (IOException ex) {
      return new Enterprise();
    }
  }

  /** public int parseLineForX(String) throws IOException */
  public int parseLineForX(final String line) throws IOException {
    return extractInt(line, X_PATTERN, "x");
  }

  /** public int parseLineForY(String) throws IOException */
  public int parseLineForY(final String line) throws IOException {
    return extractInt(line, Y_PATTERN, "y");
  }

  /** public int parseLineForShields(String) throws IOException */
  public int parseLineForShields(final String line) throws IOException {
    // Enterprise line's s: value (shields); [q] lines use parseLineForQuadrantSymbol
    return extractInt(line, S_PATTERN, "s");
  }

  /** public int parseLineForEnergy(String) throws IOException */
  public int parseLineForEnergy(final String line) throws IOException {
    return extractInt(line, E_PATTERN, "e");
  }

  /** public int parseLineForTorpedoes(String) throws IOException */
  public int parseLineForTorpedoes(final String line) throws IOException {
    return extractInt(line, T_PATTERN, "t");
  }

  /** public HashMap<String,Integer> parseLineForQuadrantSymbol(String) throws IOException */
  public HashMap<String,Integer> parseLineForQuadrantSymbol(final String line) throws IOException {
    Matcher m = S_PATTERN.matcher(line);
    if (!m.find()) throw new IOException("no s:XYZ token in: " + line);
    String digits = m.group(1);
    if (!digits.matches("\\d{3}")) throw new IOException("quadrant symbol must be 3 digits: " + digits);

    // s:XYZ —— X=stars, Y=starbases, Z=klingons （与 Quadrant.symbol() 对齐）
    HashMap<String,Integer> map = new HashMap<>();
    map.put("stars", Character.digit(digits.charAt(0), 10));
    map.put("starbases", Character.digit(digits.charAt(1), 10));
    map.put("klingons", Character.digit(digits.charAt(2), 10));
    return map;
  }

  /** public Boolean success() */
  public Boolean success() {
    return this.ok;
  }

  // ---- private helpers ----
  private static int extractInt(final String line, final Pattern pattern, final String token) throws IOException {
    if (line == null) throw new IOException("line is null");
    Matcher m = pattern.matcher(line);
    if (!m.find()) throw new IOException("missing '" + token + ":' in: " + line);
    try {
      return Integer.parseInt(m.group(1));
    } catch (NumberFormatException ex) {
      throw new IOException("bad integer for '" + token + ":' in: " + line);
    }
  }
}
