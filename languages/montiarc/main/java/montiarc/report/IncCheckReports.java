/* (c) https://github.com/MontiCore/monticore */
package montiarc.report;

import de.monticore.generating.templateengine.reporting.commons.ReportManager;

import java.nio.file.Path;
import java.util.function.Function;
import java.util.function.UnaryOperator;

/**
 * A ReportManagerFactory creating inc-check reporters for ma2java generation.
 */
public class IncCheckReports implements ReportManager.ReportManagerFactory {

  private final String outputDir;

  /** Transforms paths before they are written to the inc gen file*/
  private final Function<Path, Path> reportPathOutput;

  public IncCheckReports(String outputDir, UnaryOperator<Path> reportPathOutput) {
    this.outputDir = outputDir;
    this.reportPathOutput = reportPathOutput;
  }

  @Override
  public ReportManager provide(String modelName) {

    ReportManager reporter = new ReportManager(outputDir);

    IncCheckGenerationReporter incGenGradleReporter =
      new IncCheckGenerationReporter(outputDir, reportPathOutput, modelName);

    reporter.addReportEventHandler(incGenGradleReporter);
    return reporter;
  }
}
