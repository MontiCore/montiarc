package montiarc.cocos;

import java.util.HashSet;
import java.util.List;

import de.monticore.types.TypesPrinter;
import de.se_rwth.commons.StringTransformations;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTComponent;
import montiarc._ast.ASTComponentVariableDeclaration;
import montiarc._ast.ASTElement;
import montiarc._ast.ASTInterface;
import montiarc._ast.ASTMontiArcInvariant;
import montiarc._ast.ASTPort;
import montiarc._ast.ASTVariable;
import montiarc._cocos.MontiArcASTComponentCoCo;

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
      
      // Check port names
      else if (e instanceof ASTInterface) {
        ASTInterface decl = (ASTInterface) e;
        for (ASTPort port : decl.getPorts()) {
          List<String> portInstanceNames = port.getNames();
          if (portInstanceNames.isEmpty()) {
            String implicitName = TypesPrinter.printType(port.getType());
            portInstanceNames.add(StringTransformations.uncapitalize(implicitName));
          }
          for (String name : portInstanceNames) {
            checkList(names, name, "0xMA053", "port", e);
          }
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
  
  public void checkList(HashSet<String> names, String name, String errorCode, String type,
      ASTElement e) {
    if (names.contains(name)) {
      Log.error(String.format("%s The name of %s '%s' is already used.", errorCode, type, name),
          e.get_SourcePositionStart());
    }
    else {
      names.add(name);
    }
  }
}
