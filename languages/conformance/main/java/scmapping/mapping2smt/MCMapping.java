/* (c) https://github.com/MontiCore/monticore */
package scmapping.mapping2smt;

import com.microsoft.z3.*;
import montiarc.conformance.automaton2smt.smtAutomaton.ISMTAutomaton;
import de.monticore.ocl2smt.ocl2smt.expr2smt.expr2z3.Z3ExprFactory;
import java.util.*;
import scmapping._ast.ASTMappingRule;
import scmapping._ast.ASTSCMapping;
import scmapping.util.SCZ3TypeFactory;

public class MCMapping implements AutomataMapping {
  protected MapExpression2smt refExprConv;
  protected MapExpression2smt conExprConv;
  protected FuncDecl<?> mapInput;
  protected FuncDecl<?> mapOutput;
  protected FuncDecl<?> mapState;

  protected ASTSCMapping mapping;

  public MCMapping(ASTSCMapping mapping) {
    this.mapping = mapping;
  }

  @Override
  public BoolExpr[] init(
      ISMTAutomaton con,
      ISMTAutomaton ref,
      Expr<?> in,
      Expr<?> out,
      Expr<?> src,
      Expr<?> tgt,
      Context ctx) {

    Set<BoolExpr> mappingRules = new HashSet<>();

    // build mapping functions
    mapInput = ctx.mkFuncDecl("map_input", con.getInputSort(), ref.getInputSort());
    mapState = ctx.mkFuncDecl("map_state", con.getStateSort(), ref.getStateSort());
    mapOutput = ctx.mkFuncDecl("map_output", con.getOutputSort(), ref.getOutputSort());

    SCZ3TypeFactory refTFactory = new SCZ3TypeFactory(ref.getCDAst(), ref::getSort, ctx);
    SCZ3TypeFactory conTFactory = new SCZ3TypeFactory(con.getCDAst(), con::getSort, ctx);
    Z3ExprFactory conEFactory = new Z3ExprFactory(conTFactory, ctx);
    Z3ExprFactory refEFactory = new Z3ExprFactory(refTFactory, ctx);
    refExprConv =
        new MapExpression2smt(refEFactory, refTFactory, ref, this, in, src, out, ctx, true);
    conExprConv =
        new MapExpression2smt(conEFactory, conTFactory, con, this, in, src, out, ctx, false);

    // convert input and output to smt
    mapping.getInputRules().forEach(rule -> mappingRules.add(rule2smt(ctx, rule, src)));
    mapping.getOutputRules().forEach(rule -> mappingRules.add(rule2smt(ctx, rule, src)));

    // convert state rules
    mapping.getStateRules().forEach(rule -> mappingRules.add(rule2smt(ctx, rule, src)));
    mapping.getStateRules().forEach(rule -> mappingRules.add(rule2smt(ctx, rule, tgt)));

    return mappingRules.toArray(new BoolExpr[0]);
  }

  private BoolExpr rule2smt(Context ctx, ASTMappingRule rule, Expr<?> state) {

    conExprConv.setSate(state);
    refExprConv.setSate(state);
    BoolExpr d = conExprConv.convertBoolExpr(rule.getConcrete());
    BoolExpr f = refExprConv.convertBoolExpr(rule.getReference());
    return ctx.mkImplies(d, f);
  }

  @Override
  public Expr<?> mapInput(Expr<?> input) {
    return mapInput.apply(input);
  }

  @Override
  public Expr<?> mapOutput(Expr<?> output) {
    return mapOutput.apply(output);
  }

  @Override
  public Expr<?> mapState(Expr<?> state) {
    return mapState.apply(state);
  }
}
