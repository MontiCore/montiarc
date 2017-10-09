package infrastructure;


import java.nio.file.Paths;

import de.monticore.ModelingLanguageFamily;
import de.monticore.io.paths.ModelPath;
import de.monticore.symboltable.GlobalScope;
import de.monticore.symboltable.Scope;
import montiarc._symboltable.MontiArcLanguageFamily;
import montiarc.helper.JavaHelper;

public abstract class AutomatonBaseTest {
  protected static Scope createSymTab(String modelPath) {
    ModelingLanguageFamily fam = new MontiArcLanguageFamily();
    final ModelPath mp = new ModelPath(Paths.get(modelPath), Paths.get("src/main/resources/defaultTypes"));
    GlobalScope scope = new GlobalScope(mp, fam);
    JavaHelper.addJavaPrimitiveTypes(scope);
    return scope;
  }
}
