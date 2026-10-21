/* (c) https://github.com/MontiCore/monticore */
package variablearc.presence;

import arcbasis._ast.ASTArcComponentType;
import arcbasis._visitor.ArcBasisVisitor2;
import com.google.common.base.Preconditions;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import de.monticore.ast.ASTNode;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.compsymbols._ast.ASTSubcomponentArgument;
import de.monticore.symbols.compsymbols._symboltable.ComponentTypeSymbol;
import de.monticore.symbols.compsymbols._symboltable.ComponentTypeSymbolSurrogate;
import de.monticore.symbols.compsymbols._symboltable.SubcomponentSymbol;
import de.monticore.symboltable.ISymbol;
import de.monticore.types.check.CompKindExpression;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types3.TypeCheck3;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc.VariableArcMill;
import variablearc._ast.ASTVariableArcComponentType;
import variablearc._symboltable.IVariableArcComponentTypeSymbol;
import variablearc._visitor.VariableArcTraverser;
import variablearc.context.VariableArcWithinComponentResolver;
import variablearc.smt.FormulaSolver;
import variablearc.smt.exp2smt.IDeriveSMTExpr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Calculates the {@link PresenceFormulas} of component types from the
 * {@link LocalPresence} of their ASTs and stores them at their symbols, and
 * the presence formulas of the nodes at their ASTs. This is the only
 * place where expressions of the model are translated into SMT formulas.
 * Super components are calculated before the component types that extend
 * them, as their constraints are included.
 */
public class PresenceFormulaCalculator {

  protected final PresenceResolver presenceResolver;
  protected final Context context;
  protected final FormulaSolver formulas;
  protected final IDeriveSMTExpr converter;
  protected final Set<ComponentTypeSymbol> calculating = new HashSet<>();

  public PresenceFormulaCalculator() {
    this(new PresenceResolver(new VariableArcWithinComponentResolver()));
  }

  /**
   * @param presenceResolver resolves the presence of super components to
   *                         include their constraints
   */
  public PresenceFormulaCalculator(@NotNull PresenceResolver presenceResolver) {
    this.presenceResolver = Preconditions.checkNotNull(presenceResolver);
    this.formulas = new FormulaSolver();
    this.context = this.formulas.getContext();
    this.converter = VariableArcMill.fullConverter(this.context);
  }

  /**
   * Calculates the formulas of the component type and of all its inner
   * component types.
   */
  public void calculate(@NotNull ASTArcComponentType component) {
    Preconditions.checkNotNull(component);
    List<ASTArcComponentType> types = new ArrayList<>();
    VariableArcTraverser traverser = VariableArcMill.inheritanceTraverser();
    traverser.add4ArcBasis(new ArcBasisVisitor2() {
      @Override
      public void visit(ASTArcComponentType node) {
        types.add(node);
      }
    });
    component.accept(traverser);
    types.stream().filter(ASTArcComponentType::isPresentSymbol).forEach(type -> this.calculate(type.getSymbol()));
  }

  /**
   * Calculates the formulas of the component type, unless it has some
   * already, after those of its super components.
   */
  public void calculate(@NotNull ComponentTypeSymbol type) {
    Preconditions.checkNotNull(type);
    ComponentTypeSymbol symbol = unwrap(type);
    if (!(symbol instanceof IPresenceFormulasOwner owner) || owner.getPresenceFormulas().isPresent()
      || !this.calculating.add(symbol)) {
      return;
    }
    try {
      symbol.getSuperComponentsList().forEach(superComponent -> this.calculate(superComponent.getTypeInfo()));
      Optional<ASTVariableArcComponentType> ast = symbol.isPresentAstNode()
        && symbol.getAstNode() instanceof ASTVariableArcComponentType component ? Optional.of(component) : Optional.empty();
      LocalPresence local = ast.flatMap(ASTVariableArcComponentType::getLocalPresence).orElseGet(LocalPresence::empty);
      String name = symbol.getFullName();
      Map<PresenceCondition, BoolExpr> translated = new HashMap<>();

      Map<ASTNode, BoolExpr> nodes = new IdentityHashMap<>();
      local.getNodes().forEach((node, condition) -> nodes.put(node, this.translate(name, condition, translated)));
      ast.ifPresent(component -> component.setNodePresence(nodes));
      Map<ISymbol, BoolExpr> members = new IdentityHashMap<>();
      this.presenceResolver.withinComponentResolver.getLocalSymbols(symbol, ISymbol.class).stream()
        .filter(member -> member.isPresentAstNode() && nodes.containsKey(member.getAstNode()))
        .forEach(member -> members.put(member, nodes.get(member.getAstNode())));
      BoolExpr delayed = this.formulas.or(local.getDelayedBehaviors().stream()
        .map(condition -> this.translate(name, condition, translated))
        .toList());
      List<BoolExpr> constraints = new ArrayList<>();
      for (LocalPresence.ConditionalConstraint constraint : local.getConstraints()) {
        constraints.add(this.formulas.implies(this.translate(name, constraint.condition(), translated),
          this.translate(name, constraint.constraint())));
      }

      Map<String, Expr<?>> defaults = new LinkedHashMap<>();
      if (symbol instanceof IVariableArcComponentTypeSymbol variableArcSymbol) {
        for (VariableSymbol parameter : symbol.getParameterList()) {
          variableArcSymbol.getParameterDefault(parameter.getName())
            .flatMap(value -> this.translateValue(name, value, Optional.ofNullable(parameter.getType())))
            .ifPresent(value -> defaults.put(parameter.getName(), value));
        }
      }
      Map<SubcomponentSymbol, Map<String, Expr<?>>> arguments = new IdentityHashMap<>();
      for (SubcomponentSymbol subcomponent : symbol.getSubcomponents()) {
        if (subcomponent.isTypePresent()) {
          arguments.put(subcomponent, this.argumentsOf(name, subcomponent.getType()));
        }
      }
      List<Map<String, Expr<?>>> superArguments = symbol.getSuperComponentsList().stream()
        .map(superComponent -> this.argumentsOf(name, superComponent))
        .toList();

      owner.setPresenceFormulas(new PresenceFormulas(this.formulas.and(constraints), delayed, members, defaults,
        arguments, superArguments));
      for (CompKindExpression superComponent : symbol.getSuperComponentsList()) {
        constraints.add(this.presenceResolver.liftFromSuper(symbol, superComponent,
          this.presenceResolver.presenceOf(superComponent.getTypeInfo())));
      }
      owner.setPresenceFormulas(new PresenceFormulas(this.formulas.and(constraints), delayed, members, defaults,
        arguments, superArguments));
    } finally {
      this.calculating.remove(symbol);
    }
  }

