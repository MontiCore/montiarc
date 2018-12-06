package montiarc.cocos;

import de.monticore.symboltable.Scope;
import de.monticore.symboltable.Symbol;
import de.monticore.symboltable.types.JTypeSymbol;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTComponent;
import montiarc._cocos.MontiArcASTComponentCoCo;
import montiarc._symboltable.ComponentInstanceSymbol;
import montiarc._symboltable.ComponentSymbol;
import montiarc._symboltable.ComponentSymbolReference;
import montiarc._symboltable.PortSymbol;

import java.util.Collection;

/**
 * Checks that the name of the component is not ambiguous to an existing Java
 * type. Also checks for subcomponent and port types that the same holds.
 *
 * @author Manuel Pützer
 */
public class AmbiguousTypes implements MontiArcASTComponentCoCo {

  @Override
  public void check(ASTComponent node) {
    if(!node.getSymbolOpt().isPresent()) {
      Log.error("0xMA010 ComponentSymbol of component AST "+ node.getName() +" is missing");
      return;
    }
      ComponentSymbol comp = (ComponentSymbol) node.getSymbolOpt().get();
    final Scope compEnclosingScope = comp.getEnclosingScope();

    // Check the type of the component which is currently checked
    if (compEnclosingScope.resolveMany(comp.getFullName(), JTypeSymbol.KIND).size() >= 1) {
      Log.error(
          String.format("0xMA012 The name of component %s is ambiguous",
              comp.getName()),
          comp.getSourcePosition());
    }

    // Check all subcomponents of the current component
    for (ComponentInstanceSymbol subcomp : comp.getSubComponents()) {
      final ComponentSymbolReference subCompType = subcomp.getComponentType();
      if(subCompType.existsReferencedSymbol()) {
        final Collection<Symbol> amiguousTypes =
            compEnclosingScope.resolveMany(subCompType.getFullName(), JTypeSymbol.KIND);
        if (amiguousTypes.size() >= 1) {
          Log.error(
              String.format("0xMA040 The type %s of subcomponent %s is ambiguous",
                  subCompType.getName(),
                  subcomp.getName()),
              subcomp.getSourcePosition());
        }
      }
    }

    // Check all port types of the current components ports
    for (PortSymbol portSymbol : comp.getPorts()) {
      if (portSymbol.getTypeReference().existsReferencedSymbol()) {
        final String typeNameFq =
            portSymbol.getTypeReference().getReferencedSymbol().getFullName();

        if (compEnclosingScope.resolveMany(typeNameFq, ComponentSymbol.KIND).size() >= 1) {
          Log.error(
              String.format("0xMA040 The type %s of port %s is ambiguous",
                  portSymbol.getTypeReference().getName(),
                  portSymbol.getName()),
              portSymbol.getSourcePosition());
        }
      }
    }
  }
}
