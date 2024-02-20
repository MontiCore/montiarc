/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator;

import com.google.common.base.Preconditions;
import de.monticore.generating.templateengine.reporting.Reporting;
import de.monticore.io.paths.MCPath;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcTool;
import montiarc._ast.ASTMACompilationUnit;
import montiarc.generator.codegen.MA2JSimGen;
import montiarc.report.IncCheckUtil;
import montiarc.report.UpToDateResults;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.codehaus.commons.nullanalysis.NotNull;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public class MA2JSimTool extends MontiArcTool {

  public static final String MA2JSIM_INC_CHECK_REPORT_DIR = "ma2jsim-inc-data";

  public static void main(@NotNull String[] args) {
    Preconditions.checkNotNull(args);
    MA2JSimTool tool = new MA2JSimTool();
    tool.init();
    tool.run(args);
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
    options.addOption(org.apache.commons.cli.Option.builder("symbolic")
        .longOpt("symbolic-execution")
        .desc("Sets the template to symbolic template).")
        .build());
    return super.addStandardOptions(options);
  }

  @Override
  public void runTasks(@NotNull Collection<ASTMACompilationUnit> asts, @NotNull CommandLine cl) {
    Preconditions.checkNotNull(asts);
    Preconditions.checkNotNull(cl);
    super.runTasks(asts, cl);

    if(cl.hasOption("output")) {
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
    String reportDir = Optional.ofNullable(cl.getOptionValue("report")).orElse("");
    String hwc = Optional.ofNullable(cl.getOptionValue("hwc")).orElse("");
    String concatModelPath = String.join(File.pathSeparator, modelpath);

    Collection<ASTMACompilationUnit> models4NewGeneration;

    boolean writeReports = !reportDir.isEmpty();
    if (writeReports) {
      IncCheckUtil.Config incCheckConfig =
        new IncCheckUtil.Config(concatModelPath, target, reportDir, MA2JSIM_INC_CHECK_REPORT_DIR, hwc);
      IncCheckUtil.configureIncCheckReporting(incCheckConfig);

      Map<String, ASTMACompilationUnit> astByQName = IncCheckUtil.resolveAstByQName(asts);
      UpToDateResults upToDateInfo = IncCheckUtil.calcUpToDateData(astByQName, incCheckConfig);

      IncCheckUtil.removeOutdatedGenerationResults(upToDateInfo, incCheckConfig);
      models4NewGeneration = IncCheckUtil.calcReportedModelsForNewGeneration(upToDateInfo, astByQName);
    } else {
      models4NewGeneration = asts;
    }

    MCPath modelPaths = new MCPath(splitPathEntries(modelpath));

    for (ASTMACompilationUnit ast : models4NewGeneration) {
      Optional<Path> modelLocation = IncCheckUtil.findModelLocation(modelPaths, ast);
      boolean writeReport4ThisModel = writeReports && modelLocation.isPresent();

      // Init reporting for current ast
      if (writeReport4ThisModel) {
        IncCheckUtil.setIncCheckReportingOn(ast, modelLocation.get());
      }

      // In all cases
      this.generate(ast, target, hwc);

      if (writeReport4ThisModel) {
        Reporting.flush(ast);
      }
    }
  }



  public void generate(@NotNull ASTMACompilationUnit ast, @NotNull String target, @NotNull String hwc) {
    Preconditions.checkNotNull(ast);
    Preconditions.checkNotNull(target);
    Preconditions.checkNotNull(hwc);
    Preconditions.checkArgument(ast.getComponentType().isPresentSymbol());
    Preconditions.checkArgument(!target.isEmpty());
    MA2JSimGen generator = new MA2JSimGen(Paths.get(target), splitPathEntriesToList(hwc));
    generator.generate(ast);
  }

}
