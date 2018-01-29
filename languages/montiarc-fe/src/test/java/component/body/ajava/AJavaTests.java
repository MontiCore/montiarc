package component.body.ajava;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import contextconditions.AbstractCoCoTest;
import contextconditions.AbstractCoCoTestExpectedErrorInfo;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTMontiArcNode;
import montiarc._cocos.MontiArcCoCoChecker;
import montiarc.cocos.AtMostOneInitBlock;
import montiarc.cocos.InitBlockOnlyOnEmbeddedAJava;
import montiarc.cocos.MontiArcCoCos;

/**
 * This class checks all context conditions related the definition of AJava
 * behavior
 *
 * @author Andreas Wortmann
 */
public class AJavaTests extends AbstractCoCoTest {
  
  private static final String MP = "";
  
  private static final String PACKAGE = "component.body.ajava";
  
  @BeforeClass
  public static void setUp() {
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testAssignExpressionToOutgoingPort() {
    checkValid(MP, PACKAGE + "." + "AssignExpressionToOutgoingPort");
  }
  
  @Test
  public void testInitBlockWithAJava() {
    checkValid(MP, PACKAGE + "." + "InitBlockWithAJava");
  }
  
  @Ignore("@JP: Sollte nach dem Umstieg auf die neue MontiArc-Version behoben werden.")
  @Test
  public void testInvalidInitBlockAssigment() {
    checkValid(MP, PACKAGE + "." + "InvalidInitBlockAssigment");
  }
  
  @Test
  public void testAJavaComputeBlockNameIsLowerCase() {
    ASTMontiArcNode node = getAstNode(MP, PACKAGE + "." + "AJavaComputeBlockNameIsLowerCase");
    checkInvalid(MontiArcCoCos.createChecker(), node,
        new AbstractCoCoTestExpectedErrorInfo(1, "xMA174"));
  }
  
  @Test
  public void testChangeIncomingPortInCompute() {
    ASTMontiArcNode node = getAstNode(MP, PACKAGE + "." + "ChangeIncomingPortInCompute");
    checkInvalid(MontiArcCoCos.createChecker(), node,
        new AbstractCoCoTestExpectedErrorInfo(4, "xMA078"));
  }
  
  @Ignore("@JP: Hier sollten drei Fehler entstehen (siehe Model). Bitte einbauen")
  @Test
  public void testWrongPortUsage() {
    ASTMontiArcNode node = getAstNode(MP, PACKAGE + "." + "WrongPortUsage");
    checkInvalid(MontiArcCoCos.createChecker(), node,
        new AbstractCoCoTestExpectedErrorInfo(1, "xMA030", "xMA078"));
  }
  
  @Ignore("@JP: Laut Konsole entsteht hier 2x Fehler 0xMA030. Wieso wird das nicht geprüft?")
  @Test
  public void testUsingInexistingPort() {
    ASTMontiArcNode node = getAstNode(MP, PACKAGE + "." + "UsingInexistingPort");
    AbstractCoCoTestExpectedErrorInfo expectedErrors = new AbstractCoCoTestExpectedErrorInfo(2,
        "xMA030");
    // error occurs in symboltable only. Therefore no CoCo check via
    // checkInvalid
    expectedErrors.checkExpectedPresent(Log.getFindings(), "");
  }
  
  @Test
  public void testInitBlockWithoutComputeBlock() {
    ASTMontiArcNode node = getAstNode(MP, PACKAGE + "." + "InitBlockWithoutComputeBlock");
    checkInvalid(new MontiArcCoCoChecker().addCoCo(new InitBlockOnlyOnEmbeddedAJava()),
        node,
        new AbstractCoCoTestExpectedErrorInfo(1, "xMA063"));
  }
  
  @Test
  public void testTwoInitBlocks() {
    ASTMontiArcNode node = getAstNode(MP, PACKAGE + "." + "TwoInitBlocks");
    checkInvalid(new MontiArcCoCoChecker().addCoCo(new AtMostOneInitBlock()), node,
        new AbstractCoCoTestExpectedErrorInfo(1, "xMA078"));
  }
  
  @Test
  public void testValidAJavaComponent() {
    checkValid(MP, PACKAGE + "." + "ValidAJavaComponent");
  }
  
  @Test
  public void testUsedPortsAndParametersExist() {
    checkValid(MP, PACKAGE + "." + "UsedPortsAndParametersExist");
  }
  
  @Ignore("@JP: Currently throws two times 0xMA030 - one for variable j and one for sum. Both errors are wrong.")
  @Test
  public void testComplexCodeExample() {
    checkValid(MP, PACKAGE + "." + "ComplexCodeExample");
  }
  
}
