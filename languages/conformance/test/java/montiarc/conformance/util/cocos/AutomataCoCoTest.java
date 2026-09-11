/* (c) https://github.com/MontiCore/monticore */
package montiarc.conformance.util.cocos;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import montiarc._ast.ASTMACompilationUnit;
import montiarc.conformance.AutomatonTestBase;
import montiarc.conformance.util.AutomataLoader;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;

import java.io.File;

@Disabled // TODO: Incorrect Symbol Table Setup: 0xFD226 internal error: resolved 2 occurrences of Symbol
public class AutomataCoCoTest extends AutomatonTestBase {
  public String RELATIVE_MODEL_PATH = "test/resources/montiarc/conformance/";

  @BeforeEach
  public void setup() {
    LogStub.init();
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
