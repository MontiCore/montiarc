package components.body.ajava;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Optional;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import de.monticore.symboltable.Scope;
import de.se_rwth.commons.logging.Log;
import infrastructure.AbstractCoCoTest;
import infrastructure.ExpectedErrorInfo;
import montiarc.MontiArcTool;
import montiarc._ast.ASTMontiArcNode;
import montiarc._cocos.MontiArcCoCoChecker;
import montiarc._symboltable.ComponentSymbol;
import montiarc._symboltable.JavaBehaviorSymbol;
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
  
  private static final String PACKAGE = "components.body.ajava";
  
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
        new ExpectedErrorInfo(1, "xMA174"));
  }
  
  @Test
  public void testChangeIncomingPortInCompute() {
    ASTMontiArcNode node = getAstNode(MP, PACKAGE + "." + "ChangeIncomingPortInCompute");
    checkInvalid(MontiArcCoCos.createChecker(), node,
        new ExpectedErrorInfo(4, "xMA078"));
  }
  
  @Ignore("@JP: Hier sollten drei Fehler entstehen (siehe Model). Bitte einbauen")
  @Test
  public void testWrongPortUsage() {
    ASTMontiArcNode node = getAstNode(MP, PACKAGE + "." + "WrongPortUsage");
    checkInvalid(MontiArcCoCos.createChecker(), node,
        new ExpectedErrorInfo(1, "xMA030", "xMA078"));
  }
  
  @Ignore("@JP: Laut Konsole entsteht hier 2x Fehler 0xMA030. Wieso wird das nicht geprüft?")
  @Test
  public void testUsingInexistingPort() {
    ASTMontiArcNode node = getAstNode(MP, PACKAGE + "." + "UsingInexistingPort");
    ExpectedErrorInfo expectedErrors = new ExpectedErrorInfo(2,
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
        new ExpectedErrorInfo(1, "xMA063"));
  }
  
  @Test
  public void testTwoInitBlocks() {
    ASTMontiArcNode node = getAstNode(MP, PACKAGE + "." + "TwoInitBlocks");
    checkInvalid(new MontiArcCoCoChecker().addCoCo(new AtMostOneInitBlock()), node,
        new ExpectedErrorInfo(1, "xMA078"));
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
  
  @Test
  public void testResolveDistanceLoggerBehavior() {
    MontiArcTool tool = new MontiArcTool();
    String modelPath = "src/test/resources";
    ASTMontiArcNode node = getAstNode(MP, PACKAGE + "." + "DistanceLogger");
    assertNotNull(node);
    
    Scope symtab = tool.createSymbolTable(modelPath);
    Optional<ComponentSymbol> oFoo = symtab.<ComponentSymbol> resolve(PACKAGE + "." + "DistanceLogger",
        ComponentSymbol.KIND);
    assertTrue(oFoo.isPresent());
    
    ComponentSymbol foo = oFoo.get();
    Optional<JavaBehaviorSymbol> aJavaDef = foo.getSpannedScope()
        .<JavaBehaviorSymbol> resolve("increaseHulu", JavaBehaviorSymbol.KIND);
    assertTrue(aJavaDef.isPresent());
  }
  
  
}
