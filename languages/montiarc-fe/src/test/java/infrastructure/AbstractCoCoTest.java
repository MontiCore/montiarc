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

import org.junit.Before;

import de.monticore.symboltable.Scope;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcTool;
import montiarc._ast.ASTMontiArcNode;
import montiarc._cocos.MontiArcCoCoChecker;
import montiarc._symboltable.ComponentSymbol;

/**
 * Abstract base class for all tests that do more than just parsing
 * 
 * @author (last commit) Crispin Kirchner, Andreas Wortmann
 */
public abstract class AbstractCoCoTest {
  
  protected static final String MODEL_PATH = "src/test/resources/";
  
  protected static final String FAKE_JAVA_TYPES_PATH = "target/librarymodels/";
  
  protected static final MontiArcTool MONTIARCTOOL = new MontiArcTool();
  
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
    
    return (ASTMontiArcNode) comp.getAstNode().get();
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
  
  protected Scope loadDefaultSymbolTable() {
    return MONTIARCTOOL.initSymbolTable(Paths.get(MODEL_PATH).toFile(),
        Paths.get(FAKE_JAVA_TYPES_PATH).toFile());
  }
  
  /**
   * Checks all cocos on the given node, and checks for absence of errors. Use this for checking
   * valid models.
   */
  protected void checkValid(String model) {
    Log.getFindings().clear();
    MONTIARCTOOL.checkCoCos(loadComponentAST(model));
    new ExpectedErrorInfo().checkOnlyExpectedPresent(Log.getFindings());
  }
  
  /**
   * Runs coco checks on the model with two different coco sets: Once with all cocos, checking that
   * the expected errors are present; once only with the given cocos, checking that no addditional
   * errors are present.
   */
  protected static void checkInvalid(MontiArcCoCoChecker cocos, ASTMontiArcNode node,
      ExpectedErrorInfo expectedErrors) {
    
    // check whether all the expected errors are present when using all cocos
    Log.getFindings().clear();
    MONTIARCTOOL.checkCoCos(node);
    expectedErrors.checkExpectedPresent(Log.getFindings(), "Got no findings when checking all "
        + "cocos. Did you forget to add the new coco to MontiArcCocos?");
    
    // check whether only the expected errors are present when using only the given cocos
    Log.getFindings().clear();
    cocos.checkAll(node);
    expectedErrors.checkOnlyExpectedPresent(Log.getFindings(), "Got no findings when checking only "
        + "the given coco. Did you pass an empty coco checker?");
  }
  
}
