/* (c) https://github.com/MontiCore/monticore */
package montiarc.conformance.automaton2smt.sort;

import arcbasis._ast.ASTComponentType;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.microsoft.z3.*;
import montiarc.conformance.automaton2smt.cd.CD2SMT;
import montiarc.conformance.util.AutomataUtils;
import montiarc.conformance.util.SMTAutomataUtils;
import montiarc.conformance.util.VoidSymbol;
import de.monticore.symbols.compsymbols._symboltable.PortSymbol;

import java.util.*;
import java.util.function.Function;

public class OutputSort implements SMTSort<VoidSymbol, PortSymbol> {
  private final Context ctx;
  private final DatatypeSort<?> sort;

  private final Constructor<?> constructor;
  private final BiMap<PortSymbol, String> portAccessorMap = HashBiMap.create();

  public OutputSort(
          ASTComponentType comp, CD2SMT cd2SMT, Context ctx, Function<String, String> ident) {
    this.ctx = ctx;
    List<Sort> sorts = new ArrayList<>();
    List<String> accessors = new ArrayList<>();

    // declare accessors function for each global variable
    for (PortSymbol port : AutomataUtils.getOutPorts(comp)) {
      String accessor = SMTAutomataUtils.mkAccessorName(port);
      Sort sort = ctx.mkSeqSort(SMTAutomataUtils.getSMTSort(port.getTypeInfo(), cd2SMT, ctx));

      portAccessorMap.put(port, accessor);
      sorts.add(sort);
      accessors.add(accessor);
    }

    constructor = SMTAutomataUtils.mkConstructor(ident.apply("Output"), accessors, sorts, ctx);
    sort = SMTAutomataUtils.mkDatatype(ident.apply("OutputSort"), List.of(constructor), ctx);
  }

  @Override
  public DatatypeSort<?> getSort() {
    return sort;
  }

  @Override
  public Expr<?> mkConst(VoidSymbol voidSymbol, Map<PortSymbol, Expr<?>> vars) {

    List<Expr<?>> args = new ArrayList<>();

    for (FuncDecl<?> accessor : constructor.getAccessorDecls()) {
      PortSymbol port = portAccessorMap.inverse().get(accessor.getName().toString());

      if (vars.containsKey(port)) {
        args.add(ctx.mkUnit(vars.get(port))); // case out=2 many times
      } else {
        args.add(ctx.mkEmptySeq(accessor.getRange()));
      }
    }
    return constructor.ConstructorDecl().apply(args.toArray(Expr[]::new));
  }

  @Override
  public Expr<?> getProperty(Expr<?> expr, PortSymbol property) {
    Optional<FuncDecl<?>> accessor =
        Arrays.stream(constructor.getAccessorDecls())
            .filter(a -> a.getName().toString().equals(SMTAutomataUtils.mkAccessorName(property)))
            .findFirst();
    assert accessor.isPresent();
    return accessor.get().apply(expr);
  }

  @Override
  public String print(Expr<?> expr) {
    List<String> valueList = new ArrayList<>();
    for (FuncDecl<?> accessor : constructor.getAccessorDecls()) {

      Expr<SeqSort<?>> property = (Expr<SeqSort<?>>) accessor.apply(expr).simplify();

      String portName = portAccessorMap.inverse().get(accessor.getName().toString()).getName();
      List<String> subValues = new ArrayList<>();
      for (Expr<?> arg : property.getArgs()) {

        String value = property.getArgs().length > 1 ? arg.getArgs()[0].toString() : arg.toString();
        if (value.endsWith(SMTAutomataUtils.CON_SUFFIX) || value.endsWith(SMTAutomataUtils.REF_SUFFIX)) {
          value = value.substring(0, value.length() - 4);
        }
        subValues.add(value);
      }

      valueList.add(portName + " = " + subValues);
    }
    String res = valueList.toString();
    return res.length() >= 3 ? res.substring(1, res.length() - 1) : res;
  }

  @Override
  public BoolExpr checkConstructor(Expr<?> expr, VoidSymbol constructor) {
    return ctx.mkTrue();
  }
}
