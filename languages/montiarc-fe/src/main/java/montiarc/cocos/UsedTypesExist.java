/* (c) https://github.com/MontiCore/monticore */
package montiarc.cocos;

import de.monticore.symboltable.types.references.ActualTypeArgument;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTComponent;
import montiarc._cocos.MontiArcASTComponentCoCo;
import montiarc._symboltable.ComponentSymbol;
import montiarc._symboltable.ComponentSymbolReference;
import montiarc._symboltable.PortSymbol;
import montiarc._symboltable.VariableSymbol;

/**
 * Checks whether types of ports and variables exist.
 *
 * No literature reference
 *
 */
public class UsedTypesExist implements MontiArcASTComponentCoCo {
  
  /**
   * @see montiarc._cocos.MontiArcASTComponentCoCo#check(montiarc._ast.ASTComponent)
   */
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
    ComponentSymbol comp = (ComponentSymbol) node.getSymbolOpt().get();
    for (PortSymbol p : comp.getPorts()) {
      if (!p.getTypeReference().existsReferencedSymbol()) {
        Log.error(
            String.format("0xMA101 Port type %s of port %s is used but does " +
                              "not exist.",
                p.getTypeReference().getName(), p.getName()),
            p.getSourcePosition());
      }
    }
    
    for (VariableSymbol v : comp.getVariables()) {
      if (!v.getTypeReference().existsReferencedSymbol()) {
        Log.error(String.format("0xMA101 Type %s of variable %s is used but " +
                                    "does not exist.",
            v.getTypeReference().getName(), v.getName()),
            v.getSourcePosition());
        
      }
    }

    if(comp.getSuperComponent().isPresent()){
      final ComponentSymbolReference superCompRef = comp.getSuperComponent().get();
      for (ActualTypeArgument actualTypeArgument : superCompRef.getActualTypeArguments()) {
        if(!actualTypeArgument.getType().existsReferencedSymbol()){
          Log.error(String.format("0xMA102 Type %s is used but does not exist.",
              actualTypeArgument.getType().getName()),
              node.getHead().getSuperComponent().get_SourcePositionStart());
        }
      }
    }
  }
}
