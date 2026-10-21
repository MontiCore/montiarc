/* (c) https://github.com/MontiCore/monticore */
package arcbasis.context;

import arcbasis.ArcBasisMill;
import arcbasis._ast.ASTPortAccess;
import arcbasis._symboltable.IArcBasisScope;
import arcbasis._visitor.ArcBasisTraverser;
import com.google.common.base.Preconditions;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.compsymbols._symboltable.ComponentType2TypeSymbolAdapter;
import de.monticore.symbols.compsymbols._symboltable.ComponentTypeSymbol;
import de.monticore.symbols.compsymbols._symboltable.Port2VariableAdapter;
import de.monticore.symbols.compsymbols._symboltable.PortSymbol;
import de.monticore.symbols.compsymbols._symboltable.Subcomponent2VariableAdapter;
import de.monticore.symbols.compsymbols._symboltable.SubcomponentSymbol;
import de.monticore.symboltable.IArtifactScope;
import de.monticore.symboltable.IGlobalScope;
import de.monticore.symboltable.IScope;
import de.monticore.symboltable.ISymbol;
import de.monticore.types.check.CompKindExpression;
import de.monticore.types.check.CompKindOfComponentType;
import de.monticore.types.check.CompKindOfGenericComponentType;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.SymTypeVariable;
import de.monticore.types3.generics.TypeParameterRelations;
import de.monticore.visitor.IVisitor;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Resolves the members of component types, including inherited ones, and
 * their types as seen in a component type expression: the component type
 * expression ({@code owner}) binds the type parameters of the component
 * type, e.g., {@code K<int>} binds the type of a port {@code T i} to
 * {@code int}.
 */
public class WithinComponentResolver {

  /**
   * @return the component type expression of the component type itself,
   * i.e., with its type parameters as free type variables
   */
  public CompKindExpression kindOf(@NotNull ComponentTypeSymbol type) {
    Preconditions.checkNotNull(type);
    if (type.getTypeParameters().isEmpty()) {
      return new CompKindOfComponentType(type);
    }
    return new CompKindOfGenericComponentType(type, type.getTypeParameters().stream()
      .map(SymTypeExpressionFactory::createTypeVariable)
      .map(SymTypeExpression.class::cast)
      .toList());
  }

  /**
   * Resolves the members of the given kind and name of the component type,
   * including inherited ones.
   *
   * @return the members declared by the component type and the inherited
   * members that are not hidden by a declared member of the same name;
   * several if more than one member has the name
   */
  public <S extends ISymbol> List<S> resolveMany(@NotNull ComponentTypeSymbol type,
                                                 @NotNull String name,
                                                 @NotNull Class<S> kind) {
    Preconditions.checkNotNull(type);
    Preconditions.checkNotNull(name);
    Preconditions.checkNotNull(kind);
    return this.resolveMany(type, name, kind, new HashSet<>());
  }

  /**
   * Resolves the symbols of the given kind and name that are visible in the
   * scope. The scope is assumed to be the spanned scope of a component type
   * or a scope nested in it, e.g., of a mode or a block.
   *
   * @return the symbols declared in the innermost scope between the given
   * scope and the component type that declares any, otherwise the members of
   * the component type as resolved by
   * {@link #resolveMany(ComponentTypeSymbol, String, Class)}
   */
  public <S extends ISymbol> List<S> resolveMany(@NotNull IScope scope,
                                                 @NotNull String name,
                                                 @NotNull Class<S> kind) {
    Preconditions.checkNotNull(kind);
    return this.resolveMany(scope, name, kind::isInstance).stream().map(kind::cast).toList();
  }

  /**
   * Resolves the symbols of the given name that are visible in the scope and
   * match the predicate, e.g., all ports and variables. Only matching symbols
   * hide those of enclosing scopes and of the component type. The scope is
   * assumed to be the spanned scope of a component type or a scope nested in
   * it, e.g., of a mode or a block.
   *
   * @return the matching symbols declared in the innermost scope between the
   * given scope and the component type that declares any, otherwise the
   * matching members of the component type
   */
  public List<ISymbol> resolveMany(@NotNull IScope scope,
                                   @NotNull String name,
                                   @NotNull Predicate<ISymbol> matches) {
    Preconditions.checkNotNull(scope);
    Preconditions.checkNotNull(name);
    Preconditions.checkNotNull(matches);
    for (IScope current = scope; current != null && !this.isOutsideComponents(current); current = current.getEnclosingScope()) {
      if (current.isPresentSpanningSymbol() && current.getSpanningSymbol() instanceof ComponentTypeSymbol component) {
        return this.resolveMany(component, name, matches, new HashSet<>());
      }
      List<ISymbol> localSymbols = this.getLocalSymbols(current, ISymbol.class).stream()
        .filter(symbol -> symbol.getName().equals(name))
        .filter(matches)
        .toList();
      if (!localSymbols.isEmpty()) {
        return localSymbols;
      }
    }
    return List.of();
  }

