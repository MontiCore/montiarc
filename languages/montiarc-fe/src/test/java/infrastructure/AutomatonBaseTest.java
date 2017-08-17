package infrastructure;


import java.nio.file.Paths;

import de.monticore.ModelingLanguageFamily;
import de.monticore.io.paths.ModelPath;
import de.monticore.lang.montiarc.helper.JavaHelper;
import de.monticore.lang.montiarc.montiarc._symboltable.MontiArcLanguageFamily;
import de.monticore.symboltable.GlobalScope;
import de.monticore.symboltable.Scope;

public abstract class AutomatonBaseTest {
  protected static Scope createSymTab(String modelPath) {
    // TODO remove usage of cd adapter. See MontiArcAutomatonLanguageFamilyWithCDAdapter.
    // ModelingLanguageFamily fam = new MontiArcAutomatonLanguageFamily();
    ModelingLanguageFamily fam = new MontiArcLanguageFamily();
    final ModelPath mp = new ModelPath(Paths.get(modelPath), Paths.get("src/main/resources/defaultTypes"));
    GlobalScope scope = new GlobalScope(mp, fam);
    JavaHelper.addJavaPrimitiveTypes(scope);
    return scope;
  }
}
