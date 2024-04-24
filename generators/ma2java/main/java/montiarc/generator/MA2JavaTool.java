/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator;

import com.google.common.base.Preconditions;
import de.monticore.generating.templateengine.reporting.Reporting;
import de.monticore.io.paths.MCPath;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.MCFatalError;
import montiarc.MontiArcTool;
import montiarc._ast.ASTMACompilationUnit;
import montiarc._cocos.MontiArcCoCoChecker;
import montiarc.cocos.DseSupportedTypes;
import montiarc.generator.codegen.MontiArcGenerator;
import montiarc.report.VersionFileDeserializer;
import montiarc.report.IncCheckUtil;
import montiarc.report.UpToDateResults;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.codehaus.commons.nullanalysis.NotNull;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static montiarc.cocos.IdentifiersAreNoJavaKeywords.AutomatonStateNamesAreNoJavaKeywords;
import static montiarc.cocos.IdentifiersAreNoJavaKeywords.ComponentInstanceNamesAreNoJavaKeywords;
import static montiarc.cocos.IdentifiersAreNoJavaKeywords.ComponentTypeNamesAreNoJavaKeywords;
import static montiarc.cocos.IdentifiersAreNoJavaKeywords.FieldNamesAreNoJavaKeywords;
import static montiarc.cocos.IdentifiersAreNoJavaKeywords.ParameterNamesAreNoJavaKeywords;
import static montiarc.cocos.IdentifiersAreNoJavaKeywords.PortNoNamesAreNoJavaKeywords;
import static montiarc.cocos.IdentifiersAreNoJavaKeywords.TypeParameterNamesAreNoJavaKeywords;

public class MA2JavaTool extends MontiArcTool {

  public static final String MA2JAVA_INC_CHECK_REPORT_DIR = "ma2java-inc-data";
  public static final String MA2JAVA_INC_CHECK_VERSION_PATH = "montiarc/generator/MA2JavaToolVersion.txt";

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
    try {
      tool.run(args);
    } catch (MCFatalError error) {
      if (Log.isDebugEnabled("")) {
        throw new Error("Compilation failed; see the compiler error output for details.", error);
      } else {
        throw new Error("Compilation failed; see the compiler error output for details.");
      }
    }
  }

  @Override
  public Options addStandardOptions(@NotNull Options options) {
    Preconditions.checkNotNull(options);
    options.addOption(org.apache.commons.cli.Option.builder("o")
      .longOpt("output")
      .hasArgs()
      .desc("Sets the target path for the generated files (optional).")
      .build());
    options.addOption(org.apache.commons.cli.Option.builder("hwc")
      .longOpt("handwritten-code")
      .hasArgs()
      .desc("Sets the artifact path for handwritten code (optional).")
      .build());
    options.addOption(org.apache.commons.cli.Option.builder("dse")
      .longOpt("dynamic-symbolic-execution")
      .desc("Sets the template to symbolic template).")
      .build());
    return super.addStandardOptions(options);
  }

  @Override
  public void runTasks(@NotNull Collection<ASTMACompilationUnit> asts, @NotNull CommandLine cl) {
    Preconditions.checkNotNull(asts);
    Preconditions.checkNotNull(cl);
    super.runTasks(asts, cl);

    if (cl.hasOption("dse")) {
      Log.info("Perform remaining context-condition checks", "MontiArcTool-dse");
      asts.forEach(this::runAdditionalCoCosDse);
    }

    if (cl.hasOption("output")) {
      Log.info("Generate java", "MontiArcTool");

      this.generate(asts, cl);
    }
  }

  /**
   * @param cl Must at least contain the option "output"
   */
  public void generate(@NotNull Collection<ASTMACompilationUnit> asts, @NotNull CommandLine cl) {
    // The majority of this method deals about the reporting of the tooling execution.
    Preconditions.checkNotNull(asts);
    Preconditions.checkNotNull(cl);
    Preconditions.checkArgument(cl.hasOption("output"));

    String target = cl.getOptionValue("output");
    List<String> modelpaths = getAllModelDirsFrom(cl);
    Optional<String> reportDir =  Optional.ofNullable(cl.getOptionValue("report"));
    List<String> hwcs = getAllHwcDirsFrom(cl);
    boolean dse = cl.hasOption("dse");

    Collection<ASTMACompilationUnit> models4NewGeneration;

    boolean writeReports = reportDir.isPresent() && !modelpaths.isEmpty();
    if(writeReports) {
      IncCheckUtil.Config incCheckConfig = new IncCheckUtil.Config(
        modelpaths, target, reportDir.get(), MA2JAVA_INC_CHECK_REPORT_DIR, hwcs, ma2javaVersionSupplier.get()
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
    MCPath modelPaths = new MCPath(modelpaths.toArray(new String[0]));
    List<String> componentNames = asts.stream().map(a -> a.getComponentType().getName()).collect(Collectors.toList());
    List<String> imports = asts.stream()
      .map(a -> a.getComponentType().getSymbol().getPackageName())
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
      this.generate(ast, target, hwcs, dse);
      if (dse) {
        this.generateDseOnlyFiles(ast, target, hwcs, componentNames, imports);
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
    Preconditions.checkArgument(ast.getComponentType().isPresentSymbol());

    List<Path> hwcsAsPath = hwcs.stream().map(Paths::get).collect(Collectors.toList());
    MontiArcGenerator generator = new MontiArcGenerator(Path.of(target), hwcsAsPath);
    generator.generate(ast, dse);
  }

  @Override
  public void runAdditionalCoCos(@NotNull ASTMACompilationUnit ast) {
    Preconditions.checkNotNull(ast);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new PortNoNamesAreNoJavaKeywords());
    checker.addCoCo(new ParameterNamesAreNoJavaKeywords());
    checker.addCoCo(new TypeParameterNamesAreNoJavaKeywords());
    checker.addCoCo(new FieldNamesAreNoJavaKeywords());
    checker.addCoCo(new AutomatonStateNamesAreNoJavaKeywords());
    checker.addCoCo(new ComponentTypeNamesAreNoJavaKeywords());
    checker.addCoCo(new ComponentInstanceNamesAreNoJavaKeywords());

    checker.checkAll(ast);
  }

  public void runAdditionalCoCosDse(@NotNull ASTMACompilationUnit ast) {
    Preconditions.checkNotNull(ast);
    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();

    checker.addCoCo(new DseSupportedTypes.DseParameters_VariablesTypes());

    checker.checkAll(ast);
  }
}