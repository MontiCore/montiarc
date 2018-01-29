/*
 * Copyright (c) 2016 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package contextconditions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import de.monticore.symboltable.Scope;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcTool;
import montiarc._ast.ASTMontiArcNode;
import montiarc._cocos.MontiArcCoCoChecker;
import montiarc._symboltable.ComponentSymbol;

/**
 * @author (last commit) Crispin Kirchner
 */
public class AbstractCoCoTest {
  
  private static final String MODEL_PATH = "src/test/resources/";
  
  private static final MontiArcTool MONTIARCTOOL = new MontiArcTool();
  
  protected static ASTMontiArcNode getAstNode(String modelPath, String model) {
    File f = Paths.get(MODEL_PATH + modelPath).toFile();
    Scope symTab = MONTIARCTOOL.createSymbolTable(f,Paths.get("src/main/resources/defaultTypes").toFile());
    ComponentSymbol comp = symTab.<ComponentSymbol> resolve(
        model, ComponentSymbol.KIND).orElse(null);
    assertNotNull("Could not resolve model " + model, comp);
    
    return (ASTMontiArcNode) comp.getAstNode().get();
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
    new AbstractCoCoTestExpectedErrorInfo().checkOnlyExpectedPresent(Log.getFindings());
  }
  
  /**
   * Runs coco checks on the model with two different coco sets: Once with all cocos, checking that
   * the expected errors are present; once only with the given cocos, checking that no addditional
   * errors are present.
   */
  protected static void checkInvalid(MontiArcCoCoChecker cocos, ASTMontiArcNode node,
      AbstractCoCoTestExpectedErrorInfo expectedErrors) {
    
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
