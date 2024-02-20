/* (c) https://github.com/MontiCore/monticore */
package montiarc.report;

import de.monticore.generating.templateengine.reporting.reporter.IncGenGradleReporter;
import org.codehaus.commons.nullanalysis.NotNull;

import java.nio.file.Path;
import java.util.function.Function;

/**
 * For every model file and the generation process of its java code, the created file contains the following entries:
 * <br>
 * <ul>
 *   <li><i>arc:</i> path to the model file (relative form the model path) and its checksum</li>
 *   <li><i>hwc:</i> path to a detected hwc file for the model, relative from the hwc path</li>
 *   <li><i>gen:</i> relative path to the location where a hwc file would have been expected if it existed. This entry
 *   shows that there was no such hwc file</li>
 *   <li><i>out:</i> path to a generated file</li>
 * </ul>
 */
public class IncCheckGenerationReporter extends IncGenGradleReporter {
  public IncCheckGenerationReporter(@NotNull String outputDir,
                                    @NotNull Function<Path, Path> reportPathOutput,
                                    @NotNull String modelName) {
    super(outputDir, reportPathOutput, modelName, "arc");
  }
}
