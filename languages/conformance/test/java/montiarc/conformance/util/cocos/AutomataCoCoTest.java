/* (c) https://github.com/MontiCore/monticore */
package montiarc.conformance.util.cocos;

import montiarc.conformance.AutomatonAbstractTest;
import montiarc.conformance.util.AutomataLoader;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.se_rwth.commons.logging.Log;
import java.io.File;
import montiarc._ast.ASTMACompilationUnit;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AutomataCoCoTest extends AutomatonAbstractTest {
  public String RELATIVE_MODEL_PATH = "test/resources/montiarc/conformance/";

  @BeforeEach
  public void setup() {
    Log.init();
    Log.enableFailQuick(false);
    initMills();
  }

  @Test
  public void CoCoTest() {
    // Given
    File aut = new File(RELATIVE_MODEL_PATH + "util/cocos/CoCos.arc");
    File cd = new File(RELATIVE_MODEL_PATH + "util/cocos/CoCos.cd");

    // When
    Pair<ASTCDCompilationUnit, ASTMACompilationUnit> res = AutomataLoader.loadModels(aut, cd);

    // Then
    Assertions.assertNotNull(res);
    Assertions.assertNotNull(res.getKey());
    Assertions.assertNotNull(res.getValue());
    Assertions.assertEquals(3, Log.getErrorCount());
  }
}
