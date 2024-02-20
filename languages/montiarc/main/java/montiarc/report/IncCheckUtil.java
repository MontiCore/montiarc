/* (c) https://github.com/MontiCore/monticore */
package montiarc.report;

import com.google.common.base.Preconditions;
import de.monticore.generating.templateengine.reporting.Reporting;
import de.monticore.generating.templateengine.reporting.commons.ReportManager;
import de.monticore.io.paths.MCPath;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTMACompilationUnit;
import org.codehaus.commons.nullanalysis.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/** Contains methods that tools usually use when processing asts for the aspect of inc-checking */
public final class IncCheckUtil {

  private IncCheckUtil() {}

  public static final class Config {
    private final String modelpath;
    private final String targetDir;
    private final String reportingBaseDir;
    private final String concreteReportingSubDir;
    private final String hwc;

    /**
     * @param reportingBaseDir may an empty string if no reporting is desired {@code ""}
     * @param concreteReportingSubDir conventional subdirectory of {@code reportingBaseDir}. Has to be present
     * @param hwc may be an empty String {@code ""} if no handwritten code path is available
     */
    public Config (
      @NotNull String modelpath,
      @NotNull String targetDir,
      @NotNull String reportingBaseDir,
      @NotNull String concreteReportingSubDir,
      @NotNull String hwc) {

      this.modelpath = Preconditions.checkNotNull(modelpath);
      this.targetDir = Preconditions.checkNotNull(targetDir);
      this.reportingBaseDir = Preconditions.checkNotNull(reportingBaseDir);
      this.concreteReportingSubDir = Preconditions.checkNotNull(concreteReportingSubDir);
      this.hwc = Preconditions.checkNotNull(hwc);

      Preconditions.checkArgument(!modelpath.isEmpty());
      Preconditions.checkArgument(!targetDir.isEmpty());
    }

    public String getModelpath() {
      return modelpath;
    }

    public String getTargetDir() {
      return targetDir;
    }

    public String getReportingBaseDir() {
      return reportingBaseDir;
    }

    public String getConcreteReportingSubDir() {
      return concreteReportingSubDir;
    }

    public String getHwc() {
      return hwc;
    }
  }

  /**
   * Initializes the reporting infrastructure with incremental-checking specific reporters.
   */
  public static void configureIncCheckReporting(@NotNull Config config) {
    Preconditions.checkNotNull(config);
    UnaryOperator<Path> reportPathTransformer = new RelativizeIncCheckPaths(
      Path.of(config.getModelpath()),
      config.getHwc().isEmpty() ? Optional.empty() : Optional.of(Path.of(config.getHwc())),
      Path.of(config.getTargetDir())
    );

    ReportManager.ReportManagerFactory reporterFactory = new IncCheckReports(
      config.getReportingBaseDir() + File.separator + config.getConcreteReportingSubDir(),
      reportPathTransformer
    );

    Reporting.init(config.getReportingBaseDir(), config.getConcreteReportingSubDir(), reporterFactory);
  }

  /**
   * Puts all asts into a map where they can be accessed by the qualified name of their top level component
   */
  public static Map<String, ASTMACompilationUnit> resolveAstByQName(@NotNull Collection<ASTMACompilationUnit> asts) {
    Preconditions.checkNotNull(asts);
    return asts.stream().collect(Collectors.toMap(
      a -> a.getComponentType().getSymbol().getFullName(),
      Function.identity()
    ));
  }

  /**
   * Checks whether a model is up-to-date for every parsed model and model known from a previous run of this tool.
   */
  public static UpToDateResults calcUpToDateData(@NotNull Map<String, ASTMACompilationUnit> astByQName,
                                                 @NotNull Config config) {
    Preconditions.checkNotNull(astByQName);
    Preconditions.checkNotNull(config);

    Map<String, IncCheckData> incCheckDataByQName = deserializeIncCheckData(config);
    UpToDateCalculator upToDateCalc = new UpToDateCalculator(
      splitPathEntriesToList(config.getModelpath()),
      config.getHwc().isEmpty() ? Collections.emptyList() : splitPathEntriesToList(config.getHwc()),
      Path.of(config.getTargetDir())
    );
    return upToDateCalc.checkUpToDateness(astByQName, incCheckDataByQName);
  }

  /**
   * Checks whether there are is inc check data from reports from previous tool runs and for deserializes these reports
   * into {@link IncCheckData}, qualified by the qualified name of the model that they describe.
   */
  public static Map<String, IncCheckData> deserializeIncCheckData(Config config) {
    String fullReportDir = config.getReportingBaseDir() + File.separator + config.getConcreteReportingSubDir();

    if (config.getReportingBaseDir().isEmpty()) {
      return Collections.emptyMap();
    }

    Map<String, IncCheckData> result = new HashMap<>();
    Path reportDirPath = Path.of(fullReportDir);

    List<Path> incCheckFiles;

    try (Stream<Path> reportFiles = Files.walk(reportDirPath)) {
      incCheckFiles = reportFiles
        .filter(p -> p.toFile().isFile())
        .filter(p -> p.endsWith("IncGenGradleCheck.txt"))
        .collect(Collectors.toList());
    } catch (IOException e) {
      Log.debug(String.format(
        "Could not fully read old reports. Increment data will only partially be evaluated. '%s'", e.getMessage()
      ), "IncCheckUtil");
      return result;
    }

    for (Path incCheckFile : incCheckFiles) {
      try {
        IncCheckData data = IncCheckDataParser.parseFromIncCheckFile(incCheckFile);

        String qName = data.getModelFile().toString().replace(File.separator, ".");
        qName = qName.substring(0, qName.length() - ".arc".length());

        result.put(qName, data);
      } catch (IOException | java.text.ParseException e) {
        Log.warn(String.format(
          "Error while parsing report file '%s'. Increment data will only partially be evaluated. '%s'",
          incCheckFile, e.getMessage()
        ));
      }
    }

    return result;
  }

