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
import de.monticore.automaton.ioautomaton._symboltable.AutomatonSymbol;
import de.monticore.lang.montiarc.ajava._symboltable.AJavaDefinitionSymbol;
import de.monticore.symboltable.SymbolKind;
import de.monticore.templateclassgenerator.util.GeneratorInterface;

/**
 * Maps SymbolKinds to a certain generator interface that generates code for
 * this kind of symbol.
 *
 * @author Jerome Pfeiffer
 * @version $Revision$, $Date$
 */
public class BehaviorGeneratorsMap {
  
  public static final Map<SymbolKind, GeneratorInterface> behaviorGenerators = createMap();
  
  private static Map<SymbolKind, GeneratorInterface> createMap() {
    Map<SymbolKind, GeneratorInterface> result = new HashMap<>();
    result.put(AJavaDefinitionSymbol.KIND, AJavaMainFactory.create());
    result.put(AutomatonSymbol.KIND, AutomatonImplMainFactory.create());
    return result;
  }
}
