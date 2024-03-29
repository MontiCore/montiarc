/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis._ast.ASTComponentType;
import arcbasis._symboltable.ComponentTypeSymbol;
import com.google.common.base.Preconditions;
import de.monticore.symbols.compsymbols._symboltable.ComponentSymbol;
import de.monticore.symbols.compsymbols._symboltable.SubcomponentSymbol;
import de.monticore.types.check.CompKindExpression;
import de.se_rwth.commons.logging.Log;
import montiarc.util.ArcError;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * [Hab16] R13: "A reference cycle is given if two component types declare each other as subcomponents. Since
 * instantiation of such a system will result in an endless instantiation process, these cycles are forbidden." This
 * also transfers to deeper nested subcomponents. Example from Hab16: component A { B myB; } component B { A myA; }
 */
public class NoSubcomponentReferenceCycle implements ArcBasisASTComponentTypeCoCo {

  @Override
  public void check(@NotNull ASTComponentType astComp) {
    Preconditions.checkNotNull(astComp);
    Preconditions.checkArgument(astComp.isPresentSymbol());

    ComponentTypeSymbol comp = astComp.getSymbol();

    Optional<List<ComponentSymbol>> referenceCycle = findRefCycle(comp);
    if (referenceCycle.isPresent()) {
      Log.error(ArcError.SUBCOMPONENT_REFERENCE_CYCLE.format(printCycle(referenceCycle.get())),
        astComp.get_SourcePositionStart(), astComp.get_SourcePositionEnd()
      );
    }
  }

  /**
   * Checks whether the given component is part of an instantiation reference cycle
   *
   * @param compArg root element of the recursion
   * @return any cycles where the root-element directly or indirectly instantiates itself
   */
  protected Optional<List<ComponentSymbol>> findRefCycle(@NotNull ComponentTypeSymbol compArg) {
    Preconditions.checkNotNull(compArg);

    Deque<ComponentSymbol> stack = new LinkedList<>();
    stack.push(compArg);

    return findRefCycleDepthFirst(stack);
  }

  /**
   * Recursively iterates this component and its subcomponents to find duplicate types in the subcomponents and the
   * given stack
   *
   * @param trace recursion branch. The first entry is the root (the first parameter o {@link
   *              #check(ASTComponentType)}), then it contains a trace of instantiated sub elements, the last element is
   *              the current end of recursion
   * @return a cyclic instantiation reference, if it is contained in the model
   */
  protected Optional<List<ComponentSymbol>> findRefCycleDepthFirst(@NotNull Deque<ComponentSymbol> trace) {
    Preconditions.checkNotNull(trace);
    Preconditions.checkNotNull(trace.peekLast());

    Collection<ComponentSymbol> instantiatedTypes = trace.peekLast().getSubcomponents().stream()
      .filter(SubcomponentSymbol::isTypePresent)
      .map(SubcomponentSymbol::getType)
      .map(CompKindExpression::getTypeInfo)
      .distinct()
      .collect(Collectors.toList());

    for (ComponentSymbol type : instantiatedTypes) {
      if (type.equals(trace.peekFirst())) { // check for cycle
        List<ComponentSymbol> cycle = new ArrayList<>(trace);
        return Optional.of(cycle);
      } else if (!trace.contains(type)) { // else it is also a cycle, but it is dealt with by an other run of this coco
        trace.addLast(type);
        Optional<List<ComponentSymbol>> cycle = findRefCycleDepthFirst(trace);
        if (cycle.isPresent()) {
          return cycle;
        }
        trace.removeLast();
      }
    }

    // Code reaches this location if
    // * instantiatedTypes is empty or
    // * no instantiatedType is in a cycle or
    // * no instantiatedType is in a cycle that contains the root component of this run
    return Optional.empty();
  }

  /**
   * Represents a given list as a string
   *
   * @param cycle a list (preferably random access) of component types that instantiate each other (each component only
   *              has to be listed once)
   * @return a string where every list entry is named at least twice
   */
  protected static String printCycle(@NotNull List<ComponentSymbol> cycle) {
    Preconditions.checkNotNull(cycle);
    Preconditions.checkArgument(!cycle.isEmpty());

    StringBuilder printer = new StringBuilder();
    for (int i = 0; i < cycle.size(); i++) {
      ComponentSymbol enclCompType = cycle.get(i);
      ComponentSymbol subCompType = cycle.get((i + 1) % cycle.size());

      printer.append("\n'").append(enclCompType.getFullName()).append("' instantiates '").append(subCompType.getFullName()).append("'");
    }

    return printer.toString();
  }
}
