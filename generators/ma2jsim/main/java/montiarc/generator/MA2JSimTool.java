/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator;

import com.google.common.base.Preconditions;
import de.monticore.generating.templateengine.reporting.Reporting;
import de.monticore.io.paths.MCPath;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcMill;
import montiarc.MontiArcTool;
import montiarc._ast.ASTMACompilationUnit;
import montiarc._cocos.IdentifiersAreNoJavaKeywords;
import montiarc._cocos.MontiArcCoCoChecker;
import montiarc.generator.codegen.MA2JSimGen;
import montiarc.report.IncCheckUtil;
import montiarc.report.UpToDateResults;
import montiarc.report.VersionFileDeserializer;
import montiarc.util.MontiArcError;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.help.HelpFormatter;
import org.codehaus.commons.nullanalysis.NotNull;
import org.codehaus.commons.nullanalysis.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
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
import java.util.stream.Stream;

public class MA2JSimTool extends MontiArcTool {

  public static final String MA2JSIM_INC_CHECK_REPORT_DIR = "ma2jsim-inc-data";
  public static final String MA2JSIM_INC_CHECK_VERSION_PATH = "buildinfo.properties";

  /*
   * We do not use MontiarcTool#version because when there are only code changes in the generator,
   * but not the original tool, then MontiarcTool#version will not change. By employing a new
   * version field here, we can capture changes in the generator.
   */
  private Supplier<String> ma2jsimVersionSupplier =
    new VersionFileDeserializer(MA2JSIM_INC_CHECK_VERSION_PATH)::loadVersion;

  public void setMa2JavaVersionSupplier(@NotNull Supplier<String> versionSupplier) {
    this.ma2jsimVersionSupplier = Preconditions.checkNotNull(versionSupplier);
  }

  public static void main(@NotNull String[] args) {
    Preconditions.checkNotNull(args);
    MA2JSimTool tool = new MA2JSimTool();
    tool.init();
    tool.run(args);
  }

  @Override
  public void run(@NotNull String[] args) {
    try {
      //parse input options from the command line
      CommandLineParser cliParser = new DefaultParser();

      if (args.length > 0 && args[0].equals("run")) {
        if (args.length == 1) {
          printHelp();
          return;
        }
        Options options = this.initRunSimulationOptions();
        CommandLine cl = cliParser.parse(options, args, true);
        runSimulation(args[1], cl);
      }
    } catch (ParseException e) {
      Log.error(String.format(MontiArcError.TOOL_PARSE_IOEXCEPTION.toString(), e.getMessage()));
    } catch (Exception e) {
      Log.error(e.getMessage());
    }

    if (!(args.length > 0 && args[0].equals("run"))) {
      super.run(args);
    }
  }

  @Override
  public Options addStandardOptions(@NotNull Options options) {
    Preconditions.checkNotNull(options);
    super.addStandardOptions(options);
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
    return options;
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

    boolean novar = cl.hasOption("novar");

    this.doRun(i, p, hwc, o, pp, s, r, c2mc, novar);
  }

  protected void doRun(@NotNull String[] i,
                       @NotNull String[] p,
                       @NotNull String[] hwc,
                       @Nullable String o,
                       @Nullable String pp,
                       @Nullable String s,
                       @Nullable String r,
                       boolean c2mc,
                       boolean novar) {
    Preconditions.checkNotNull(i);
    Preconditions.checkNotNull(p);
    Preconditions.checkNotNull(hwc);
    Preconditions.checkArgument(i.length > 0);

    this.initGlobalScope(p);
    this.initBuiltInSymbols(c2mc);
    this.compile(i, hwc, o, pp, s, r, c2mc, novar);
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
                                           boolean novar) {
    Preconditions.checkNotNull(i);
    Preconditions.checkNotNull(hwc);
    Preconditions.checkArgument(i.length > 0);

    Set<ASTMACompilationUnit> asts = super.compile(i, pp, s, r, c2mc, novar);

    if (o != null) {
      Log.info(() -> "Generate java", "MontiArcTool");
      this.generate(asts, i, hwc, o, r);
    }

    return asts;
  }

  public void generate(@NotNull Collection<ASTMACompilationUnit> asts,
                       @NotNull String[] input,
                       @NotNull String[] hwc,
                       @NotNull String o,
                       @Nullable String r) {
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
        Arrays.asList(input), o, reportDir.get(), MA2JSIM_INC_CHECK_REPORT_DIR, Arrays.asList(hwc), ma2jsimVersionSupplier.get()
      );
      IncCheckUtil.configureIncCheckReporting(incCheckConfig);

      Map<String, ASTMACompilationUnit> astByQName = IncCheckUtil.resolveAstByQName(asts);
      UpToDateResults upToDateInfo = IncCheckUtil.calcUpToDateData(astByQName, incCheckConfig);

