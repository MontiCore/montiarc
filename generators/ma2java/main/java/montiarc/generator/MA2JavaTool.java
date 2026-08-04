/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator;

import com.google.common.base.Preconditions;
import de.monticore.generating.templateengine.reporting.Reporting;
import de.monticore.io.paths.MCPath;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcTool;
import montiarc._ast.ASTMACompilationUnit;
import montiarc._cocos.MontiArcCoCoChecker;
import montiarc.cocos.DseSupportedTypes;
import montiarc._cocos.IdentifiersAreNoJavaKeywords;
import montiarc.generator.codegen.MontiArcGenerator;
import montiarc.report.IncCheckUtil;
import montiarc.report.UpToDateResults;
import montiarc.report.VersionFileDeserializer;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.codehaus.commons.nullanalysis.NotNull;
import org.codehaus.commons.nullanalysis.Nullable;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;



public class MA2JavaTool extends MontiArcTool {

  public static final String MA2JAVA_INC_CHECK_REPORT_DIR = "ma2java-inc-data";
  public static final String MA2JAVA_INC_CHECK_VERSION_PATH = "buildInfo.properties";

  /*
   * We do not use MontiarcTool#version because when there are only code changes in the generator,
   * but not the original tool, then MontiarcTool#version will not change. By employing a new
   * version field here, we can capture changes in the generator.
   */
  private Supplier<String> ma2javaVersionSupplier =
    new VersionFileDeserializer(MA2JAVA_INC_CHECK_VERSION_PATH)::loadVersion;

  public void setMa2JavaVersionSupplier(@NotNull Supplier<String> versionSupplier) {
    this.ma2javaVersionSupplier = Preconditions.checkNotNull(versionSupplier);
  }

  public static void main(@NotNull String[] args) {
    Preconditions.checkNotNull(args);
    MA2JavaTool tool = new MA2JavaTool();
    tool.init();
    tool.run(args);
  }

  @Override
  public Options addStandardOptions(@NotNull Options options) {
    Preconditions.checkNotNull(options);
    options.addOption(org.apache.commons.cli.Option.builder("o")
      .longOpt("output")
      .desc("Generates java code to the specified directory")
      .hasArg()
      .argName("dir")
      .get());
    options.addOption(org.apache.commons.cli.Option.builder("hwc")
      .longOpt("handwritten-code")
      .desc("Sets the artifact path for handwritten code, space separated")
      .hasArgs()
      .argName("paths")
      .get());
    options.addOption(org.apache.commons.cli.Option.builder("dse")
      .longOpt("dynamic-symbolic-execution")
      .desc("Enables code generation for dynamic-symbolic execution")
      .get());
    return super.addStandardOptions(options);
  }

  @Override
  protected void doRun(@NotNull CommandLine cl) {
    Preconditions.checkArgument(!cl.hasOption("h"));
    Preconditions.checkArgument(!cl.hasOption("v"));
    Preconditions.checkArgument(cl.hasOption("i"));
    Preconditions.checkNotNull(cl);

    String[] i = cl.hasOption("i") ? splitPathEntries(cl.getOptionValues("i")) : new String[0];

    String[] p = cl.hasOption("path") ? splitPathEntries(cl.getOptionValues("path")) : new String[0];

    String[] hwc = cl.hasOption("hwc") ? splitPathEntries(cl.getOptionValues("hwc")) : new String[0];

    String o = cl.getOptionValue("o");

    String pp = cl.hasOption("pp") ? Optional.of(cl.getOptionValue("pp")).orElse("") : null;

    String s = cl.getOptionValue("s");

    String r = cl.getOptionValue("r");

    boolean c2mc = cl.hasOption("c2mc");

    boolean dse = cl.hasOption("dse");

    boolean novar = cl.hasOption("novar");

    this.doRun(i, p, hwc, o, pp, s, r, c2mc, dse, novar);
  }

