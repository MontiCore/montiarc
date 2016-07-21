/*
 * Copyright (c) 2015 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.lang.montiarc.cocos;

import java.util.ArrayList;
import java.util.List;

import de.monticore.lang.montiarc.montiarc._ast.ASTInterface;
import de.monticore.lang.montiarc.montiarc._ast.ASTPort;
import de.monticore.lang.montiarc.montiarc._cocos.MontiArcASTInterfaceCoCo;
import de.monticore.types.TypesPrinter;
import de.se_rwth.commons.StringTransformations;
import de.se_rwth.commons.logging.Log;

/**
 * Checks that port names are unique (including implicit port names derived from ports without a
 * name).
 *
 * @author Arne Haber, Robert Heim
 */
public class UniquePorts implements MontiArcASTInterfaceCoCo {

  /**
   * @see de.monticore.lang.montiarc.montiarc._cocos.MontiArcASTInterfaceCoCo#check(de.monticore.lang.montiarc.montiarc._ast.ASTInterface)
   */
  @Override
  public void check(ASTInterface node) {
    List<String> usedNames = new ArrayList<>();
    for (ASTPort port : node.getPorts()) {
      String name = "";
      if (port.getName().isPresent()) {
        name = port.getName().get();
      }
      else {
        // calc implicit name
        String implicitName = TypesPrinter.printType(port.getType());
        name = StringTransformations.uncapitalize(implicitName);
      }
      if (usedNames.contains(name)) {
        Log.error(String.format("0xAC002 The name of port '%s' is ambiguos!", name),
            port.get_SourcePositionStart());
      }
      usedNames.add(name);

    }
  }

}
