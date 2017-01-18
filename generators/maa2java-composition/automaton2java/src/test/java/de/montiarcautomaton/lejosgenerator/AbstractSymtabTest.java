package de.montiarcautomaton.lejosgenerator;


import java.nio.file.Paths;

import de.monticore.ModelingLanguageFamily;
import de.monticore.automaton.ioautomaton.JavaHelper;
import de.monticore.io.paths.ModelPath;
import de.monticore.lang.montiarc.montiarcautomaton.cdadapter.MontiArcAutomatonLanguageFamilyWithCDAdapter;
import de.monticore.symboltable.GlobalScope;
import de.monticore.symboltable.Scope;

public class AbstractSymtabTest {
  protected static Scope createSymTab(String modelPath) {
    // TODO remove usage of cd adapter. See MontiArcAutomatonLanguageFamilyWithCDAdapter.
    // ModelingLanguageFamily fam = new MontiArcAutomatonLanguageFamily();
    ModelingLanguageFamily fam = new MontiArcAutomatonLanguageFamilyWithCDAdapter();
    final ModelPath mp = new ModelPath(Paths.get(modelPath), Paths.get("src/main/resources/defaultTypes"));
    GlobalScope scope = new GlobalScope(mp, fam);
    JavaHelper.addJavaPrimitiveTypes(scope);
    return scope;
  }
}
