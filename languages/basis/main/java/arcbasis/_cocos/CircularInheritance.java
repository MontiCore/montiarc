/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis._ast.ASTComponentType;
import arcbasis._symboltable.ComponentTypeSymbol;
import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import montiarc.util.ArcError;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Checks whether there is a circular inheritance in the inheritance tree of this component type.
 *
 * @implements [Hab16] R11: Inheritance cycles of component types are forbidden. (p. 67, lst. 3.46)
 */
public class CircularInheritance implements ArcBasisASTComponentTypeCoCo {

  @Override
  public void check(@NotNull ASTComponentType node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkArgument(node.isPresentSymbol());

    if (!node.getSymbol().isPresentParent()) {
      return;
    }
    List<String> superComps = new ArrayList<>();
    ComponentTypeSymbol symbol = node.getSymbol();
    while (symbol.isPresentParent()) {
      superComps.add(symbol.getFullName());
      symbol = symbol.getParent().getTypeInfo();
      if (superComps.contains(symbol.getFullName())) {
        Log.error(ArcError.CIRCULAR_INHERITANCE.format(symbol.getFullName()), symbol.getSourcePosition());
        return;
      }
    }
  }
}