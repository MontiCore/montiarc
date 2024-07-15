/* (c) https://github.com/MontiCore/monticore */
package scmapping.mapping2smt;

import com.microsoft.z3.*;
import montiarc.conformance.automaton2smt.smtAutomaton.ISMTAutomaton;

public class SMTStringMapping implements AutomataMapping {
  final String mapping;
  protected FuncDecl<?> mapInput;
  protected FuncDecl<?> mapOutput;
  protected FuncDecl<?> mapState;

  public SMTStringMapping(String mapping) {
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
    Sort s_con = con.getStateSort();
    Sort in_con = con.getInputSort();
    Sort out_con = con.getOutputSort();

    Sort s_ref = ref.getStateSort();
    Sort in_ref = ref.getInputSort();
    Sort out_ref = ref.getOutputSort();

    mapInput = ctx.mkFuncDecl("map_input", con.getInputSort(), ref.getInputSort());
    mapOutput =
        ctx.mkFuncDecl(
            "map_output", ctx.mkSeqSort(con.getOutputSort()), ctx.mkSeqSort(ref.getOutputSort()));
    mapState = ctx.mkFuncDecl("map_state", con.getStateSort(), ref.getStateSort());

    Symbol[] sortNames = {
      s_con.getName(),
      s_ref.getName(),
      in_con.getName(),
      in_ref.getName(),
      out_con.getName(),
      out_ref.getName()
    };
    Sort[] sorts = {s_con, s_ref, in_con, in_ref, out_con, out_ref};

    FuncDecl<?>[] funcDecls = {
      mapState,
      mapInput,
      mapOutput,
      in.getFuncDecl(),
      out.getFuncDecl(),
      src.getFuncDecl(),
      tgt.getFuncDecl()
    };
    Symbol[] funcNames = {
      mapState.getName(),
      mapInput.getName(),
      mapOutput.getName(),
      in.getFuncDecl().getName(),
      out.getFuncDecl().getName(),
      src.getFuncDecl().getName(),
      tgt.getFuncDecl().getName()
    };
    return ctx.parseSMTLIB2String(mapping, sortNames, sorts, funcNames, funcDecls);
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
