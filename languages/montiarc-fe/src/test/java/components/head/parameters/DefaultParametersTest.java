/*
 * Copyright (c) 2016 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package components.head.parameters;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import de.monticore.symboltable.types.JFieldSymbol;
import infrastructure.AbstractCoCoTest;
import infrastructure.ExpectedErrorInfo;
import montiarc._ast.ASTParameter;
import montiarc._cocos.MontiArcCoCoChecker;
import montiarc._symboltable.ComponentSymbol;
import montiarc.cocos.DefaultParametersCorrectlyAssigned;
import montiarc.cocos.DefaultParametersHaveCorrectOrder;
import montiarc.cocos.SubcomponentParametersCorrectlyAssigned;

/**
 * This class checks all context conditions related to default parameters
 *
 * @author Robert Heim, Andreas Wortmann
 */
public class DefaultParametersTest extends AbstractCoCoTest {

  private static final String PACKAGE = "components.head.parameters";

  @Test
  public void testDefaultParametersInCorrectOrder() {
    checkValid(PACKAGE + "." + "DefaultParametersInCorrectOrder");
  }
  
  @Test
  public void testComponentWithDefaultParameters() {
    checkValid(PACKAGE + "." + "ComponentWithDefaultParameters");
  }
  
  @Test
  public void testComposedTestComponent() {
    checkInvalid(new MontiArcCoCoChecker().addCoCo(new SubcomponentParametersCorrectlyAssigned()),
        loadComponentAST(PACKAGE + "." + "ComposedTestComponent"),
        new ExpectedErrorInfo(1, "xMA064"));
  }
  
  @Test
  public void testComposedComponentUsingDefaultParameters() {
    checkValid(PACKAGE + "." + "ComposedComponentUsingDefaultParameters");
  }

  @Test
  public void testDefaultParametersInIncorrectOrder() {
    checkInvalid(new MontiArcCoCoChecker().addCoCo(new DefaultParametersHaveCorrectOrder()),
        loadComponentAST(PACKAGE + "." + "DefaultParametersInIncorrectOrder"),
        new ExpectedErrorInfo(1, "xMA056"));
  }

  @Test
  public void testWrongDefaultParameterType() {
    checkInvalid(new MontiArcCoCoChecker().addCoCo(new DefaultParametersCorrectlyAssigned()),
        loadComponentAST(PACKAGE + "." + "DefaultParameterWrongType"),
        new ExpectedErrorInfo(1, "xMA062"));
  }

  @Test
  public void testValidDefaultParameters() {
    ComponentSymbol comp = this.loadComponentSymbol(PACKAGE, "ValidDefaultParameters") ;
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
