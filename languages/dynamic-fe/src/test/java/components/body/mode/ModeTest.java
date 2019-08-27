/* (c) https://github.com/MontiCore/monticore */
package components.body.mode;

import de.monticore.io.paths.ModelPath;
import dynamicmontiarc._cocos.DynamicMontiArcCoCoChecker;
import dynamicmontiarc._parser.DynamicMontiArcParser;
import dynamicmontiarc.cocos.*;
import infrastructure.AbstractCoCoTest;
import infrastructure.ExpectedErrorInfo;
import montiarc._ast.ASTMACompilationUnit;
import montiarc._ast.ASTMontiArcNode;
import montiarc._cocos.MontiArcASTGuardExpressionCoCo;
import montiarc._cocos.MontiArcASTIOAssignmentCoCo;
import montiarc._symboltable.ComponentSymbol;
import montiarc.cocos.UseOfUndeclaredField;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Contains test for test models using the mode automata embedded in a MontiArc
 * component.
 */
public class ModeTest extends AbstractCoCoTest {

  private static String PACKAGE = "components.body.mode";

  @Test
  public void testDynamicComponent() {
    String qualifiedModelName = PACKAGE + "." + "DynamicComponent";
    DynamicMontiArcParser parser = new DynamicMontiArcParser();
    Optional<ASTMACompilationUnit> compAST = Optional.empty();
    Path filePath = Paths.get(MODEL_PATH
        + qualifiedModelName.replace(".", File.separator)
        + ".arc");
    try {
      compAST = parser.parse(filePath.toString());
    } catch (IOException e) {
      e.printStackTrace();
    }
    assertTrue(compAST.isPresent());

    final Optional<ComponentSymbol> componentSymbol =
        DYNAMICMATOOL.loadComponentSymbolWithoutCocos(
            qualifiedModelName,
            Paths.get(MODEL_PATH).toFile(),
            Paths.get(FAKE_JAVA_TYPES_PATH).toFile());
    assertTrue(componentSymbol.isPresent());

    final Optional<? extends ASTMontiArcNode> astNode = DYNAMICMATOOL.getAstNode(qualifiedModelName, MODEL_PATH);
    assertTrue(astNode.isPresent());

    final ASTMontiArcNode astDynamicMontiArcNode = loadComponentAST(qualifiedModelName);
    assertNotNull(astDynamicMontiArcNode);
  }

  @Test
  public void testAdder() {
    String qualifiedModelName = PACKAGE + "." + "Adder";
    checkValid(qualifiedModelName);
  }

  @Test
  public void testMultiplier() {
    String qualifiedModelName = PACKAGE + "." + "Multiplier";
    checkValid(qualifiedModelName);
  }

  @Test
  public void testModeNameLowerCase() {
    String qualifiedModelName = PACKAGE + "." + "ModeNameLowercase";
    ExpectedErrorInfo errors =
        new ExpectedErrorInfo(5, "xMA200");
    final DynamicMontiArcCoCoChecker checker =
        new DynamicMontiArcCoCoChecker().addCoCo(new ModeNameUppercase());
    checkInvalid(checker, loadComponentAST(qualifiedModelName), errors);
  }

  @Test
  public void testNoInitialMode() {
    String qualifiedModelName = PACKAGE + "." + "NoInitialMode";
    ExpectedErrorInfo errors =
        new ExpectedErrorInfo(1, "xMA203");
    final DynamicMontiArcCoCoChecker checker =
        new DynamicMontiArcCoCoChecker().addCoCo(new InitialMode());
    checkInvalid(checker, loadComponentAST(qualifiedModelName), errors);
  }

  @Test
  public void testInitialModeDoesNotExist() {
    String qualifiedModelName = PACKAGE + "." + "InitialModeDoesNotExist";
    ExpectedErrorInfo errors =
        new ExpectedErrorInfo(1, "xMA201");
    final DynamicMontiArcCoCoChecker checker =
        new DynamicMontiArcCoCoChecker().addCoCo(new InitialMode());
    checkInvalid(checker, loadComponentAST(qualifiedModelName), errors);
  }

  @Test
  public void testTwoInitialModes() {
    String qualifiedModelName = PACKAGE + "." + "TwoInitialModes";
    ExpectedErrorInfo errors =
        new ExpectedErrorInfo(2, "xMA202");
    final DynamicMontiArcCoCoChecker checker =
        new DynamicMontiArcCoCoChecker().addCoCo(new InitialMode());
    checkInvalid(checker, loadComponentAST(qualifiedModelName), errors);
  }

  @Test
  public void testUseOfUndefinedCmp() {
    String qualifiedModelName = PACKAGE + "." + "UseOfUndefinedCmp";
    ExpectedErrorInfo errors =
        new ExpectedErrorInfo(2, "xMA204");
    final DynamicMontiArcCoCoChecker checker =
        new DynamicMontiArcCoCoChecker().addCoCo(new UseUndefinedComponent());
    checkInvalid(checker, loadComponentAST(qualifiedModelName), errors);
  }

  @Test
  public void testMultipleModeAutomata() {
    String qualifiedModelName = PACKAGE + "." + "MultipleModeAutomata";
    ExpectedErrorInfo errors =
        new ExpectedErrorInfo(2, "xMA206");
    final DynamicMontiArcCoCoChecker checker =
        new DynamicMontiArcCoCoChecker().addCoCo(new AtMostOneModeAutomaton());
    checkInvalid(checker, loadComponentAST(qualifiedModelName), errors);
  }

