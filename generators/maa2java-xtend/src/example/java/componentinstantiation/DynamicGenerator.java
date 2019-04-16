package componentinstantiation;

import java.nio.file.Paths;

import de.montiarcautomaton.generator.MontiArcGeneratorTool;


public class DynamicGenerator {

  private static final String MODELPATH = "src/test/resources/models/initial";
  private static final String TARGETPATH = "target/generated-test-sources/";



  public static void main(String[] args)  {
    MontiArcGeneratorTool script = new MontiArcGeneratorTool();
    script.enableDynamicGeneration(true);
    script.generate(Paths.get(MODELPATH).toFile(), Paths.get(TARGETPATH).toFile(), Paths.get(MODELPATH).toFile());    
  }
  

}

