package de.monticore.lang.montiarc.mawithcd;

import java.nio.file.Paths;

import de.monticore.ModelingLanguageFamily;
import de.monticore.io.paths.ModelPath;
import de.monticore.lang.montiarc.montiarc._symboltable.MaWithCdLanguageFamily;
import de.monticore.symboltable.GlobalScope;
import de.monticore.symboltable.Scope;

public abstract class AbstractSymTabTest {
  
  protected static Scope createSymTab(String modelPathString) {
    ModelingLanguageFamily family = new MaWithCdLanguageFamily();
    
    // TODO java defaultTypes should be added from JavaDSL/MontiArc itself
    final ModelPath modelPath = new ModelPath(Paths.get(modelPathString), Paths.get("src/main/resources/defaultTypes"));
    GlobalScope scope = new GlobalScope(modelPath, family);
    
    return scope;
  }
}
