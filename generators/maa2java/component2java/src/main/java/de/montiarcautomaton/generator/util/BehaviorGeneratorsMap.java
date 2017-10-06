/*
 * Copyright (c) 2017 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.montiarcautomaton.generator.util;

import java.util.HashMap;

import java.util.Map;

import _templates.de.montiarcautomaton.lib.ajava.AJavaMainFactory;
import _templates.de.montiarcautomaton.lib.automaton.AutomatonImplMainFactory;
import de.monticore.templateclassgenerator.util.GeneratorInterface;
import montiarc._ast.ASTAutomatonBehavior;
import montiarc._ast.ASTJavaPBehavior;

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
    result.put(ASTJavaPBehavior.class, AJavaMainFactory.create());
    result.put(ASTAutomatonBehavior.class, AutomatonImplMainFactory.create());
    return result;
  }
}
