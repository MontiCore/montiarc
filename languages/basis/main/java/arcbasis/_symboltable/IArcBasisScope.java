/* (c) https://github.com/MontiCore/monticore */
package arcbasis._symboltable;

import com.google.common.base.Preconditions;
import com.google.common.collect.LinkedListMultimap;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.monticore.symboltable.ISymbol;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.monticore.symboltable.resolving.ResolvedSeveralEntriesForSymbolException;
import de.se_rwth.commons.Names;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface IArcBasisScope extends IArcBasisScopeTOP {

  /**
   * Filters the provided type symbols for those which match the provided name string. Here, a name match is defined
   * that either the name attribute of the symbol or its fully qualified name matches the provided name string. As such,
   * overrides the default filter method provided by monticore, as this requires that names are stored in the provided
   * multimap as simple names.
   *
   * @param name    the named of the type symbol to resolve
   * @param symbols the symbols under consideration
   * @return the filtered type symbol, or an empty optional
   * @throws ResolvedSeveralEntriesForSymbolException if multiple matching symbols are found
   */
  @Override
  default Optional<TypeSymbol> filterType(@NotNull String name,
                                         @NotNull LinkedListMultimap<String, TypeSymbol> symbols) {
    Preconditions.checkNotNull(name);
    Preconditions.checkNotNull(symbols);

    return getResolvedOrThrowException(Stream.concat(Stream.concat(this.filterSimpleName(name, symbols),
      this.filterName(name, symbols)), this.filterQualifiedName(name, symbols)).collect(Collectors.toSet()));
  }

  /**
   * Filters the provided oo-type symbols for those which match the provided name string. Here, a name match is defined
   * that either the name attribute of the symbol or its fully qualified name matches the provided name string. As such,
   * overrides the default filter method provided by monticore, as this requires that names are stored in the provided
   * multimap as simple names.
   *
   * @param name    the named of the oo-type symbol to resolve
   * @param symbols the symbols under consideration
   * @return the filtered oo-type symbol, or an empty optional
   * @throws ResolvedSeveralEntriesForSymbolException if multiple matching symbols are found
   */
  @Override
  default Optional<OOTypeSymbol> filterOOType(@NotNull String name,
                                             @NotNull LinkedListMultimap<String, OOTypeSymbol> symbols) {
    Preconditions.checkNotNull(name);
    Preconditions.checkNotNull(symbols);

    return getResolvedOrThrowException(Stream.concat(Stream.concat(this.filterSimpleName(name, symbols),
      this.filterName(name, symbols)), this.filterQualifiedName(name, symbols)).collect(Collectors.toSet()));
  }

  /**
   * Filters the provided component type symbols for those which match the provided name string. Here, a name match is
   * defined that either the name attribute of the symbol or its fully qualified name matches the provided name string.
   * As such, overrides the default filter method provided by monticore, as this requires that names are stored in the
   * provided multimap as simple names.
   *
   * @param name    the named of the component ype symbol to resolve
   * @param symbols the symbols under consideration
   * @return the filtered component type symbol, or an empty optional
   * @throws ResolvedSeveralEntriesForSymbolException if multiple matching symbols are found
   */
  @Override
  default Optional<ComponentTypeSymbol> filterComponentType(@NotNull String name,
                                             @NotNull LinkedListMultimap<String, ComponentTypeSymbol> symbols) {
    Preconditions.checkNotNull(name);
    Preconditions.checkNotNull(symbols);

    return getResolvedOrThrowException(Stream.concat(Stream.concat(this.filterSimpleName(name, symbols),
      this.filterName(name, symbols)), this.filterQualifiedName(name, symbols)).collect(Collectors.toSet()));
  }

  default <T extends ISymbol> Stream<T> filterSimpleName(@NotNull String name,
                                                         @NotNull LinkedListMultimap<String, T> symbols) {
    Preconditions.checkNotNull(name);
    Preconditions.checkNotNull(symbols);
    return symbols.containsKey(Names.getSimpleName(name)) ? symbols.get(Names.getSimpleName(name)).stream()
      .filter(s -> s.getName().equals(name)) : Stream.empty();
  }

  default <T extends ISymbol> Stream<T> filterName(@NotNull String name,
                                                   @NotNull LinkedListMultimap<String, T> symbols) {
    Preconditions.checkNotNull(name);
    Preconditions.checkNotNull(symbols);
    return symbols.values().stream()
      .filter(s -> s.getName().equals(name));
  }

  default <T extends ISymbol> Stream<T> filterQualifiedName(@NotNull String name,
                                                            @NotNull LinkedListMultimap<String, T> symbols) {
    Preconditions.checkNotNull(name);
    Preconditions.checkNotNull(symbols);
    return symbols.containsKey(name) ? symbols.get(name).stream()
      .filter(s -> s.getName().equals(name)) : Stream.empty();
  }

  @Override
  default List<VariableSymbol> resolveAdaptedVariableLocallyMany(boolean foundSymbols,
                                                                 String name,
                                                                 AccessModifier modifier,
                                                                 Predicate<VariableSymbol> predicate) {

    List<PortSymbol> ports = resolvePortLocallyMany(foundSymbols, name, AccessModifier.ALL_INCLUSION, x -> true);

    List<VariableSymbol> adapters = new ArrayList<>(ports.size());

    for (PortSymbol port : ports) {
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
    return adapters;
  }

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
      if (component.isPresent() && component.get().isPresentParentComponent()) {
        return component.get().getParent().getTypeInfo().getSpannedScope()
          .resolvePortMany(false, name, modifier, predicate);
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
      if (component.isPresent() && component.get().isPresentParentComponent()) {
        return component.get().getParent().getTypeInfo().getSpannedScope()
          .resolveVariableMany(false, name, modifier, predicate);
      }
    }
    return new ArrayList<>();
  }
}
