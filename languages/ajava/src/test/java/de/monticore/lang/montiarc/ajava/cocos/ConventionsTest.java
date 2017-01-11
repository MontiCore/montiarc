/*
 * Copyright (c) 2017 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.lang.montiarc.ajava.cocos;

import org.junit.Before;

import org.junit.Test;

import de.se_rwth.commons.logging.Log;

/**
 * TODO: Write me!
 *
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 * @since TODO: add version number
 */
public class ConventionsTest extends AbstractCocoTest {
  
  @Before
  public void setup() {
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testAJavaDefinitionUpperCase() {
    checkInvalid(getAstNode("src/test/resources", "cocos.conventions.invalid.AJavaDefinitionUpperCase"), new ExpectedErrorInfo(1, "xAA310"));
  }
  
}
