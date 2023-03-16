/* (c) https://github.com/MontiCore/monticore */
package variablearc._symboltable;

import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis._symboltable.InstanceVisitor;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symboltable.modifiers.AccessModifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public interface IVariableArcScope extends IVariableArcScopeTOP {

  @Override
  default List<ArcFeatureSymbol> resolveArcFeatureMany(boolean foundSymbols, String name,
                                                       AccessModifier modifier,
                                                       Predicate<ArcFeatureSymbol> predicate) {
    List<ArcFeatureSymbol> symbols =
      IVariableArcScopeTOP.super.resolveArcFeatureMany(foundSymbols, name, modifier, predicate);
    symbols.addAll(resolveArcFeatureOfParentMany(foundSymbols || symbols.size() > 0, name, modifier, predicate));
    return symbols;
  }

  default List<ArcFeatureSymbol> resolveArcFeatureOfParentMany(boolean foundSymbols, String name,
                                                               AccessModifier modifier,
                                                               Predicate<ArcFeatureSymbol> predicate) {
    if (!foundSymbols && this.isPresentSpanningSymbol()) {
      Optional<ComponentTypeSymbol> component = new InstanceVisitor().asComponent(this.getSpanningSymbol());
      if (component.isPresent() && component.get().isPresentParent()) {
        return ((IVariableArcScope) component.get().getParent().getTypeInfo().getSpannedScope())
          .resolveArcFeatureMany(false, name, modifier, predicate);
      }
    }
    return new ArrayList<>();
  }

  @Override
  default List<ArcFeatureSymbol> continueArcFeatureWithEnclosingScope(boolean foundSymbols, String name,
                                                                      AccessModifier modifier,
                                                                      Predicate<ArcFeatureSymbol> predicate) {
    if (checkIfContinueWithEnclosingScope(foundSymbols) && (getEnclosingScope() != null)) {
      if (isPresentSpanningSymbol() && new InstanceVisitor().asComponent(this.getSpanningSymbol()).isPresent()) {
        return getEnclosingScope().resolveArcFeatureManyEnclosing(foundSymbols, name, modifier, predicate);
      } else {
        return getEnclosingScope().resolveArcFeatureMany(foundSymbols, name, modifier, predicate);
      }
    }
    return new ArrayList<>();
  }

  default List<ArcFeatureSymbol> resolveArcFeatureManyEnclosing(boolean foundSymbols, String name,
                                                                AccessModifier modifier,
                                                                Predicate<ArcFeatureSymbol> predicate) {
    return continueArcFeatureWithEnclosingScope(foundSymbols, name, modifier, predicate);
  }

  @Override
  default List<VariableSymbol> resolveAdaptedVariableLocallyMany(boolean foundSymbols,
                                                                 String name,
                                                                 AccessModifier modifier,
                                                                 Predicate<VariableSymbol> predicate) {

    List<VariableSymbol> adapters =
      IVariableArcScopeTOP.super.resolveAdaptedVariableLocallyMany(foundSymbols, name, modifier, predicate);

    List<ArcFeatureSymbol> arcFeatures =
      resolveArcFeatureLocallyMany(foundSymbols, name, AccessModifier.ALL_INCLUSION, x -> true);

    for (ArcFeatureSymbol feature : arcFeatures) {

      if (getLocalVariableSymbols().stream().filter(v -> v instanceof ArcFeature2VariableAdapter)
        .noneMatch(v -> ((ArcFeature2VariableAdapter) v).getAdaptee().equals(feature))) {
        // instantiate the adapter
        VariableSymbol adapter = new ArcFeature2VariableAdapter(feature);

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
}