  /**
   * Resolves the value symbols, i.e., ports and variables, of the given name
   * that are visible in the scope. The scope is assumed to be the spanned
   * scope of a component type or a scope nested in it, e.g., of a mode or a
   * block.
   *
   * @return the ports and variables declared in the innermost scope between
   * the given scope and the component type that declares any, otherwise the
   * ports and variables of the component type
   */
  public List<ISymbol> resolveValues(@NotNull IScope scope, @NotNull String name) {
    return this.resolveMany(scope, name, this::isValue);
  }

  /**
   * @return whether the symbol is a value symbol, i.e., a port or a variable
   */
  public boolean isValue(@NotNull ISymbol symbol) {
    return symbol instanceof VariableSymbol || symbol instanceof PortSymbol;
  }

  /**
   * Resolves the ports of the given name that are visible in the scope. The
   * scope is assumed to be the spanned scope of a component type or a scope
   * nested in it, e.g., of a mode or a block.
   *
   * @return the ports among the value symbols resolved by
   * {@link #resolveValues}, i.e., none if a variable of the name hides them
   */
  public List<PortSymbol> resolvePorts(@NotNull IScope scope, @NotNull String name) {
    return this.resolveValues(scope, name).stream()
      .filter(PortSymbol.class::isInstance)
      .map(PortSymbol.class::cast)
      .toList();
  }

  /**
   * Collects the members of the given kind of the component type, including
   * inherited ones.
   *
   * @return the members declared by the component type and the inherited
   * members that are not hidden by a declared member of the same name
   */
  public <S extends ISymbol> List<S> getAll(@NotNull ComponentTypeSymbol type, @NotNull Class<S> kind) {
    Preconditions.checkNotNull(type);
    Preconditions.checkNotNull(kind);
    return this.getAll(type, kind, new HashSet<>());
  }

  /**
   * Collects the symbols of the given kind that are visible in the scope.
   * The scope is assumed to be the spanned scope of a component type or a
   * scope nested in it, e.g., of a mode or a block.
   *
   * @return the symbols declared in the given scope and the scopes between it
   * and the component type, followed by the members of the component type as
   * collected by {@link #getAll(ComponentTypeSymbol, Class)}
   */
  public <S extends ISymbol> List<S> getAll(@NotNull IScope scope, @NotNull Class<S> kind) {
    Preconditions.checkNotNull(scope);
    Preconditions.checkNotNull(kind);
    List<S> visible = new ArrayList<>();
    for (IScope current = scope; current != null && !this.isOutsideComponents(current); current = current.getEnclosingScope()) {
      if (current.isPresentSpanningSymbol() && current.getSpanningSymbol() instanceof ComponentTypeSymbol component) {
        visible.addAll(this.getAll(component, kind));
        return visible;
      }
      visible.addAll(this.getLocalSymbols(current, kind));
    }
    return visible;
  }

  /**
   * @return the super components along which the member is inherited by the
   * component type, an empty list if the member is declared by the type
   * itself, or empty if it is no member of the type
   */
  public Optional<List<CompKindExpression>> superPathTo(@NotNull ComponentTypeSymbol type, @NotNull ISymbol member) {
    Preconditions.checkNotNull(type);
    Preconditions.checkNotNull(member);
    return this.superPathTo(type, member, new HashSet<>());
  }

  /**
   * @param owner  a component type expression, e.g., created by
   *               {@link #kindOf(ComponentTypeSymbol)}
   * @param member a port or variable of the owner's component type or of one
   *               of its super components
   * @return the type of the member with the type parameters bound by the
   * owner, if the member is typed
   */
  public Optional<SymTypeExpression> typeOf(@NotNull CompKindExpression owner, @NotNull ISymbol member) {
    Preconditions.checkNotNull(owner);
    Preconditions.checkNotNull(member);
    return this.declaringOwner(owner, member, new HashSet<>())
      .flatMap(declaring -> this.getRawType(member).map(raw -> this.bind(raw, declaring)));
  }

