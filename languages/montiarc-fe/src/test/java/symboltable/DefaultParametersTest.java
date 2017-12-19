/*
 * Copyright (c) 2016 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package symboltable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.nio.file.Paths;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import de.monticore.symboltable.Scope;
import de.monticore.symboltable.types.JFieldSymbol;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcTool;
import montiarc._ast.ASTParameter;
import montiarc._symboltable.ComponentSymbol;

public class DefaultParametersTest {

  public static final boolean ENABLE_FAIL_QUICK = true;
  
  private final String MODEL_PATH = "src/test/resources/symboltable";
  
  private static MontiArcTool tool; 

  @BeforeClass
  public static void setUp() {
    // ensure an empty log
    Log.getFindings().clear();
    Log.enableFailQuick(ENABLE_FAIL_QUICK);
    tool = new MontiArcTool();
  }

  @Test
  public void testSubcomponentWithInstanceName() {
    ComponentSymbol comp = tool.getComponentSymbol("features.DefaultParameters", Paths.get(MODEL_PATH).toFile()).orElse(null);
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
