/*
 * Copyright (c) 2015 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.lang.montiarc;

import java.nio.file.Paths;

import de.monticore.ModelingLanguageFamily;
import de.monticore.io.paths.ModelPath;
import de.monticore.lang.montiarc.montiarc._symboltable.JavaHelper;
import de.monticore.lang.montiarc.montiarc._symboltable.MontiArcLanguageFamily;
import de.monticore.symboltable.GlobalScope;
import de.monticore.symboltable.Scope;

/**
 * Common methods for symboltable tests
 *
 * @author Robert Heim
 */
public class AbstractSymtabTest {
  protected static Scope createSymTab(String modelPath) {
    ModelingLanguageFamily fam = new MontiArcLanguageFamily();
    // TODO how to add java default types?
    final ModelPath mp = new ModelPath(Paths.get(modelPath),
        Paths.get("src/main/resources/defaultTypes"));
    GlobalScope scope = new GlobalScope(mp, fam);
    JavaHelper.addJavaPrimitiveTypes(scope);
    return scope;
  }
}
