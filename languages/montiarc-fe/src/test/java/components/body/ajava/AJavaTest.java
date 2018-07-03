package components.body.ajava;

import static org.junit.Assert.assertEquals;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import de.monticore.symboltable.Scope;
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
 * This class checks all context conditions related the definition of AJava behavior
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
    checkValid(PACKAGE + "." + "AssignExpressionToOutgoingPort");
  }
  
  @Test
  public void testJavaPVariableIdentifiersUnique() {
    ASTMontiArcNode node = loadComponentAST(PACKAGE + "." + "JavaPVariableIdentifiersUnique");
    checkInvalid(new MontiArcCoCoChecker().addCoCo(new JavaPVariableIdentifiersUnique()), node, new ExpectedErrorInfo(3, "xMA016", "xMA094", "xMA095"));
  }
  
  @Test
  public void testInitBlockWithAJava() {
    checkValid(PACKAGE + "." + "InitBlockWithAJava");
  }
  
  @Ignore("@JP: Sollte nach dem Umstieg auf die neue MontiArc-Version behoben werden.")
  @Test
  public void testInvalidInitBlockAssigment() {
    checkValid(PACKAGE + "." + "InvalidInitBlockAssigment");
  }
  
  @Test
  public void testAJavaComputeBlockNameIsLowerCase() {
    ASTMontiArcNode node = loadComponentAST(PACKAGE + "." + "AJavaComputeBlockNameIsLowerCase");
    checkInvalid(MontiArcCoCos.createChecker(), node,
        new ExpectedErrorInfo(1, "xMA015"));
  }
  
  @Ignore("@JP wartet auf aktualisierung des InputUnchangedVisitor")
  @Test
  public void testChangeIncomingPortInCompute() {
    ASTMontiArcNode node = loadComponentAST(PACKAGE + "." + "ChangeIncomingPortInCompute");
    checkInvalid(MontiArcCoCos.createChecker(), node,
        new ExpectedErrorInfo(2, "xMA078"));
  }
  
  @Ignore("@JP: Hier sollten drei Fehler entstehen (siehe Model). Bitte einbauen")
  @Test
  public void testWrongPortUsage() {
    ASTMontiArcNode node = loadComponentAST(PACKAGE + "." + "WrongPortUsage");
    checkInvalid(MontiArcCoCos.createChecker(), node,
        new ExpectedErrorInfo(1, "xMA030", "xMA078"));
  }
  
  @Ignore("@JP: Laut Konsole entsteht hier 2x Fehler 0xMA030. Wieso wird das nicht geprüft?")
  @Test
  public void testUsingInexistingPort() {
    loadComponentAST(PACKAGE + "." + "UsingInexistingPort");
    ExpectedErrorInfo expectedErrors = new ExpectedErrorInfo(2,
        "xMA030");
    // error occurs in symboltable only. Therefore no CoCo check via
    // checkInvalid
    expectedErrors.checkExpectedPresent(Log.getFindings(), "");
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
  
  @Ignore("@JP: Currently throws two times 0xMA030 - one for variable j and one for sum. Both errors are wrong.")
  @Test
  public void testComplexCodeExample() {
    checkValid(PACKAGE + "." + "ComplexCodeExample");
  }
  

  @Test
  public void testLocalVariablesInComputeBlock() {
    loadComponentAST(PACKAGE + "." +
                         "LocalVariablesInComputeBlock");
    final ComponentSymbol symbol = loadComponentSymbol(PACKAGE, "LocalVariablesInComputeBlock");
    Collection<VariableSymbol> foundVars = new ArrayList<>();
    symbol.getSpannedScope().getSubScopes().forEach(s -> s.<VariableSymbol> resolveLocally(VariableSymbol.KIND).forEach(v -> foundVars.add(v)));
    assertEquals(2, foundVars.size());

    checkValid(PACKAGE + "." + "LocalVariablesInComputeBlock");
  }

  @Test
  @Ignore("TODO: Currently forward declaration of variables is required. " +
              "Else the symboltable cannot be built correctly and the " +
              "variable is not found.")
  public void testDefineComponentVarAfterCompute() {
    loadComponentAST(PACKAGE + "." +
                         "DefineComponentVarAfterCompute");

    checkValid(PACKAGE + "." + "DefineComponentVarAfterCompute");
  }

  @Test
  @Ignore("TODO: Should not be working, as there are variables which are " +
              "declared more than once.")
  public void testAmbiguousAJavaVariableNames() {
    checkValid(PACKAGE + "." + "AmbiguousAJavaVariableNames");
  }
}
