/*
 * Copyright (c) 2016 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package montiarc.cocos;

import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTComponent;
import montiarc._cocos.MontiArcASTComponentCoCo;
import montiarc._symboltable.ComponentSymbol;

/**
 * @implements [Hab16] B2: Top-level component type definitions do not have instance names. (p. 59.
 * Lst. 3.32)
 * @author Crispin Kirchner
 */
public class TopLevelComponentHasNoInstanceName
    implements MontiArcASTComponentCoCo {
  
  /**
   * @see montiarc._cocos.MontiArcASTComponentCoCo#check(montiarc._ast.ASTComponent)
   */
  @Override
  public void check(ASTComponent node) {
    if (!node.isPresentSymbol()) {
      Log.error(String.format(
          "0xMA071 Symbol of component \"%s\" is missing. " +
              "The context condition \"%s\" can't be checked that way.",
          node.getName(), TopLevelComponentHasNoInstanceName.class.getName()));
    }
    
    ComponentSymbol symbol = (ComponentSymbol) node.getSymbolOpt().get();
    if (!symbol.isInnerComponent() && node.isPresentInstanceName()) {
      Log.error(
          String.format("0xMA007 Top level component \"%s\" has an instance name", node.getName()),
          node.get_SourcePositionStart());
    }
  }
  
}
