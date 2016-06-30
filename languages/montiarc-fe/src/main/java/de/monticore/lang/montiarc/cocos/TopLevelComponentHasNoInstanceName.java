/*
 * Copyright (c) 2016 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.lang.montiarc.cocos;

import de.monticore.lang.montiarc.montiarc._ast.ASTComponent;
import de.monticore.lang.montiarc.montiarc._cocos.MontiArcASTComponentCoCo;
import de.monticore.lang.montiarc.montiarc._symboltable.ComponentSymbol;
import de.se_rwth.commons.logging.Log;

/**
 * @author Crispin Kirchner
 */
public class TopLevelComponentHasNoInstanceName
    implements MontiArcASTComponentCoCo {

  /**
   * @see de.monticore.lang.montiarc.montiarc._cocos.MontiArcASTComponentCoCo#check(de.monticore.lang.montiarc.montiarc._ast.ASTComponent)
   */
  @Override
  public void check(ASTComponent node) {
    if (!node.symbolIsPresent()) {
      Log.error(String.format(
          "0xE51E8 Symbol of component \"%s\" is missing. " +
              "The context condition \"%s\" can't be checked that way.",
          node.getName(), TopLevelComponentHasNoInstanceName.class.getName()));
    }

    ComponentSymbol symbol = (ComponentSymbol) node.getSymbol().get();
    if (!symbol.isInnerComponent() && node.instanceNameIsPresent()) {
      Log.error(
          String.format("0x3F207 Top level component \"%s\" has an instance name", node.getName()),
          node.get_SourcePositionStart());
    }
  }

}
