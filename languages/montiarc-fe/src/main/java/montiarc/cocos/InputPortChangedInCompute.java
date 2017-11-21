package montiarc.cocos;

import de.monticore.symboltable.Scope;
import de.monticore.symboltable.Symbol;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTJavaPBehavior;
import montiarc._ast.ASTPort;
import montiarc._cocos.MontiArcASTJavaPBehaviorCoCo;
import montiarc._symboltable.PortSymbol;
import montiarc.visitor.InputUnchangedVisitor;

import java.util.List;
import java.util.Optional;

/**
 * Context condition that checks whether an input port is changed in a AJava compute statement.
 *
 * @author Michael Mutert
 */
public class InputPortChangedInCompute implements MontiArcASTJavaPBehaviorCoCo {

  @Override
  public void check(ASTJavaPBehavior node) {

    List<String> possiblePorts;

    InputUnchangedVisitor visitor = new InputUnchangedVisitor();
    visitor.handle(node);
    possiblePorts = visitor.getPossiblePorts();

    // Try to resolve the names of possible ports
    Scope enclosingScope = node.getEnclosingScope().get();

    for (String possiblePort : possiblePorts) {
      Optional<Symbol> portSymbol = enclosingScope.resolve(possiblePort,
          PortSymbol.KIND);
      if (portSymbol.isPresent()) {

        //Check whether the port is incoming
        ASTPort port = (ASTPort) portSymbol.get().getAstNode().get();
        if (port.isIncoming()) {
          Log.error("0xMA078 The incoming port " + possiblePort + " was changed in a compute statement.", node.get_SourcePositionStart());
        }
      }
    }
  }
}
