/* (c) https://github.com/MontiCore/monticore */
package montiarc.report;

import com.google.common.base.Preconditions;
import org.codehaus.commons.nullanalysis.NotNull;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.DataFormatException;

import static montiarc.report.IncCheckGenerationReporter.VER_PREFIX;

/**
 * Parses Files created by {@link de.monticore.generating.templateengine.reporting.reporter.IncGenGradleReporter}
 * for .arc files and deserializes them into {@link IncCheckData}.
 */
public final class IncCheckDataParser {

  private Path modelFile;
  private String modelHash;
  private final Set<Path> usedHwcFiles;
  private final Set<Path> absentHwcFiles;
  private final Set<Path> outFiles;
  private String versionInfo;

  private static final String ARC_PREFIX = "arc:";
  private static final String HWC_PREFIX = "hwc:";
  private static final String OUT_PREFIX = "out:";
  private static final String GEN_PREFIX = "gen:";

  private IncCheckDataParser() {
    this.usedHwcFiles = new HashSet<>();
    this.absentHwcFiles = new HashSet<>();
    this.outFiles = new HashSet<>();
    this.versionInfo = "";
  }

  /**
   * @throws IOException if the file could not be read
   * @throws ParseException if the file has a malformed structure
   */
  public static IncCheckData parseFromIncCheckFile(@NotNull Path incCheckFile) throws IOException, ParseException {
    Preconditions.checkArgument(incCheckFile.toFile().isFile());

    IncCheckDataParser parser = new IncCheckDataParser();
    return parser.deserialize(incCheckFile);
  }

  private IncCheckData deserialize(@NotNull Path incCheckFile) throws IOException, ParseException {
    try (Stream<String> lines = Files.lines(incCheckFile, Charset.defaultCharset())) {
      for (String line : lines.collect(Collectors.toList())) {
        this.processLine(line);
      }

    } catch (IOException e) {
      throw new IOException("Error parsing report from previous run. Incremental check not working.", e);
    }

    try {
      return new IncCheckData(modelFile, modelHash, usedHwcFiles, absentHwcFiles, outFiles, incCheckFile, versionInfo);
    } catch (DataFormatException e) {
      ParseException rethrown = new ParseException(e.getMessage(), 0);
      rethrown.initCause(e);
      throw rethrown;
    }
  }

  private void processLine(String line) throws ParseException {
    String linePrefix = line.substring(0, 4);
    switch (linePrefix) {
      case ARC_PREFIX: processModelFile(line); break;
      case HWC_PREFIX: processHwcFile(line); break;
      case GEN_PREFIX: processAbsentHwcFile(line); break;
      case OUT_PREFIX: processOutgoingFile(line); break;
      case VER_PREFIX: processVersionInfo(line); break;
      default: break;
    }
  }

  private void processModelFile(String line) throws ParseException {
    verifyLineHasPrefix(line, ARC_PREFIX);

    String data = line.substring(ARC_PREFIX.length());
    String[] components = data.split(" ");

    try {
      this.modelFile = Path.of(components[0]);
      this.modelHash = components[1];

    } catch (ArrayIndexOutOfBoundsException e) {
      throw new ParseException(String.format("Malformed report file: Too few entries in line '%s'", line), 4);
    }

    if (components.length > 2) {
      throw new ParseException(String.format("Malformed report file: Too many entries in line '%s'", line), 4);
    }
  }

  private void processHwcFile(String line) throws ParseException {
    verifyLineHasPrefix(line, HWC_PREFIX);

    String cleanLine = line.substring(HWC_PREFIX.length());
    usedHwcFiles.add(Path.of(cleanLine));
  }

  private void processAbsentHwcFile(String line) throws ParseException {
    verifyLineHasPrefix(line, GEN_PREFIX);

    String cleanLine = line.substring(GEN_PREFIX.length());
    absentHwcFiles.add(Path.of(cleanLine));
  }

  private void processOutgoingFile(String line) throws ParseException {
    verifyLineHasPrefix(line, OUT_PREFIX);

    String cleanLine = line.substring(OUT_PREFIX.length());
    outFiles.add(Path.of(cleanLine));
  }

  private void processVersionInfo(String line) throws ParseException {
    verifyLineHasPrefix(line, VER_PREFIX);
    versionInfo = line.substring(VER_PREFIX.length());
  }

  private void verifyLineHasPrefix(String line, String linePrefix) throws ParseException {
    if (!line.startsWith(linePrefix)) {
      throw new ParseException(String.format("'%s' should be a prefix of '%s'", linePrefix, line), 0);
    }
  }
}