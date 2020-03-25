/* (c) https://github.com/MontiCore/monticore */
package infrastructure;

import de.monticore.symboltable.Scope;
import de.monticore.symboltable.Symbol;
import de.se_rwth.commons.logging.Log;
import dynamicmontiarc.DynamicMontiArcTool;
import dynamicmontiarc._ast.ASTDynamicMontiArcNode;
import dynamicmontiarc._cocos.DynamicMontiArcCoCoChecker;
import montiarc._ast.ASTMontiArcNode;
import montiarc._symboltable.ComponentSymbol;
import org.junit.Before;

import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Abstract base class for all tests that do more than just parsing
 * 
 */
public abstract class AbstractCoCoTest {
  
  protected static final String MODEL_PATH = "src/test/resources/";
  
  protected static final String FAKE_JAVA_TYPES_PATH = "target/librarymodels/";

  protected static final DynamicMontiArcTool DYNAMICMATOOL = new DynamicMontiArcTool();

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
    ComponentSymbol comp = loadComponentSymbolFromModelPath(qualifiedModelName, MODEL_PATH).orElse(null);

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
  protected Optional<ComponentSymbol> loadComponentSymbolFromModelPath(String qualifiedModelName,
                                                                       String modelPath){
    Scope symTab = DYNAMICMATOOL.initSymbolTable(Paths.get(modelPath).toFile(),
        Paths.get(FAKE_JAVA_TYPES_PATH).toFile());
    final Optional<ComponentSymbol> componentSymbol = symTab.<ComponentSymbol>resolve(
            qualifiedModelName, ComponentSymbol.KIND);
    return componentSymbol;
  }

  /**
   * Load the component with the given name from the given package that resides
   * in the given model path.
   * @param packageName The package that contains the component
   * @param modelName The unqualified name of the component
   * @param modelPath The model path containing the package
   * @return The loaded component symbol
   */
  protected Optional<ComponentSymbol> loadComponentSymbolFromModelPath(String packageName,
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
    return DYNAMICMATOOL.initSymbolTable(Paths.get(MODEL_PATH).toFile(),
        Paths.get(FAKE_JAVA_TYPES_PATH).toFile());
  }
  
  /**
   * Checks all cocos on the given node, and checks for absence of errors. Use
   * this for checking valid models.
   * @param model The fully qualified name of the model
   */
  protected void checkValid(String model) {
    Log.getFindings().clear();
    DYNAMICMATOOL.checkCoCos(loadCompilationUnitAST(model));
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
  protected static void checkInvalid(DynamicMontiArcCoCoChecker cocos, ASTMontiArcNode node,
                                     ExpectedErrorInfo expectedErrors) {
    
    // check whether all the expected errors are present when using all cocos
    Log.getFindings().clear();
    DYNAMICMATOOL.checkCoCos(node);
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