      IncCheckUtil.removeOutdatedGenerationResults(upToDateInfo, incCheckConfig);
      models4NewGeneration = IncCheckUtil.calcReportedModelsForNewGeneration(upToDateInfo, astByQName);
    } else {
      models4NewGeneration = asts;
    }

    MCPath modelPaths = new MCPath(input);

    for (ASTMACompilationUnit ast : models4NewGeneration) {
      Optional<Path> modelLocation = IncCheckUtil.findModelLocation(modelPaths, ast);
      boolean writeReport4ThisModel = writeReports && modelLocation.isPresent();

      // Init reporting for current ast
      if (writeReport4ThisModel) {
        IncCheckUtil.setIncCheckReportingOn(ast, modelLocation.get());
      }

      // In all cases
      this.generate(ast, o, hwc);

      if (writeReport4ThisModel) {
        Reporting.flush(ast);
      }
    }
  }

  public void generate(@NotNull ASTMACompilationUnit ast, @NotNull String target, @NotNull String[] hwc) {
    Preconditions.checkNotNull(ast);
    Preconditions.checkNotNull(target);
    Preconditions.checkNotNull(hwc);
    Preconditions.checkArgument(ast.getArcComponentType().isPresentSymbol());
    Preconditions.checkArgument(!target.isEmpty());

    List<Path> hwcsAsPaths = Arrays.stream(hwc).map(Paths::get).collect(Collectors.toList());
    MA2JSimGen generator = new MA2JSimGen(Paths.get(target), hwcsAsPaths);
    generator.generate(ast);
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

  @Override
  public void defaultImportTrafo(@NotNull ASTMACompilationUnit ast, boolean c2mc) {
    super.defaultImportTrafo(ast, c2mc);
    ast.addImportStatement(MontiArcMill.mCImportStatementBuilder()
      .setMCQualifiedName(MontiArcMill.mCQualifiedNameBuilder()
        .setPartsList(List.of("montiarc", "lang"))
        .build())
      .setStar(true)
      .build());
  }

  protected Options initRunSimulationOptions() {
    Options options = new Options();
    options.addOption(org.apache.commons.cli.Option.builder("cp")
      .hasArgs()
      .longOpt("classpath")
      .desc("Additional java user classes added to the simulation runtime classpath.")
      .get());
    return options;
  }

  protected void printHelp() {
    HelpFormatter formatter = HelpFormatter.builder().setShowSince(false).get();
    try {
      formatter.printHelp("MontiArc [build]", " The main MontiArc build command.", initOptions(), "", true);
      formatter.printHelp("MontiArc create <name>", " Create a new MontiArc project with the given name in the current folder.", initCreateOptions(), "", true);
      formatter.printHelp("MontiArc run <CompName.arc>",
        " Run the simulator for a component. Additional parameters are forwarded to the Component. This commands needs Java installed on the system.",
        initRunSimulationOptions(),
        "",
        true);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  protected void runSimulation(String inputPath, CommandLine options) throws InterruptedException, IOException {
    Path path = Paths.get(inputPath);
    String dir = path.getParent() != null ? path.getParent().toString() : ".";
    String fileName = path.getFileName().toString();
    int dotIndex = fileName.lastIndexOf('.');
    String name = (dotIndex > 0)
      ? fileName.substring(0, dotIndex)
      : fileName;

    StringBuilder classpath = new StringBuilder(System.getProperty("java.class.path"));
    if (options.hasOption("cp")) {
      String[] additionalCPValues = splitPathEntries(options.getOptionValues("cp"));
      for (String additionalCPValue : additionalCPValues) {
        classpath.append(File.pathSeparator).append(additionalCPValue);
      }
    }

    List<String> javacCommand = new ArrayList<>();
    javacCommand.add("javac");
    javacCommand.add("-cp");
    javacCommand.add(classpath.toString());
    javacCommand.add("-d");
    javacCommand.add(dir + File.separator + "build");

    try (Stream<Path> walk = Files.list(Path.of(dir))) {
      List<String> javaFiles = walk
        .map(Path::toString)
        .filter(s -> s.endsWith(".java"))
        .toList();
      javacCommand.addAll(javaFiles);
    }

    ProcessBuilder builder = new ProcessBuilder(javacCommand);
    Process process = builder.redirectErrorStream(true).start();
    String processOutput = new String(process.getInputStream().readAllBytes());
    process.waitFor();
    if (process.exitValue() != 0) {
      Log.error(MontiArcError.TOOL_SIMULATION_FAILED.format(process.exitValue(), processOutput));
      return;
    }

    classpath.append(File.pathSeparator).append(dir).append(File.separator).append("build");

    List<String> runCommand = new ArrayList<>();
    runCommand.add("java");
    runCommand.add("-cp");
    runCommand.add(classpath.toString());
    runCommand.add("Deploy" + name);
    runCommand.addAll(options.getArgList().subList(2, options.getArgList().size()));

    builder = new ProcessBuilder(runCommand);
    process = builder.redirectErrorStream(true).start();
    processOutput = new String(process.getInputStream().readAllBytes());
    process.waitFor();
    if (process.exitValue() != 0) {
      Log.error(MontiArcError.TOOL_SIMULATION_FAILED.format(process.exitValue(), processOutput));
    }
  }
}
