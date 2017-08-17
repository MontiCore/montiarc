/*
 * Copyright (c) 2015 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package montiarc.cocos;

import java.util.ArrayList;
import java.util.List;

import de.monticore.types.TypesPrinter;
import de.se_rwth.commons.StringTransformations;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTInterface;
import montiarc._ast.ASTPort;
import montiarc._cocos.MontiArcASTInterfaceCoCo;

/**
 * Checks that port names are unique (including implicit port names derived from ports without a
 * name).
 *
 * @author Arne Haber, Robert Heim
 */
public class PortNamesAreUnique implements MontiArcASTInterfaceCoCo {

  /**
   * @see montiarc._cocos.MontiArcASTInterfaceCoCo#check(montiarc._ast.ASTInterface)
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
