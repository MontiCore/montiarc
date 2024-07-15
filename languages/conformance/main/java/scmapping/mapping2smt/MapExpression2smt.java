/* (c) https://github.com/MontiCore/monticore */
package scmapping.mapping2smt;

import com.microsoft.z3.*;
import montiarc.conformance.automaton2smt.smtAutomaton.ISMTAutomaton;
import de.monticore.expressions.commonexpressions._ast.*;
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
import de.se_rwth.commons.logging.Log;
import java.util.Optional;
import montiarc.MontiArcMill;
import scmapping._ast.ASTSeqExpression;
import scmapping.util.MappingUtil;
import scmapping.util.SCZ3TypeFactory;

import static montiarc.conformance.util.SymbolTableUtil.*;

public class MapExpression2smt extends OCLExprConverter<Z3ExprAdapter> {

  protected Context ctx;
  protected boolean isReference;
  protected ISMTAutomaton smtAut;

  protected MCMapping mapping;

  protected Expr<?> in;
  protected Expr<?> state;

  protected Expr<?> out;

  protected SCZ3TypeFactory tFactory;

  public MapExpression2smt(
      Z3ExprFactory exprFactory,
      SCZ3TypeFactory typeFactory,
      ISMTAutomaton smtAut,
      MCMapping mapper,
      Expr<?> in,
      Expr<?> state,
      Expr<?> out,
      Context ctx,
      boolean isReference) {
    super(exprFactory, typeFactory);
    this.tFactory = typeFactory;
    this.ctx = ctx;
    this.in = in;
    this.out = out;
    this.state = state;
    this.mapping = mapper;
    this.smtAut = smtAut;
    this.isReference = isReference;
  }

  public BoolExpr convertBoolExpr(ASTExpression node) {
    Z3ExprAdapter boolExpr = convertExpr(node);
    if (!boolExpr.isBoolExpr()) {
      Log.error("Expected boolExpr but got " + boolExpr.getType().getName());
      assert false;
    }

    return (BoolExpr) boolExpr.getExpr();
  }

  @Override
  protected Z3ExprAdapter convert(ASTNameExpression node) {
    Expr<?> expr = null;
    Z3TypeAdapter type = null;
    Optional<PortSymbol> portSym = resolvePort(node.getName(), smtAut.getComponent());
    if (portSym.isPresent() && portSym.get().isOutgoing()) {
      expr = smtAut.getProperty(getOutput(), portSym.get());
      type = (Z3TypeAdapter) tFactory.adapt(portSym.get().getType());
    }

    if (portSym.isPresent() && portSym.get().isIncoming() && expr == null) {
      expr = smtAut.getProperty(getInput(), portSym.get());
      type = (Z3TypeAdapter) tFactory.adapt(portSym.get().getType());
    }

    Optional<VariableSymbol> varSym = resolveGlobalVar(node.getName(), smtAut.getComponent());
    if (varSym.isPresent() && expr == null) {
      expr = smtAut.getProperty(state, varSym.get());
      type = (Z3TypeAdapter) tFactory.adapt(varSym.get().getType());
    }

    if (expr != null && type != null) {
      return new Z3ExprAdapter(expr, type);
    }

    Log.error(
        String.format(
            "Symbol %s at position %s Not found in the Montiarc components\n",
            node.getName(), MappingUtil.printPosition(node)));

    assert false;
    return null;
  }

  public Z3ExprAdapter convertSeq(ASTSeqExpression node, Sort sort) {

    if (node.getExpressionList().isEmpty()) {
      Optional<Z3TypeAdapter> elmType = tFactory.adaptQName(sort.toString());
      return new Z3ExprAdapter(ctx.mkEmptySeq(sort), tFactory.mkType("seq", sort, ExprKind.SEQ));
    }
    Expr<?> expr =
        ctx.mkConcat(
            node.streamExpressions()
                .map(e -> ctx.mkUnit(convertExpr(e).getExpr()))
                .toArray(Expr[]::new));
    return new Z3ExprAdapter(expr, tFactory.mkType("seq", sort, ExprKind.SEQ));
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
    // case: state == Known
    if (right instanceof ASTNameExpression) {

      Optional<SCStateSymbol> stateSym =
          resolveState(((ASTNameExpression) right).getName(), smtAut.getComponent());
      if (stateSym.isPresent()) {
        BoolExpr expr = smtAut.checkConstructor(state, stateSym.get());
        return new Z3ExprAdapter(expr, tFactory.mkBoolType());
      }
    }

    Z3ExprAdapter cond = eFactory.mkBool(true);
    // case: port == value or var == value
    if (left instanceof ASTNameExpression) {
      Optional<PortSymbol> portSym =
          resolvePort(((ASTNameExpression) left).getName(), smtAut.getComponent());
      if (portSym.isPresent() && portSym.get().isIncoming()) {
        BoolExpr expr = smtAut.checkConstructor(getInput(), portSym.get());
        cond = new Z3ExprAdapter(expr, tFactory.mkBoolType());
      }
    }

    Z3ExprAdapter leftExpr = convertExpr(left);
    Z3ExprAdapter rightExpr;

    if (right instanceof ASTSeqExpression) {
      rightExpr = convertSeq((ASTSeqExpression) right, ctx.mkSeqSort(leftExpr.getType().getSort()));
    } else {
      rightExpr = convertExpr(right);
    }

    return eFactory.mkAnd(eFactory.mkEq(leftExpr, rightExpr), cond);
  }

  @Override
  protected Z3ExprAdapter convert(ASTFieldAccessExpression node) {
    Z3TypeFactory z3TFactory = tFactory;

    String enumConstName = MontiArcMill.prettyPrint(node, false);
    String cdName = smtAut.getCDAst().getCDDefinition().getName();
    String packageName =
        smtAut.getCDAst().getMCPackageDeclaration().getMCQualifiedName().getQName();
    String enumConstFullName = packageName + "." + cdName + "." + enumConstName;

    Optional<FieldSymbol> enumConst = resolveEnumConst(enumConstFullName, smtAut.getCDAst());
    if (enumConst.isPresent()) {
      Expr<?> expr = smtAut.mkConst(enumConst.get());
      return new Z3ExprAdapter(
          expr, z3TFactory.mkType(enumConstName, expr.getSort(), ExprKind.ENUM));
    } else {
      String cdFile = smtAut.getCDAst().get_SourcePositionStart().getFileName().orElse(cdName);
      Log.error("Enum  constant " + enumConstName + " Not found in the class diagram " + cdFile);
      assert false;
      return null;
    }
  }

  public void setSate(Expr<?> state) {
    this.state = isReference ? mapping.mapState(state) : state;
  }

  public Expr<?> getInput() {
    return isReference ? mapping.mapInput(in) : in;
  }

  public Expr<?> getOutput() {
    return isReference ? mapping.mapOutput(out) : out;
  }
}
