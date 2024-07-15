/* (c) https://github.com/MontiCore/monticore */
package montiarc.conformance.automaton2smt.smtAutomaton;

import arcbasis._ast.ASTComponentType;
import com.microsoft.z3.*;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.scbasis._ast.ASTSCTransition;
import de.monticore.symbols.compsymbols._symboltable.PortSymbol;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symboltable.ISymbol;
import java.util.Map;

public interface ISMTAutomaton {
  /***
   * This method evaluates the input part of a transition.
   * @param transition the transition as AST.
   * @param in the input
   * @param src the current src of the automaton
   * @return the evaluation as boolExpr
   */
  BoolExpr evaluateGuard(ASTSCTransition transition, Expr<?> in, Expr<?> src);

  /***
   * This method evaluates the action part of a transition.
   * @param trans The transition as AST.
   * @param in The input of the transition
   * @param src the current src
   * @param tgt the next src
   * @param out the outputs of the transition
   * @return the evaluation as bool expression
   */
  BoolExpr evaluateAction(ASTSCTransition trans, Expr<?> in, Expr<?> src, Expr<?> tgt, Expr<?> out);

  /***
   * This method evaluates a transition
   * @param trans The transition as AST.
   * @param in The input of the transition
   * @param src the current src
   * @param tgt the next src
   * @param out the outputs of the transition
   * @return the evaluation as bool expression
   */
  BoolExpr evaluateTransition(
      ASTSCTransition trans, Expr<?> in, Expr<?> src, Expr<?> tgt, Expr<?> out);

  /***
   * @return the Sort defined for states
   */
  Sort getStateSort();

  /***
   * @return the Sort defined for inputs values
   */
  Sort getInputSort();

  /***
   * @return the Sort defined for outputs
   */
  Sort getOutputSort();

  /***
   * @return Human Readable String with all Information.
   */
  String print(Expr<?> expr);

  /***
   * build an Input, Output or state constant.
   * @param constrSymbol the symbol of the constructor to use.
   * @param args arguments of the constructor.
   */
  Expr<?> mkConst(ISymbol constrSymbol, Map<ISymbol, Expr<?>> args);

  BoolExpr checkConstructor(Expr<?> expr, ISymbol symbol);

  /***
   * evaluate an expression to get property.
   * @param expr expression to evaluate.
   * @param property symbol corresponding to the property.
   */
  Expr<?> getProperty(Expr<?> expr, ISymbol property);

  /***
   * make a constant from a field symbol (Enum constant).
   * @param fieldSymbol a field symbol (symbol of an enum constant).
   */
  Expr<?> mkConst(FieldSymbol fieldSymbol);

  /**
   * @return the ast of the ISMTAutomaton.
   */
  ASTComponentType getComponent();

  /**
   * @return the class diagram containing the datatypes.
   */
  ASTCDCompilationUnit getCDAst();

  /***
   * return the SMT sort corresponding to the type of port.
   * @param port the port symbol of the port
   */
  Sort getSort(PortSymbol port);

  /***
   * return the SMT sort corresponding to the type of ASTCDEnum.
   * @param astcdEnum the ASTCDEnum.
   */
  Sort getSort(ASTCDEnum astcdEnum);
}
