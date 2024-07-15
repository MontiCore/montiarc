/* (c) https://github.com/MontiCore/monticore */
package montiarc.conformance.automaton2smt.sort;

import com.microsoft.z3.*;
import de.monticore.symboltable.ISymbol;
import java.util.Map;

/***
 * This interface is the common interface to define custom data-types representing
 * states, inputs and outputs of a statechart in SMT.
 */
public interface SMTSort<ConstructorSymbol extends ISymbol, PropertySymbol extends ISymbol> {
  /***
   * @return the sort defined in SMT.
   */
  DatatypeSort<?> getSort();

  /***
   * This method declares a constant in SMT that has as type the SMT Sort.
   * @param constructor symbol representing the constructor to use the build the SMT-expr.
   * @return the expression.
   */
  Expr<?> mkConst(ConstructorSymbol constructor, Map<PropertySymbol, Expr<?>> vars);

  /***
   * This method accesses the property of an SMT-expr
   * @param expr the SMT-expr.
   * @param property the symbol of the SMT-expr.
   * @return the accessed property.
   */
  Expr<?> getProperty(Expr<?> expr, PropertySymbol property);

  /***
   * return a bool expression that checks if the expression was build with he constructor
   * corresponding to constructor "constructor"
   * @param expr the expression.
   * @param constructor symbol corresponding to the constructor
   */
  BoolExpr checkConstructor(Expr<?> expr, ConstructorSymbol constructor);

  /***
   * print SMTExpression to a Human Readable String.
   * @param expr expression to print.
   * @return the Human readable String.
   */
  String print(Expr<?> expr);
}
