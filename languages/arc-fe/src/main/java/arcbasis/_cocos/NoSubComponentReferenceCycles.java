/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis._ast.ASTComponentType;
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
 * instantiation of such a system will result in an endless instantiation process, these cycles are forbidden."
 * This also transfers to deeper nested subcomponents. Example from Hab16:
 * component A { B myB; }
 * component B { A myA; }
 */
public class NoSubComponentReferenceCycles implements ArcBasisASTComponentTypeCoCo {

  @Override
  public void check(ASTComponentType astComp) {
    Preconditions.checkArgument(astComp != null);
    Preconditions.checkArgument(astComp.isPresentSymbol(), "ASTComponent '%s' has no symbol. "
      + "Thus can not check CoCo " + this.getClass().getName() + ". "
      + "Did you forget to run the SymbolTableCreator before checking cocos?", astComp.getName());

    ComponentTypeSymbol comp = astComp.getSymbol();

    try {
      Optional<List<ComponentTypeSymbol>> referenceCycle = findRefCycle(comp);
      if(referenceCycle.isPresent()) {
        Log.error(ArcError.NO_SUBCOMPONENT_CYCLE.format(comp.getFullName() + " or one of it's subcomponents",
          printCycle(referenceCycle.get())
        ));
      }
    } catch(SurrogateNotResolvableException e) {
      Log.error(String.format("Can not check coco '%s' on component type '%s' because component type surrogate '%s', " +
        "which is contained in the the component types' transitive subcomponent instantiations, does not lead " +
        "anywhere.", this.getClass().getSimpleName(), comp.getFullName(), e.getSurrogate().getFullName()
      ));
    }
  }

  protected Optional<List<ComponentTypeSymbol>> findRefCycle(@NotNull ComponentTypeSymbol compArg) {
    Preconditions.checkArgument(compArg != null);

    ComponentTypeSymbol comp = skipSurrogate(compArg);
    Deque<ComponentTypeSymbol> stack = new LinkedList<>();
    stack.push(comp);

    return findRefCycleDepthFirst(comp, stack);
  }

  protected Optional<List<ComponentTypeSymbol>> findRefCycleDepthFirst(
    @NotNull ComponentTypeSymbol curComp, @NotNull Deque<ComponentTypeSymbol> trace) {

    Preconditions.checkArgument(trace != null);
    Preconditions.checkArgument(curComp != null);
    Preconditions.checkArgument(!(curComp instanceof ComponentTypeSymbolSurrogate));
    Preconditions.checkArgument(trace.peek().equals(curComp));

    Collection<ComponentTypeSymbol> instantiatedTypes = curComp.getSubComponents().stream()
      .map(inst -> inst.getType())
      .map(compT -> skipSurrogate(compT))
      .distinct()
      .collect(Collectors.toList());

    for(ComponentTypeSymbol type : instantiatedTypes) {
      if (trace.contains(type)) { // cycle!
        trace.push(type);
        List<ComponentTypeSymbol> cycle = new ArrayList<>(trace);
        // Deque always appends at the start of its internal list.
        // Thus we reverse the stack to get the cycle in the correct direction.
        Collections.reverse(cycle);
        return Optional.of(cycle);
      } else {
        trace.push(type);
        Optional<List<ComponentTypeSymbol>> optCycle = findRefCycleDepthFirst(type, trace);
        if(optCycle.isPresent()) {
          return optCycle;
        }
        trace.remove(type);
      }
    }

    // Code reaches this location if
    // * instantiatedTypes is empty or
    // * no instantiatedType is in a cycle
    return Optional.empty();
  }

  protected static ComponentTypeSymbol skipSurrogate(@NotNull ComponentTypeSymbol sym) {
    Preconditions.checkArgument(sym != null);

    ComponentTypeSymbol curSym = sym;
    while(curSym instanceof ComponentTypeSymbolSurrogate) {
      ComponentTypeSymbolSurrogate surrogate = (ComponentTypeSymbolSurrogate) curSym;
      ComponentTypeSymbol updatedSym = surrogate.lazyLoadDelegate();
      if(updatedSym == surrogate) {
        // Error logging might be not in fail fast mode, so we break this otherwise endlessly repeating cycle via
        // exception.
        throw new SurrogateNotResolvableException(surrogate);
      }
      curSym = updatedSym;
    }
    return curSym;
  }

  protected static String printCycle(@NotNull List<ComponentTypeSymbol> cycle) {
    Preconditions.checkArgument(cycle != null);
    Preconditions.checkArgument(cycle.size() >= 2);

    StringBuffer printer = new StringBuffer();
    for(int i = 0; i < cycle.size() - 1; i++) {
      ComponentTypeSymbol enclCompType = cycle.get(i);
      ComponentTypeSymbol subCompType = cycle.get(i + 1);

      printer.append(String.format("\n'%s' instantiates '%s'", enclCompType.getFullName(), subCompType.getFullName()));
    }

    return printer.toString();
  }

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
