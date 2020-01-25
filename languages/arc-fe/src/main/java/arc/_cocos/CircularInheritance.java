/* (c) https://github.com/MontiCore/monticore */
package arc._cocos;

import arc._ast.ASTComponent;
import arc._symboltable.ComponentSymbol;
import arc.util.ArcError;
import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Checks whether there is a circular inheritance in the inheritance tree of this component type.
 *
 * @implements [Hab16] R11: Inheritance cycles of component types are forbidden. (p. 67, lst. 3.46)
 */
public class CircularInheritance implements ArcASTComponentCoCo {

  @Override
  public void check(@NotNull ASTComponent node) {
    Preconditions.checkArgument(node != null);
    Preconditions.checkArgument(node.isPresentSymbol(), "ASTComponent node '%s' has no symbol. "
      + "Did you forget to run the SymbolTableCreator before checking cocos?", node.getName());
    if (!node.getSymbol().isPresentParentComponent()) {
      return;
    }
    List<String> superComps = new ArrayList<>();
    ComponentSymbol symbol = node.getSymbol();
    while (symbol.isPresentParentComponent()) {
      superComps.add(symbol.getFullName());
      symbol = symbol.getParentInfo();
      if (superComps.contains(symbol.getFullName())) {
        Log.error(String.format(ArcError.CIRCULAR_INHERITANCE.toString(), symbol.getFullName()));
        return;
      }
    }
  }
}