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
 * Base class for all tests that do more than just parsing
 * 
 * @author (last commit) Crispin Kirchner, Andreas Wortmann
 */
public abstract class AbstractCoCoTest {
  
  protected static final String MODEL_PATH = "src/test/resources/";
  
  protected static final MontiArcTool MONTIARCTOOL = new MontiArcTool();
  
  protected static ASTMontiArcNode getAstNode(String modelPath, String model) {
    File f = Paths.get(MODEL_PATH + modelPath).toFile();
    Scope symTab = MONTIARCTOOL.initSymbolTable(f,Paths.get("src/main/resources/defaultTypes").toFile());
    ComponentSymbol comp = symTab.<ComponentSymbol> resolve(
        model, ComponentSymbol.KIND).orElse(null);
    assertNotNull("Could not resolve model " + model, comp);
    
    return (ASTMontiArcNode) comp.getAstNode().get();
  }
  
  protected static ASTMontiArcNode getAstNode(String qualifiedModelName) {
    return getAstNode("", qualifiedModelName);
  }
  
  @Before
  public void cleanUpLog() {
    Log.getFindings().clear();
  }
  
  protected Scope loadDefaultSymbolTable() {
    return MONTIARCTOOL.initSymbolTable(Paths.get(MODEL_PATH).toFile(),
        Paths.get("src/main/resources/defaultTypes").toFile()); 
   }
  
  /**
   * Checks all cocos on the given model. Don't use for writing new test cases, use checkValid and
   * checkInvalid instead.
   */
  @Deprecated
  protected static void runCheckerWithSymTab(String modelPath, String model) {
    Log.getFindings().clear();
    
    ASTMontiArcNode node = getAstNode(modelPath, model);
    
    MONTIARCTOOL.checkCoCos(node);
  }
  
  /**
   * Checks all cocos on the given node, and checks for absence of errors. Use this for checking
   * valid models.
   */
  protected static void checkValid(String modelPath, String model) {
    Log.getFindings().clear();
    MONTIARCTOOL.checkCoCos(getAstNode(modelPath, model));
    new ExpectedErrorInfo().checkOnlyExpectedPresent(Log.getFindings());
  }
  
  protected static void checkValid(String model) {
    checkValid("", model);
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
  
  protected ComponentSymbol loadComponentSymbol(String modelPath, String packageName, String unqualifiedComponentName) {
    Scope symTab = new MontiArcTool().initSymbolTable(MODEL_PATH + modelPath);
    String qualifiedName = packageName + "." + unqualifiedComponentName;
    ComponentSymbol comp = symTab.<ComponentSymbol> resolve(
        qualifiedName, ComponentSymbol.KIND).orElse(null);
    assertNotNull(comp);
    
    assertEquals(packageName, comp.getPackageName());
    assertEquals(unqualifiedComponentName, comp.getName());
    assertEquals(qualifiedName, comp.getFullName());
    
    return comp;
  }
  
  protected ComponentSymbol loadComponentSymbol(String packageName, String unqualifiedComponentName) {
    return this.loadComponentSymbol("", packageName, unqualifiedComponentName);
  }

}
