/*
 * Copyright (c) 2017 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.montiarcautomaton.generator.util;

import java.util.HashMap;
import java.util.Map;

import _templates.de.montiarcautomaton.ajava.AJavaMainFactory;
import _templates.mc.montiarcautomaton.automaton.lib.AutomatonImplMainFactory;
import de.monticore.lang.montiarc.ajava._ast.ASTAJavaDefinition;
import de.monticore.lang.montiarc.montiarcautomaton._ast.ASTAutomatonDefinition;
import de.monticore.templateclassgenerator.util.GeneratorInterface;

/**
 * Maps SymbolKinds to a certain generator interface that generates code for
 * this kind of symbol.
 *
 * @author Jerome Pfeiffer
 * @version $Revision$, $Date$
 */
public class BehaviorGeneratorsMap {
  
  public static final Map<Class<?>, GeneratorInterface> behaviorGenerators = createMap();
  
  private static Map<Class<?>, GeneratorInterface> createMap() {
    Map<Class<?>, GeneratorInterface> result = new HashMap<>();
    result.put(ASTAJavaDefinition.class, AJavaMainFactory.create());
    result.put(ASTAutomatonDefinition.class, AutomatonImplMainFactory.create());
    return result;
  }
}