  /**
   * @return the arguments of the instantiation by the names of the
   * parameters, in the vocabulary of the instantiating component type
   */
  protected Map<String, Expr<?>> argumentsOf(@NotNull String type, @NotNull CompKindExpression kind) {
    if (kind.getParamBindings().isEmpty() && !kind.getArguments().isEmpty()) {
      kind.bindParams();
    }
    Map<String, Expr<?>> arguments = new LinkedHashMap<>();
    List<VariableSymbol> parameters = kind.getTypeInfo().getParameterList();
    List<Optional<SymTypeExpression>> parameterTypes = kind.getParameterTypes();
    for (int i = 0; i < parameters.size(); i++) {
      VariableSymbol parameter = parameters.get(i);
      Optional<SymTypeExpression> parameterType = i < parameterTypes.size() ? parameterTypes.get(i) : Optional.empty();
      kind.getParamBindingFor(parameter)
        .map(ASTSubcomponentArgument::getExpression)
        .flatMap(argument -> this.translateValue(type, argument, parameterType))
        .ifPresent(argument -> arguments.put(parameter.getName(), argument));
    }
    return arguments;
  }

  protected BoolExpr translate(@NotNull String type,
                               @NotNull PresenceCondition condition,
                               @NotNull Map<PresenceCondition, BoolExpr> translated) {
    BoolExpr known = translated.get(condition);
    if (known != null) {
      return known;
    }
    BoolExpr formula = this.formulas.and(condition.literals().stream()
      .map(literal -> literal.negated() ? this.formulas.not(this.translate(type, literal.condition()))
        : this.translate(type, literal.condition()))
      .toList());
    translated.put(condition, formula);
    return formula;
  }

  /**
   * @param condition a boolean expression of the component type
   * @return the condition in the vocabulary of the component type; a condition
   * that cannot be converted is an unknown value, which is marked by
   * {@link FormulaSolver#OBSCURE_MARKER} if its type cannot be derived
   */
  protected BoolExpr translate(@NotNull String type, @NotNull ASTExpression condition) {
    if (IDeriveSMTExpr.isObscure(condition)) {
      return (BoolExpr) this.qualifyUnknowns(type,
        this.context.mkBoolConst(FormulaSolver.OBSCURE_MARKER + this.converter.unknownName(condition)));
    }
    BoolExpr translated = this.converter.toBool(condition).orElseGet(() -> {
      this.converter.setUnknown(condition, this.context.getBoolSort());
      return this.converter.getResult().getValueAsBool().orElseThrow();
    });
    return (BoolExpr) this.qualifyUnknowns(type, translated);
  }

  /**
   * @param expression an expression of the component type
   * @param target     the type the expression is assigned to, which the type
   *                   check needs to know before the expression is typed
   *                   without it
   * @return the value of the expression in the vocabulary of the component
   * type, an unknown value if it cannot be converted, or empty if its sort
   * cannot be determined
   */
  protected Optional<Expr<?>> translateValue(@NotNull String type,
                                             @NotNull ASTExpression expression,
                                             @NotNull Optional<SymTypeExpression> target) {
    if (expression.getEnclosingScope() != null) {
      target.ifPresent(targetType -> TypeCheck3.typeOf(expression, targetType));
    }
    Optional<Expr<?>> translated = this.converter.toExpr(expression);
    if (translated.isEmpty() && !IDeriveSMTExpr.isObscure(expression)) {
      this.converter.setUnknown(expression);
      translated = this.converter.getResult().getValue();
    }
    return translated.map(value -> this.qualifyUnknowns(type, value));
  }

  /**
   * @return the expression with the unknown values qualified by the name of
   * the component type, such that the unknown values of different component
   * types are different in the common context
   */
  protected Expr<?> qualifyUnknowns(@NotNull String type, @NotNull Expr<?> expression) {
    List<Expr<?>> from = new ArrayList<>();
    List<Expr<?>> to = new ArrayList<>();
    for (Expr<?> constant : this.formulas.constantsOf(expression)) {
      String name = constant.getFuncDecl().getName().toString();
      if (IDeriveSMTExpr.isUnknown(constant) && !name.contains(IDeriveSMTExpr.UNKNOWN_MARKER + type + "@")) {
        from.add(constant);
        to.add(this.context.mkConst(name.replace(IDeriveSMTExpr.UNKNOWN_MARKER, IDeriveSMTExpr.UNKNOWN_MARKER + type + "@"),
          constant.getSort()));
      }
    }
    return from.isEmpty() ? expression : expression.substitute(from.toArray(Expr[]::new), to.toArray(Expr[]::new));
  }

  protected static ComponentTypeSymbol unwrap(@NotNull ComponentTypeSymbol type) {
    return type instanceof ComponentTypeSymbolSurrogate surrogate ? surrogate.lazyLoadDelegate() : type;
  }
}
