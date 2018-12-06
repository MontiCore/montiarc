/*
 * Copyright (c) 2016 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package infrastructure;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.awt.*;
import java.io.File;
import java.nio.file.Paths;

import de.monticore.symboltable.Symbol;
import montiarc._cocos.*;
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
      .addCoCo(new UsedTypesExist())
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
      .addCoCo(new PortNamesAreNotJavaKeywords())
      .addCoCo(new UnusedImports())
      .addCoCo(new AmbiguousTypes())
      
      /// AJava Cocos
      /// /////////////////////////////////////////////////////////////
      .addCoCo(new AJavaUsesCorrectPortDirection())
      .addCoCo(new AJavaInitUsedPortsAndVariablesExist())
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
      .addCoCo(new ArraysOfGenericTypes())
      .addCoCo(new AutomatonHasNoInitialState())
      .addCoCo(new MultipleAssignmentsSameIdentifier())
      .addCoCo(new AutomatonUsesCorrectPortDirection())
      .addCoCo((MontiArcASTInitialStateDeclarationCoCo) new AutomatonReactionWithAlternatives())
      .addCoCo((MontiArcASTTransitionCoCo) new AutomatonReactionWithAlternatives())
      .addCoCo((MontiArcASTIOAssignmentCoCo) new UseOfForbiddenExpression())
      .addCoCo((MontiArcASTGuardExpressionCoCo) new UseOfForbiddenExpression())
      .addCoCo((MontiArcASTStateCoCo) new NamesCorrectlyCapitalized())
      .addCoCo((MontiArcASTParameterCoCo) new UseOfProhibitedIdentifiers())
      .addCoCo((MontiArcASTVariableDeclarationCoCo) new UseOfProhibitedIdentifiers())
      .addCoCo((MontiArcASTPortCoCo) new UseOfProhibitedIdentifiers()) 
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
      .addCoCo(new ProhibitGenericsWithBounds())

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
  
  protected static final MontiArcTool MONTIARCTOOL
      = new MontiArcTool(new MontiArcLanguageFamily(), checker);
  
  @Before
  public void cleanUpLog() {
    Log.getFindings().clear();
    Log.enableFailQuick(false);
  }

  /**
   * Loads the component AST Node for the given fully qualified model Name
   * @param qualifiedModelName The qualified name of the model
   * @return The AST node of the model
   */
  protected ASTMontiArcNode loadComponentAST(String qualifiedModelName) {
    ComponentSymbol comp = loadComponentSymbol(qualifiedModelName);
    assertNotNull("Could not resolve model " + qualifiedModelName, comp);
    ASTMontiArcNode node = (ASTMontiArcNode) comp.getAstNode().orElse(null);
    assertNotNull("Could not find ASTComponent for model " + qualifiedModelName, node);
    return node;
  }

  /**
   * Loads the component AST Node for the specified model from the specified package.
   * @param packageName Name of the package containing the model
   * @param modelName Name of the model
   * @return The AST node of the component
   */
  protected ASTMontiArcNode loadComponentAST(String packageName, String modelName){
    return loadComponentAST(packageName + "." + modelName);
  }

  /**
   * Loads the symbol of the component specified by the fully qualified component
   * model name.
   * @param qualifiedModelName The fully qualified name of the model to load
   * @return The symbol of the loaded model
   */
  protected ComponentSymbol loadComponentSymbol(String qualifiedModelName){
    ComponentSymbol comp = loadComponentSymbolFromModelPath(qualifiedModelName, MODEL_PATH);

    assertNotNull(comp);
    assertEquals(qualifiedModelName, comp.getFullName());

    return comp;
  }

  /**
   * Loads the component with the given component name that is located in the
   * given package
   * @param packageName The package that contains the component
   * @param unqualifiedComponentName The unqualified name of the component
   * @return The symbol of the component
   */
  protected ComponentSymbol loadComponentSymbol(String packageName,
      String unqualifiedComponentName) {
    String qualifiedName = packageName + "." + unqualifiedComponentName;
    return loadComponentSymbol(qualifiedName);
  }

  /**
   * Load the component with the given name from the given package that resides
   * in the given model path.
   * @param qualifiedModelName The qualified name of the model
   * @param modelPath The model path containing the package
   * @return The loaded component symbol
   */
  protected ComponentSymbol loadComponentSymbolFromModelPath(String qualifiedModelName,
                                                String modelPath){
    Scope symTab = MONTIARCTOOL.initSymbolTable(Paths.get(modelPath).toFile(),
        Paths.get(FAKE_JAVA_TYPES_PATH).toFile());
    ComponentSymbol comp = symTab.<ComponentSymbol> resolve(
        qualifiedModelName, ComponentSymbol.KIND).orElse(null);
    return comp;
  }

  /**
   * Load the component with the given name from the given package that resides
   * in the given model path.
   * @param packageName The package that contains the component
   * @param modelName The unqualified name of the component
   * @param modelPath The model path containing the package
   * @return The loaded component symbol
   */
  protected ComponentSymbol loadComponentSymbolFromModelPath(String packageName,
                                                String modelName,
                                                String modelPath){
    return loadComponentSymbolFromModelPath(
        packageName + "." + modelName, modelPath);
  }


  protected ASTMontiArcNode loadCompilationUnitAST(String qualifiedModelName) {
    Symbol comp = loadComponentSymbol(qualifiedModelName);
    assertNotNull("Could not resolve model " + qualifiedModelName, comp);
    ASTMontiArcNode node = (ASTMontiArcNode) comp.getEnclosingScope().getAstNode().orElse(null);
    assertNotNull("Could not find ASTMACompilationUnit for model " + qualifiedModelName, node);
    return node;
  }

  /**
   * Initializes the symbol table with the normal model path and the types
   * of the java standard library, like java.util, java.lang, etc
   * @return The symbol table
   */
  protected Scope loadDefaultSymbolTable() {
    return MONTIARCTOOL.initSymbolTable(Paths.get(MODEL_PATH).toFile(),
        Paths.get(FAKE_JAVA_TYPES_PATH).toFile());
  }
  
  /**
   * Checks all cocos on the given node, and checks for absence of errors. Use
   * this for checking valid models.
   * @param model The fully qualified name of the model
   */
  protected void checkValid(String model) {
    Log.getFindings().clear();
    MONTIARCTOOL.checkCoCos(loadCompilationUnitAST(model));
    new ExpectedErrorInfo().checkOnlyExpectedPresent(Log.getFindings());
  }

  /**
   * See {@link AbstractCoCoTest#checkValid(String)}
   * @param packageName Package name of the component to check
   * @param modelName The unqualified name of the component
   */
  protected void checkValid(String packageName, String modelName){
    checkValid(packageName + "." + modelName);
  }
  
  /**
   * Runs coco checks on the model with two different coco sets: Once with all
   * cocos, checking that the expected errors are present; once only with the
   * given cocos, checking that no addditional errors are present.
   * @param cocos The checker containing all the cocos to check
   * @param node The node of the component to check
   * @param expectedErrors The information about expected errors
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
