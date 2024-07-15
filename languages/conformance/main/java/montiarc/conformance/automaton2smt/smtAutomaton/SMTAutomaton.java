/* (c) https://github.com/MontiCore/monticore */
package montiarc.conformance.automaton2smt.smtAutomaton;



import arcbasis._ast.ASTComponentType;
import arcbasis._symboltable.Port2VariableAdapter;
import com.microsoft.z3.*;
import montiarc.conformance.automaton2smt.cd.CD2SMT;
import montiarc.conformance.automaton2smt.sort.InputSort;
import montiarc.conformance.automaton2smt.sort.OutputSort;
import montiarc.conformance.automaton2smt.sort.SMTSort;
import montiarc.conformance.automaton2smt.sort.StateSort;
import montiarc.conformance.expression2smt.AutExpression2smt;
import montiarc.conformance.util.AutomataUtils;
import montiarc.conformance.util.SMTAutomataUtils;
import montiarc.conformance.util.SymbolTableUtil;
import montiarc.conformance.util.VoidSymbol;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.expressions.assignmentexpressions._ast.ASTAssignmentExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.ocl2smt.ocl2smt.expr2smt.expr2z3.Z3ExprFactory;
import de.monticore.scbasis._ast.ASTSCTransition;
import de.monticore.scbasis._symboltable.SCStateSymbol;
import de.monticore.sctransitions4code._ast.ASTTransitionBody;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.compsymbols._symboltable.PortSymbol;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symboltable.ISymbol;
import de.se_rwth.commons.logging.Log;
import java.util.*;
import java.util.function.Function;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import scmapping.util.SCZ3TypeFactory;

public class SMTAutomaton implements ISMTAutomaton {
  protected Z3ExprFactory eFactory;
  protected SCZ3TypeFactory tFactory;
  protected Context ctx;
  protected ASTComponentType comp;
  protected CD2SMT cd2SMT;
  protected SMTSort<SCStateSymbol, VariableSymbol> smtState;
  protected SMTSort<PortSymbol, PortSymbol> inputPort;
  protected SMTSort<VoidSymbol, PortSymbol> outputPort;
  protected AutExpression2smt exprConv;
  protected Function<String, String> ident;

  /***
   * The Constructor transforms a Statechart in smt.
   * @param comp the Automaton as MontiArc Component.
   * @param cd the class Diagram where some types are declared.
   * @param ident a suffix to identify the declarations for a specific automaton.
   * @param ctx the smt context of the declarations.
   */
  public SMTAutomaton(
      ASTComponentType comp, ASTCDCompilationUnit cd, Function<String, String> ident, Context ctx) {

    this.comp = comp;
    this.ctx = ctx;
    this.cd2SMT = new CD2SMT(cd, ident, ctx);
    this.ident = ident;
    tFactory = new SCZ3TypeFactory(cd, this::getSort, ctx);
    eFactory = new Z3ExprFactory(tFactory, ctx);

    // convert states to SMT
    smtState = new StateSort(comp, cd2SMT, ctx, ident);
    inputPort = new InputSort(comp, cd2SMT, ctx, ident);
    outputPort = new OutputSort(comp, cd2SMT, ctx, ident);
  }

  @Override
  public BoolExpr evaluateTransition(
      ASTSCTransition trans, Expr<?> in, Expr<?> src, Expr<?> tgt, Expr<?> out) {

    // init the expression converter
    exprConv = new AutExpression2smt(eFactory, tFactory, this, src, tgt, in, out);

    // check source state

    BoolExpr checkSourceState = smtState.checkConstructor(src, trans.getSourceNameSymbol());

    // check the next state
    BoolExpr checkTargetState = smtState.checkConstructor(tgt, trans.getTargetNameSymbol());

    // check input value
    BoolExpr checkInput = evaluateGuard(trans, in, src);

    // check output value
    BoolExpr checkOutput = evaluateAction(trans, in, src, tgt, out);

    return ctx.mkAnd(checkSourceState, checkTargetState, checkInput, checkOutput);
  }

  @Override
  public BoolExpr evaluateGuard(ASTSCTransition trans, Expr<?> input, Expr<?> src) {
    exprConv = new AutExpression2smt(eFactory, tFactory, this, src, null, input, null);
    ASTTransitionBody body = ((ASTTransitionBody) trans.getSCTBody());
    return body.isPresentPre() ? exprConv.convertBoolExpr(body.getPre()) : ctx.mkTrue();
  }

  @Override
  public BoolExpr evaluateAction(
      ASTSCTransition trans, Expr<?> in, Expr<?> src, Expr<?> tgt, Expr<?> out) {

    exprConv = new AutExpression2smt(eFactory, tFactory, this, src, tgt, in, out);
    Map<PortSymbol, Expr<?>> outputActions = new HashMap<>();
    BoolExpr res = ctx.mkTrue();

    for (ASTAssignmentExpression act : AutomataUtils.getActionList(trans)) {

      Pair<ISymbol, Expr<?>> evAction = convert(act);

      if (SymbolTableUtil.isGlobalVar(evAction.getLeft())) {
        Expr<?> globalVar = smtState.getProperty(tgt, (VariableSymbol) evAction.getLeft());
        res = ctx.mkAnd(res, ctx.mkEq(globalVar, evAction.getRight()));
      } else {
        outputActions.put((PortSymbol) evAction.getLeft(), evAction.getRight());
      }
    }
    Expr<?> output = outputPort.mkConst(VoidSymbol.getInstance(), outputActions);
    res = ctx.mkAnd(res, ctx.mkEq(out, output));

    return res;
  }