  /**
   * Removes generated files that are outdated, using the given up-to-date information.
   */
  public static void removeOutdatedGenerationResults(@NotNull UpToDateResults upToDateInfo, @NotNull Config config) {
    Preconditions.checkNotNull(upToDateInfo);
    Preconditions.checkNotNull(config);
    Path targetPath = Path.of(config.targetDir);

    for (IncCheckData data4Deleted : upToDateInfo.deletedModels.values()) {
      List<Path> outFilesResolvedByTargetDir = data4Deleted.getOutFiles().stream()
        .map(targetPath::resolve)
        .collect(Collectors.toList());
      tryDeleteFilesAt(outFilesResolvedByTargetDir);
      tryDeleteFileAt(data4Deleted.getIncCheckFile());
    }

    for (IncCheckData data4Modified : upToDateInfo.updatedModels.values()) {
      List<Path> outFilesResolvedByTargetDir = data4Modified.getOutFiles().stream()
        .map(targetPath::resolve)
        .collect(Collectors.toList());
      tryDeleteFilesAt(outFilesResolvedByTargetDir);
    }
  }

  /**
   * Determines for which parsed models a new generation process is needed, using the given up-to-date information.
   */
  public static Collection<ASTMACompilationUnit> calcReportedModelsForNewGeneration(
    @NotNull UpToDateResults upToDateInfo,
    @NotNull Map<String, ASTMACompilationUnit> astByQName) {
    Preconditions.checkNotNull(upToDateInfo);
    Preconditions.checkNotNull(astByQName);

    Set<ASTMACompilationUnit> result =
      new HashSet<>(upToDateInfo.addedModels.size() + upToDateInfo.updatedModels.size());
    upToDateInfo.addedModels.forEach(addedName -> result.add(astByQName.get(addedName)));
    upToDateInfo.updatedModels.keySet().forEach(addedName -> result.add(astByQName.get(addedName)));

    return result;
  }

  /**
   * Sets the reporting focus on the given model to start reporting inc-check-data.
   */
  public static void setIncCheckReportingOn(@NotNull ASTMACompilationUnit ast, @NotNull Path modelLocation) {
    Preconditions.checkNotNull(ast);
    Preconditions.checkNotNull(modelLocation);

    String simpleName = ast.getComponentType().getName();
    String packName = ast.getComponentType().getSymbol().getPackageName();
    String qName = ast.getComponentType().getSymbol().getFullName();

    // The generated folder that contains the incGen file will be in lower case, whether the last path element contains
    // capital letters or not. However, if we not apply the toLowerCase transformation a folder with the model name
    // (correctly capitalized) we also be created. However, it will be empty and thus redundant and confusing when
    // navigating the directory. Therefore, we apply the toLowerCase transformation.
    Reporting.on(Names.getPathFromPackage(packName) + File.separator + simpleName.toLowerCase());
    Reporting.reportParseInputFile(modelLocation, Names.getPathFromPackage(qName));
  }

  /**
   * If present, locates the model in the given model path.
   */
  public static Optional<Path> findModelLocation(@NotNull MCPath modelPath, @NotNull ASTMACompilationUnit ast) {
    Preconditions.checkNotNull(modelPath);
    Preconditions.checkNotNull(ast);

    String qName = ast.getComponentType().getSymbol().getFullName();
    return modelPath
      .find(qName, "arc")
      .flatMap(MCPath::toPath);
  }

  public static void tryDeleteFilesAt(Collection<Path> fileLocations) {
    fileLocations.forEach(IncCheckUtil::tryDeleteFileAt);
  }
  private static void tryDeleteFileAt(Path fileLocation) {
    if (fileLocation.toFile().exists()) {
      try {
        Files.delete(fileLocation);
      } catch (IOException e) {
        Log.warn(String.format("Could not delete generated outdated file '%s': %s", fileLocation, e.getMessage()));
      }
    }
  }

  /**
   * Like {@link #splitPathEntries(String)}, but returns a {@code List<Path>} instead.
   */
  private static @NotNull List<Path> splitPathEntriesToList(@NotNull String composedPath) {
    return Arrays.stream(splitPathEntries(composedPath))
      .map(Path::of)
      .collect(Collectors.toList());
  }

  /**
   * Splits composedPath on their {@link File#pathSeparator}, e.g. {@code some/path:another/path} on Unix would return
   * {@code {some/path, another/path}} and {@code some\path;other\path} on Windows would return
   * {@code {some\path, other\path}}
   */
  private static @NotNull String[] splitPathEntries(@NotNull String composedPath) {
    Preconditions.checkNotNull(composedPath);

    return composedPath.split(Pattern.quote(File.pathSeparator));
  }
}
