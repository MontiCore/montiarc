/* (c) https://github.com/MontiCore/monticore */
package montiarc.conformance.automaton2smt.sort;



import arcbasis._ast.ASTComponentType;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.microsoft.z3.*;
import montiarc.conformance.automaton2smt.cd.CD2SMT;
import montiarc.conformance.util.AutomataUtils;
import de.monticore.scbasis._symboltable.SCStateSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.se_rwth.commons.logging.Log;
import montiarc.conformance.util.SMTAutomataUtils;

import java.util.*;
import java.util.function.Function;

public class StateSort implements SMTSort<SCStateSymbol, VariableSymbol> {
  private final DatatypeSort<?> sort;
  protected Context ctx;
  protected Map<SCStateSymbol, Constructor<?>> stateConstrMap = new HashMap<>();
  protected BiMap<VariableSymbol, String> varAccessor = HashBiMap.create();
  protected Constructor<?> constructor;
  protected ASTComponentType comp;

  public StateSort(
          ASTComponentType comp, CD2SMT cd2SMT, Context ctx, Function<String, String> ident) {
    this.ctx = ctx;
    this.comp = comp;

    // add constructors for each state
    for (SCStateSymbol state : AutomataUtils.getStateList(comp)) {
      Constructor<?> constr = SMTAutomataUtils.mkConstructor(ident.apply(state.getName()), ctx);
      stateConstrMap.put(state, constr);
    }

    Sort subStateSort =
        SMTAutomataUtils.mkDatatype(ident.apply("SubState"), new ArrayList<>(stateConstrMap.values()), ctx);

    List<Sort> sorts = new ArrayList<>(List.of(subStateSort));
    List<String> accessors = new ArrayList<>(List.of(SMTAutomataUtils.STATE_ACCESSOR));

    // declare accessors function for each global variable
    for (VariableSymbol var : AutomataUtils.getGlobalVariables(comp)) {
      String accessor = SMTAutomataUtils.mkAccessorName(var);
      accessors.add(accessor);
      varAccessor.put(var, accessor);

      sorts.add(SMTAutomataUtils.getSMTSort(var.getType().getTypeInfo(), cd2SMT, ctx));
    }

    // make a single constructor for states
    constructor = SMTAutomataUtils.mkConstructor("State", accessors, sorts, ctx);
    sort = SMTAutomataUtils.mkDatatype(ident.apply("StateSort"), List.of(constructor), ctx);
  }

  @Override
  public DatatypeSort<?> getSort() {
    return sort;
  }

  @Override
  public Expr<?> mkConst(SCStateSymbol stateSymbol, Map<VariableSymbol, Expr<?>> args) {

    List<Expr<?>> orderedArgs = new ArrayList<>();
    Expr<?> stateExpr = ctx.mkConst(stateConstrMap.get(stateSymbol).ConstructorDecl());

    for (FuncDecl<?> accessor : constructor.getAccessorDecls()) {

      if (accessor.getName().toString().equals(SMTAutomataUtils.STATE_ACCESSOR)) {
        orderedArgs.add(stateExpr);
      } else {
        VariableSymbol var = varAccessor.inverse().get(accessor.toString());
        orderedArgs.add(args.get(var));
      }
    }
    return constructor.ConstructorDecl().apply(orderedArgs.toArray(Expr[]::new));
  }

  @Override
  public Expr<?> getProperty(Expr<?> expr, VariableSymbol property) {
    Optional<FuncDecl<?>> accessor =
        Arrays.stream(constructor.getAccessorDecls())
            .filter(a -> a.getName().toString().equals(SMTAutomataUtils.mkAccessorName(property)))
            .findFirst();

    assert accessor.isPresent();
    return accessor.get().apply(expr);
  }

  @Override
  public String print(Expr<?> expr) {
    assert (expr.getSort().equals(sort));
    List<String> globalVars = new ArrayList<>();
    String stateName = "";

    for (FuncDecl<?> accessor : constructor.getAccessorDecls()) {
      if (accessor.getName().toString().equals(SMTAutomataUtils.STATE_ACCESSOR)) {
        stateName = getConstructor(accessor.apply(expr)).getName();
      } else {
        VariableSymbol var = varAccessor.inverse().get(accessor.getName().toString());
        Expr<?> value = accessor.apply(expr).simplify();
        globalVars.add(var.getName() + "=" + value);
      }
    }
    if (globalVars.isEmpty()) {
      return stateName;
    } else {
      String gv = globalVars.toString();
      return stateName + "{" + gv.substring(1, gv.length() - 1) + "}";
    }
  }

  @Override
  public BoolExpr checkConstructor(Expr<?> expr, SCStateSymbol stateSymbol) {
    Optional<FuncDecl<?>> accessor =
        Arrays.stream(constructor.getAccessorDecls())
            .filter(a -> a.getName().toString().equals(SMTAutomataUtils.STATE_ACCESSOR))
            .findFirst();
    assert accessor.isPresent();

    return ctx.mkEq(
        accessor.get().apply(expr), ctx.mkConst(stateConstrMap.get(stateSymbol).ConstructorDecl()));
  }

  private SCStateSymbol getConstructor(Expr<?> expr) {
    for (Map.Entry<SCStateSymbol, Constructor<?>> constr : stateConstrMap.entrySet()) {
      if (constr.getValue().getTesterDecl().apply(expr).simplify().isTrue()) {
        return constr.getKey();
      }
    }
    Log.error("Expressions" + expr + " cannot be identified as a state");
    assert false;
    return null;
  }
}