  protected Pair<ISymbol, Expr<?>> convert(ASTAssignmentExpression node) {

    ISymbol symbol = SymbolTableUtil.resolveSymbol(((ASTNameExpression) node.getLeft()).getName(),comp);

    Expr<?> value = exprConv.convertExpr(node.getRight()).getExpr();
    ISymbol left = symbol;

    if (SymbolTableUtil.isOutPortVar(symbol, comp)) {
      left = AutomataUtils.getPortSymbol((Port2VariableAdapter) symbol, comp);
    }

    return new ImmutablePair<>(left, value);
  }

  @Override
  public DatatypeSort<?> getStateSort() {
    return smtState.getSort();
  }

  @Override
  public String print(Expr<?> expr) {
    String res;
    if (expr.getSort().equals(inputPort.getSort())) {
      res = inputPort.print(expr);
    } else if (expr.getSort().equals(smtState.getSort())) {
      res = smtState.print(expr);
    } else if (expr.getSort().equals(outputPort.getSort())) {
      res = outputPort.print(expr);
    } else {
      res = expr.toString();
    }
    return res;
  }

  @Override
  public DatatypeSort<?> getInputSort() {
    return inputPort.getSort();
  }

  @Override
  public DatatypeSort<?> getOutputSort() {
    return outputPort.getSort();
  }

  @Override
  public ASTComponentType getComponent() {
    return comp;
  }

  @Override
  public ASTCDCompilationUnit getCDAst() {
    return cd2SMT.getAst();
  }

  @Override
  public Sort getSort(PortSymbol port) {
    return SMTAutomataUtils.getSMTSort(port.getTypeInfo(), cd2SMT, ctx);
  }

  @Override
  public Sort getSort(ASTCDEnum astcdEnum) {
    return cd2SMT.getSort(astcdEnum);
  }

  @Override
  public Expr<?> mkConst(FieldSymbol fieldSymbol) {
    return cd2SMT.getConst(fieldSymbol);
  }

  @Override
  public Expr<?> mkConst(ISymbol constrSymbol, Map<ISymbol, Expr<?>> args) {
    if (constrSymbol instanceof SCStateSymbol) {
      Map<VariableSymbol, Expr<?>> sArgs = new HashMap<>();
      args.forEach((key, value) -> sArgs.put((VariableSymbol) key, value));
      return smtState.mkConst((SCStateSymbol) constrSymbol, sArgs);
    } else if (SymbolTableUtil.isInPort(constrSymbol)) {
      Map<PortSymbol, Expr<?>> sArgs = new HashMap<>();
      args.forEach((key, value) -> sArgs.put((PortSymbol) key, value));
      return inputPort.mkConst((PortSymbol) constrSymbol, sArgs);
    } else if (SymbolTableUtil.isOutPort(constrSymbol)) {
      Map<PortSymbol, Expr<?>> sArgs = new HashMap<>();
      args.forEach((key, value) -> sArgs.put((PortSymbol) key, value));
      return outputPort.mkConst(VoidSymbol.getInstance(), sArgs);
    } else {
      assert false;
      Log.error("Unrecognized constructor symbol symbol " + constrSymbol.getName());
      return null;
    }
  }

  @Override
  public Expr<?> getProperty(Expr<?> expr, ISymbol property) {
    if (expr.getSort().equals(inputPort.getSort())) {
      return inputPort.getProperty(expr, (PortSymbol) property);
    } else if (expr.getSort().equals(outputPort.getSort())) {
      return outputPort.getProperty(expr, (PortSymbol) property);
    } else if (expr.getSort().equals(smtState.getSort())) {
      return smtState.getProperty(expr, (VariableSymbol) property);
    } else {
      Log.error("Unrecognized constructor sort symbol " + expr.getSort());

      assert false;
      return null;
    }
  }

  @Override
  public BoolExpr checkConstructor(Expr<?> expr, ISymbol constrSymbol) {
    if (constrSymbol instanceof SCStateSymbol) {
      return smtState.checkConstructor(expr, (SCStateSymbol) constrSymbol);
    } else if (SymbolTableUtil.isOutPort(constrSymbol)) {
      return outputPort.checkConstructor(expr, VoidSymbol.getInstance());
    } else if (SymbolTableUtil.isInPort(constrSymbol)) {
      return inputPort.checkConstructor(expr, (PortSymbol) constrSymbol);
    }
    Log.error("Unrecognized constructor symbol " + constrSymbol.getName());
    return ctx.mkTrue();
  }
}
