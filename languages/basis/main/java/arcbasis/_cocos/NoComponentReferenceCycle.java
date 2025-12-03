/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis._ast.ASTArcComponentType;
import arcbasis._ast.ASTComponentInstance;
import com.google.common.base.Preconditions;
import de.monticore.symbols.compsymbols._symboltable.ComponentTypeSymbol;
import de.monticore.symbols.compsymbols._symboltable.SubcomponentSymbol;
import de.monticore.types.check.CompKindExpression;
import de.se_rwth.commons.logging.Log;
import montiarc.util.ArcError;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

/**
 * [Hab16] R13: "A reference cycle is given if two component types declare each other as subcomponents. Since
 * instantiation of such a system will result in an endless instantiation process, these cycles are forbidden." This
 * also transfers to deeper nested subcomponents. Example from Hab16: component A { B myB; } component B { A myA; }
 */
public class NoComponentReferenceCycle implements ArcBasisASTArcComponentTypeCoCo {

  @Override
  public void check(@NotNull ASTArcComponentType node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkArgument(node.isPresentSymbol());

    Deque<ComponentTypeSymbol> stack = new LinkedList<>();
    stack.push(node.getSymbol());

    for (ASTComponentInstance subcomponent : node.getSubComponents()) {
      if (!subcomponent.isPresentSymbol() || !subcomponent.getSymbol().isTypePresent()) continue;

      stack.push(subcomponent.getSymbol().getType().getTypeInfo());
      List<ComponentTypeSymbol> referenceCycle = this.findRefCycleDepthFirst(stack);
      if (!referenceCycle.isEmpty()) {
        Log.error(ArcError.COMPONENT_REFERENCE_CYCLE.format(node.getName(), printCycle(referenceCycle)),
          subcomponent.get_SourcePositionStart(), subcomponent.get_SourcePositionEnd()
        );
      }
      stack.pop();
    }
  }

  /**
   * Detects reference cycles in component type hierarchies using depth-first search (DFS).
   *
   * <p>This method searches for self-references where a component type contains itself
   * (directly or indirectly) through its subcomponents. It uses the provided recursion
   * trace (a double-ended queue) where the last element represents the current search root
   * and the first element is the trace origin. A cycle exists if the first and last elements
   * match, confirming the root loops back to the origin.</p>
   *
   * <p>If no cycle is found at the current node, it recursively explores all distinct
   * instantiated subcomponent types of the origin using DFS, maintaining the trace to
   * track the path. This prevents infinite recursion in cyclic graphs and ensures complete
   * cycle detection.</p>
   *
   * @param trace the recursion stack, never {@code null} or empty. The
   *              {@linkplain Deque#peekLast() last element} is the current search root,
   *              and {@linkplain Deque#peekFirst() first element} is the trace origin.
   * @return the full cycle path as a <em>defensive copy</em> list of
   * {@link ComponentTypeSymbol}s if a cycle is detected; otherwise, an empty list
   * @throws NullPointerException if {@code trace} is {@code null} or empty
   */
  protected List<ComponentTypeSymbol> findRefCycleDepthFirst(@NotNull Deque<ComponentTypeSymbol> trace) {
    Preconditions.checkNotNull(trace);
    Preconditions.checkNotNull(trace.peekLast());

    if (trace.peekFirst().equals(trace.peekLast())) {
      return List.copyOf(trace);
    }

    Collection<ComponentTypeSymbol> instantiatedTypes = trace.peekFirst().getSubcomponents().stream()
      .filter(SubcomponentSymbol::isTypePresent)
      .map(SubcomponentSymbol::getType)
      .map(CompKindExpression::getTypeInfo)
      .distinct().toList();

    for (ComponentTypeSymbol type : instantiatedTypes) {
      trace.push(type);
      List<ComponentTypeSymbol> cycle = this.findRefCycleDepthFirst(trace);
      trace.pop();
      if (!cycle.isEmpty()) return cycle;
    }

    return Collections.emptyList();
  }

  /**
   * Formats a reference cycle for readable printing.
   *
   * @param cycle the detected cycle path from {@link #findRefCycleDepthFirst(Deque)}
   * @return formatted string like "Cycle detected: A → B → C → A"
   */
  protected static String printCycle(@NotNull List<ComponentTypeSymbol> cycle) {
    Preconditions.checkNotNull(cycle);
    Preconditions.checkArgument(!cycle.isEmpty());

    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < cycle.size(); i++) {
      sb.append(cycle.get(i).getName());
      if (i < cycle.size() - 1) {
        sb.append(" -> ");
      }
    }
    return sb.toString();
  }
}