  /**
   * @param owner        a component type expression, e.g., created by
   *                     {@link #kindOf(ComponentTypeSymbol)}
   * @param subcomponent a subcomponent of the owner's component type or of
   *                     one of its super components
   * @return the component type expression of the subcomponent with the type
   * parameters bound by the owner, if the subcomponent is typed
   */
  public Optional<CompKindExpression> kindOf(@NotNull CompKindExpression owner, @NotNull SubcomponentSymbol subcomponent) {
    Preconditions.checkNotNull(owner);
    Preconditions.checkNotNull(subcomponent);
    if (!subcomponent.isTypePresent()) {
      return Optional.empty();
    }
    return this.declaringOwner(owner, subcomponent, new HashSet<>())
      .map(declaring -> this.bind(subcomponent.getType(), declaring));
  }

  /**
   * @return the super components of the owner's component type with the type
   * parameters bound by the owner
   */
  public List<CompKindExpression> getSuperComponents(@NotNull CompKindExpression owner) {
    Preconditions.checkNotNull(owner);
    return owner.getTypeInfo().getSuperComponentsList().stream()
      .map(superComponent -> this.bind(superComponent, owner))
      .toList();
  }

  /**
   * @param owner   a component type expression, e.g., created by
   *                {@link #kindOf(ComponentTypeSymbol)}
   * @param typeVar a type parameter of the owner's component type
   * @return the upper bounds of the type parameter with the type parameters
   * bound by the owner
   */
  public List<SymTypeExpression> boundsOf(@NotNull CompKindExpression owner, @NotNull TypeVarSymbol typeVar) {
    Preconditions.checkNotNull(owner);
    Preconditions.checkNotNull(typeVar);
    Preconditions.checkArgument(owner.getTypeInfo().getTypeParameters().contains(typeVar),
      "%s is no type parameter of %s", typeVar.getName(), owner.getTypeInfo().getFullName());
    return typeVar.getSuperTypesList().stream()
      .map(bound -> this.bind(bound, owner))
      .toList();
  }

  /**
   * @return all ports the port access may refer to, i.e., ports of the
   * component or, for qualified accesses, ports of the subcomponents of that
   * name, including inherited ones
   */
  public List<PortAccessCandidate> resolvePortAccess(@NotNull ComponentTypeSymbol component,
                                                     @NotNull ASTPortAccess access) {
    Preconditions.checkNotNull(component);
    Preconditions.checkNotNull(access);
    List<PortAccessCandidate> candidates = new ArrayList<>();
    if (!access.isPresentComponent()) {
      for (PortSymbol port : this.resolveMany(access.getEnclosingScope(), access.getPort(), PortSymbol.class)) {
        candidates.add(new PortAccessCandidate(Optional.empty(), port));
      }
      return candidates;
    }
    for (SubcomponentSymbol subcomponent : this.resolveMany(access.getEnclosingScope(), access.getComponent(), SubcomponentSymbol.class)) {
      if (!subcomponent.isTypePresent()) continue;
      for (PortSymbol port : this.resolveMany(subcomponent.getType().getTypeInfo(), access.getPort(), PortSymbol.class)) {
        candidates.add(new PortAccessCandidate(Optional.of(subcomponent), port));
      }
    }
    return candidates;
  }

  /**
   * @return the type of the candidate's port as seen in the component
   */
  public Optional<SymTypeExpression> typeOf(@NotNull ComponentTypeSymbol component,
                                            @NotNull PortAccessCandidate candidate) {
    Preconditions.checkNotNull(component);
    Preconditions.checkNotNull(candidate);
    CompKindExpression self = this.kindOf(component);
    Optional<CompKindExpression> owner = candidate.subcomponent().isPresent()
      ? this.kindOf(self, candidate.subcomponent().get())
      : Optional.of(self);
    return owner.flatMap(kind -> this.typeOf(kind, candidate.port()));
  }

  /**
   * @return the type of the accessed port as seen in the component type that
   * encloses the port access, i.e., with the type parameters bound by the
   * component type and, for qualified accesses, by the subcomponent's type;
   * the declared type of the port if no component type encloses the port
   * access; or empty if the port, the subcomponent, or their types cannot be
   * resolved
   */
  public Optional<SymTypeExpression> typeOf(@NotNull ASTPortAccess access) {
    Preconditions.checkNotNull(access);
    if (!access.isPresentPortSymbol()) {
      return Optional.empty();
    }
    Optional<ComponentTypeSymbol> component = this.enclosingComponentOf(access.getEnclosingScope());
    if (component.isEmpty()) {
      return this.getRawType(access.getPortSymbol());
    }
    CompKindExpression self = this.kindOf(component.get());
    Optional<CompKindExpression> owner = !access.isPresentComponent() ? Optional.of(self)
      : access.isPresentComponentSymbol() ? this.kindOf(self, access.getComponentSymbol())
      : Optional.empty();
    return owner.flatMap(kind -> this.typeOf(kind, access.getPortSymbol()));
  }

