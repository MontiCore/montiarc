/*
 * Copyright (c) 2016 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package components.head.parameters;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import org.junit.BeforeClass;
import org.junit.Test;

import de.monticore.symboltable.Scope;
import de.se_rwth.commons.logging.Log;
import infrastructure.AbstractCoCoTest;
import infrastructure.ExpectedErrorInfo;
import montiarc.MontiArcTool;
import montiarc._cocos.MontiArcCoCoChecker;
import montiarc._symboltable.ComponentSymbol;
import montiarc.cocos.ParameterNamesAreUnique;

/**
 * This class tests all context conditions related to parameters
 * 
 * @author Crispin Kirchner, Andreas Wortmann
 */
public class ParametersTest extends AbstractCoCoTest {
	
  private static final String MP = "";
  private static final String PACKAGE = "components.head.parameters";
  
  @BeforeClass
  public static void setUp() {
    Log.enableFailQuick(false);
  }

  @Test
  public void testParameterNamesUniqueTestValid() {
    checkValid(MP, PACKAGE + "." + "ParameterNamesUnique");
  }

  @Test
  public void testParameterNamesUniqueTestInvalid() {
    checkInvalid(new MontiArcCoCoChecker().addCoCo(new ParameterNamesAreUnique()),
        getAstNode(MP, PACKAGE + "." + "ParameterAmbiguous"),
        new ExpectedErrorInfo(1, "xMA069"));
    
  }
  
  @Test
  public void testComponentWithParameters() {
    String unqualifiedComponentName = "ComponentWithParameters";
    ComponentSymbol comp = this.loadComponentSymbol(MP, PACKAGE, unqualifiedComponentName);
    
    assertFalse(comp.isInnerComponent());
    assertEquals(1, comp.getConfigParameters().size());
    assertEquals(0, comp.getFormalTypeParameters().size());
  }
}
