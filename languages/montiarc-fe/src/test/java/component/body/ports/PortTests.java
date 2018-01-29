package component.body.ports;

import org.junit.BeforeClass;
import org.junit.Test;

import contextconditions.AbstractCoCoTest;
import contextconditions.AbstractCoCoTestExpectedErrorInfo;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTMontiArcNode;
import montiarc.cocos.MontiArcCoCos;

/**
 * This class checks all context conditions directly related to port definitions
 *
 * @author Andreas Wortmann
 */
public class PortTests extends AbstractCoCoTest {
  
  private static final String MP = "";
  private static final String PACKAGE = "component.body.ports";
  
  @BeforeClass
  public static void setUp() {
    Log.enableFailQuick(false);
  }
  

  @Test
  public void testInexistingPortType() {
    //TODO: Star imports?
    ASTMontiArcNode node = getAstNode(MP, PACKAGE + "." + "InexistingPortType");
    checkInvalid(MontiArcCoCos.createChecker(), node, new AbstractCoCoTestExpectedErrorInfo(1,"xMA076"));

    checkValid("","contextconditions.valid.BumpControl");
  }
  
  @Test
  public void testBumpControl() {
    checkValid(MP, PACKAGE + "." +"BumpControl");
  }
  
  @Test
  public void testNonUniquePortNames() {
    ASTMontiArcNode node = getAstNode(MP, PACKAGE + "." + "NonUniquePortNames");
    checkInvalid(MontiArcCoCos.createChecker(),node, new AbstractCoCoTestExpectedErrorInfo(3, "xMA053"));
  }

  
}