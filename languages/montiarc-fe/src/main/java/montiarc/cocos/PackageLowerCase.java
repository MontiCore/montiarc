/*
 * Copyright (c) 2015 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package montiarc.cocos;

import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTMACompilationUnit;
import montiarc._cocos.MontiArcASTMACompilationUnitCoCo;
import montiarc._symboltable.MontiArcModelNameCalculator;

/**
 * Ensures, that packages of components are lower-case. This is required for inner components, see
 * {@link MontiArcModelNameCalculator}.
 *
 * @author Robert Heim
 */
public class PackageLowerCase implements MontiArcASTMACompilationUnitCoCo {

  /**
   * @see montiarc._cocos.MontiArcASTMACompilationUnitCoCo#check(montiarc._ast.ASTMACompilationUnit)
   */
  @Override
  public void check(ASTMACompilationUnit node) {
    String pack = Names.getQualifiedName(node.getPackage());
    if (pack.toUpperCase().equals(pack)) {
      Log.error("0xMA054 The package must be lower case", node.get_SourcePositionStart());
    }
  }

}
