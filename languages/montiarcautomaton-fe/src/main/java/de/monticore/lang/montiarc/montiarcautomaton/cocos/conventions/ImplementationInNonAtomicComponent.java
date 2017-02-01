/*
 * Copyright (c) 2017 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.lang.montiarc.montiarcautomaton.cocos.conventions;

import java.util.Collection;

import de.monticore.automaton.ioautomaton._symboltable.AutomatonSymbol;
import de.monticore.lang.montiarc.montiarc._ast.ASTComponent;
import de.monticore.lang.montiarc.montiarc._cocos.MontiArcASTComponentCoCo;
import de.monticore.lang.montiarc.montiarc._symboltable.ComponentSymbol;
import de.se_rwth.commons.logging.Log;

/**
 * TODO: Write me!
 *
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 * @since TODO: add version number
 */
public class ImplementationInNonAtomicComponent
    implements MontiArcASTComponentCoCo {

  /**
   * @see de.monticore.lang.montiarc.montiarc._cocos.MontiArcASTComponentCoCo#check(de.monticore.lang.montiarc.montiarc._ast.ASTComponent)
   */
  @Override
  public void check(ASTComponent node) {
    ComponentSymbol cs = (ComponentSymbol) node.getSymbol().get();
    Collection<AutomatonSymbol> a = cs.getSpannedScope().<AutomatonSymbol> resolveLocally(AutomatonSymbol.KIND);
    if(cs.isDecomposed() && !a.isEmpty()) {
      Log.error("0xAB141 There must not be a behavior embedding in composed components.",
          node.get_SourcePositionStart());
    }
    
  }
  
  
}
