/*
 * Copyright (c) 2016 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package infrastructure;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.nio.file.Paths;
import java.util.Optional;

import de.monticore.ModelingLanguage;
import de.monticore.symboltable.Symbol;
import jdk.nashorn.internal.runtime.options.OptionTemplate;
import montiarc._ast.ASTMACompilationUnit;
import montiarc._cocos.*;
import montiarc._symboltable.MontiArcLanguage;
import montiarc._symboltable.MontiArcLanguageFamily;
import montiarc.cocos.*;
import org.junit.Before;

import de.monticore.symboltable.Scope;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcTool;
import montiarc._ast.ASTMontiArcNode;
import montiarc._symboltable.ComponentSymbol;

/**
 * Abstract base class for all tests that do more than just parsing
 * 
 * @author (last commit) Crispin Kirchner, Andreas Wortmann
 */
public abstract class AbstractCoCoTest {
  
  protected static final String MODEL_PATH = "src/test/resources/";
  
  protected static final String FAKE_JAVA_TYPES_PATH = "target/librarymodels/";
  
  // TODO Remove when inner components are allowed again
  private static final MontiArcCoCoChecker checker = new MontiArcCoCoChecker()
      .addCoCo(new PortUsage())
      .addCoCo(new UsedPortAndVarTypesExist())
      .addCoCo(new SubComponentsConnected())
      .addCoCo(new SubcomponentParametersCorrectlyAssigned())
      .addCoCo(new PackageLowerCase())
      .addCoCo((MontiArcASTComponentCoCo) new NamesCorrectlyCapitalized())
      .addCoCo(new DefaultParametersHaveCorrectOrder())
      .addCoCo(new DefaultParametersCorrectlyAssigned())
      .addCoCo(new ComponentWithTypeParametersHasInstance())
      .addCoCo(new CircularInheritance())
      .addCoCo(new IOAssignmentCallFollowsMethodCall())
      .addCoCo(new AllGenericParametersOfSuperClassSet())
      .addCoCo(new TypeParameterNamesUnique())
      .addCoCo(new TopLevelComponentHasNoInstanceName())
      .addCoCo((MontiArcASTConnectorCoCo) new ConnectorEndPointIsCorrectlyQualified())
      .addCoCo(new InPortUniqueSender())
      .addCoCo(new ImportsValid())
      .addCoCo(new SubcomponentReferenceCycle())
      .addCoCo(new ReferencedSubComponentExists())
      .addCoCo(new PortNamesAreNotJavaKeywords())
      .addCoCo(new UnusedImports())
      .addCoCo(new AmbiguousTypes())
      
      /// AJava Cocos
      /// /////////////////////////////////////////////////////////////
      .addCoCo(new AJavaUsesCorrectPortDirection())
      .addCoCo(new UsedPortsAndVariablesExist())
      .addCoCo(new MultipleBehaviorImplementation())
      .addCoCo(new InitBlockOnlyOnEmbeddedAJava())
      .addCoCo(new AtMostOneInitBlock())
      .addCoCo(new AJavaUsesExistingVariablesAndPorts())
      /* MontiArcAutomaton Cocos */
      
      /// Automaton Cocos
      /// /////////////////////////////////////////////////////////////
      .addCoCo(new ImplementationInNonAtomicComponent())
      
      // CONVENTIONS
      .addCoCo((MontiArcASTBehaviorElementCoCo) new NamesCorrectlyCapitalized())
      .addCoCo(new AutomatonHasNoState())
      .addCoCo(new AutomatonHasNoInitialState())
      .addCoCo(new MultipleAssignmentsSameIdentifier())
      .addCoCo(new AutomatonUsesCorrectPortDirection())
      .addCoCo((MontiArcASTInitialStateDeclarationCoCo) new AutomatonReactionWithAlternatives())
      .addCoCo((MontiArcASTTransitionCoCo) new AutomatonReactionWithAlternatives())
      .addCoCo((MontiArcASTIOAssignmentCoCo) new UseOfForbiddenExpression())
      .addCoCo((MontiArcASTGuardExpressionCoCo) new UseOfForbiddenExpression())
      .addCoCo((MontiArcASTStateCoCo) new NamesCorrectlyCapitalized())
      .addCoCo(new ConnectorSourceAndTargetComponentDiffer())
      .addCoCo(new ConnectorSourceAndTargetExistAndFit())
      .addCoCo(new ImportsAreUnique())
      
      // REFERENTIAL INTEGRITY
      .addCoCo(new UseOfUndeclaredState())
      .addCoCo((MontiArcASTIOAssignmentCoCo) new UseOfUndeclaredField())
      .addCoCo((MontiArcASTGuardExpressionCoCo) new UseOfUndeclaredField())
      .addCoCo(new SubcomponentGenericTypesCorrectlyAssigned())
      .addCoCo(new AssignmentHasNoName())
      .addCoCo(new ConfigurationParametersCorrectlyInherited())
      .addCoCo(new InnerComponentNotExtendsDefiningComponent())
      
