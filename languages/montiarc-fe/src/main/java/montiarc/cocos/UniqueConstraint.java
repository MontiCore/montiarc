/*
 * Copyright (c) 2015 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package montiarc.cocos;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTComponent;
import montiarc._ast.ASTMontiArcInvariant;
import montiarc._cocos.MontiArcASTComponentCoCo;

/**
 * Checks that names of invariants within a component are unique.
 *
 * @author Arne Haber, Robert Heim
 */
public class UniqueConstraint implements MontiArcASTComponentCoCo {

  /**
   * @see montiarc._cocos.MontiArcASTComponentCoCo#check(montiarc._ast.ASTComponent)
   */
  @Override
  public void check(ASTComponent node) {
    final List<String> usedNames = new ArrayList<String>();

    for (ASTMontiArcInvariant inv : node.getBody().getElements().stream()
        .filter(a -> a instanceof ASTMontiArcInvariant).map(a -> (ASTMontiArcInvariant) a)
        .collect(Collectors.toList())) {

      if (usedNames.contains(inv.getName())) {
        Log.error(String.format("0xMA052 The name of constraint '%s' is ambiguos!", inv.getName()),
            inv.get_SourcePositionStart());
      }

      usedNames.add(inv.getName());
    }
  }

}
