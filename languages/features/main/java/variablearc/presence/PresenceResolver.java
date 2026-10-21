/* (c) https://github.com/MontiCore/monticore */
package variablearc.presence;

import arcbasis.context.PortAccessCandidate;
import arcbasis.context.WithinComponentResolver;
import com.google.common.base.Preconditions;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import com.microsoft.z3.IntExpr;
import com.microsoft.z3.RealSort;
import com.microsoft.z3.Sort;
import de.monticore.ast.ASTNode;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.compsymbols._symboltable.ComponentTypeSymbol;
import de.monticore.symbols.compsymbols._symboltable.ComponentTypeSymbolSurrogate;
import de.monticore.symbols.compsymbols._symboltable.PortSymbol;
import de.monticore.symbols.compsymbols._symboltable.SubcomponentSymbol;
import de.monticore.symboltable.ISymbol;
import de.monticore.types.check.CompKindExpression;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc._ast.ASTVariableArcComponentType;
import variablearc.smt.FormulaSolver;
import variablearc.smt.exp2smt.IDeriveSMTExpr;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.UnaryOperator;

/**
 * Resolves the presence conditions of the members and AST nodes of component
 * types in the vocabulary of a component type from their
 * {@link PresenceFormulas}. The presence of a member is the conjunction of
 * the constraints of the type (including inherited ones) and the local
 * presence of the member. Members of super components and subcomponents are
 * lifted into the vocabulary of the viewing type by substituting the
 * parameters by their arguments. The conditions are SMT formulas in the
 * context of the presence formulas of the global scope.
 */
public class PresenceResolver {

  protected final WithinComponentResolver withinComponentResolver;
  protected final FormulaSolver formulas = new FormulaSolver();

  public PresenceResolver(@NotNull WithinComponentResolver withinComponentResolver) {
    this.withinComponentResolver = Preconditions.checkNotNull(withinComponentResolver);
  }

  /**
   * @return the Z3 context of the presence formulas of the global scope
   */
  public Context getContext() {
    return this.formulas.getContext();
  }

  /**
   * @return a solver for formulas of the context that assumes the constraints
   * of the component type
   */
  public FormulaSolver solverFor(@NotNull ComponentTypeSymbol component) {
    Preconditions.checkNotNull(component);
    FormulaSolver solver = new FormulaSolver();
    solver.assume(this.presenceOf(component));
    return solver;
  }

  /**
   * @return a solver for formulas of the context without assumption
   */
  public FormulaSolver solver() {
    return new FormulaSolver();
  }

  /**
   * @return the presence formulas stored at the component type symbol, empty
   * if there are none, e.g., because it is loaded from a symbol file without
   * them
   */
  public Optional<PresenceFormulas> formulasOf(@NotNull ComponentTypeSymbol type) {
    Preconditions.checkNotNull(type);
    return unwrap(type) instanceof IPresenceFormulasOwner owner ? owner.getPresenceFormulas() : Optional.empty();
  }

  /**
   * @return the constraints of the component type, including those of its
   * super components
   */
  public BoolExpr presenceOf(@NotNull ComponentTypeSymbol type) {
    Preconditions.checkNotNull(type);
    return this.formulasOf(type).map(PresenceFormulas::getConstraints).orElseGet(this.formulas::top);
  }

  /**
   * @return the condition under which a delayed behavior description of the
   * component type is present
   */
  public BoolExpr delayOf(@NotNull ComponentTypeSymbol type) {
    Preconditions.checkNotNull(type);
    return this.formulasOf(type).map(PresenceFormulas::getDelayed).orElseGet(this.formulas::bottom);
  }

  /**
   * @param member a member of the component type or of one of its super
   *               components
   */
  public BoolExpr presenceOf(@NotNull ComponentTypeSymbol type, @NotNull ISymbol member) {
    Preconditions.checkNotNull(type);
    Preconditions.checkNotNull(member);
    List<CompKindExpression> path = this.withinComponentResolver.superPathTo(type, member)
      .orElseThrow(() -> new IllegalArgumentException(member.getName() + " is not a member of " + type.getFullName()));
    BoolExpr presence = this.localPresenceOf(path.isEmpty() ? type : path.get(path.size() - 1).getTypeInfo(), member);
    for (int i = path.size() - 1; i >= 0; i--) {
      presence = this.liftFromSuper(i == 0 ? type : path.get(i - 1).getTypeInfo(), path.get(i), presence);
    }
    return this.formulas.and(this.presenceOf(type), presence, this.formulas.not(this.redeclarationOf(type, member, path)));
  }

