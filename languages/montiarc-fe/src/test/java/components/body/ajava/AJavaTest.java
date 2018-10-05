package components.body.ajava;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import de.monticore.ast.ASTNode;
import de.se_rwth.commons.logging.Log;
import infrastructure.AbstractCoCoTest;
import infrastructure.ExpectedErrorInfo;
import montiarc._ast.ASTMontiArcNode;
import montiarc._cocos.MontiArcCoCoChecker;
import montiarc._symboltable.ComponentSymbol;
import montiarc._symboltable.VariableSymbol;
import montiarc.cocos.AtMostOneInitBlock;
import montiarc.cocos.InitBlockOnlyOnEmbeddedAJava;
import montiarc.cocos.JavaPVariableIdentifiersUnique;
import montiarc.cocos.MontiArcCoCos;

/**
 * This class checks all context conditions related the definition of AJava
 * behavior
 *
 * @author Andreas Wortmann
 */
public class AJavaTest extends AbstractCoCoTest {
  
  private static final String PACKAGE = "components.body.ajava";
  
  @BeforeClass
  public static void setUp() {
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testAssignExpressionToOutgoingPort() {
    ASTMontiArcNode node = loadComponentAST(PACKAGE + "." + "AssignExpressionToOutgoingPort");
    checkInvalid(MontiArcCoCos.createChecker(), node, new ExpectedErrorInfo(1, "xMA105"));
  }
  
  @Test
  public void testJavaPVariableIdentifiersUnique() {
    ASTMontiArcNode node = loadComponentAST(PACKAGE + "." + "JavaPVariableIdentifiersUnique");
    checkInvalid(new MontiArcCoCoChecker().addCoCo(new JavaPVariableIdentifiersUnique()), node,
        new ExpectedErrorInfo(3, "xMA016", "xMA094", "xMA095"));
  }
  
  @Test
  public void testDistanceLogger() {
    final String qualifiedModelName = PACKAGE + "." + "DistanceLogger";
    final MontiArcCoCoChecker cocos = MontiArcCoCos.createChecker();
    final ExpectedErrorInfo errors = new ExpectedErrorInfo(2, "xMA015", "xMA110");
    checkInvalid(cocos, loadComponentAST(qualifiedModelName), errors);
  }
  
  @Test
  public void testInitBlockWithAJava() {
    checkValid(PACKAGE + "." + "InitBlockWithAJava");
  }
  
  @Test
  public void testInvalidInitBlockAssigment() {
    checkValid(PACKAGE + "." + "InvalidInitBlockAssigment");
  }
  
  @Test
  public void testAJavaComputeBlockNameIsLowerCase() {
    final ASTMontiArcNode node = loadComponentAST(
        PACKAGE + "." + "AJavaComputeBlockNameIsLowerCase");
    final ExpectedErrorInfo errors = new ExpectedErrorInfo(1, "xMA015");
    checkInvalid(MontiArcCoCos.createChecker(), node, errors);
  }
  
  @Test
  public void testChangeIncomingPortInCompute() {
    ASTMontiArcNode node = loadComponentAST(PACKAGE + "." + "ChangeIncomingPortInCompute");
    final ExpectedErrorInfo errors = new ExpectedErrorInfo(2, "xMA104");
    checkInvalid(MontiArcCoCos.createChecker(), node, errors);
  }
  
  @Test
  public void testWrongPortUsage() {
    ASTMontiArcNode node = loadComponentAST(PACKAGE + "." + "WrongPortUsage");
    checkInvalid(MontiArcCoCos.createChecker(), node,
        new ExpectedErrorInfo(12, "xMA104", "xMA105", "xMA106", "xMA107", "xMA108", "xMA109",
            "xMA110", "xMA111", "xMA112", "xMA113"));
  }
  
  @Test
  public void testUsingInexistingPort() {
    ASTMontiArcNode node = loadComponentAST(PACKAGE + "." + "UsingInexistingPort");
    ExpectedErrorInfo expectedErrors = new ExpectedErrorInfo(2,
        "xMA107");
    checkInvalid(MontiArcCoCos.createChecker(), node, expectedErrors);
  }
  
  @Test
  public void testInitBlockWithoutComputeBlock() {
    ASTMontiArcNode node = loadComponentAST(PACKAGE + "." + "InitBlockWithoutComputeBlock");
    checkInvalid(new MontiArcCoCoChecker().addCoCo(new InitBlockOnlyOnEmbeddedAJava()),
        node,
        new ExpectedErrorInfo(1, "xMA063"));
  }
  
  @Test
  public void testTwoInitBlocks() {
    ASTMontiArcNode node = loadComponentAST(PACKAGE + "." + "TwoInitBlocks");
    checkInvalid(new MontiArcCoCoChecker().addCoCo(new AtMostOneInitBlock()), node,
        new ExpectedErrorInfo(1, "xMA080"));
  }
  
  @Test
  public void testValidAJavaComponent() {
    checkValid(PACKAGE + "." + "ValidAJavaComponent");
  }
  
  @Test
  public void testUsedPortsAndParametersExist() {
    checkValid(PACKAGE + "." + "UsedPortsAndParametersExist");
  }
  
  @Test
  public void testComplexCodeExample() {
    checkValid(PACKAGE + "." + "ComplexCodeExample");
  }
  
  @Test
  public void testDuplicateLocalVariables() {
    ASTMontiArcNode node = loadComponentAST(PACKAGE + "." + "DuplicateLocalVariables");
    checkInvalid(new MontiArcCoCoChecker().addCoCo(new JavaPVariableIdentifiersUnique()), node,
        new ExpectedErrorInfo(4, "xMA095"));
    
  }
  
  @Test
  public void testLocalVariablesInComputeBlock() {
    loadComponentAST(PACKAGE + "." +
        "LocalVariablesInComputeBlock");
    final ComponentSymbol symbol = loadComponentSymbol(PACKAGE, "LocalVariablesInComputeBlock");
    Collection<VariableSymbol> foundVars = new ArrayList<>();
    symbol.getSpannedScope().getSubScopes().forEach(
        s -> s.<VariableSymbol> resolveLocally(VariableSymbol.KIND).forEach(v -> foundVars.add(v)));
    assertEquals(1, foundVars.size());
    
    checkValid(PACKAGE + "." + "LocalVariablesInComputeBlock");
  }
  
  @Test
  public void testDefineComponentVarAfterCompute() {
    loadComponentAST(PACKAGE + "." +
        "DefineComponentVarAfterCompute");
    
    checkValid(PACKAGE + "." + "DefineComponentVarAfterCompute");
  }
  
  @Test
  public void testAmbiguousAJavaVariableNames() {
    ASTMontiArcNode ast = loadComponentAST(PACKAGE + "." + "AmbiguousAJavaVariableNames");
    checkInvalid(MontiArcCoCos.createChecker(), ast, new ExpectedErrorInfo(1, "xMA016"));
  }
}
