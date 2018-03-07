/*
 * Copyright (c) 2018 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package montiarc.cocos;

import java.util.Collection;

import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTComponent;
import montiarc._cocos.MontiArcASTComponentCoCo;
import montiarc._symboltable.ComponentSymbol;

/**
 * @implements [Hab16] R12: An inner component type definition must not extend
 * the component type in which it is defined. (p. 68, lst. 3.47)
 * 
 * @author Jerome Pfeiffer
 * @version $Revision$, $Date$
 */
public class InnerComponentNotExtendsDefiningComponent implements MontiArcASTComponentCoCo {
  
  /**
   * @see montiarc._cocos.MontiArcASTComponentCoCo#check(montiarc._ast.ASTComponent)
   */
  @Override
  public void check(ASTComponent node) {
    ComponentSymbol definingComp = (ComponentSymbol) node.getSymbol().get();
    String nameOfDefiningComp = definingComp.getName();
    Collection<ComponentSymbol> innerComps = definingComp.getInnerComponents();
    for (ComponentSymbol inner : innerComps) {
      if (inner.getSuperComponent().isPresent()) {
        String superOfInner = inner.getSuperComponent().get().getName();
        if (superOfInner.equals(nameOfDefiningComp)) {
          Log.error(String.format("0xMA083 Inner component of type extends the component type "
              + superOfInner + " which is the same component type it is defined in."),
              inner.getAstNode().get().get_SourcePositionStart());
        }
      }
    }
  }
  
}
