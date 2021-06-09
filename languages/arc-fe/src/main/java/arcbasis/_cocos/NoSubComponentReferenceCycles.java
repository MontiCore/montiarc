/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis._ast.ASTComponentType;
import arcbasis._symboltable.ComponentInstanceSymbol;
import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis._symboltable.ComponentTypeSymbolSurrogate;
import arcbasis.util.ArcError;
import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.*;
import java.util.stream.Collectors;

/**
 * [Hab16] R13: "A reference cycle is given if two component types declare each other as subcomponents. Since
 * instantiation of such a system will result in an endless instantiation process, these cycles are forbidden." This
 * also transfers to deeper nested subcomponents. Example from Hab16: component A { B myB; } component B { A myA; }
 */
public class NoSubComponentReferenceCycles implements ArcBasisASTComponentTypeCoCo {

  @Override
  public void check(ASTComponentType astComp) {
    Preconditions.checkArgument(astComp != null);
    Preconditions.checkArgument(astComp.isPresentSymbol(), "ASTComponent '%s' has no symbol. "
      + "Did you forget to run the SymbolTableCreator before checking cocos?", astComp.getName());

    ComponentTypeSymbol comp = astComp.getSymbol();

    try {
      Optional<List<ComponentTypeSymbol>> referenceCycle = findRefCycle(comp);
      if (referenceCycle.isPresent()) {
        Log.error(ArcError.NO_SUBCOMPONENT_CYCLE.format(printCycle(referenceCycle.get())),
          astComp.get_SourcePositionStart()
        );
      }
    } catch (SurrogateNotResolvableException e) {
      Log.error(String.format("Can not check coco '%s' on component type '%s' because component type surrogate '%s', " +
        "which is contained in the the component types' transitive subcomponent instantiations, does not lead " +
        "anywhere.", this.getClass().getSimpleName(), comp.getFullName(), e.getSurrogate().getFullName()
      ));
    }
  }

  /**
   * Checks whether the given component is part of a instantiation reference cycle
   *
   * @param compArg root element of the recursion
   * @return any cycles where the root-element directly or indirectly instantiates itself
   */
  protected Optional<List<ComponentTypeSymbol>> findRefCycle(@NotNull ComponentTypeSymbol compArg) {
    Preconditions.checkNotNull(compArg);

    Deque<ComponentTypeSymbol> stack = new LinkedList<>();
    stack.push(skipSurrogate(compArg));

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
  protected Optional<List<ComponentTypeSymbol>> findRefCycleDepthFirst(@NotNull Deque<ComponentTypeSymbol> trace) {
    Preconditions.checkNotNull(trace);
    Preconditions.checkNotNull(trace.peekLast());

    Collection<ComponentTypeSymbol> instantiatedTypes = trace.peekLast().getSubComponents().stream()
      .map(ComponentInstanceSymbol::getType)
      .map(NoSubComponentReferenceCycles::skipSurrogate)
      .distinct()
      .collect(Collectors.toList());

    for (ComponentTypeSymbol type : instantiatedTypes) {
      if (type.equals(trace.peekFirst())) { // check for cycle
        List<ComponentTypeSymbol> cycle = new ArrayList<>(trace);
        return Optional.of(cycle);
      } else if (!trace.contains(type)) { // else it is also a cycle, but it is dealt with by an other run of this coco
        trace.addLast(type);
        Optional<List<ComponentTypeSymbol>> cycle = findRefCycleDepthFirst(trace);
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
   * Resolves a component symbol
   *
   * @param sym component symbol or a surrogate for that
   * @return a loaded component symbol
   */
  protected static ComponentTypeSymbol skipSurrogate(@NotNull ComponentTypeSymbol sym) {
    Preconditions.checkArgument(sym != null);

    ComponentTypeSymbol curSym = sym;
    while (curSym instanceof ComponentTypeSymbolSurrogate) {
      ComponentTypeSymbolSurrogate surrogate = (ComponentTypeSymbolSurrogate) curSym;
      ComponentTypeSymbol updatedSym = surrogate.lazyLoadDelegate();
      if (updatedSym == surrogate) {
        throw new SurrogateNotResolvableException(surrogate);
      }
      curSym = updatedSym;
    }
    return curSym;
  }

  /**
   * Represents a given list as a string
   *
   * @param cycle a list (preferably random access) of component types that instantiate each other (each component only
   *              has to be listed once)
   * @return a string where every list entry is named at least twice
   */
  protected static String printCycle(@NotNull List<ComponentTypeSymbol> cycle) {
    Preconditions.checkArgument(cycle != null);
    Preconditions.checkArgument(cycle.size() != 0);

    StringBuilder printer = new StringBuilder();
    for (int i = 0; i < cycle.size(); i++) {
      ComponentTypeSymbol enclCompType = cycle.get(i);
      ComponentTypeSymbol subCompType = cycle.get((i + 1) % cycle.size());

      printer.append("\n'").append(enclCompType.getFullName()).append("' instantiates '").append(subCompType.getFullName()).append("'");
    }

    return printer.toString();
  }

  /**
   * Custom exception thrown and caught only by {@link NoSubComponentReferenceCycles this class}
   */
  protected static class SurrogateNotResolvableException extends RuntimeException {
    protected final ComponentTypeSymbolSurrogate surrogate;

    public SurrogateNotResolvableException(@NotNull ComponentTypeSymbolSurrogate surr) {
      this.surrogate = Preconditions.checkNotNull(surr);
    }

    public ComponentTypeSymbolSurrogate getSurrogate() {
      return surrogate;
    }
  }
}
