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
import montiarc.report.IncCheckUtil;
import montiarc.report.UpToDateResults;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.codehaus.commons.nullanalysis.NotNull;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
    String[] modelpath = Optional.ofNullable(cl.getOptionValues("modelpath")).orElse(new String[]{});
    String reportDir =  Optional.ofNullable(cl.getOptionValue("report")).orElse("");
    String hwc = Optional.ofNullable(cl.getOptionValue("hwc")).orElse("");
    boolean dse = cl.hasOption("dse");
    String concatModelPath = String.join(File.pathSeparator, modelpath);

    Collection<ASTMACompilationUnit> models4NewGeneration;

    boolean writeReports = !reportDir.isEmpty();
    if(writeReports) {
      IncCheckUtil.Config incCheckConfig =
        new IncCheckUtil.Config(concatModelPath, target, reportDir, MA2JAVA_INC_CHECK_REPORT_DIR, hwc);
      IncCheckUtil.configureIncCheckReporting(incCheckConfig);

      Map<String, ASTMACompilationUnit> astByQName = IncCheckUtil.resolveAstByQName(asts);
      UpToDateResults upToDateInfo = IncCheckUtil.calcUpToDateData(astByQName, incCheckConfig);

      IncCheckUtil.removeOutdatedGenerationResults(upToDateInfo, incCheckConfig);
      models4NewGeneration = IncCheckUtil.calcReportedModelsForNewGeneration(upToDateInfo, astByQName);
    } else {
      models4NewGeneration = asts;
    }

    // Pre-calculate some variable values that will be used for every processed model
    MCPath modelPaths = new MCPath(splitPathEntries(modelpath));
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
      this.generate(ast, target, hwc, dse);
      if (dse) {
        this.generateDseOnlyFiles(ast, target, hwc, componentNames, imports);
      }

      if (writeReport4ThisModel) {
        Reporting.flush(ast);
      }
    }
  }

  public void generateDseOnlyFiles(@NotNull ASTMACompilationUnit ast,
                       @NotNull String target,
                       @NotNull String hwc,
                       @NotNull List<String> componentNames,
                       @NotNull List<String> imports) {

    Preconditions.checkNotNull(ast);
    Preconditions.checkNotNull(target);
    Preconditions.checkNotNull(hwc);
    Preconditions.checkArgument(!target.isEmpty());

    MontiArcGenerator generator = new MontiArcGenerator(Paths.get(target), splitPathEntriesToList(hwc));
    generator.generateMain(ast, componentNames, new ArrayList<>(imports));
  }

  public void generate(@NotNull ASTMACompilationUnit ast, @NotNull String target,
                       @NotNull String hwc, boolean dse) {
    Preconditions.checkNotNull(ast);
    Preconditions.checkNotNull(target);
    Preconditions.checkNotNull(hwc);
    Preconditions.checkArgument(ast.getComponentType().isPresentSymbol());
    Preconditions.checkArgument(!target.isEmpty());

    MontiArcGenerator generator = new MontiArcGenerator(Paths.get(target), splitPathEntriesToList(hwc));
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