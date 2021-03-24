/* (c) https://github.com/MontiCore/monticore */
package arccore._symboltable;

import arcbasis._symboltable.ComponentTypeSymbol;
import com.google.common.base.Preconditions;
import com.google.common.collect.LinkedListMultimap;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.monticore.symboltable.ISymbol;
import de.monticore.symboltable.resolving.ResolvedSeveralEntriesForSymbolException;
import de.monticore.utils.Names;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ArcCoreScope extends ArcCoreScopeTOP {

  public ArcCoreScope() {
    super();
  }

  public ArcCoreScope(boolean shadowing) {
    super(shadowing);
  }

  public ArcCoreScope(@NotNull IArcCoreScope enclosingScope) {
    super(Preconditions.checkNotNull(enclosingScope));
  }

  public ArcCoreScope(@NotNull IArcCoreScope enclosingScope, boolean shadowing) {
    super(Preconditions.checkNotNull(enclosingScope), shadowing);
  }

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
  public Optional<TypeSymbol> filterType(@NotNull String name,
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
  public Optional<OOTypeSymbol> filterOOType(@NotNull String name,
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
  public Optional<ComponentTypeSymbol> filterComponentType(@NotNull String name,
                                                           @NotNull LinkedListMultimap<String, ComponentTypeSymbol> symbols) {
    Preconditions.checkNotNull(name);
    Preconditions.checkNotNull(symbols);

    return getResolvedOrThrowException(Stream.concat(Stream.concat(this.filterSimpleName(name, symbols),
      this.filterName(name, symbols)), this.filterQualifiedName(name, symbols)).collect(Collectors.toSet()));
  }

  protected <T extends ISymbol> Stream<T> filterSimpleName(@NotNull String name,
                                                           @NotNull LinkedListMultimap<String, T> symbols) {
    Preconditions.checkNotNull(name);
    Preconditions.checkNotNull(symbols);
    return symbols.containsKey(Names.getSimpleName(name)) ? symbols.get(Names.getSimpleName(name)).stream()
      .filter(s -> s.getName().equals(name)) : Stream.empty();
  }

  protected <T extends ISymbol> Stream<T> filterName(@NotNull String name,
                                                     @NotNull LinkedListMultimap<String, T> symbols) {
    Preconditions.checkNotNull(name);
    Preconditions.checkNotNull(symbols);
    return symbols.values().stream()
      .filter(s -> s.getName().equals(name));
  }

  protected <T extends ISymbol> Stream<T> filterQualifiedName(@NotNull String name,
                                                              @NotNull LinkedListMultimap<String, T> symbols) {
    Preconditions.checkNotNull(name);
    Preconditions.checkNotNull(symbols);
    return symbols.containsKey(name) ? symbols.get(name).stream()
      .filter(s -> s.getName().equals(name)) : Stream.empty();
  }
}