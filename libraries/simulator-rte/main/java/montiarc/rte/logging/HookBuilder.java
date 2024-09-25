/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.logging;

import de.se_rwth.commons.logging.ConsoleLogHook;
import de.se_rwth.commons.logging.FileLogHook;
import de.se_rwth.commons.logging.ILogHook;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;

/**
 * Convenient builder for creating filtered log hooks.
 * @see FilterHook
 */
@SuppressWarnings("unused")
public final class HookBuilder {
  private final List<String> monitoredElements;
  private final List<String> monitoredAspects;

  private final String logBasePath;
  private List<Path> fileLogPaths;
  private boolean logToConsole = false;

  public HookBuilder() {
    this.monitoredElements = new ArrayList<>();
    this.monitoredAspects = new ArrayList<>();
    this.fileLogPaths = new ArrayList<>();
    this.logBasePath = System.getenv(Configuration.LOG_BASE_PATH);
  }

  public HookBuilder reportToFile(Path reportFilePath) {
    if (reportFilePath == null) {
      throw new NullPointerException("HookBuilder#reportToFile must not be invoked with null argument");
    }
    fileLogPaths.add(reportFilePath);
    return this;
  }

  /**
   * Adds a file reporter to the given file path after prefixing it, if it is not absolute, with a path derived from the
   * @code pathPrefix}. If it is absolute, it the path is added without this prefix.
   *
   * @param pathPrefix Transforms the canonical name of this class to a path by creating a sequence of path segments from all package segments and the class name.
   */
  public HookBuilder reportToFile(Path reportFilePath, Class<?> pathPrefix) {
    if (reportFilePath == null || pathPrefix == null) {
      throw new NullPointerException("HookBuilder#reportToFile must not be invoked with null argument");
    }

    if (reportFilePath.isAbsolute()) {
      return reportToFile(reportFilePath);
    } else {
      String classNameAsPath =
        pathPrefix.getCanonicalName()
                  .replace('.', File.separatorChar);
      return reportToFile(Path.of(classNameAsPath).resolve(reportFilePath));
    }
  }

  /** Idempotent call */
  public HookBuilder reportToConsole() {
    logToConsole = true;
    return this;
  }

  /**
   * See {@link FilterHook} for details.
   */
  public HookBuilder addAspect(String aspect) {
    if (aspect == null) {
      throw new NullPointerException("HookBuilder#addAspect must not be invoked with null argument");
    }

    this.monitoredAspects.add(aspect);
    return this;
  }

  /**
   * Wildcards may be used, see {@link FilterHook} for details.
   */
  public HookBuilder monitor(String monitorString) {
    if (monitorString == null) {
      throw new NullPointerException("HookBuilder#monitor must not be invoked with null argument");
    }

    this.monitoredElements.add(monitorString);
    return this;
  }

  /**
   * Transforms all paths that have been added via {@link #reportToFile(Path)}.
   */
  public HookBuilder transformFilePaths(UnaryOperator<Path> transformer) {
    List<Path> transformed = new ArrayList<>(this.fileLogPaths.size());
    for (Path p : this.fileLogPaths) {
      transformed.add(transformer.apply(p));
    }
    this.fileLogPaths = transformed;

    return this;
  }

  public ILogHook build() {
    return new FilterHook(buildHooks(), monitoredElements, monitoredAspects);
  }

  private List<ILogHook> buildHooks() {
    List<ILogHook> hooks = new ArrayList<>(fileLogPaths.size() + 1);  // +1 for optional console log

    for (Path path : fileLogPaths) {
      if (logBasePath != null && !path.isAbsolute()) {
        path = Path.of(logBasePath).resolve(path);
      }
      hooks.add(new FileLogHook(path.toAbsolutePath().toString()));
    }

    if (logToConsole) {
      hooks.add(new ConsoleLogHook());
    }

    return hooks;
  }
}
