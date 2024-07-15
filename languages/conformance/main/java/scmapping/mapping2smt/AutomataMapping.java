/* (c) https://github.com/MontiCore/monticore */
package scmapping.mapping2smt;

import com.microsoft.z3.*;
import montiarc.conformance.automaton2smt.smtAutomaton.ISMTAutomaton;

public interface AutomataMapping {

  BoolExpr[] init(
      ISMTAutomaton concrete,
      ISMTAutomaton reference,
      Expr<?> input,
      Expr<?> output,
      Expr<?> src,
      Expr<?> tgt,
      Context ctx);

  Expr<?> mapInput(Expr<?> input);

  Expr<?> mapOutput(Expr<?> output);

  Expr<?> mapState(Expr<?> state);
}
