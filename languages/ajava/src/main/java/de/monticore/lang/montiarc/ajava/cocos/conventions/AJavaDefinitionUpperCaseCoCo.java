/*
 * Copyright (c) 2017 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.lang.montiarc.ajava.cocos.conventions;

import de.monticore.lang.montiarc.javap._ast.ASTAJavaDefinition;
import de.monticore.lang.montiarc.javap._cocos.AJavaASTAJavaDefinitionCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * TODO: Write me!
 *
 * @author  (last commit) $Author$
 * @version $Revision$,
 *          $Date$
 * @since   TODO: add version number
 *
 */
public class AJavaDefinitionUpperCaseCoCo implements AJavaASTAJavaDefinitionCoCo {

  /**
   * @see de.monticore.lang.montiarc.ajava._cocos.AJavaASTAJavaDefinitionCoCo#check(de.monticore.lang.montiarc.ajava._ast.ASTAJavaDefinition)
   */
  @Override
  public void check(ASTAJavaDefinition node) {
    if (!Character.isUpperCase(node.getName().charAt(0))) {
      Log.error("0xAA310 The name of the ajava definition should start with an uppercase letter.", node.get_SourcePositionStart());
    }
  }
  
}
