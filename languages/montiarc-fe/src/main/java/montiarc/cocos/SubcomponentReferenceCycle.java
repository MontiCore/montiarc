/*
 * Copyright (c) 2018 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package montiarc.cocos;

import de.se_rwth.commons.SourcePosition;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTComponent;
import montiarc._cocos.MontiArcASTComponentCoCo;
import montiarc._symboltable.ComponentInstanceSymbol;
import montiarc._symboltable.ComponentSymbol;

import java.util.Optional;

/**
 * @implements [Hab16] R13: Subcomponent reference cycles are forbidden. (p. 68,
 * lst. 3.48)
 * 
 * @author Jerome Pfeiffer
 * @version $Revision$, $Date$
 */
public class SubcomponentReferenceCycle implements MontiArcASTComponentCoCo {

  /**
   * @see montiarc._cocos.MontiArcASTComponentCoCo#check(montiarc._ast.ASTComponent)
   */
  @Override
  public void check(ASTComponent node) {
    if (!node.getSymbolOpt().isPresent()) {
      Log.error(
          String.format("0xMA010 ASTComponent node \"%s\" has no " +
                            "symbol. Did you forget to run the " +
                            "SymbolTableCreator before checking cocos?",
              node.getName()));
      return;
    }
    ComponentSymbol comp = (ComponentSymbol) node.getSymbolOpt().get();
    for(ComponentInstanceSymbol subCompInstance : comp.getSubComponents()) {
      SourcePosition sourcePos = SourcePosition.getDefaultSourcePosition();
      if(subCompInstance.getAstNode().isPresent()){
        sourcePos = subCompInstance.getAstNode().get().get_SourcePositionStart();
      }

      final Optional<ComponentSymbol> subCompInstanceCompOpt
          = subCompInstance.getComponentType().getReferencedComponent();
      if(!subCompInstanceCompOpt.isPresent()){
        continue;
      }
      for(ComponentInstanceSymbol subsubcomp : subCompInstanceCompOpt.get().getSubComponents()) {
        if(comp.getName().equals(subsubcomp.getComponentType().getName())) {
          Log.error(
              String.format("0xMA086 Subcomponent %s declares %s as " +
                                "subcomponent as well.",
                  subCompInstance.getName(), comp.getName()),
              sourcePos);
        }
      }
    }
  }
  
}
