/*
 * Copyright (c) 2015 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.montiarc.generator.codegen;

import java.nio.file.Path;
import java.nio.file.Paths;

import de.monticore.ModelingLanguageFamily;
import de.monticore.io.paths.ModelPath;
import de.monticore.java.lang.JavaDSLLanguage;
import de.monticore.lang.montiarc.montiarc._symboltable.JavaHelper;
import de.monticore.lang.montiarc.montiarc._symboltable.MontiArcLanguage;
import de.monticore.symboltable.GlobalScope;
import de.monticore.symboltable.Scope;

/**
 * Common methods for symboltable tests
 *
 * @author Robert Heim
 */
public class AbstractSymtabTest {
  protected static Scope createSymTab(Path modelPath) {
    ModelingLanguageFamily fam = new ModelingLanguageFamily();
    fam.addModelingLanguage(new MontiArcLanguage());
    fam.addModelingLanguage(new JavaDSLLanguage());
    
    final ModelPath mp = new ModelPath(modelPath, Paths.get("src/main/resources/defaultTypes"));
    GlobalScope scope = new GlobalScope(mp, fam);
    JavaHelper.addJavaPrimitiveTypes(scope);
    return scope;
  }
  
  protected static Scope createJavaSymTab(Path... modelPath) {
    ModelingLanguageFamily fam = new ModelingLanguageFamily();
    // fam.addModelingLanguage(new MontiArcLanguage());
    fam.addModelingLanguage(new JavaDSLLanguage());
    // Java2Arc
    // fam.addResolver(new Java2ArcResolver());
    
    final ModelPath mp = new ModelPath(modelPath);
    GlobalScope scope = new GlobalScope(mp, fam);
    JavaHelper.addJavaPrimitiveTypes(scope);
    
    return scope;
  }
}
