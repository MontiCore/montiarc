/* (c) https://github.com/MontiCore/monticore */
package scmapping.mapping2smt;

import com.microsoft.z3.*;
import montiarc.conformance.automaton2smt.smtAutomaton.ISMTAutomaton;

public class IDMapping implements AutomataMapping {
  public static IDMapping getIDMapping() {
    return new IDMapping();
  }

  @Override
  public BoolExpr[] init(
      ISMTAutomaton concrete,
      ISMTAutomaton reference,
      Expr<?> in,
      Expr<?> out,
      Expr<?> src,
      Expr<?> tgt,
      Context ctx) {
    return new BoolExpr[0];
  }

  @Override
  public Expr<?> mapInput(Expr<?> input) {
    return input;
  }

  @Override
  public Expr<?> mapOutput(Expr<?> output) {
    return output;
  }

  @Override
  public Expr<?> mapState(Expr<?> state) {
    return state;
  }
}
