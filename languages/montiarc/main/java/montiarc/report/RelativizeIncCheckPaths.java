/* (c) https://github.com/MontiCore/monticore */
package montiarc.report;

import com.google.common.base.Preconditions;
import org.codehaus.commons.nullanalysis.NotNull;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Transforms paths to be relative to the model path, the hwc path or the output path. Used as {@code reportPathOutput}
 * in {@link de.monticore.generating.templateengine.reporting.reporter.IncGenGradleReporter#IncGenGradleReporter(String, java.util.function.Function, String)}
 * <br>
 * We relativize paths for the scenario similar to the following:
 * The inc check usually uses the <i>gen:</i> tag to define absolutely where it would expect a newly created hwc class.
 * E.g.: <i>gen:/home/myuser/documents/montiarc-project/main/java/pack/age/MyComponent.java</i>
 * In case a new hwc class is created there, the inc check can detect this with a simple check on the existence of a
 * file under that path. But imagine that a new hwc path is added before the tool is executed again, e.g. under
 * <i>/home/myuser/documents/montiarc-project/build/generatedByOtherProject/java</i>. If a hwc file is placed there, it
 * will not be detected by the inc check, as there is no <i>gen:</i> entry in the inc check file for it. The inc check
 * may come to the wrong conclusion that a regeneration is not necessary as there was no hwc file before and apparently
 * is not now either (at least not under the location registered in the inc check file).
 * <br>
 * This class provides a solution to this problem by relativizing the paths in the inc check file.
 * Our inc check will then append these relative paths to the current model / hwc / output path to also check
 * new locations.
 */
public class RelativizeIncCheckPaths implements UnaryOperator<Path> {

  protected final Collection<Path> modelPaths;
  protected final Collection<Path> hwcPaths;
  protected final Path outPath;

  public RelativizeIncCheckPaths(@NotNull Collection<Path> modelPaths,
                                 @NotNull Collection<Path> hwcPaths,
                                 @NotNull Path outPath) {
    Preconditions.checkNotNull(modelPaths);
    Preconditions.checkNotNull(hwcPaths);
    Preconditions.checkNotNull(outPath);
    Preconditions.checkArgument(!outPath.toString().contains(File.pathSeparator));

    this.modelPaths = preparePaths(modelPaths);
    this.hwcPaths = preparePaths(hwcPaths);
    this.outPath = preparePaths(List.of(outPath)).stream()
      .findFirst()
      .orElseThrow(IllegalStateException::new);
  }
  @Override
  public Path apply(@NotNull Path path) {
    Preconditions.checkNotNull(path);

    for (Path modelPath : modelPaths) {
      if (path.startsWith(modelPath)) {
        return modelPath.relativize(path);
      }
    }

    for (Path hwcPath : hwcPaths) {
      if (path.startsWith(hwcPath)) {
        return hwcPath.relativize(path);
      }
    }

    if (path.startsWith(outPath)) {
      return outPath.relativize(path);
    }

    return path;
  }

  /**
   * Prepares the given paths by splitting them at the path separator, normalizing them and making them absolute.
   */
  protected Collection<Path> preparePaths(@NotNull Collection<Path> paths) {
    Preconditions.checkNotNull(paths);

    return paths.stream()
      .map(this::splitPaths).flatMap(Collection::stream)
      .map(Path::toAbsolutePath)
      .map(Path::normalize)
      .collect(Collectors.toList());
  }

  /**
   * Splits the given path at the path separator.
   */
  protected final Collection<Path> splitPaths(@NotNull Path path) {
    Preconditions.checkNotNull(path);

    String[] elements = path.toString().split(Pattern.quote(File.pathSeparator));
    return Arrays.stream(elements)
      .map(Path::of)
      .collect(Collectors.toList());
  }
}