  /**
   * @param member a symbol declared by the component type, in its spanned
   *               scope or in a scope nested in it
   * @return the presence of a symbol of the spanned scope stored at the
   * component type symbol, or that of the AST node of a symbol of a nested
   * scope, which only exists with the AST
   */
  protected BoolExpr localPresenceOf(@NotNull ComponentTypeSymbol type, @NotNull ISymbol member) {
    if (member.getEnclosingScope() == unwrap(type).getSpannedScope()) {
      return this.formulasOf(type).flatMap(formulas -> formulas.of(member)).orElseGet(this.formulas::top);
    }
    return member.isPresentAstNode() ? this.localPresenceOf(type, member.getAstNode()) : this.formulas.top();
  }

  /**
   * @return the presence of the node stored at the AST of the component type
   */
  protected BoolExpr localPresenceOf(@NotNull ComponentTypeSymbol type, @NotNull ASTNode node) {
    ComponentTypeSymbol symbol = unwrap(type);
    if (symbol.isPresentAstNode() && symbol.getAstNode() instanceof ASTVariableArcComponentType ast) {
      return ast.getNodePresence().getOrDefault(node, this.formulas.top());
    }
    return this.formulas.top();
  }

  /**
   * @param path the super components along which the member is inherited
   * @return the condition under which a symbol of the same name and kind that
   * is declared by the component type or by a super component on the path
   * replaces the inherited member
   */
  protected BoolExpr redeclarationOf(@NotNull ComponentTypeSymbol type,
                                     @NotNull ISymbol member,
                                     @NotNull List<CompKindExpression> path) {
    List<BoolExpr> redeclarations = new ArrayList<>();
    for (int i = 0; i < path.size(); i++) {
      ComponentTypeSymbol redeclaring = i == 0 ? type : path.get(i - 1).getTypeInfo();
      this.withinComponentResolver.getLocalSymbols(redeclaring, member.getName()).stream()
        .filter(symbol -> this.isSameKind(symbol, member))
        .forEach(symbol -> redeclarations.add(this.presenceOf(type, symbol)));
    }
    return this.formulas.or(redeclarations);
  }

  protected boolean isSameKind(@NotNull ISymbol symbol, @NotNull ISymbol member) {
    return symbol instanceof PortSymbol && member instanceof PortSymbol
      || symbol instanceof SubcomponentSymbol && member instanceof SubcomponentSymbol
      || symbol instanceof VariableSymbol && member instanceof VariableSymbol;
  }

  /**
   * @param node a node of the AST of the component type
   */
  public BoolExpr presenceOf(@NotNull ComponentTypeSymbol type, @NotNull ASTNode node) {
    Preconditions.checkNotNull(type);
    Preconditions.checkNotNull(node);
    return this.formulas.and(this.presenceOf(type), this.localPresenceOf(type, node));
  }

  /**
   * @param subcomponent a subcomponent of the owner (or of one of its super
   *                     components)
   * @param member       a member of the subcomponent's type
   * @return the presence of the subcomponent's member in the vocabulary of
   * the owner
   */
  public BoolExpr presenceOf(@NotNull ComponentTypeSymbol owner,
                             @NotNull SubcomponentSymbol subcomponent,
                             @NotNull ISymbol member) {
    Preconditions.checkNotNull(owner);
    Preconditions.checkNotNull(subcomponent);
    Preconditions.checkNotNull(member);
    Preconditions.checkArgument(subcomponent.isTypePresent());
    return this.formulas.and(
      this.presenceOf(owner, subcomponent),
      this.lift(owner, subcomponent, this.presenceOf(subcomponent.getType().getTypeInfo(), member))
    );
  }

  /**
   * @return the presence of the candidate's port in the vocabulary of the
   * component
   */
  public BoolExpr presenceOf(@NotNull ComponentTypeSymbol component, @NotNull PortAccessCandidate candidate) {
    Preconditions.checkNotNull(component);
    Preconditions.checkNotNull(candidate);
    return candidate.subcomponent()
      .map(subcomponent -> this.presenceOf(component, subcomponent, candidate.port()))
      .orElseGet(() -> this.presenceOf(component, candidate.port()));
  }

