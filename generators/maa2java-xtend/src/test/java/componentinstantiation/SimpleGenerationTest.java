package componentinstantiation;

import java.nio.file.Paths;

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
  public void testGeneration() {
    MontiArcGeneratorTool script = new MontiArcGeneratorTool();
    script.enableDynamicGeneration(true);
    script.generate(Paths.get(MODELPATH).toFile(), Paths.get(TARGETPATH).toFile(), Paths.get(MODELPATH).toFile());
  }

}

