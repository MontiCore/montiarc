/* (c) https://github.com/MontiCore/monticore */
package montiarc.report;

import com.google.common.base.Preconditions;
import org.codehaus.commons.nullanalysis.NotNull;

import java.io.File;
import java.nio.file.Path;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.zip.DataFormatException;

/**
 * Information about involved files in a generation process:</br>
 * 1) The path to the MontiArc model that is the basis for the generation</br>
 * 2) The md5 hash of the MontiArc model in hexadecimal</br>
 * 3) Handwritten Code files for the MontiArc model
 * 4) Locations where no hwc files were found
 * 5) Files that were created / generated based on the MontiArc model</br>
 */
public final class IncCheckData {
  private final Path modelFile;
  private final String modelHash;
  private final Set<Path> usedHwcFiles;
  private final Set<Path> absentHwcFiles;
  private final Set<Path> outFiles;
  private final Path incCheckFile;

  /** 32 hexa digits = md5 hash */
  private final Pattern isHexaMd5Hash = Pattern.compile("[0-9a-f]{32}");

  public IncCheckData(@NotNull Path modelFile,
                      @NotNull String modelHash,
                      @NotNull Set<Path> usedHwcFiles,
                      @NotNull Set<Path> absentHwcFiles,
                      @NotNull Set<Path> outFiles,
                      @NotNull Path incCheckFile) throws DataFormatException {
    this.modelFile = checkValidPath(modelFile);
    this.modelHash = checkHash(modelHash);
    this.usedHwcFiles = Set.copyOf(checkValidPaths(usedHwcFiles));
    this.absentHwcFiles = Set.copyOf(checkValidPaths(absentHwcFiles));
    this.outFiles = Set.copyOf(checkValidPaths(outFiles));
    this.incCheckFile = checkValidPath(incCheckFile);
  }

  private Set<Path> checkValidPaths(@NotNull Set<Path> paths) throws DataFormatException {
    Preconditions.checkNotNull(paths);

    for (Path path : paths) {
      checkValidPath(path);
    }

    return paths;
  }

  private Path checkValidPath(@NotNull Path path) throws DataFormatException {
    Preconditions.checkNotNull(path);

    if (path.toString().contains(File.pathSeparator)) {
      throw new DataFormatException("Paths must not contain any path separators.");
    }

    return path;
  }

  private String checkHash(@NotNull String hash) throws DataFormatException {
    Preconditions.checkNotNull(hash);

    if (!isHexaMd5Hash.matcher(hash).matches()) {
      throw new DataFormatException("Hash must be a 32 digit hexadecimal number.");
    }

    return hash;
  }

  public Path getModelFile() {
    return modelFile;
  }

  public String getModelHash() {
    return modelHash;
  }

  public Set<Path> getUsedHwcFiles() {
    return usedHwcFiles;
  }

  public Set<Path> getAbsentHwcFiles() {
    return absentHwcFiles;
  }

  public Set<Path> getOutFiles() {
    return outFiles;
  }

  public Path getIncCheckFile() {
    return incCheckFile;
  }
}