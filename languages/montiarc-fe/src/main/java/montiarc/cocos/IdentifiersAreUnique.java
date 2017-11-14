package montiarc.cocos;

import de.monticore.types.TypesPrinter;
import de.se_rwth.commons.StringTransformations;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.*;
import montiarc._cocos.MontiArcASTComponentBodyCoCo;
import montiarc._cocos.MontiArcASTComponentCoCo;

import java.util.HashSet;

public class IdentifiersAreUnique implements MontiArcASTComponentCoCo {

  @Override
  public void check(ASTComponent node) {
    HashSet<String> names = new HashSet<>();

    for (ASTElement e : node.getBody().getElements()) {

      // Check variable declarations
      if (e instanceof ASTComponentVariableDeclaration) {
        ASTComponentVariableDeclaration decl = (ASTComponentVariableDeclaration) e;
        for (ASTVariable var : decl.getVariables()) {
          checkList(names, var.getName(), "0xMA035", "port", e);
        }
      }

      //Check port names
      else if (e instanceof ASTInterface) {
        ASTInterface decl = (ASTInterface) e;
        for (ASTPort port : decl.getPorts()) {
          String name = "";
          if (port.getName().isPresent()) {
            name = port.getName().get();
          } else {
            // calc implicit name
            String implicitName = TypesPrinter.printType(port.getType());
            name = StringTransformations.uncapitalize(implicitName);
          }

          checkList(names, name, "0xMA053", "port", e);
        }
      }

      // Check constraints
      else if (e instanceof ASTMontiArcInvariant) {
        ASTMontiArcInvariant invariant = (ASTMontiArcInvariant) e;
        String name = invariant.getName();
        checkList(names, name, "0xMA052", "invariant", e);
      }
    }
  }

  public void checkList(HashSet<String> names, String name, String errorCode, String type, ASTElement e){
    if (names.contains(name)) {
      Log.error(String.format("%s The name of %s '%s' is already used.", errorCode, type, name),
          e.get_SourcePositionStart());
    } else {
      names.add(name);
    }
  }
}
