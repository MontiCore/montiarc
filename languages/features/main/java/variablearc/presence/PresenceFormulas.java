/* (c) https://github.com/MontiCore/monticore */
package variablearc.presence;

import com.google.common.base.Preconditions;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Expr;
import de.monticore.symbols.compsymbols._symboltable.SubcomponentSymbol;
import de.monticore.symboltable.ISymbol;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * The presence formulas of a single component type in the vocabulary of that
 * type, i.e., over its features, parameters, unknown values, and the
 * features of its subcomponents it references. Lifting them into the
 * vocabulary of another component type substitutes the parameters by the
 * arguments of the instantiation (or by the default values).
 */
public final class PresenceFormulas {

  private final BoolExpr constraints;
  private final BoolExpr delayed;
  private final Map<ISymbol, BoolExpr> symbols;
  private final Map<String, Expr<?>> defaults;
  private final Map<SubcomponentSymbol, Map<String, Expr<?>>> arguments;
  private final List<Map<String, Expr<?>>> superArguments;

  /**
   * @param constraints    the constraints of the component type, including
   *                       those of its super components
   * @param delayed        the condition under which a delayed behavior
   *                       description of the component type is present
   * @param symbols        the presence of the symbols the component type
   *                       declares that are not always present
   * @param defaults       the default values of the parameters by their names
   * @param arguments      the arguments of the subcomponents the component
   *                       type declares by the names of the parameters
   * @param superArguments the arguments of the super components by the names
   *                       of their parameters, in the order of the super
   *                       components
   */
  public PresenceFormulas(@NotNull BoolExpr constraints,
                          @NotNull BoolExpr delayed,
                          @NotNull Map<ISymbol, BoolExpr> symbols,
                          @NotNull Map<String, Expr<?>> defaults,
                          @NotNull Map<SubcomponentSymbol, Map<String, Expr<?>>> arguments,
                          @NotNull List<Map<String, Expr<?>>> superArguments) {
    this.constraints = Preconditions.checkNotNull(constraints);
    this.delayed = Preconditions.checkNotNull(delayed);
    this.symbols = Collections.unmodifiableMap(new IdentityHashMap<>(Preconditions.checkNotNull(symbols)));
    this.defaults = Collections.unmodifiableMap(new LinkedHashMap<>(Preconditions.checkNotNull(defaults)));
    this.arguments = Collections.unmodifiableMap(new IdentityHashMap<>(Preconditions.checkNotNull(arguments)));
    this.superArguments = List.copyOf(Preconditions.checkNotNull(superArguments));
  }

  public BoolExpr getConstraints() {
    return this.constraints;
  }

  public BoolExpr getDelayed() {
    return this.delayed;
  }

  public Map<ISymbol, BoolExpr> getSymbols() {
    return this.symbols;
  }

  public Map<String, Expr<?>> getDefaults() {
    return this.defaults;
  }

  public Map<SubcomponentSymbol, Map<String, Expr<?>>> getArguments() {
    return this.arguments;
  }

  public List<Map<String, Expr<?>>> getSuperArguments() {
    return this.superArguments;
  }

  /**
   * @return the presence of the member, empty if it is always present
   */
  public Optional<BoolExpr> of(@NotNull ISymbol symbol) {
    Preconditions.checkNotNull(symbol);
    return Optional.ofNullable(this.symbols.get(symbol));
  }
}
