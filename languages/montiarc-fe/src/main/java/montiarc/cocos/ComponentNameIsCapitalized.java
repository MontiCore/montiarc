/*
 * Copyright (c) 2015 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package montiarc.cocos;

import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTComponent;
import montiarc._cocos.MontiArcASTComponentCoCo;
import montiarc._symboltable.MontiArcModelNameCalculator;

/**
 * Ensures, that component names start in upper-case. This is required for inner components, see
 * {@link MontiArcModelNameCalculator}.
 *
 * @author Robert Heim
 */
public class ComponentNameIsCapitalized implements MontiArcASTComponentCoCo {

  /**
   * @see montiarc._cocos.MontiArcASTComponentCoCo#check(montiarc._ast.ASTComponent)
   */
  @Override
  public void check(ASTComponent node) {
    if (!Character.isUpperCase(node.getName().charAt(0))) {
      Log.error("0xAC004 Component names must be start in upper-case",
          node.get_SourcePositionStart());
    }
  }
}
