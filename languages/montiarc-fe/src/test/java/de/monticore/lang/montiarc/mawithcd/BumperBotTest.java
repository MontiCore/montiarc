/*
 * Copyright (c) 2016 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.lang.montiarc.mawithcd;

import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import de.monticore.lang.montiarc.montiarc._symboltable.ComponentSymbol;
import de.monticore.lang.montiarc.montiarc._symboltable.PortSymbol;
import de.monticore.symboltable.Scope;
import de.monticore.symboltable.types.JTypeSymbol;
import de.se_rwth.commons.logging.Log;

/**
 * TODO: Write me!
 *
 * @author (last commit) Crispin Kirchner
 * @version $Revision$, $Date$
 * @since TODO: add version number
 */
public class BumperBotTest extends AbstractSymTabTest {
  
	@Before
	public void setup() {
		Log.getFindings().clear();
		Log.enableFailQuick(false);
	}

	
  /**
   * This test crashes.
   */
  //@Ignore
  @Test
  public void test() {
    Scope symbolTable = createSymTab("src/test/resources");
    ComponentSymbol motorSymbol= symbolTable
        .<ComponentSymbol>resolve("bumperbot.library.Motor", ComponentSymbol.KIND)
        .orElse(null);
    
    assertNotNull(motorSymbol);

    PortSymbol commandPort = motorSymbol.getIncomingPort("command").orElse(null);
     
    assertNotNull(commandPort);
    
    JTypeSymbol typeSymbol = commandPort
        .getTypeReference()
        .getReferencedSymbol();
    
    assertNotNull(typeSymbol);

    //assertTrue(typeSymbol instanceof Cd2MaTypeAdapter);
  }
}