  /**
   * @return the formula in the vocabulary of the subcomponent's type lifted
   * into the vocabulary of the owner: parameters are replaced by their
   * arguments (or default values), all other variables are qualified by the
   * name of the subcomponent
   */
  public BoolExpr lift(@NotNull ComponentTypeSymbol owner,
                       @NotNull SubcomponentSymbol subcomponent,
                       @NotNull BoolExpr formula) {
    Preconditions.checkNotNull(owner);
    Preconditions.checkNotNull(subcomponent);
    Preconditions.checkNotNull(formula);
    Preconditions.checkArgument(subcomponent.isTypePresent());
    List<CompKindExpression> path = this.withinComponentResolver.superPathTo(owner, subcomponent)
      .orElseThrow(() -> new IllegalArgumentException(subcomponent.getName() + " is not a subcomponent of " + owner.getFullName()));
    ComponentTypeSymbol declaring = path.isEmpty() ? owner : path.get(path.size() - 1).getTypeInfo();
    Map<String, Expr<?>> arguments = this.formulasOf(declaring)
      .map(formulas -> formulas.getArguments().getOrDefault(subcomponent, Map.of()))
      .orElse(Map.of());
    UnaryOperator<String> qualify = name -> qualify(subcomponent, name);
    BoolExpr lifted = (BoolExpr) this.substitute(formula, subcomponent.getType().getTypeInfo(), arguments, qualify, qualify,
      new HashSet<>());
    for (int i = path.size() - 1; i >= 0; i--) {
      lifted = this.liftFromSuper(i == 0 ? owner : path.get(i - 1).getTypeInfo(), path.get(i), lifted);
    }
    return lifted;
  }

  /**
   * @param child          the component type that extends the super component
   * @param superComponent one of the super components of the child
   * @return the formula in the vocabulary of the super component lifted into
   * the vocabulary of the child component: parameters are replaced by the
   * arguments of the extends clause (or default values), all other variables
   * are shared with the child component
   */
  public BoolExpr liftFromSuper(@NotNull ComponentTypeSymbol child,
                                @NotNull CompKindExpression superComponent,
                                @NotNull BoolExpr formula) {
    Preconditions.checkNotNull(child);
    Preconditions.checkNotNull(superComponent);
    Preconditions.checkNotNull(formula);
    List<CompKindExpression> superComponents = unwrap(child).getSuperComponentsList();
    int index = 0;
    while (index < superComponents.size() && superComponents.get(index) != superComponent) index++;
    int position = index;
    Map<String, Expr<?>> arguments = this.formulasOf(child)
      .filter(formulas -> position < formulas.getSuperArguments().size())
      .map(formulas -> formulas.getSuperArguments().get(position))
      .orElse(Map.of());
    String prefix = "^" + superComponent.getTypeInfo().getFullName() + ".";
    return (BoolExpr) this.substitute(formula, superComponent.getTypeInfo(), arguments, name -> name, name -> prefix + name,
      new HashSet<>());
  }

  /**
   * @return the name of the variable of the subcomponent's type in the
   * vocabulary of the component that declares the subcomponent
   */
  public static String qualify(@NotNull SubcomponentSymbol subcomponent, @NotNull String name) {
    Preconditions.checkNotNull(subcomponent);
    Preconditions.checkNotNull(name);
    return subcomponent.getName() + "." + name;
  }

  /**
   * @param type      the instantiated component type
   * @param arguments the arguments of the instantiation by the names of the
   *                  parameters
   * @param rename    the name of a variable that is no parameter in the target
   *                  vocabulary
   * @param unbound   the name of a parameter without value in the target
   *                  vocabulary
   * @param resolving the parameters whose default values are being
   *                  substituted, to terminate on cyclic default values
   */
  @SuppressWarnings("unchecked")
  protected <R extends Sort> Expr<R> substitute(@NotNull Expr<R> formula,
                                                @NotNull ComponentTypeSymbol type,
                                                @NotNull Map<String, Expr<?>> arguments,
                                                @NotNull UnaryOperator<String> rename,
                                                @NotNull UnaryOperator<String> unbound,
                                                @NotNull Set<String> resolving) {
    List<Expr<?>> from = new ArrayList<>();
    List<Expr<?>> to = new ArrayList<>();
    for (Expr<?> constant : this.formulas.constantsOf(formula)) {
      String name = constant.getFuncDecl().getName().toString();
      boolean parameter = type.getParameterList().stream().anyMatch(p -> Objects.equals(p.getName(), name));
      from.add(constant);
      to.add(parameter
        ? this.valueOf(type, name, constant.getSort(), arguments, rename, unbound, resolving)
        : this.getContext().mkConst(rename.apply(name), constant.getSort()));
    }
    return from.isEmpty() ? formula : (Expr<R>) formula.substitute(from.toArray(Expr[]::new), to.toArray(Expr[]::new));
  }

