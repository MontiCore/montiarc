/* (c) https://github.com/MontiCore/monticore */
package arcbasis.helper;

import arcbasis._symboltable.IArcBasisScope;
import arcbasis._symboltable.PortSymbol;
import arcbasis.visitor.GuardExpressionVisitor;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.expressions.prettyprint.ExpressionsBasisPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExpressionUtil {
  public static List<PortSymbol> getPortsInGuardExpression(ASTExpression node) {
    List<PortSymbol> ports = new ArrayList<>();

    for (ASTNameExpression guardExpressionElement : getGuardExpressionElements(node)) {
      String name = guardExpressionElement.getName();
      IArcBasisScope s = (IArcBasisScope) node.getEnclosingScope();
      Optional<PortSymbol> port = s.resolvePort(name);
      port.ifPresent(ports::add);
    }
    return ports;
  }

  public static String printExpression(ASTExpression node) {
    IndentPrinter printer = new IndentPrinter();
    ExpressionsBasisPrettyPrinter prettyPrinter = new ExpressionsBasisPrettyPrinter(printer);
    node.accept(prettyPrinter);
    return printer.getContent();
  }

  /**
   * Returns all NameExpressions that appear in the guard of the execution statement
   *
   * @param node
   * @return
   */
  private static List<ASTNameExpression> getGuardExpressionElements(ASTExpression node) {
    GuardExpressionVisitor visitor = new GuardExpressionVisitor();
    node.accept(visitor);
    return visitor.getExpressions();
  }
}