  /**
   * @return the innermost component type whose spanned scope is or encloses
   * the scope
   */
  public Optional<ComponentTypeSymbol> enclosingComponentOf(IScope scope) {
    for (IScope current = scope; current != null; current = current.getEnclosingScope()) {
      if (current.isPresentSpanningSymbol() && current.getSpanningSymbol() instanceof ComponentTypeSymbol component) {
        return Optional.of(component);
      }
    }
    return Optional.empty();
  }

  protected <S extends ISymbol> List<S> resolveMany(@NotNull ComponentTypeSymbol type,
                                                    @NotNull String name,
                                                    @NotNull Class<S> kind,
                                                    @NotNull Set<String> visited) {
    return this.resolveMany(type, name, kind::isInstance, visited).stream().map(kind::cast).toList();
  }

  protected List<ISymbol> resolveMany(@NotNull ComponentTypeSymbol type,
                                      @NotNull String name,
                                      @NotNull Predicate<ISymbol> matches,
                                      @NotNull Set<String> visited) {
    if (!visited.add(type.getFullName())) {
      return List.of();
    }
    List<ISymbol> resolved = new ArrayList<>(this.getLocalSymbols(type, ISymbol.class).stream()
      .filter(symbol -> symbol.getName().equals(name))
      .filter(matches)
      .toList());
    List<ISymbol> local = List.copyOf(resolved);
    for (CompKindExpression superComponent : type.getSuperComponentsList()) {
      for (ISymbol symbol : this.resolveMany(superComponent.getTypeInfo(), name, matches, visited)) {
        if (local.stream().noneMatch(declared -> this.hides(declared, symbol))) {
          resolved.add(symbol);
        }
      }
    }
    return resolved;
  }

  protected <S extends ISymbol> List<S> getAll(@NotNull ComponentTypeSymbol type,
                                               @NotNull Class<S> kind,
                                               @NotNull Set<String> visited) {
    if (!visited.add(type.getFullName())) {
      return List.of();
    }
    Map<String, List<S>> byName = new LinkedHashMap<>();
    for (S symbol : this.getLocalSymbols(type, kind)) {
      byName.computeIfAbsent(symbol.getName(), n -> new ArrayList<>()).add(symbol);
    }
    List<S> localSymbols = this.getLocalSymbols(type, kind);
    for (CompKindExpression superComponent : type.getSuperComponentsList()) {
      for (S symbol : this.getAll(superComponent.getTypeInfo(), kind, visited)) {
        if (localSymbols.stream().noneMatch(declared -> this.hides(declared, symbol))) {
          byName.computeIfAbsent(symbol.getName(), n -> new ArrayList<>()).add(symbol);
        }
      }
    }
    return byName.values().stream().flatMap(List::stream).toList();
  }

  protected Optional<List<CompKindExpression>> superPathTo(@NotNull ComponentTypeSymbol type,
                                                           @NotNull ISymbol member,
                                                           @NotNull Set<String> visited) {
    if (!visited.add(type.getFullName())) {
      return Optional.empty();
    }
    if (this.isDeclaredIn(type, member)) {
      return Optional.of(List.of());
    }
    for (CompKindExpression superComponent : type.getSuperComponentsList()) {
      Optional<List<CompKindExpression>> path = this.superPathTo(superComponent.getTypeInfo(), member, visited);
      if (path.isPresent()) {
        List<CompKindExpression> extended = new ArrayList<>();
        extended.add(superComponent);
        extended.addAll(path.get());
        return Optional.of(List.copyOf(extended));
      }
    }
    return Optional.empty();
  }

  /**
   * @return the owner or the bound super component of the owner whose
   * component type declares the member
   */
  protected Optional<CompKindExpression> declaringOwner(@NotNull CompKindExpression owner,
                                                        @NotNull ISymbol member,
                                                        @NotNull Set<String> visited) {
    ComponentTypeSymbol type = owner.getTypeInfo();
    if (!visited.add(type.getFullName())) {
      return Optional.empty();
    }
    if (this.isDeclaredIn(type, member)) {
      return Optional.of(owner);
    }
    for (CompKindExpression superComponent : this.getSuperComponents(owner)) {
      Optional<CompKindExpression> declaring = this.declaringOwner(superComponent, member, visited);
      if (declaring.isPresent()) {
        return declaring;
      }
    }
    return Optional.empty();
  }

