/*
 * Copyright (c) 2016 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package symboltable;

import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import de.monticore.symboltable.Scope;
import de.monticore.symboltable.types.JTypeSymbol;
import de.se_rwth.commons.logging.Log;
import montiarc._symboltable.ComponentSymbol;
import montiarc._symboltable.PortSymbol;

public class BumperBotTest extends AbstractSymboltableTest {
  
  @Before
  public void setup() {
    Log.getFindings().clear();
    Log.enableFailQuick(false);
  }
  
  @Test
  public void test() {
    Scope symbolTable = createSymTab("src/test/resources");
    ComponentSymbol motorSymbol = symbolTable
        .<ComponentSymbol> resolve("contextconditions.valid.Motor", ComponentSymbol.KIND)
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
