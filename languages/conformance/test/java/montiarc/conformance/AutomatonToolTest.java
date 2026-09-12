/* (c) https://github.com/MontiCore/monticore */
package montiarc.conformance;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;

public class AutomatonToolTest extends AutomatonTestBase {
  public String RELATIVE_MODEL_PATH = "test/resources/montiarc/conformance/automaton2smt/";

  @Test
  @Disabled // TODO: Causes System.exit calls / ignores Log setup
  public void toolTest() {
    //Given
    String conAutFile = RELATIVE_MODEL_PATH + "concrete/Concrete.arc";
    String conCDFile = RELATIVE_MODEL_PATH + "concrete/Datatypes.cd";
    String refAutFile = RELATIVE_MODEL_PATH + "reference/Reference.arc";
    String refCDFile = RELATIVE_MODEL_PATH + "reference/Datatypes.cd";
    String mapping = RELATIVE_MODEL_PATH + "mapping.map";

    //When
    new AutConformanceTool()
        .run(
            new String[] {"-c", conAutFile, conCDFile, "-r", refAutFile, refCDFile, "-m", mapping});
  }
}
