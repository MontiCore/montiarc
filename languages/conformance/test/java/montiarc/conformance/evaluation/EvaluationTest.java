/* (c) https://github.com/MontiCore/monticore */
package montiarc.conformance.evaluation;


import static montiarc.conformance.util.AutomataLoader.loadMapping;

import montiarc.conformance.AutomataConfChecker;
import de.se_rwth.commons.logging.Log;
import java.io.File;

import montiarc.conformance.AutomatonAbstractTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import scmapping.mapping2smt.AutomataMapping;
import scmapping.mapping2smt.MCMapping;

class EvaluationTest extends AutomatonAbstractTest {
  public String modelDir = "test/resources/montiarc/conformance/eval/";

  @BeforeEach
  public void setup() {
    Log.init();
    initMills();
  }

  @Test
  public void testMiniEvaluation() {
    // Given
    File conAutFile = new File(modelDir + "mini/concrete/Concrete.arc");
    File conCDFile = new File(modelDir + "mini/concrete/Datatypes.cd");
    File refAutFile = new File(modelDir + "mini/reference/Reference.arc");
    File refCDFile = new File(modelDir + "mini/reference/Datatypes.cd");
    loadModels(refAutFile, refCDFile, conAutFile, conCDFile);

    // When
    AutomataConfChecker checker = new AutomataConfChecker();
    AutomataMapping mapping =
        new MCMapping(
            loadMapping(modelDir + "mini/mapping.map", refAut, refCD, conAut, conCD));

    // Then
    Assertions.assertTrue(checker.isConform(refAut, conAut, refCD, conCD, mapping));
  }

  @Test
  public void testBigEvaluation() {
    long start = System.currentTimeMillis();

    // Given
    File conAutFile = new File(modelDir + "big/concrete/Concrete.arc");
    File conCDFile = new File(modelDir + "big/concrete/Datatypes.cd");
    File refAutFile = new File(modelDir + "big/reference/Reference.arc");
    File refCDFile = new File(modelDir + "big/reference/Datatypes.cd");
    loadModels(refAutFile, refCDFile, conAutFile, conCDFile);

    // when
    AutomataConfChecker checker = new AutomataConfChecker();
    AutomataMapping mapping =
        new MCMapping(
            loadMapping(modelDir + "big/mapping.map", refAut, refCD, conAut, conCD));


    //Then
    Assertions.assertFalse(checker.isConform(refAut, conAut, refCD, conCD, mapping));
    double duration = (System.currentTimeMillis() - start) / 1000.0;
    System.out.println("Execution time: " + duration);
  }
}
