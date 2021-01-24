/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis._ast.ASTComponentInstance;
import arcbasis._symboltable.ComponentInstanceSymbol;
import arcbasis._symboltable.ComponentTypeSymbolSurrogate;
import arcbasis.util.ArcError;
import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import org.codehaus.commons.nullanalysis.NotNull;

/**
 * Checks for each component instance whether its component type exists.
 */
public class ComponentInstanceTypeExists implements ArcBasisASTComponentInstanceCoCo {

  @Override
  public void check(@NotNull ASTComponentInstance node) {
    Preconditions.checkArgument(node != null);
    Preconditions.checkArgument(node.isPresentSymbol(), "ASTComponentInstance node '%s' has no "
        + "symbol. Did you forget to run the SymbolTableCreator before checking cocos?",
      node.getName());
    ComponentInstanceSymbol symbol = node.getSymbol();
    if (symbol.getType() instanceof ComponentTypeSymbolSurrogate) {
      Log.error(String.format(ArcError.MISSING_TYPE_OF_COMPONENT_INSTANCE.toString(),
        symbol.getType().getName(), symbol.getName()));
    }
  }
}