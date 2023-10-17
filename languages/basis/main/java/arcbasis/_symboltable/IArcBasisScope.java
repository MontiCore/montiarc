/* (c) https://github.com/MontiCore/monticore */
package arcbasis._symboltable;

import arcbasis.check.CompTypeExpression;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symboltable.modifiers.AccessModifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public interface IArcBasisScope extends IArcBasisScopeTOP {

  @Override
  default List<VariableSymbol> resolveAdaptedVariableLocallyMany(boolean foundSymbols,
                                                                 String name,
                                                                 AccessModifier modifier,
                                                                 Predicate<VariableSymbol> predicate) {

    List<PortSymbol> ports = resolvePortLocallyMany(foundSymbols, name, AccessModifier.ALL_INCLUSION, x -> true);
    List<ComponentInstanceSymbol> instances = resolveComponentInstanceLocallyMany(foundSymbols, name, AccessModifier.ALL_INCLUSION, x -> true);

    List<VariableSymbol> adapters = new ArrayList<>(ports.size() + instances.size());

    for (PortSymbol port : ports) {

      if (getLocalVariableSymbols().stream().filter(v -> v instanceof Port2VariableAdapter)
        .noneMatch(v -> ((Port2VariableAdapter) v).getAdaptee().equals(port))) {

        // instantiate the adapter
        VariableSymbol adapter = new Port2VariableAdapter(port);

        // filter by modifier and predicate
        if (modifier.includes(adapter.getAccessModifier()) && predicate.test(adapter)) {

          // add the adapter to the result
          adapters.add(adapter);

          // add the adapter to the scope
          this.add(adapter);
        }
      }
    }
    for (ComponentInstanceSymbol instance : instances) {

      if (getLocalVariableSymbols().stream().filter(v -> v instanceof ComponentInstance2VariableAdapter)
        .noneMatch(v -> ((ComponentInstance2VariableAdapter) v).getAdaptee().equals(instance))) {

        // instantiate the adapter
        VariableSymbol adapter = new ComponentInstance2VariableAdapter(instance);

        // filter by modifier and predicate
        if (modifier.includes(adapter.getAccessModifier()) && predicate.test(adapter)) {

          // add the adapter to the result
          adapters.add(adapter);

          // add the adapter to the scope
          this.add(adapter);
        }
      }
    }
    return adapters;
  }

  /**
   * Overrides the default behavior of resolving in enclosing scopes for component
   * type symbol scopes. Component type symbols only resolve in the enclosing scope if
   * it's an artifact scope.
   *
   * @return the resolved port symbols in the enclosing scope
   */
  @Override
  default List<PortSymbol> resolvePortMany(boolean foundSymbols,
                                           String name,
                                           AccessModifier modifier,
                                           Predicate<PortSymbol> predicate) {

    List<PortSymbol> symbols = IArcBasisScopeTOP.super.resolvePortMany(foundSymbols, name, modifier, predicate);
    symbols.addAll(resolvePortOfParentMany(foundSymbols || symbols.size() > 0, name, modifier, predicate));
    return symbols;
  }

  default List<PortSymbol> resolvePortOfParentMany(boolean foundSymbols, String name,
                                                   AccessModifier modifier,
                                                   Predicate<PortSymbol> predicate) {
    if (!foundSymbols && this.isPresentSpanningSymbol()) {
      Optional<ComponentTypeSymbol> component = new InstanceVisitor().asComponent(this.getSpanningSymbol());
      if (component.isPresent() && !component.get().isEmptyParents()) {
        ArrayList<PortSymbol> symbols = new ArrayList<>();
        for (CompTypeExpression parent : component.get().getParentsList()) {
          symbols.addAll(parent.getTypeInfo().getSpannedScope().resolvePortMany(false, name, modifier, predicate));
        }
        return symbols;
      }
    }
    return new ArrayList<>();
  }

  @Override
  default List<VariableSymbol> resolveVariableMany(boolean foundSymbols, String name,
                                                   AccessModifier modifier,
                                                   Predicate<VariableSymbol> predicate) {
    List<VariableSymbol> symbols = IArcBasisScopeTOP.super.resolveVariableMany(foundSymbols, name, modifier, predicate);
    symbols.addAll(resolveVariableOfParentMany(foundSymbols || symbols.size() > 0, name, modifier, predicate));
    return symbols;
  }

  default List<VariableSymbol> resolveVariableOfParentMany(boolean foundSymbols, String name,
                                                           AccessModifier modifier,
                                                           Predicate<VariableSymbol> predicate) {
    if (!foundSymbols && isPresentSpanningSymbol()) {
      Optional<ComponentTypeSymbol> component = new InstanceVisitor().asComponent(this.getSpanningSymbol());
      if (component.isPresent() && !component.get().isEmptyParents()) {
        ArrayList<VariableSymbol> symbols = new ArrayList<>();
        for (CompTypeExpression parent : component.get().getParentsList()) {
         symbols.addAll(parent.getTypeInfo().getSpannedScope()
            .resolveVariableMany(false, name, modifier, predicate));
        }
        return symbols;
      }
    }
    return new ArrayList<>();
  }

  @Override
  default List<PortSymbol> continuePortWithEnclosingScope(boolean foundSymbols, String name, AccessModifier modifier,
                                                          Predicate<PortSymbol> predicate) {
    if (checkIfContinueWithEnclosingScope(foundSymbols) && (getEnclosingScope() != null)) {
      if (isPresentSpanningSymbol() && new InstanceVisitor().asComponent(this.getSpanningSymbol()).isPresent()) {
        return getEnclosingScope().resolvePortManyEnclosing(foundSymbols, name, modifier, predicate);
      } else {
        return getEnclosingScope().resolvePortMany(foundSymbols, name, modifier, predicate);
      }
    }
    return new ArrayList<>();
  }

  default List<PortSymbol> resolvePortManyEnclosing(boolean foundSymbols, String name, AccessModifier modifier,
                                                    Predicate<PortSymbol> predicate) {
    return continuePortWithEnclosingScope(foundSymbols, name, modifier, predicate);
  }

  @Override
  default List<VariableSymbol> continueVariableWithEnclosingScope(boolean foundSymbols, String name,
                                                                  AccessModifier modifier,
                                                                  Predicate<VariableSymbol> predicate) {
    if (checkIfContinueWithEnclosingScope(foundSymbols) && (getEnclosingScope() != null)) {
      if (isPresentSpanningSymbol() && new InstanceVisitor().asComponent(this.getSpanningSymbol()).isPresent()) {
        return getEnclosingScope().resolveVariableManyEnclosing(foundSymbols, name, modifier, predicate);
      } else {
        return getEnclosingScope().resolveVariableMany(foundSymbols, name, modifier, predicate);
      }
    }
    return new ArrayList<>();
  }

  default List<VariableSymbol> resolveVariableManyEnclosing(boolean foundSymbols, String name, AccessModifier modifier,
                                                            Predicate<VariableSymbol> predicate) {
    return continueVariableWithEnclosingScope(foundSymbols, name, modifier, predicate);
  }

  @Override
  default List<ComponentInstanceSymbol> continueComponentInstanceWithEnclosingScope(boolean foundSymbols, String name,
                                                                                    AccessModifier modifier,
                                                                                    Predicate<ComponentInstanceSymbol> predicate) {
    if (checkIfContinueWithEnclosingScope(foundSymbols) && (getEnclosingScope() != null)) {
      if (isPresentSpanningSymbol() && new InstanceVisitor().asComponent(this.getSpanningSymbol()).isPresent()) {
        return getEnclosingScope().resolveComponentInstanceManyEnclosing(foundSymbols, name, modifier, predicate);
      } else {
        return getEnclosingScope().resolveComponentInstanceMany(foundSymbols, name, modifier, predicate);
      }
    }
    return new ArrayList<>();
  }

  default List<ComponentInstanceSymbol> resolveComponentInstanceManyEnclosing(boolean foundSymbols, String name,
                                                                              AccessModifier modifier,
                                                                              Predicate<ComponentInstanceSymbol> predicate) {
    return continueComponentInstanceWithEnclosingScope(foundSymbols, name, modifier, predicate);
  }
}