  protected void doRun(@NotNull String[] i,
                       @NotNull String[] p,
                       @NotNull String[] hwc,
                       @Nullable String o,
                       @Nullable String pp,
                       @Nullable String s,
                       @Nullable String r,
                       boolean c2mc,
                       boolean dse,
                       boolean novar) {
    Preconditions.checkNotNull(i);
    Preconditions.checkNotNull(p);
    Preconditions.checkNotNull(hwc);
    Preconditions.checkArgument(i.length > 0);

    this.initGlobalScope(p);
    this.initBuiltInSymbols(c2mc);
    this.compile(i, hwc, o, pp, s, r, c2mc, dse, novar);
  }

  /**
   * Parses all MontiArc component models found in the specified input files
   * and directories and checks context-condition. Optionally pretty-prints
   * the models, serializes their symbol table, generates reports,
   * and translates the models to java code.
   * <p>
   * Class2MC (c2mc) can be enabled to import symbols from the Java runtime
   * environment (Java RTE). Context-condition checking for variable components
   * can be skipped (novar) to improve performance.
   *
   * @param i     Array of file and directory paths that form the model path,
   *              i.e, the paths that contain the MontiArc models to be compiled.
   *              At least one path must be provided.
   * @param hwc   Array of file and directory paths to handwritten java code to
   *              be considered for TOP classes during code generation.
   *              May be empty.
   * @param o     Path to the directory where the generated java code should be stored.
   *              If {@code null}, code generation is disabled.
   * @param pp    Path to the directory where pretty-printed models should be stored.
   *              If {@code null}, pretty-printing is disabled.
   *              If an empty string is provided, models are printed to standard output.
   * @param s     Path to the directory where the symbol table should be serialized.
   *              If {@code null}, symbol table serialization is disabled.
   * @param r     Path to the directory where reports should be stored.
   *              If {@code null}, report generation is disabled.
   * @param c2mc  Enables importing of Java symbols (via Class2MC).
   * @param dse   Enables code generation for dynamic symbolic execution.
   * @param novar Disables context-condition checking for variable components to improve performance.
   * @return A collection of the abstract syntay trees (ASTs) of the parsed
   * input models found in the given model paths.
   */
  public Set<ASTMACompilationUnit> compile(@NotNull String[] i,
                                           @NotNull String[] hwc,
                                           @Nullable String o,
                                           @Nullable String pp,
                                           @Nullable String s,
                                           @Nullable String r,
                                           boolean c2mc,
                                           boolean dse,
                                           boolean novar) {
    Preconditions.checkNotNull(i);
    Preconditions.checkNotNull(hwc);
    Preconditions.checkArgument(i.length > 0);

    Set<ASTMACompilationUnit> asts = super.compile(i, pp, s, r, c2mc, novar);

    if (dse) {
      Log.info(() -> "Check dse-specific context-conditions", "MA2JavaTool-dse");
      asts.forEach(this::runAdditionalCoCosDse);
    }

    if (o != null) {
      Log.info(() -> "Generate java", "MontiArcTool");
      this.generate(asts, i, hwc, o, r, dse);
    }

    return asts;
  }

  public void generate(@NotNull Collection<ASTMACompilationUnit> asts,
                       @NotNull String[] input,
                       @NotNull String[] hwc,
                       @NotNull String o,
                       @Nullable String r,
                       boolean dse) {
    Preconditions.checkNotNull(asts);
    Preconditions.checkNotNull(input);
    Preconditions.checkNotNull(hwc);
    Preconditions.checkNotNull(o);
    Preconditions.checkArgument(input.length > 0);

    Optional<String> reportDir = Optional.ofNullable(r);

    Collection<ASTMACompilationUnit> models4NewGeneration;

    boolean writeReports = reportDir.isPresent();
    if (writeReports) {
      IncCheckUtil.Config incCheckConfig = new IncCheckUtil.Config(
        Arrays.asList(input), o, reportDir.get(), MA2JAVA_INC_CHECK_REPORT_DIR, Arrays.asList(hwc), ma2javaVersionSupplier.get()
      );
      IncCheckUtil.configureIncCheckReporting(incCheckConfig);

      Map<String, ASTMACompilationUnit> astByQName = IncCheckUtil.resolveAstByQName(asts);
      UpToDateResults upToDateInfo = IncCheckUtil.calcUpToDateData(astByQName, incCheckConfig);

      IncCheckUtil.removeOutdatedGenerationResults(upToDateInfo, incCheckConfig);
      models4NewGeneration = IncCheckUtil.calcReportedModelsForNewGeneration(upToDateInfo, astByQName);
    } else {
      models4NewGeneration = asts;
    }

    // Pre-calculate some variable values that will be used for every processed model
    MCPath modelPaths = new MCPath(input);
    List<String> componentNames = asts.stream().map(a -> a.getArcComponentType().getName()).collect(Collectors.toList());
    List<String> imports = asts.stream()
      .map(a -> a.getArcComponentType().getSymbol().getPackageName())
      .distinct()
      .collect(Collectors.toList());

    // For every ast, execute the generation process
    for (ASTMACompilationUnit ast : models4NewGeneration) {
      Optional<Path> modelLocation = IncCheckUtil.findModelLocation(modelPaths, ast);
      boolean writeReport4ThisModel = writeReports && modelLocation.isPresent();

      // Init reporting for current ast
      if (writeReport4ThisModel) {
        IncCheckUtil.setIncCheckReportingOn(ast, modelLocation.get());
      }

      // In all cases:
      this.generate(ast, o, Arrays.asList(hwc), dse);
      if (dse) {
        this.generateDseOnlyFiles(ast, o, Arrays.asList(hwc), componentNames, imports);
      }

      if (writeReport4ThisModel) {
        Reporting.flush(ast);
      }
    }
  }

