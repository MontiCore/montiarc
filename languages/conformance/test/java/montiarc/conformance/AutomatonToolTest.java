/* (c) https://github.com/MontiCore/monticore */
package montiarc.conformance;

import montiarc.conformance.AutConformanceTool;
import org.junit.jupiter.api.Test;

public class AutomatonToolTest extends AutomatonAbstractTest {
  public String RELATIVE_MODEL_PATH = "test/resources/montiarc/conformance/automaton2smt/";

  @Test
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
