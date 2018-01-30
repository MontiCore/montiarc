package component.body.automaton.transition.stimuli;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import contextconditions.AbstractCoCoTest;
import contextconditions.AbstractCoCoTestExpectedErrorInfo;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTMontiArcNode;
import montiarc.cocos.MontiArcCoCos;

/**
 * This class checks all context conditions related to automaton transition stimuli
 *
 * @author Andreas Wortmann
 */
public class StimuliTests extends AbstractCoCoTest {
  
  private static final String MP = "";
  
  private static final String PACKAGE = "component.body.automaton.transition.stimuli";
  
  @BeforeClass
  public static void setUp() {
    Log.enableFailQuick(false);
  }

  @Ignore("@JP: Kann mit der Aktualisierung auf neue JavaDSL-Version "
      + "aktiviert werden (inkl. CoCos AutomatonReactionTypeDoesNotFitOutputType"
      + " und AutomatonInitialReactionTypeDoesNotFitOutputType)")
  @Test
  public void testMultipleAssignmentTypeConflics() {
    ASTMontiArcNode node = getAstNode(MP, PACKAGE + "." + "MultipleAssignmentTypeConflics");
    checkInvalid(MontiArcCoCos.createChecker(),node, new AbstractCoCoTestExpectedErrorInfo(2, "xMA046"));
  }

}
