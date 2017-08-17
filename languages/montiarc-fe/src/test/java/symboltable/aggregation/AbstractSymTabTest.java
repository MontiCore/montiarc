package symboltable.aggregation;

import java.nio.file.Paths;

import de.monticore.ModelingLanguageFamily;
import de.monticore.io.paths.ModelPath;
import de.monticore.symboltable.GlobalScope;
import de.monticore.symboltable.Scope;
import montiarc._symboltable.MontiArcLanguageFamily;

public abstract class AbstractSymTabTest {
  
  protected static Scope createSymTab(String modelPathString) {
    ModelingLanguageFamily family = new MontiArcLanguageFamily();
    
    // TODO java defaultTypes should be added from JavaDSL/MontiArc itself
    final ModelPath modelPath = new ModelPath(Paths.get(modelPathString), Paths.get("src/main/resources/defaultTypes"));
    GlobalScope scope = new GlobalScope(modelPath, family);
    
    return scope;
  }
}