  protected boolean isOutsideComponents(@NotNull IScope scope) {
    return scope instanceof IArtifactScope || scope instanceof IGlobalScope;
  }

  /**
   * @return whether the member is declared in the spanned scope of the
   * component type or in a scope nested in it that is not spanned by another
   * component type, e.g., the scope of a mode
   */
  protected boolean isDeclaredIn(@NotNull ComponentTypeSymbol type, @NotNull ISymbol member) {
    for (IScope current = member.getEnclosingScope(); current != null; current = current.getEnclosingScope()) {
      if (current == type.getSpannedScope()) {
        return true;
      }
      if (current.isPresentSpanningSymbol() && current.getSpanningSymbol() instanceof ComponentTypeSymbol) {
        return false;
      }
    }
    return false;
  }

  protected Optional<SymTypeExpression> getRawType(@NotNull ISymbol member) {
    if (member instanceof PortSymbol port) {
      return port.isTypePresent() ? Optional.of(port.getType()) : Optional.empty();
    } else if (member instanceof VariableSymbol variable) {
      return Optional.ofNullable(variable.getType());
    }
    return Optional.empty();
  }

  protected SymTypeExpression bind(@NotNull SymTypeExpression type, @NotNull CompKindExpression owner) {
    Map<SymTypeVariable, SymTypeExpression> bindings = new LinkedHashMap<>();
    if (owner.isGenericComponentType()) {
      owner.asGenericComponentType().getTypeVarBindings()
        .forEach((variable, argument) -> bindings.put(SymTypeExpressionFactory.createTypeVariable(variable), argument));
    }
    return bindings.isEmpty() ? type : TypeParameterRelations.replaceTypeVariables(type, bindings);
  }

  protected CompKindExpression bind(@NotNull CompKindExpression kind, @NotNull CompKindExpression owner) {
    if (!kind.isGenericComponentType()) {
      return kind;
    }
    return new CompKindOfGenericComponentType(kind.getTypeInfo(), kind.asGenericComponentType().getTypeBindingsAsList().stream()
      .map(argument -> this.bind(argument, owner))
      .toList());
  }

  /**
   * @return the symbols of the kind declared by the component type itself,
   * i.e., in its spanned scope, excluding adapters
   */
  public <S extends ISymbol> List<S> getLocalSymbols(@NotNull ComponentTypeSymbol type, @NotNull Class<S> kind) {
    Preconditions.checkNotNull(type);
    Preconditions.checkNotNull(kind);
    return this.getLocalSymbols(type.getSpannedScope(), kind);
  }

  protected <S extends ISymbol> List<S> getLocalSymbols(@NotNull IScope scope, @NotNull Class<S> kind) {
    LocalSymbolCollector collector = new LocalSymbolCollector(scope);
    ArcBasisTraverser traverser = ArcBasisMill.inheritanceTraverser();
    traverser.add4IVisitor(collector);
    ((IArcBasisScope) scope).accept(traverser);
    return collector.getSymbols().stream()
      .filter(kind::isInstance)
      .filter(symbol -> !this.isAdapter(symbol))
      .map(kind::cast)
      .toList();
  }

  /**
   * @param declared a symbol declared by a component type
   * @param inherited a symbol inherited by the component type
   * @return whether the declared symbol hides the inherited symbol, i.e.,
   * whether they share the name
   */
  protected boolean hides(@NotNull ISymbol declared, @NotNull ISymbol inherited) {
    return declared.getName().equals(inherited.getName());
  }

  /**
   * @return the symbols of the name declared by the component type itself
   */
  public List<ISymbol> getLocalSymbols(@NotNull ComponentTypeSymbol type, @NotNull String name) {
    Preconditions.checkNotNull(type);
    Preconditions.checkNotNull(name);
    return this.getLocalSymbols(type, ISymbol.class).stream()
      .filter(symbol -> symbol.getName().equals(name))
      .toList();
  }

  protected boolean isAdapter(@NotNull ISymbol symbol) {
    return symbol instanceof Port2VariableAdapter
      || symbol instanceof Subcomponent2VariableAdapter
      || symbol instanceof ComponentType2TypeSymbolAdapter;
  }

  protected static class LocalSymbolCollector implements IVisitor {

    protected final IScope scope;
    protected final List<ISymbol> symbols = new ArrayList<>();

    protected LocalSymbolCollector(@NotNull IScope scope) {
      this.scope = scope;
    }

    @Override
    public void visit(ISymbol symbol) {
      if (symbol.getEnclosingScope() == this.scope) {
        this.symbols.add(symbol);
      }
    }

    protected List<ISymbol> getSymbols() {
      return this.symbols;
    }
  }
}
