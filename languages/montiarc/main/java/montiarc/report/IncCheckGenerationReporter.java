/* (c) https://github.com/MontiCore/monticore */
package montiarc.report;

import com.google.common.base.Preconditions;
import de.monticore.ast.ASTNode;
import de.monticore.generating.templateengine.reporting.commons.ReportingHelper;
import de.monticore.generating.templateengine.reporting.reporter.IncGenGradleReporter;
import org.codehaus.commons.nullanalysis.NotNull;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
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
 *   <li><i>ver:</i> the currently used tool version. May be empty if one does not want to include it.</li>
 * </ul>
 */
public class IncCheckGenerationReporter extends IncGenGradleReporter {

  public static final String VER_PREFIX = "ver:";

  private final String toolVersion;

  public IncCheckGenerationReporter(@NotNull String outputDir,
                                    @NotNull Function<Path, Path> reportPathOutput,
                                    @NotNull String modelName,
                                    @NotNull String toolVersion) {
    super(outputDir, reportPathOutput, modelName, "arc");
    this.toolVersion = Preconditions.checkNotNull(toolVersion);
  }

  @Override
  public void flush(@NotNull ASTNode node) {
    Preconditions.checkNotNull(node);

    openFile();

    writeFileStates(node);
    writeVersion();

    super.flush(node);
  }

  /**
   * Serializes all the content into the report file that {@link IncGenGradleReporter#flush(ASTNode)}
   * also serializes.
   * <br>
   * @throws IllegalStateException If the serialization file has not been opened yet.
   */
  protected void writeFileStates(@NotNull ASTNode node) {
    Preconditions.checkState(fileOpen, "Reporting file is not opened yet");

    for (Path lateOne : filesThatMatterButAreNotThereInTime) {
      if (modelToArtifactMap.containsKey(lateOne)) {
        Path toAdd = Paths.get(modelToArtifactMap.get(lateOne).toString(), lateOne.toString());
        if (!modelFiles.contains(toAdd)) {
          modelFiles.add(toAdd);
        }
      }
    }


    if (inputFile != null && !inputFile.toString().isEmpty()) {
      String checkSum;
      if (node != null) {
        checkSum = ReportingHelper.getChecksum(inputFile.toFile().getAbsolutePath());
      } else {
        checkSum = GEN_ERROR;
      }
      writeLine(fileExtension + ":" + printPath(inputFile) + " " + checkSum);
      for (Path s : modelFiles) {
        //only local files are important
        if (!s.toAbsolutePath().toString().contains(".jar" + File.separator)) {
          File inputFile = s.toFile();
          if (inputFile.exists()) {
            checkSum = ReportingHelper.getChecksum(inputFile.toString());
          } else {
            checkSum = MISSING;
          }
          writeLine(fileExtension + ":" + printPath(s) + " " + checkSum);
        }
      }
    }
    //create check: user templates changed or deleted?
    for (Path s : userTemplates) {
      //only local files are important
      if (!s.endsWith(".jar")) {
        File inputFile = s.toFile();
        String checkSum;
        if (inputFile.exists()) {
          checkSum = ReportingHelper.getChecksum(inputFile.toString());
        }else{
          checkSum = MISSING;
        }
        writeLine("ftl:" + printPath(s) + " " + checkSum);
      }
    }
    // create check: used file deleted?
    usedHWCFiles.forEach(f -> writeLine("hwc:" + printPath(f)));
    /*for (Path p : usedHWCFiles) {
      writeLine("hwc:" + printPath(p));
    }*/ // TODO check whether removable

    // create check: relevant file added?
    notExistentHWCFiles.forEach(f -> writeLine("gen:" + printPath(f)));
    /*for (Path p : notExistentHWCFiles) {
      writeLine("gen:" + printPath(p));
    }*/

    outputFiles.forEach(f -> writeLine("out:" + printPath(f)));
    /*for (Path p : outputFiles) {
      writeLine("out:" + printPath(p));
    }*/
  }

  /**
   * Writes the current generator version into the report file
   */
  protected void writeVersion() {
    Preconditions.checkState(fileOpen, "Reporting file is not opened yet");
    if (!toolVersion.isEmpty()) {
      writeLine("ver:" + toolVersion);
    }
  }
}
