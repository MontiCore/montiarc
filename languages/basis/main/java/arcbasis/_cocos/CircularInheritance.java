/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis._ast.ASTComponentType;
import com.google.common.base.Preconditions;
import de.monticore.symbols.compsymbols._symboltable.ComponentSymbol;
import de.monticore.symboltable.ISymbol;
import de.monticore.types.check.CompKindExpression;
import de.se_rwth.commons.logging.Log;
import montiarc.util.ArcError;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.Collection;
import java.util.HashSet;

/**
 * This context-condition checks that no component extends itself either
 * directly or transitively.
 * <p>
 * Implements [Hab16] R11: Inheritance cycles of component types are forbidden.
 * (p. 67, lst. 3.46)
 */
public class CircularInheritance implements ArcBasisASTComponentTypeCoCo {

  /**
   * Checks that the component does not extend itself.
   * @param node the component that is to be checked
   */
  @Override
  public void check(@NotNull ASTComponentType node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkArgument(node.isPresentSymbol());

    this.check(node, node.getSymbol(), new HashSet<>());
  }

  /**
   * Recursively check that the root component does not transitively extend
   * itself starting with the parent of the next component symbol.
   * Terminates the search if the parent has already been visited, i.e,
   * if there is a component inheritance cycle and only logs an error if
   * the parent is the root of the search.
   * @param root the root component of the search
   * @param next the next component whose parent is to be checked
   * @param visited the (component) symbols already checked
   */
  protected void check(@NotNull ASTComponentType root,
                       @NotNull ComponentSymbol next,
                       @NotNull Collection<ISymbol> visited) {
    Preconditions.checkNotNull(root);
    Preconditions.checkNotNull(next);
    Preconditions.checkNotNull(visited);
    for (CompKindExpression parent : next.getSuperComponentsList()) {
      if (parent.getTypeInfo().equals(root.getSymbol())) {
        // log error if the parent is the root of the search
        Log.error(ArcError.CIRCULAR_INHERITANCE.format(root.getName()),
          root.getHead().get_SourcePositionStart(), root.getHead().get_SourcePositionEnd()
        );
      } else if (!visited.contains(parent.getTypeInfo())) {
        // continue if the parent has not been visited yet
        visited.add(parent.getTypeInfo());
        this.check(root, parent.getTypeInfo(), visited);
      }
    }
  }
}