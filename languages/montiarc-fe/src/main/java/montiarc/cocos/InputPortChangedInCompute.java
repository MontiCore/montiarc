package montiarc.cocos;

import java.util.List;
import java.util.Optional;

import de.monticore.symboltable.Scope;
import de.monticore.symboltable.Symbol;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTJavaPBehavior;
import montiarc._ast.ASTPort;
import montiarc._cocos.MontiArcASTJavaPBehaviorCoCo;
import montiarc._symboltable.PortSymbol;
import montiarc.visitor.InputUnchangedVisitor;

/**
 * Context condition that checks whether an input port is changed in a
 * AJava compute statement.
 *
 * @implements: No literature reference, AJava CoCo
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
    Scope enclosingScope = node.getEnclosingScopeOpt().get();
    
    for (String possiblePort : possiblePorts) {
      Optional<Symbol> portSymbol = enclosingScope.resolve(possiblePort,
          PortSymbol.KIND);
      if (portSymbol.isPresent()) {
        
        // Check whether the port is incoming
        ASTPort port = (ASTPort) portSymbol.get().getAstNode().get();
        if (port.isIncoming()) {
          Log.error(
              String.format("0xMA078 The incoming port %s was changed " +
                                "in a compute statement.", possiblePort),
              node.get_SourcePositionStart());
        }
      }
    }
  }
}
