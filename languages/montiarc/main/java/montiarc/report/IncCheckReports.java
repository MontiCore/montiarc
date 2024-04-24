/* (c) https://github.com/MontiCore/monticore */
package montiarc.report;

import com.google.common.base.Preconditions;
import de.monticore.generating.templateengine.reporting.commons.ReportManager;
import org.codehaus.commons.nullanalysis.NotNull;

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

  private final String toolVersion;

  public IncCheckReports(@NotNull String outputDir,
                         @NotNull UnaryOperator<Path> reportPathOutput,
                         @NotNull String toolVersion) {
    this.outputDir = Preconditions.checkNotNull(outputDir);
    this.reportPathOutput = Preconditions.checkNotNull(reportPathOutput);
    this.toolVersion = Preconditions.checkNotNull(toolVersion);
  }

  @Override
  public ReportManager provide(String modelName) {

    ReportManager reporter = new ReportManager(outputDir);

    IncCheckGenerationReporter incGenGradleReporter =
      new IncCheckGenerationReporter(outputDir, reportPathOutput, modelName, toolVersion);

    reporter.addReportEventHandler(incGenGradleReporter);
    return reporter;
  }
}
