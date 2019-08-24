/* (c) https://github.com/MontiCore/monticore */
package montiarc.cocos;

import de.monticore.symboltable.types.JFieldSymbol;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTComponent;
import montiarc._ast.ASTElement;
import montiarc._ast.ASTJavaPInitializer;
import montiarc._ast.ASTValueInitialization;
import montiarc._cocos.MontiArcASTComponentCoCo;
import montiarc._symboltable.ComponentSymbol;
import montiarc._symboltable.PortSymbol;
import montiarc._symboltable.VariableSymbol;

import java.util.Optional;

/**
 * Checks whether all used ports in the ajava initialisation exist in the
 * component definition.
 * SymboltableCreator already ensures that used ports and variables in the
 * ajava behavior are consistent.
 *
 * @implements No literature reference
 *
 * @author Andreas Wortmann
 */
public class AJavaInitUsedPortsAndVariablesExist
    implements MontiArcASTComponentCoCo {

  @Override
  public void check(ASTComponent node) {
    if (!node.getSymbolOpt().isPresent()) {
      Log.error(
          String.format("0xMA010 ASTComponent node \"%s\" has no " +
                            "symbol. Did you forget to run the " +
                            "SymbolTableCreator before checking cocos?",
              node.getName()));
      return;
    }
    ComponentSymbol cmp = (ComponentSymbol) node.getSymbolOpt().get();
    checkAJavaInitialization(node, cmp);
  }
  
  private void checkAJavaInitialization(ASTComponent node, ComponentSymbol cmp) {
    for (ASTElement e : node.getBody().getElementList()) {
      if (e instanceof ASTJavaPInitializer) {
        ASTJavaPInitializer init = (ASTJavaPInitializer) e;
        for (ASTValueInitialization i : init.getValueInitializationList()) {
          String name = Names.getQualifiedName(i.getQualifiedName().getPartList());
          Optional<PortSymbol> port = cmp.getSpannedScope().<PortSymbol> resolve(name,
              PortSymbol.KIND);
          Optional<VariableSymbol> compVar = cmp.getSpannedScope()
              .<VariableSymbol> resolve(name, VariableSymbol.KIND);
          Optional<JFieldSymbol> cmpParameter = cmp.getConfigParameters().stream()
              .filter(p -> p.getName().equals(name)).findFirst();
          if (!port.isPresent() && !compVar.isPresent() && !cmpParameter.isPresent()) {
            Log.error(String.format("0xMA030 Used variable %s in AJava " +
                                        "initialization is not a port, " +
                                        "component variable or locally " +
                                        "defined variable.", name),
                i.get_SourcePositionStart());
          }
          
          if (port.isPresent()) {
            if (port.get().isIncoming()) {
              Log.error(
                  String.format("0xMA032 Port %s is incoming, " +
                                    "and thus must not be changed",
                      port.get().getName()),
                  i.get_SourcePositionStart());
            }
          }
        }
      }
    }
  }
}
