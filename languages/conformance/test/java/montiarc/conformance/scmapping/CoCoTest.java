/* (c) https://github.com/MontiCore/monticore */
package montiarc.conformance.scmapping;

import de.se_rwth.commons.logging.Log;
import java.io.File;

import montiarc.conformance.AutomatonAbstractTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import scmapping._ast.ASTSCMapping;

import static montiarc.conformance.util.AutomataLoader.loadMapping;

public class CoCoTest extends AutomatonAbstractTest {
  public String RELATIVE_MODEL_PATH = "test/resources/montiarc/conformance/";

  @BeforeEach
  public void setup() {
    Log.init();
    Log.enableFailQuick(false);
    initMills();
    File conAutFile = new File(RELATIVE_MODEL_PATH + "automaton2smt/concrete/Concrete.arc");
    File conCDFile = new File(RELATIVE_MODEL_PATH + "automaton2smt/concrete/Datatypes.cd");
    File refAutFile = new File(RELATIVE_MODEL_PATH + "automaton2smt/reference/Reference.arc");
    File refCDFile = new File(RELATIVE_MODEL_PATH + "automaton2smt/reference/Datatypes.cd");
    loadModels(refAutFile, refCDFile, conAutFile, conCDFile);
  }

  @Test
  public void CheckValidNameCoCoTest() {
    // Given
    String file = RELATIVE_MODEL_PATH + "scmapping/" + "validNames.map";

    // When
    ASTSCMapping ast = loadMapping(file, refAut, refCD, conAut, conCD);

    //Then
    Assertions.assertEquals(21, Log.getErrorCount());
    Assertions.assertNotNull(ast);
  }

  @Test
  public void CheckValueRightInEqualExpressionsCoCoTest() {
    // Given
    String file = RELATIVE_MODEL_PATH + "scmapping/" + "validEqAndNonEqExprs.map";

    // When
    ASTSCMapping ast = loadMapping(file, refAut, refCD, conAut, conCD);

    // Then
    Assertions.assertEquals(2, Log.getErrorCount());
    Assertions.assertNotNull(ast);
  }
}
