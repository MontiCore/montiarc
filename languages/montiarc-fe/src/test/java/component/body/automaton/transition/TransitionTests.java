package component.body.automaton.transition;

import org.junit.BeforeClass;
import org.junit.Test;

import de.se_rwth.commons.logging.Log;
import infrastructure.AbstractCoCoTest;
import infrastructure.ExpectedErrorInfo;
import montiarc._ast.ASTMontiArcNode;
import montiarc.cocos.MontiArcCoCos;

/**
 * This class checks all context conditions directly related to automata (not their sub-elements)
 *
 * @author Andreas Wortmann
 */
public class TransitionTests extends AbstractCoCoTest {
  
  private static final String MP = "";
  private static final String PACKAGE = "component.body.automaton.transition";
  
  @BeforeClass
  public static void setUp() {
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testUsingUndefinedStates() {
    ASTMontiArcNode node = getAstNode(MP, PACKAGE + "." + "UsingUndefinedStates");
    checkInvalid(MontiArcCoCos.createChecker(), node, new ExpectedErrorInfo(6, "xMA026", "xMA027"));
  }
}