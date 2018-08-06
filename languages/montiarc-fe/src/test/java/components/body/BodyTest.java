package components.body;

import montiarc._cocos.MontiArcCoCoChecker;
import montiarc.cocos.IdentifiersAreUnique;
import montiarc.cocos.MultipleBehaviorImplementation;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import de.se_rwth.commons.logging.Log;
import infrastructure.AbstractCoCoTest;
import infrastructure.ExpectedErrorInfo;
import montiarc._ast.ASTMontiArcNode;
import montiarc.cocos.MontiArcCoCos;

/**
 * This class checks all context conditions related the combination of elements
 * in component bodies
 *
 * @author Andreas Wortmann
 */
public class BodyTest extends AbstractCoCoTest {
  
  private static final String PACKAGE = "components.body";
  
  @BeforeClass
  public static void setUp() {
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testComponentWithAJavaAndAutomaton() {
    ASTMontiArcNode node = loadComponentAST(PACKAGE + "." + "ComponentWithAJavaAndAutomaton");
    final MontiArcCoCoChecker checker = new MontiArcCoCoChecker().addCoCo(new MultipleBehaviorImplementation());
    checkInvalid(checker, node, new ExpectedErrorInfo(1, "xMA050"));
  }

  @Test
  @Ignore("Waits for https://git.rwth-aachen.de/monticore/montiarc/core/issues/195")
  /**
   * Currently not resolvable, as the name of the artifact is lower case.
   * Waits for the resolution of ticket https://git.rwth-aachen.de/monticore/montiarc/core/issues/195.
   */
  public void testWrongCapitalization() {
    final ASTMontiArcNode node = loadComponentAST(PACKAGE + "." + "wrongCapitalization");
    final ExpectedErrorInfo errors = new ExpectedErrorInfo(); // Add error info
    final MontiArcCoCoChecker checker = MontiArcCoCos.createChecker();
    checkInvalid(checker, node, errors);
  }

  @Test
  public void testAmbiguousPortAndVariableNames() {
    ASTMontiArcNode node = loadComponentAST(PACKAGE + "." + "AmbiguousPortAndVariableNames");
    checkInvalid(MontiArcCoCos.createChecker(), node, new ExpectedErrorInfo(14,
        "xMA035", "xMA053"));
  }
  
  @Test
  public void testAmbiguousIdentifiers() {
    ASTMontiArcNode node = loadComponentAST(PACKAGE + "." + "AmbiguousIdentifiers");
    checkInvalid(new MontiArcCoCoChecker().addCoCo(new IdentifiersAreUnique()), node,
        new ExpectedErrorInfo(3, "xMA061", "xMA053"));
  }
  
}
