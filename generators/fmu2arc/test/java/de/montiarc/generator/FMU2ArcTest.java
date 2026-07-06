/* (c) https://github.com/MontiCore/monticore */
package de.montiarc.generator;

import de.se_rwth.commons.logging.Log;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FMU2ArcTest {

  Path outputDir = Paths.get("build/generated/montiarc/test/java");
  Path symOutput = Paths.get("build/generated/montiarc/test/symbols");
  Path testFmu = Paths.get("test/resources/fmu");

  @Test
  public void testRun() throws IOException {
    String[] args = new String[]{
      "--input", testFmu.toAbsolutePath().toString(),
      "--output", outputDir.toAbsolutePath().toString(),
      "--symboltable", symOutput.toAbsolutePath().toString()
    };
    FMU2ArcTool.main(args);

    Assertions.assertEquals(0, Log.getErrorCount(), () -> Log.getFindings().toString());

    FileUtils.deleteDirectory(new File("build/generated/montiarc/test"));
  }
}
