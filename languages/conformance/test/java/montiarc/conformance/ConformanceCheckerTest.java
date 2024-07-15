/* (c) https://github.com/MontiCore/monticore */
package montiarc.conformance;

import static montiarc.conformance.util.AutomataLoader.loadMapping;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.se_rwth.commons.logging.Log;
import java.io.File;

import montiarc.conformance.AutomataConfChecker;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import scmapping.mapping2smt.AutomataMapping;
import scmapping.mapping2smt.MCMapping;

class ConformanceCheckerTest extends AutomatonAbstractTest {
  public String RELATIVE_MODEL_PATH = "test/resources/montiarc/conformance/automaton2smt/";

  @BeforeEach
  public void setup() {
    Log.init();
    initMills();
  }

  @Test
  public void testConformanceChecker() {
    // Given
    File conAutFile = new File(RELATIVE_MODEL_PATH + "concrete/Concrete.arc");
    File conCDFile = new File(RELATIVE_MODEL_PATH + "concrete/Datatypes.cd");
    File refAutFile = new File(RELATIVE_MODEL_PATH + "reference/Reference.arc");
    File refCDFile = new File(RELATIVE_MODEL_PATH + "reference/Datatypes.cd");
    loadModels(refAutFile, refCDFile, conAutFile, conCDFile);

    // When
    AutomataConfChecker checker = new AutomataConfChecker();
    AutomataMapping mapping =
        new MCMapping(
            loadMapping(RELATIVE_MODEL_PATH + "mapping.map", refAut, refCD, conAut, conCD));

    //Then
    assertTrue(checker.isConform(refAut, conAut, refCD, conCD, mapping));
  }

  @Test
  public void testWithUnConformance() {
    // Given
    File conAutFile = new File(RELATIVE_MODEL_PATH + "concrete/WrongConcrete.arc");
    File conCDFile = new File(RELATIVE_MODEL_PATH + "concrete/Datatypes.cd");
    File refAutFile = new File(RELATIVE_MODEL_PATH + "reference/Reference.arc");
    File refCDFile = new File(RELATIVE_MODEL_PATH + "reference/Datatypes.cd");
    loadModels(refAutFile, refCDFile, conAutFile, conCDFile);

    // When
    AutomataConfChecker checker = new AutomataConfChecker();
    AutomataMapping mapping =
        new MCMapping(
            loadMapping(RELATIVE_MODEL_PATH + "mapping.map", refAut, refCD, conAut, conCD));

    // Then
    Assertions.assertFalse(checker.isConform(refAut, conAut, refCD, conCD, mapping));
  }
}