  /**
   * @return the argument of the parameter in the vocabulary of the component
   * that instantiates the type or its default value, converted to the sort of
   * the parameter, or an unknown value if its default value depends on itself
   * or its value cannot be determined
   */
  protected Expr<?> valueOf(@NotNull ComponentTypeSymbol type,
                            @NotNull String parameter,
                            @NotNull Sort sort,
                            @NotNull Map<String, Expr<?>> arguments,
                            @NotNull UnaryOperator<String> rename,
                            @NotNull UnaryOperator<String> unbound,
                            @NotNull Set<String> resolving) {
    Expr<?> argument = arguments.get(parameter);
    if (argument != null) {
      Optional<Expr<?>> value = this.convert(argument, sort);
      if (value.isPresent()) {
        return value.get();
      }
    } else {
      Optional<Expr<?>> defaultValue = this.formulasOf(type).map(formulas -> formulas.getDefaults().get(parameter));
      if (defaultValue.isPresent() && resolving.add(parameter)) {
        try {
          Optional<Expr<?>> value = this.convert(defaultValue.get(), sort);
          if (value.isPresent()) {
            return this.substitute(value.get(), type, arguments, rename, unbound, resolving);
          }
        } finally {
          resolving.remove(parameter);
        }
      }
    }
    return this.getContext().mkConst(unbound.apply(IDeriveSMTExpr.UNKNOWN_MARKER + parameter), sort);
  }

  /**
   * @return the value converted to the sort, as Java widens integral values
   * to floating-point values, or empty if it cannot be converted
   */
  protected Optional<Expr<?>> convert(@NotNull Expr<?> value, @NotNull Sort sort) {
    if (value.getSort().equals(sort)) {
      return Optional.of(value);
    } else if (value instanceof IntExpr integral && sort instanceof RealSort) {
      return Optional.of(this.getContext().mkInt2Real(integral));
    }
    return Optional.empty();
  }

  /**
   * @return whether the member is present in every configuration of the
   * component type, decided without a solver: the type and its super
   * components have no constraints, the member is present wherever its type
   * is, and no symbol redeclares it
   */
  public boolean isUnconditional(@NotNull ComponentTypeSymbol type, @NotNull ISymbol member) {
    Preconditions.checkNotNull(type);
    Preconditions.checkNotNull(member);
    Optional<List<CompKindExpression>> path = this.withinComponentResolver.superPathTo(type, member);
    if (path.isEmpty() || !this.presenceOf(type).isTrue()) return false;
    ComponentTypeSymbol declaring = path.get().isEmpty() ? type : path.get().get(path.get().size() - 1).getTypeInfo();
    if (!this.localPresenceOf(declaring, member).isTrue()) return false;
    for (int i = 0; i < path.get().size(); i++) {
      ComponentTypeSymbol redeclaring = i == 0 ? type : path.get().get(i - 1).getTypeInfo();
      if (this.withinComponentResolver.getLocalSymbols(redeclaring, member.getName()).stream()
        .anyMatch(symbol -> this.isSameKind(symbol, member))) return false;
    }
    return true;
  }

  /**
   * @return whether the node is present in every configuration of the
   * component type, decided without a solver
   */
  public boolean isUnconditional(@NotNull ComponentTypeSymbol type, @NotNull ASTNode node) {
    Preconditions.checkNotNull(type);
    Preconditions.checkNotNull(node);
    return this.presenceOf(type, node).isTrue();
  }

  protected static ComponentTypeSymbol unwrap(@NotNull ComponentTypeSymbol type) {
    return type instanceof ComponentTypeSymbolSurrogate surrogate ? surrogate.lazyLoadDelegate() : type;
  }
}
