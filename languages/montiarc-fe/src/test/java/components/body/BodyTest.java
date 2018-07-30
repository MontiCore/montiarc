package components.body;

import montiarc._cocos.MontiArcASTComponentCoCo;
import montiarc._cocos.MontiArcCoCoChecker;
import montiarc.cocos.IdentifiersAreUnique;
import montiarc.cocos.MultipleBehaviorImplementation;
import montiarc.cocos.NamesCorrectlyCapitalized;
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
    final String modelName = PACKAGE + "." + "ComponentWithAJavaAndAutomaton";
    final MontiArcCoCoChecker checker
        = new MontiArcCoCoChecker().addCoCo(new MultipleBehaviorImplementation());
    final ExpectedErrorInfo errors
        = new ExpectedErrorInfo(1, "xMA050");
    checkInvalid(checker, loadComponentAST(modelName), errors);
  }

  @Test
  public void testWrongCapitalization() {
    final ASTMontiArcNode node = loadComponentAST(PACKAGE + "." + "wrongCapitalization");
    final ExpectedErrorInfo errors = new ExpectedErrorInfo(2, "xMA055", "xMA077"); // Add error info
    final MontiArcCoCoChecker checker = new MontiArcCoCoChecker().addCoCo((MontiArcASTComponentCoCo) new NamesCorrectlyCapitalized());
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
