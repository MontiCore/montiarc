package componentinstantiation;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import de.montiarcautomaton.generator.MontiArcGeneratorTool;
import de.se_rwth.commons.logging.Log;


public class SimpleGenerationTest {

  private static final String MODELPATH = "src/test/resources/models/initial";
  private static final String TARGETPATH = "target/generated-test-sources/";



  @Before
  public void setup() {
    Log.enableFailQuick(false);
  }

  @Test
  public void testDynamicGeneration() throws IOException {
	FileUtils.cleanDirectory(Paths.get(TARGETPATH).toFile());  
    MontiArcGeneratorTool script = new MontiArcGeneratorTool();
    script.enableDynamicGeneration(true);
    script.generate(Paths.get(MODELPATH).toFile(), Paths.get(TARGETPATH).toFile(), Paths.get(MODELPATH).toFile());
    assertTrue(Paths.get(TARGETPATH + "genTest/DynamicSubComp.java").toFile().isFile());
    
  }
  
  @Test
  public void testGeneration() throws IOException {
	FileUtils.cleanDirectory(Paths.get(TARGETPATH).toFile());  
    MontiArcGeneratorTool script = new MontiArcGeneratorTool();
    script.enableDynamicGeneration(false);
    script.generate(Paths.get(MODELPATH).toFile(), Paths.get(TARGETPATH).toFile(), Paths.get(MODELPATH).toFile());
    assertTrue(!Paths.get(TARGETPATH + "genTest/DynamicSubComp.java").toFile().isFile());
    
  }

}

