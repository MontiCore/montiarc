/* (c) https://github.com/MontiCore/monticore */
package arc._cocos;

import arc._ast.ASTComponent;
import arc._symboltable.ComponentSymbol;
import arc.util.ArcError;
import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;

/**
 * @implements [Hab16] R12: An inner component type definition must not extend
 * the component type in which it is defined. (p. 68, lst. 3.47)
 */
public class InnerComponentNotExtendsDefiningComponent implements ArcASTComponentCoCo {

  @Override
  public void check(@NotNull ASTComponent node) {
    Preconditions.checkArgument(node != null);
    Preconditions.checkArgument(node.isPresentSymbol(), "ASTComponent node '%s' has no symbol. "
      + "Did you forget to run the SymbolTableCreator before checking cocos?", node.getName());
    ComponentSymbol outer = node.getSymbol();
    if (outer.isInnerComponent()) {
      return;
    }
    Collection<ComponentSymbol> innerComps = outer.getInnerComponents();
    Deque<String> nameStack = new ArrayDeque<>();
    nameStack.push(outer.getFullName());
    for (ComponentSymbol inner : innerComps) {
      this.checkInner(inner, nameStack);
    }
  }

  /**
   * Recursively check the inner components.
   *
   * @param comp Symbol of the component which should be checked
   * @param compNameStack Stack of fully qualified component names which occur
   * higher in the inner component hierarchy.
   */
  private void checkInner(ComponentSymbol comp, Deque<String> compNameStack) {
    if (comp.isPresentParentComponent()) {
      String superComponent = comp.getParentInfo().getFullName();
      if (compNameStack.contains(superComponent)) {
        Log.error(
          String.format(ArcError.INNER_COMPONENT_EXTENDS_OUTER.toString(),
            comp.getFullName(), superComponent),
          comp.getAstNode().get_SourcePositionStart());
      }
    }
    compNameStack.push(comp.getFullName());

    for (ComponentSymbol inner : comp.getInnerComponents()) {
      checkInner(inner, compNameStack);
    }
    compNameStack.pop();
  }
}