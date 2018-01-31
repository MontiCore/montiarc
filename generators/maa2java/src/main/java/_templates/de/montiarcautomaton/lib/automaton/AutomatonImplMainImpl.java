/*
 * Copyright (c) 2017 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package _templates.de.montiarcautomaton.lib.automaton;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import de.montiarcautomaton.generator.helper.AutomatonHelper;
import de.montiarcautomaton.generator.helper.ComponentHelper;
import de.monticore.ast.ASTNode;
import de.monticore.symboltable.CommonSymbol;
import montiarc._ast.ASTJavaPInitializer;
import montiarc._ast.ASTValueInitialization;
import montiarc._symboltable.AutomatonSymbol;
import montiarc._symboltable.ComponentSymbol;

/**
 * Generates an implementation class for components with automatons.
 *
 * @author Jerome Pfeiffer
 * @version $Revision$, $Date$
 */
public class AutomatonImplMainImpl extends AutomatonImplMain {
  
  
  /**
   * @see _templates.mc.montiarcautomaton.lejos.lib.AutomatonImplMain#doGenerate(java.nio.file.Path,
   * de.monticore.ast.ASTNode, de.monticore.symboltable.CommonSymbol)
   */
  @Override
  public void generate(Path filepath, ASTNode node, CommonSymbol symbol) {
    if (symbol.getKind().isKindOf(ComponentSymbol.KIND)) {
      ComponentSymbol comp = (ComponentSymbol) symbol;
      Collection<AutomatonSymbol> ajava = comp.getSpannedScope()
          .<AutomatonSymbol> resolveLocally(AutomatonSymbol.KIND);
      AutomatonSymbol automaton = ajava.iterator().next();
      
      String inputName = comp.getName() + "Input";
      String resultName = comp.getName() + "Result";
      String implName = comp.getName() + "Impl";
      AutomatonHelper helper = new AutomatonHelper(automaton, comp);
      ComponentHelper compHelper = new ComponentHelper(comp);

      AutomatonImplMain.generate(filepath, node, helper, comp.getPackageName(), comp.getImports(),
          comp.getName(),
          resultName, inputName, implName,          
          comp.getIncomingPorts(), compHelper, comp.getVariables(),
          helper.getStates(), comp.getConfigParameters());
    }
  }
  
}