      // TYPE CORRECTNESS
      .addCoCo(new AutomatonGuardIsNotBoolean())
      .addCoCo(new GenericInitValues())
      
      // .addCoCo(new AutomatonStimulusTypeDoesNotFitInputType())
      .addCoCo((MontiArcASTTransitionCoCo) new AutomatonReactionTypeDoesNotFitOutputType())
      .addCoCo(
          (MontiArcASTInitialStateDeclarationCoCo) new AutomatonReactionTypeDoesNotFitOutputType())
      
      .addCoCo(new AutomatonNoDataAssignedToVariable())
      
      // UNIQUENESS OF NAMES
      .addCoCo(new AutomatonInitialDeclaredMultipleTimes())
      .addCoCo(new AutomatonStateDefinedMultipleTimes())
      .addCoCo(new UseOfValueLists())
      .addCoCo(new IdentifiersAreUnique())
      .addCoCo(new JavaPVariableIdentifiersUnique());
  
  protected static final MontiArcTool MONTIARCTOOL = new MontiArcTool(new MontiArcLanguageFamily(),
      checker);
  
  @Before
  public void cleanUpLog() {
    Log.getFindings().clear();
    Log.enableFailQuick(false);
  }
  
  protected ASTMontiArcNode loadComponentAST(String qualifiedModelName) {
    File f = Paths.get(MODEL_PATH).toFile();
    Scope symTab = MONTIARCTOOL.initSymbolTable(f, Paths.get(FAKE_JAVA_TYPES_PATH).toFile());
    ComponentSymbol comp = symTab.<ComponentSymbol> resolve(
        qualifiedModelName, ComponentSymbol.KIND).orElse(null);
    assertNotNull("Could not resolve model " + qualifiedModelName, comp);
    ASTMontiArcNode node = (ASTMontiArcNode) comp.getAstNode().orElse(null);
    assertNotNull("Could not find ASTComponent for model " + qualifiedModelName, node);
    return node;
  }
  
  protected ComponentSymbol loadComponentSymbol(String packageName,
      String unqualifiedComponentName) {
    Scope symTab = MONTIARCTOOL.initSymbolTable(MODEL_PATH);
    String qualifiedName = packageName + "." + unqualifiedComponentName;
    ComponentSymbol comp = symTab.<ComponentSymbol> resolve(
        qualifiedName, ComponentSymbol.KIND).orElse(null);
    assertNotNull(comp);
    
    assertEquals(packageName, comp.getPackageName());
    assertEquals(unqualifiedComponentName, comp.getName());
    assertEquals(qualifiedName, comp.getFullName());
    
    return comp;
  }
  
  protected ASTMontiArcNode loadCompilationUnitAST(String qualifiedModelName) {
    Symbol comp = loadComponentAST(qualifiedModelName).getSymbolOpt().orElse(null);
    assertNotNull("Could not resolve model " + qualifiedModelName, comp);
    ASTMontiArcNode node = (ASTMontiArcNode) comp.getEnclosingScope().getAstNode().orElse(null);
    assertNotNull("Could not find ASTMACompilationUnit for model " + qualifiedModelName, node);
    return node;
  }
  
  protected Scope loadDefaultSymbolTable() {
    return MONTIARCTOOL.initSymbolTable(Paths.get(MODEL_PATH).toFile(),
        Paths.get(FAKE_JAVA_TYPES_PATH).toFile());
  }
  
  /**
   * Checks all cocos on the given node, and checks for absence of errors. Use
   * this for checking valid models.
   */
  protected void checkValid(String model) {
    Log.getFindings().clear();
    MONTIARCTOOL.checkCoCos(loadCompilationUnitAST(model));
    new ExpectedErrorInfo().checkOnlyExpectedPresent(Log.getFindings());
  }
  
  /**
   * Runs coco checks on the model with two different coco sets: Once with all
   * cocos, checking that the expected errors are present; once only with the
   * given cocos, checking that no addditional errors are present.
   */
  protected static void checkInvalid(MontiArcCoCoChecker cocos, ASTMontiArcNode node,
      ExpectedErrorInfo expectedErrors) {
    
    // check whether all the expected errors are present when using all cocos
    Log.getFindings().clear();
    MONTIARCTOOL.checkCoCos(node);
    expectedErrors.checkExpectedPresent(Log.getFindings(), "Got no findings when checking all "
        + "cocos. Did you forget to add the new coco to MontiArcCocos?");
    
    // check whether only the expected errors are present when using only the
    // given cocos
    Log.getFindings().clear();
    cocos.checkAll(node);
    expectedErrors.checkOnlyExpectedPresent(Log.getFindings(), "Got no findings when checking only "
        + "the given coco. Did you pass an empty coco checker?");
  }
  
}
