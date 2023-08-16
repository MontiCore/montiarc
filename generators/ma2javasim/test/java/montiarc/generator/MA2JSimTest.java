/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

public class MA2JSimTest {

  Path input = Paths.get("test/resources");
  Path hwcPath = Paths.get("test/resources/java");
  Path output = Paths.get("build/generated/montiarc/test/java");

  @Test
  public void test() {
    String[] args = new String[] {
      "-mp", input.toAbsolutePath().toString(),
      "--o", output.toAbsolutePath().toString(),
      "-hwc", hwcPath.toAbsolutePath().toString(),
      "-c2mc"
    };

    MA2JSimTool.main(args);
  }
}