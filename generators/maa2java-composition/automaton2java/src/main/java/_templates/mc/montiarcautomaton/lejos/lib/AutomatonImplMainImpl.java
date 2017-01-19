/*
 * Copyright (c) 2017 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package _templates.mc.montiarcautomaton.lejos.lib;

import java.nio.file.Path;
import java.util.Collection;

import de.montiarcautomaton.lejosgenerator.helper.AutomatonHelper;
import de.monticore.ast.ASTNode;
import de.monticore.automaton.ioautomaton._symboltable.AutomatonSymbol;
import de.monticore.lang.montiarc.montiarc._symboltable.ComponentSymbol;
import de.monticore.symboltable.CommonSymbol;

/**
 * TODO: Write me!
 *
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 * @since TODO: add version number
 */
public class AutomatonImplMainImpl extends AutomatonImplMain {
  
  /**
   * @see _templates.mc.montiarcautomaton.lejos.lib.AutomatonImplMain#doGenerate(java.nio.file.Path,
   * de.monticore.ast.ASTNode, de.monticore.symboltable.CommonSymbol)
   */
  @Override
  public void doGenerate(Path filepath, ASTNode node, CommonSymbol symbol) {
    if (symbol.getKind().isKindOf(ComponentSymbol.KIND)) {
      ComponentSymbol comp = (ComponentSymbol) symbol;
      Collection<AutomatonSymbol> ajava = comp.getSpannedScope()
          .<AutomatonSymbol> resolveLocally(AutomatonSymbol.KIND);
      AutomatonSymbol automaton = ajava.iterator().next();
      
      String inputName = comp.getName() + "Input";
      String resultName = comp.getName() + "Result";
      String implName = comp.getName() + "Impl";
      AutomatonHelper helper = new AutomatonHelper(automaton, comp);
      
      generate(filepath, node, helper, comp.getPackageName(), comp.getImports(), comp.getName(),
          resultName, inputName, implName, comp.getIncomingPorts(), helper.getVariables(),
          helper.getStates(), comp.getConfigParameters());
    }
    
  }
  
}
