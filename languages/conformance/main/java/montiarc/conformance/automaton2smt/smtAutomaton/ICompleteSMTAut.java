/* (c) https://github.com/MontiCore/monticore */
package montiarc.conformance.automaton2smt.smtAutomaton;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Expr;

public interface ICompleteSMTAut {
  BoolExpr evaluate(Expr<?> in, Expr<?> state, Expr<?> nextState, Expr<?> out);
}
