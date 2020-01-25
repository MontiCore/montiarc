/* (c) https://github.com/MontiCore/monticore */
package arc._cocos;

import arc._ast.ASTComponent;
import arc._symboltable.ComponentSymbol;
import arc.util.ArcError;
import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.NoSuchElementException;

/**
 * Checks for each component type whether an inherited component type exists
 */
public class InheritedComponentTypeExists implements ArcASTComponentCoCo {

  @Override
  public void check(@NotNull ASTComponent node) {
    Preconditions.checkArgument(node != null);
    Preconditions.checkArgument(node.isPresentSymbol(), "ASTComponent node '%s' has no symbol. "
      + "Did you forget to run the SymbolTableCreator before checking cocos?", node.getName());
    ComponentSymbol symbol = node.getSymbol();
    if (!symbol.isPresentParentComponent()) {
      return;
    }
    try {
      symbol.getParentInfo();
    }
    catch (NoSuchElementException e) {
      Log.error(String.format(ArcError.MISSING_TYPE_OF_INHERITED_COMPONENT.toString(),
        symbol.getParent().getName(), symbol.getFullName()), node.get_SourcePositionStart());
    }
  }
}