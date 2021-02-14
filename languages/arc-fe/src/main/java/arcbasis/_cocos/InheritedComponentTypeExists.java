/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis._ast.ASTComponentType;
import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis._symboltable.ComponentTypeSymbolSurrogate;
import arcbasis.util.ArcError;
import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import org.codehaus.commons.nullanalysis.NotNull;

/**
 * Checks for each component type whether an inherited component type exists
 */
public class InheritedComponentTypeExists implements ArcBasisASTComponentTypeCoCo {

  @Override
  public void check(@NotNull ASTComponentType node) {
    Preconditions.checkArgument(node != null);
    Preconditions.checkArgument(node.isPresentSymbol(), "ASTComponent node '%s' has no symbol. "
      + "Did you forget to run the SymbolTableCreator before checking cocos?", node.getName());
    ComponentTypeSymbol symbol = node.getSymbol();
    if (!symbol.isPresentParentComponent()) {
      return;
    }

    ComponentTypeSymbol parent = symbol.getParent();
    if (parent instanceof ComponentTypeSymbolSurrogate) {
      Log.error(ArcError.MISSING_TYPE_OF_INHERITED_COMPONENT.format(
        symbol.getParent().getName(), symbol.getFullName()), node.get_SourcePositionStart());
    }
  }
}