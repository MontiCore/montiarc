/*
 * Copyright (c) 2016 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package montiarc.cocos;

import java.util.ArrayList;
import java.util.List;

import de.se_rwth.commons.SourcePosition;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTComponent;
import montiarc._cocos.MontiArcASTComponentCoCo;
import montiarc._symboltable.ComponentInstanceSymbol;
import montiarc._symboltable.ComponentSymbol;

/**
 * Checks whether names of subcomponent instances of a composed component are unique
 *
 * @author Robert Heim, Andreas Wortmann
 */
public class ComponentInstanceNamesAreUnique implements MontiArcASTComponentCoCo {

  /**
   * @see montiarc._cocos.MontiArcASTComponentCoCo#check(montiarc._ast.ASTComponent)
   */
  @Override
  public void check(ASTComponent node) {
    ComponentSymbol comp = (ComponentSymbol) node.getSymbol().get();
    List<String> names = new ArrayList<>();
    for (ComponentInstanceSymbol subComp : comp.getSubComponents()) {

      if (names.contains(subComp.getFullName())) {

        SourcePosition pos = subComp.getAstNode().isPresent()
            ? subComp.getAstNode().get().get_SourcePositionStart()
            : SourcePosition.getDefaultSourcePosition();

        Log.error(String.format("0xMA061 The subcomponent instance %s is not unique",
            subComp.getFullName()), pos);
      }
      else {
        names.add(subComp.getFullName());
      }
    }

  }
}