  public void generateDseOnlyFiles(@NotNull ASTMACompilationUnit ast,
                                   @NotNull String target,
                                   @NotNull List<String> hwcs,
                                   @NotNull List<String> componentNames,
                                   @NotNull List<String> imports) {

    Preconditions.checkNotNull(ast);
    Preconditions.checkNotNull(target);
    Preconditions.checkNotNull(hwcs);
    Preconditions.checkNotNull(componentNames);
    Preconditions.checkNotNull(imports);
    Preconditions.checkArgument(!target.isEmpty());

    List<Path> hwcsAsPath = hwcs.stream().map(Paths::get).collect(Collectors.toList());
    MontiArcGenerator generator = new MontiArcGenerator(Path.of(target), hwcsAsPath);
    generator.generateMain(ast, componentNames, new ArrayList<>(imports));
  }

  public void generate(@NotNull ASTMACompilationUnit ast, @NotNull String target,
                       @NotNull List<String> hwcs, boolean dse) {
    Preconditions.checkNotNull(ast);
    Preconditions.checkNotNull(target);
    Preconditions.checkNotNull(hwcs);
    Preconditions.checkArgument(!target.isEmpty());
    Preconditions.checkArgument(ast.getArcComponentType().isPresentSymbol());

    List<Path> hwcsAsPath = hwcs.stream().map(Paths::get).collect(Collectors.toList());
    MontiArcGenerator generator = new MontiArcGenerator(Path.of(target), hwcsAsPath);
    generator.generate(ast, dse);
  }

  @Override
  public void runAdditionalCoCos(@NotNull ASTMACompilationUnit ast) {
    Preconditions.checkNotNull(ast);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new IdentifiersAreNoJavaKeywords.PortNoNamesAreNoJavaKeywords());
    checker.addCoCo(new IdentifiersAreNoJavaKeywords.ParameterNamesAreNoJavaKeywords());
    checker.addCoCo(new IdentifiersAreNoJavaKeywords.TypeParameterNamesAreNoJavaKeywords());
    checker.addCoCo(new IdentifiersAreNoJavaKeywords.FieldNamesAreNoJavaKeywords());
    checker.addCoCo(new IdentifiersAreNoJavaKeywords.AutomatonStateNamesAreNoJavaKeywords());
    checker.addCoCo(new IdentifiersAreNoJavaKeywords.ComponentTypeNamesAreNoJavaKeywords());
    checker.addCoCo(new IdentifiersAreNoJavaKeywords.ComponentInstanceNamesAreNoJavaKeywords());

    checker.checkAll(ast);
  }

  public void runAdditionalCoCosDse(@NotNull ASTMACompilationUnit ast) {
    Preconditions.checkNotNull(ast);
    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();

    checker.addCoCo(new DseSupportedTypes.DseParameters_VariablesTypes());

    checker.checkAll(ast);
  }
}
