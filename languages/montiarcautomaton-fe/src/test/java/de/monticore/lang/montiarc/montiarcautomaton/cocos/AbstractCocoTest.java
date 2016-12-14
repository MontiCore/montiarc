package de.monticore.lang.montiarc.montiarcautomaton.cocos;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;

import de.monticore.lang.montiarc.montiarc._ast.ASTMontiArcNode;
import de.monticore.lang.montiarc.montiarc._symboltable.ComponentSymbol;
import de.monticore.lang.montiarc.montiarcautomaton.AbstractSymtabTest;
import de.monticore.lang.montiarc.montiarcautomaton._cocos.MontiArcAutomatonCoCoChecker;
import de.monticore.symboltable.Scope;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;

public class AbstractCocoTest extends AbstractSymtabTest {
  
  protected static ASTMontiArcNode getAstNode(String modelPath, String model) {
    // ensure an empty log
    Log.getFindings().clear();
    
    Scope symTab = createSymTab(modelPath);
    ComponentSymbol comp = symTab.<ComponentSymbol> resolve(
        model, ComponentSymbol.KIND).orElse(null);
    assertNotNull("Could not resolve model " + model, comp);
    
    return (ASTMontiArcNode) comp.getAstNode().get();
  }
  
  /**
   * Checks all cocos on the given model
   */
  protected static void runCheckerWithSymTab(String modelPath, String model) {
    ASTMontiArcNode node = getAstNode(modelPath, model);
    
    MontiArcAutomatonCocos.createChecker().checkAll(node);
  }
  
  /**
   * Checks all cocos on the given node, and checks for absence of errors. Use this for checking
   * valid models.
   */
  protected static void checkValid(String modelPath, String model) {
    checkInvalid(MontiArcAutomatonCocos.createChecker(), getAstNode(modelPath, model),
        new ExpectedErrorInfo());
  }
  
  protected static void checkValidCD4A() {
    new ExpectedErrorInfo().checkFindings(Log.getFindings());
  }
  
  /**
   * Checks the given cocos on the given model and expects the given errors. Use this for checking
   * invalid models and verifying that the right number and type of errors are present.
   */
  protected static void checkInvalid(MontiArcAutomatonCoCoChecker cocos, ASTMontiArcNode node,
      ExpectedErrorInfo expectedErrors) {
    
    cocos.checkAll(node);
    
    expectedErrors.checkFindings(Log.getFindings());
  }
  /**
   * Checks the given cocos on the given model and expects the given errors. Use this for checking
   * invalid models and verifying that the right number and type of errors are present.
   */
  protected static void checkInvalid(ASTMontiArcNode node,
      ExpectedErrorInfo expectedErrors) {
    checkInvalid(MontiArcAutomatonCocos.createChecker(), node, expectedErrors);
  }
  
  protected static class ExpectedErrorInfo {
    private static final Pattern ERROR_CODE_PATTERN = Pattern.compile("x[0-9A-F]{5}");
    
    private int numExpectedFindings = 0;
    
    private HashSet<String> expectedErrorCodes = new HashSet<>();
    
    /**
     * Raises an error if the given error codes don't match the convention for error codes in test
     * cases (no leading zero, capital hexadecimal digits)
     */
    protected static void checkExpectedErrorCodes(String ...errorCodes) {
      
      for (String errorCode : errorCodes) {
        if (!ERROR_CODE_PATTERN.matcher(errorCode).matches()) {
          Log.error(String.format(
              "The given expected error code \"%s\" is not a valid error code (pattern: \"%s\")",
              errorCode, ERROR_CODE_PATTERN.pattern()));
        }
      }
    }
    
    protected static Set<String> collectErrorCodes(String findings) {
      Matcher matcher = ERROR_CODE_PATTERN.matcher(findings);
      
      Set<String> errorCodes = new HashSet<>();
      while (matcher.find()) {
        errorCodes.add(matcher.group());
      }
      
      return errorCodes;
    }
    
    public ExpectedErrorInfo() {
    }
    
    
    public ExpectedErrorInfo(int numExpectedFindings, String ...expectedErrorCodes) {
      checkExpectedErrorCodes(expectedErrorCodes);
      
      this.numExpectedFindings = numExpectedFindings;
      this.expectedErrorCodes.addAll(Lists.newArrayList(expectedErrorCodes));
    }
    
    public void checkFindings(List<Finding> findings) {
      String findingsString = findings.stream().map(f -> f.buildMsg())
          .collect(Collectors.joining("\n"));
      
      assertEquals(findingsString, numExpectedFindings, findings.size());
      
      // check that all expected error codes are present
      Set<String> actualErrorCodes = collectErrorCodes(findingsString);
      assertTrue("Missing errors: found " + actualErrorCodes + " instead of " + expectedErrorCodes, actualErrorCodes.containsAll(expectedErrorCodes));
      
      // check that no additional error codes are present
      Set<String> unexpectedErrorCodes = new HashSet<>(actualErrorCodes);
      unexpectedErrorCodes.removeAll(expectedErrorCodes);
      assertEquals(0, unexpectedErrorCodes.size());
    }
  }
}
