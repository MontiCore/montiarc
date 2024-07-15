/* (c) https://github.com/MontiCore/monticore */
package montiarc.conformance.automaton2smt.sort;



import arcbasis._ast.ASTComponentType;
import com.microsoft.z3.*;
import montiarc.conformance.automaton2smt.cd.CD2SMT;
import montiarc.conformance.util.AutomataUtils;
import montiarc.conformance.util.SMTAutomataUtils;
import de.monticore.symbols.compsymbols._symboltable.PortSymbol;

import java.util.*;
import java.util.function.Function;

public class InputSort implements SMTSort<PortSymbol, PortSymbol> {
  protected final Map<PortSymbol, Constructor<?>> constructors = new HashMap<>();
  private final DatatypeSort<?> sort;

  public InputSort(
          ASTComponentType comp, CD2SMT cd2SMT, Context ctx, Function<String, String> ident) {

    // make a constructor for each incoming port
    for (PortSymbol port : AutomataUtils.getInPorts(comp)) {
      Sort pSort = SMTAutomataUtils.getSMTSort(port.getTypeInfo(), cd2SMT, ctx);
      String acc = SMTAutomataUtils.mkAccessorName(port);
      Constructor<?> constructor =
          SMTAutomataUtils.mkConstructor(ident.apply(port.getName()), List.of(acc), List.of(pSort), ctx);

      constructors.put(port, constructor);
    }
    // make a datatype form the constructors
    sort = SMTAutomataUtils.mkDatatype(ident.apply("InputSort"), new ArrayList<>(constructors.values()), ctx);
  }

  @Override
  public DatatypeSort<?> getSort() {
    return sort;
  }

  @Override
  public Expr<?> mkConst(PortSymbol constructor, Map<PortSymbol, Expr<?>> var) {
    return constructors.get(constructor).ConstructorDecl().apply(var.values().iterator().next());
  }

  @Override
  public Expr<?> getProperty(Expr<?> expr, PortSymbol property) {
    return constructors.get(property).getAccessorDecls()[0].apply(expr);
  }

  @Override
  public String print(Expr<?> expr) {
    Optional<PortSymbol> port =
        constructors.entrySet().stream()
            .filter(e -> e.getValue().getTesterDecl().apply(expr).simplify().isTrue())
            .map(Map.Entry::getKey)
            .findFirst();
    assert port.isPresent();
    String value =
        constructors.get(port.get()).getAccessorDecls()[0].apply(expr).simplify().toString();
    if (value.endsWith(SMTAutomataUtils.CON_SUFFIX) || value.endsWith(SMTAutomataUtils.REF_SUFFIX)) {
      value = value.substring(0, value.length() - 4);
    }

    return port.get().getName() + "=" + value;
  }

  @Override
  public BoolExpr checkConstructor(Expr<?> expr, PortSymbol constructor) {
    return (BoolExpr) constructors.get(constructor).getTesterDecl().apply(expr);
  }

  public BoolExpr isOfInputChannel(Expr<?> expr, PortSymbol symbol) {
    assert (expr.getSort().equals(sort));
    return (BoolExpr) constructors.get(symbol).getTesterDecl().apply(expr);
  }
}
