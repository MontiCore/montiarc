/*
 * Copyright (c) 2017 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package generation;

import java.io.File;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import de.montiarcautomaton.generator.MontiArcGeneratorTool;
import de.se_rwth.commons.logging.Log;
import montiarc._symboltable.ComponentSymbol;
import montiarc._symboltable.PortSymbol;

/**
 * TODO: Write me!
 *
 * @author  (last commit) $Author$
 * @version $Revision$,
 *          $Date$
 * @since   TODO: add version number
 *
 */
public class GenerationTest {
  
  private static final String MODELPATH = "src/test/resources/";
  private static final String TARGETPATH = "target/generated-test-sources/";
  
  
  
  @Before
  public void setup() {
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testGeneration() {
    MontiArcGeneratorTool script = new MontiArcGeneratorTool();
    script.generate(Paths.get(MODELPATH).toFile(), Paths.get(TARGETPATH).toFile(), Paths.get("src/main/java").toFile());
  }
  
}