  @Test
  public void testModeAutomatonInAtomicComponent() {
    String qualifiedModelName = PACKAGE + "." + "ModeAutomatonInAtomicComponent";
    ExpectedErrorInfo errors =
        new ExpectedErrorInfo(1, "xMA205");
    final DynamicMontiArcCoCoChecker checker =
        new DynamicMontiArcCoCoChecker()
            .addCoCo(new NoModeAutomatonInAtomicComponent());
    checkInvalid(checker, loadComponentAST(qualifiedModelName), errors);
  }

  @Test
  public void testDynamicCompWithOnlyOneMode() {
    String qualifiedModelName = PACKAGE + "." + "DynamicCompWithOnlyOneMode";
    ExpectedErrorInfo errors =
        new ExpectedErrorInfo(1, "xMA207");
    final DynamicMontiArcCoCoChecker checker =
        new DynamicMontiArcCoCoChecker()
            .addCoCo(new ModeAutomatonElementsExist());
    checkInvalid(checker, loadComponentAST(qualifiedModelName), errors);
  }

  @Test
  public void testNotExistingVariableInModeTransition() {
    String qualifiedModelName = PACKAGE + "." + "NotExistingVariableInModeTransition";
    ExpectedErrorInfo errors =
        new ExpectedErrorInfo(2, "xMA079");
    final DynamicMontiArcCoCoChecker checker =
//        DynamicMontiArcCoCos.createChecker();
        new DynamicMontiArcCoCoChecker();
    checker.addCoCo((MontiArcASTIOAssignmentCoCo) new UseOfUndeclaredField());
    checker.addCoCo((MontiArcASTGuardExpressionCoCo) new UseOfUndeclaredField());
    checkInvalid(checker, loadComponentAST(qualifiedModelName), errors);
  }

  @Test
  public void testTransitionModeSourceAndTargetUndefined() {
    String qualifiedModelName =
        PACKAGE + "." + "TransitionModeSourceAndTargetUndefined";
    ExpectedErrorInfo errors =
        new ExpectedErrorInfo(3, "xMA211", "xMA212");
    final DynamicMontiArcCoCoChecker checker =
        new DynamicMontiArcCoCoChecker().addCoCo(new UsedModesAreDeclared());
    checkInvalid(checker, loadComponentAST(qualifiedModelName), errors);
  }

  @Test
  public void testPortUsedInModeTransitionGuardDoesNotExist() {
    String qualifiedModelName =
        PACKAGE + "." + "PortUsedInModeTransitionGuardDoesNotExist";
    ExpectedErrorInfo errors =
        new ExpectedErrorInfo(1, "xMA079");
    final DynamicMontiArcCoCoChecker checker = new DynamicMontiArcCoCoChecker();
    checker.addCoCo((MontiArcASTGuardExpressionCoCo) new UseOfUndeclaredField());
    final ASTMontiArcNode node = loadComponentAST(qualifiedModelName);
    checkInvalid(checker, node, errors);
  }

  @Test
  public void testOutgoingPortConnectedTwiceInMode(){
    String qualifiedModelName =
        PACKAGE + "." + "PortConnectedTwice";
    ExpectedErrorInfo errors =
        new ExpectedErrorInfo(2, "xMA213");
    final DynamicMontiArcCoCoChecker checker =
        new DynamicMontiArcCoCoChecker().addCoCo(new ConnectorUsageInModes());
    final ASTMontiArcNode node = loadComponentAST(qualifiedModelName);
    checkInvalid(checker, node, errors);
  }

  @Test
  public void testIncomingPortConnectedTwiceInMode(){
    String qualifiedModelName =
        PACKAGE + "." + "SubCompPortConnectedTwice";
    final ASTMontiArcNode node = loadComponentAST(qualifiedModelName);
    final DynamicMontiArcCoCoChecker checker = new DynamicMontiArcCoCoChecker();
    checker.addCoCo(new ConnectorUsageInModes());
    ExpectedErrorInfo errors =
        new ExpectedErrorInfo(1, "xMA214");

    checkInvalid(checker, node, errors);
  }

  @Test
  public void testOutgoingPortInModeTransition(){
    String qualifiedModelName =
        PACKAGE + "." + "OutgoingPortInModeTransition";
    ExpectedErrorInfo errors =
        new ExpectedErrorInfo(8, "xMA213", "xMA214", "xMA215", "xMA216");
    final DynamicMontiArcCoCoChecker checker = new DynamicMontiArcCoCoChecker();
    checker.addCoCo(new ModeTransitionCorrectVarAndPortUsage());

    final ASTMontiArcNode node = loadComponentAST(qualifiedModelName);
    checkInvalid(checker, node, errors);
  }

  @Test
  public void testNotAllIncomingPortsConnectedInEachMode(){
    String qualifiedModelName =
        PACKAGE + "." + "NotAllIncomingPortsConnectedInEachMode";
    ExpectedErrorInfo errors =
        new ExpectedErrorInfo(2, "xMA059", "xMA060");
    final DynamicMontiArcCoCoChecker checker = new DynamicMontiArcCoCoChecker();
    checker.addCoCo(new dynamicmontiarc.cocos.SubComponentsConnected());

    final ASTMontiArcNode node = loadComponentAST(qualifiedModelName);
    checkInvalid(checker, node, errors);
  }

  @Test
  public void testDoubleConnectedPortsBetweenModes(){
    String qualifiedModelName =
        PACKAGE + "." + "DoubleConnectedPortsBetweenModes";
    checkValid(qualifiedModelName);
  }
}

