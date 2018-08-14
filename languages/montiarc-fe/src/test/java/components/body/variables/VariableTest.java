package components.body.variables;

import montiarc._cocos.MontiArcASTComponentCoCo;
import montiarc._cocos.MontiArcCoCoChecker;
import montiarc.cocos.*;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import de.se_rwth.commons.logging.Log;
import infrastructure.AbstractCoCoTest;
import infrastructure.ExpectedErrorInfo;
import montiarc._ast.ASTMontiArcNode;

/**
 * This class checks all context conditions related the combination of elements in component bodies
 *
 * @author Andreas Wortmann
 */
public class VariableTest extends AbstractCoCoTest {
  
  private static final String PACKAGE = "components.body.variables";
  
  @BeforeClass
  public static void setUp() {
    Log.enableFailQuick(false);
  }
  
  @Test
  /*
   * Tests
   * [Wor16] AU3: The names of all inputs, outputs, and variables
   * are unique. (p. 98. Lst. 5.10)
   * [Hab16] B1: All names of model elements within a component
   * namespace have to be unique. (p. 59. Lst. 3.31)
   * [Wor16] MU1: The name of each component variable is unique
   *  among ports, variables, and configuration parameters. (p. 54 Lst. 4.5)
   */
  public void testAmbiguousVariableNames() {
    final String qualifiedModelName = PACKAGE + "." + "AmbiguousVariableNames";
    MontiArcCoCoChecker cocos = new MontiArcCoCoChecker().addCoCo(new IdentifiersAreUnique());
    final ExpectedErrorInfo errors
        = new ExpectedErrorInfo(4, "xMA035");
    checkInvalid(cocos, loadComponentAST(qualifiedModelName), errors);
  }
  
  @Test
  public void testVariableNamesAreLowerCase() {
    ASTMontiArcNode node = loadComponentAST(PACKAGE + "." + "VariableNameUpperCase");
    MontiArcCoCoChecker cocos = new MontiArcCoCoChecker().addCoCo((MontiArcASTComponentCoCo) new NamesCorrectlyCapitalized());
    checkInvalid(cocos, node, new ExpectedErrorInfo(1, "xMA018"));
  }

  //@Ignore("NoData is forbidden by grammar, so a test is superfluous at the moment.")
  @Test
  public void testNoDataNotAssigned() {
    ASTMontiArcNode node = loadComponentAST(PACKAGE + "." + "NoDataNotAssigned");
    MontiArcCoCoChecker cocos = new MontiArcCoCoChecker().addCoCo(new AutomatonNoDataAssignedToVariable());
    checkInvalid(cocos, node, new ExpectedErrorInfo(1, "xMA092"));

  }
}
