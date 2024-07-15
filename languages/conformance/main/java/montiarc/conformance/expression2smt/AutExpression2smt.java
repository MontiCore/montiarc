/* (c) https://github.com/MontiCore/monticore */
package montiarc.conformance.expression2smt;


import arcbasis._symboltable.Port2VariableAdapter;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Expr;
import montiarc.conformance.automaton2smt.smtAutomaton.SMTAutomaton;
import montiarc.conformance.util.SymbolTableUtil;
import de.monticore.expressions.commonexpressions._ast.ASTEqualsExpression;
import de.monticore.expressions.commonexpressions._ast.ASTFieldAccessExpression;
import de.monticore.expressions.commonexpressions._ast.ASTNotEqualsExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.ocl2smt.ocl2smt.expr2smt.ExprKind;
import de.monticore.ocl2smt.ocl2smt.expr2smt.expr2z3.Z3ExprAdapter;
import de.monticore.ocl2smt.ocl2smt.expr2smt.expr2z3.Z3ExprFactory;
import de.monticore.ocl2smt.ocl2smt.expr2smt.expr2z3.Z3TypeAdapter;
import de.monticore.ocl2smt.ocl2smt.expr2smt.expr2z3.Z3TypeFactory;
import de.monticore.ocl2smt.ocl2smt.oclExpr2smt.OCLExprConverter;
import de.monticore.scbasis._symboltable.SCStateSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.compsymbols._symboltable.PortSymbol;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symboltable.ISymbol;
import de.se_rwth.commons.logging.Log;
import java.util.HashMap;
import java.util.Optional;

import montiarc.conformance.util.AutomataUtils;
import scmapping.util.MappingUtil;
import scmapping.util.SCZ3TypeFactory;

public class AutExpression2smt extends OCLExprConverter<Z3ExprAdapter> {
  protected SMTAutomaton smtAut;
  protected Expr<?> in;
  protected Expr<?> out;
  protected Expr<?> src;
  protected Expr<?> tgt;
  protected SCZ3TypeFactory tFactory;

  public AutExpression2smt(
      Z3ExprFactory exprFactory,
      SCZ3TypeFactory typeFactory,
      SMTAutomaton smtAutomaton,
      Expr<?> src,
      Expr<?> tgt,
      Expr<?> in,
      Expr<?> out) {
    super(exprFactory, typeFactory);
    this.tFactory = typeFactory;
    this.smtAut = smtAutomaton;
    this.src = src;
    this.tgt = tgt;
    this.in = in;
    this.out = out;
  }

  public BoolExpr convertBoolExpr(ASTExpression node) {
    Z3ExprAdapter boolExpr = convertExpr(node);
    if (!boolExpr.isBoolExpr()) {
      Log.error("Expected boolExpr but got " + boolExpr.getType().getName());
      assert false;
    }

    return (BoolExpr) boolExpr.getExpr();
  }
  // -----------String--------------
  @Override
  protected Z3ExprAdapter convert(ASTFieldAccessExpression node) {

    Z3TypeFactory z3TFactory = tFactory;
    String name = AutomataUtils.print(node);
    Optional<FieldSymbol> fieldSymbol = SymbolTableUtil.resolveEnumConst(name, smtAut.getCDAst());
    if (fieldSymbol.isPresent()) {
      Expr<?> expr = smtAut.mkConst(fieldSymbol.get());
      return new Z3ExprAdapter(expr, z3TFactory.mkType(name, expr.getSort(), ExprKind.ENUM));
    }
    Log.error("Unrecognized constructor symbol symbol " + MappingUtil.print(node));
    assert false;
    return null;
  }

  @Override
  public Z3ExprAdapter convertExpr(ASTExpression node) {
    Z3ExprAdapter res;
    if (node instanceof ASTEqualsExpression) {
      ASTExpression left = ((ASTEqualsExpression) node).getLeft();
      ASTExpression right = ((ASTEqualsExpression) node).getRight();
      res = convertEq(left, right);
    } else if (node instanceof ASTNotEqualsExpression) {
      ASTExpression left = ((ASTNotEqualsExpression) node).getLeft();
      ASTExpression right = ((ASTNotEqualsExpression) node).getRight();
      res = eFactory.mkNot(convertEq(left, right));
    } else {
      res = super.convertExpr(node);
    }
    return res;
  }

  public Z3ExprAdapter convertEq(ASTExpression left, ASTExpression right) {
    Z3ExprAdapter cond = eFactory.mkBool(true);
    if (left instanceof ASTNameExpression) {

      Optional<PortSymbol> portSym =
          SymbolTableUtil.resolvePort(((ASTNameExpression) left).getName(), smtAut.getComponent());
      if (portSym.isPresent() && portSym.get().isIncoming()) {
        BoolExpr expr = smtAut.checkConstructor(in, portSym.get());
        cond = new Z3ExprAdapter(expr, tFactory.mkBoolType());
      }
    }

    return eFactory.mkAnd(eFactory.mkEq(convertExpr(left), convertExpr(right)), cond);
  }

  @Override
  protected Z3ExprAdapter convert(ASTNameExpression node) {
   ISymbol symbol = SymbolTableUtil.resolveSymbol(node.getName(),smtAut.getComponent());
   Expr<?> res = null;

    // case state
    if (symbol instanceof SCStateSymbol) {
      Expr<?> expr = smtAut.mkConst(symbol, new HashMap<>());
      return new Z3ExprAdapter(expr, tFactory.mkType("State", expr.getSort(), ExprKind.ENUM));
    }

    Z3TypeAdapter type = null;
    if (SymbolTableUtil.isInPortVar(symbol, smtAut.getComponent())) {
      PortSymbol portSymbol = AutomataUtils.getPortSymbol((Port2VariableAdapter) symbol, smtAut.getComponent());
      res = smtAut.getProperty(in, portSymbol);
      type = (Z3TypeAdapter) tFactory.adapt(portSymbol.getType());
    } else if (SymbolTableUtil.isInPort(symbol)) {
      res = smtAut.getProperty(in, symbol);
      type = (Z3TypeAdapter) tFactory.adapt(((PortSymbol) symbol).getType());
    } else if (SymbolTableUtil.isOutPortVar(symbol, smtAut.getComponent())) {
      PortSymbol portSymbol = AutomataUtils.getPortSymbol((Port2VariableAdapter) symbol, smtAut.getComponent());
      res = smtAut.getProperty(out, portSymbol);
      type = (Z3TypeAdapter) tFactory.adapt(portSymbol.getType());
    } else if (SymbolTableUtil.isOutPort(symbol)) {
      res = smtAut.getProperty(out, symbol);
      type = (Z3TypeAdapter) tFactory.adapt(((PortSymbol) symbol).getType());
    } else if (SymbolTableUtil.isGlobalVar(symbol)) {
      res = smtAut.getProperty(src, symbol);
      type = (Z3TypeAdapter) tFactory.adapt(((VariableSymbol) symbol).getType());
    } else {
      Log.error("Unrecognized symbol " + symbol.getName());
      assert false;
    }

    return new Z3ExprAdapter(res, type);
  }
}
