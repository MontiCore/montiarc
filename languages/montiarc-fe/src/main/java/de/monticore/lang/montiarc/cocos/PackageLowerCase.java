/*
 * Copyright (c) 2015 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.lang.montiarc.cocos;

import de.monticore.lang.montiarc.montiarc._ast.ASTMACompilationUnit;
import de.monticore.lang.montiarc.montiarc._cocos.MontiArcASTMACompilationUnitCoCo;
import de.monticore.lang.montiarc.montiarc._symboltable.MontiArcModelNameCalculator;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;

/**
 * Ensures, that packages of components are lower-case. This is required for inner components, see
 * {@link MontiArcModelNameCalculator}.
 *
 * @author Robert Heim
 */
public class PackageLowerCase implements MontiArcASTMACompilationUnitCoCo {

  /**
   * @see de.monticore.lang.montiarc.montiarc._cocos.MontiArcASTMACompilationUnitCoCo#check(de.monticore.lang.montiarc.montiarc._ast.ASTMACompilationUnit)
   */
  @Override
  public void check(ASTMACompilationUnit node) {
    String pack = Names.getQualifiedName(node.getPackage());
    if (pack.toUpperCase().equals(pack)) {
      Log.error("0xAC003 The package must be lower case", node.get_SourcePositionStart());
    }
  }

}
