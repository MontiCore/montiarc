/*
 * Copyright (c) 2016 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package symboltable;

import static org.junit.Assert.assertNotNull;

import java.nio.file.Paths;

import org.junit.Before;
import org.junit.Test;

import de.monticore.symboltable.types.JTypeSymbol;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcTool;
import montiarc._symboltable.ComponentSymbol;
import montiarc._symboltable.PortSymbol;

public class BumperBotTest {
  
  private static MontiArcTool tool;
  
  @Before
  public void setup() {
    Log.getFindings().clear();
    Log.enableFailQuick(false);
    tool = new MontiArcTool();
  }
  
  @Test
  public void test() {
    ComponentSymbol motorSymbol = tool.getComponentSymbol("contextconditions.valid.Motor", Paths.get("src/test/resources").toFile())
        .orElse(null);
    
    assertNotNull(motorSymbol);
    
    PortSymbol commandPort = motorSymbol.getIncomingPort("command").orElse(null);
    
    assertNotNull(commandPort);
    
    JTypeSymbol typeSymbol = commandPort
        .getTypeReference()
        .getReferencedSymbol();
    
    assertNotNull(typeSymbol);
  }
  
}
