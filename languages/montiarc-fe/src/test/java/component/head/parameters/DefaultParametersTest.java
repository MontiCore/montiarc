/*
 * Copyright (c) 2016 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package component.head.parameters;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.nio.file.Paths;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import de.monticore.symboltable.types.JFieldSymbol;
import de.se_rwth.commons.logging.Log;
import infrastructure.AbstractCoCoTest;
import infrastructure.ExpectedErrorInfo;
import montiarc.MontiArcTool;
import montiarc._ast.ASTParameter;
import montiarc._cocos.MontiArcCoCoChecker;
import montiarc._symboltable.ComponentSymbol;
import montiarc.cocos.DefaultParametersCorrectlyAssigned;
import montiarc.cocos.DefaultParametersHaveCorrectOrder;

/**
 * This class checks all context conditions related to default parameters
 *
 * @author Robert Heim, Andreas Wortmann
 */
public class DefaultParametersTest extends AbstractCoCoTest {
  
  private static final String MP = "";
  
  private static final String PACKAGE = "component.head.parameters";
  
  @BeforeClass
  public static void setUp() {
    Log.getFindings().clear();
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testDefaultParametersInCorrectOrder() {
    checkValid(MP, PACKAGE + "." + "DefaultParametersInCorrectOrder");
  }
  
  @Test
  public void testDefaultParametersInIncorrectOrder() {
    checkInvalid(new MontiArcCoCoChecker().addCoCo(new DefaultParametersHaveCorrectOrder()),
        getAstNode(MP, PACKAGE + "." + "DefaultParametersInIncorrectOrder"),
        new ExpectedErrorInfo(1, "xMA056"));
  }
  
  @Test
  public void testWrongDefaultParameterType() {
    checkInvalid(new MontiArcCoCoChecker().addCoCo(new DefaultParametersCorrectlyAssigned()),
        getAstNode("", PACKAGE + "." + "DefaultParameterWrongType"),
        new ExpectedErrorInfo(1, "xMA061"));
  }
  
  @Test
  public void testValidDefaultParameters() {
    MontiArcTool tool = new MontiArcTool();
    String modelPath = "src/test/resources/";
    ComponentSymbol comp = tool
        .loadComponentSymbolWithoutCocos("component.head.parameters.ValidDefaultParameters", Paths.get(modelPath).toFile())
        .orElse(null);
    assertNotNull(comp);
    
    List<JFieldSymbol> params = comp.getConfigParameters();
    for (JFieldSymbol param : params) {
      if (param.getAstNode().isPresent()) {
        ASTParameter p = (ASTParameter) param.getAstNode().get();
        if (p.getName().equals("offset")) {
          assertTrue(p.getDefaultValue().isPresent());
          assertEquals(5, p.getDefaultValue().get());
        }
        else {
          assertFalse(p.getDefaultValue().isPresent());
        }
      }
      
    }
  }
  
}
