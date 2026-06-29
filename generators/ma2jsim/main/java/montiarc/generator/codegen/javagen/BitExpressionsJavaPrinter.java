/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator.codegen.javagen;

import de.monticore.expressions.bitexpressions._prettyprint.BitExpressionsPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;

@Deprecated
public class BitExpressionsJavaPrinter extends BitExpressionsPrettyPrinter {

  public BitExpressionsJavaPrinter(IndentPrinter printer) {
    super(printer, false);
  }

  public void handle(de.monticore.expressions.bitexpressions._ast.ASTLeftShiftExpression node) {
    getPrinter().print("(");
    node.getLeft().accept(getTraverser());
    getPrinter().stripTrailing();
    getPrinter().print(")");
    getPrinter().print("<<");
    getPrinter().print("(");
    node.getRight().accept(getTraverser());
    getPrinter().print(")");
  }

  public void handle(de.monticore.expressions.bitexpressions._ast.ASTRightShiftExpression node) {
    getPrinter().print("(");
    node.getLeft().accept(getTraverser());
    getPrinter().stripTrailing();
    getPrinter().print(")");
    getPrinter().print(">>");
    getPrinter().print("(");
    node.getRight().accept(getTraverser());
    getPrinter().print(")");
  }

  public void handle(de.monticore.expressions.bitexpressions._ast.ASTLogicalRightShiftExpression node) {
    getPrinter().print("(");
    node.getLeft().accept(getTraverser());
    getPrinter().stripTrailing();
    getPrinter().print(")");
    getPrinter().print(">>>");
    getPrinter().print("(");
    node.getRight().accept(getTraverser());
    getPrinter().print(")");
  }

  public void handle(de.monticore.expressions.bitexpressions._ast.ASTBinaryAndExpression node) {
    getPrinter().print("(");
    node.getLeft().accept(getTraverser());
    getPrinter().stripTrailing();
    getPrinter().print(")");
    getPrinter().print("&");
    getPrinter().print("(");
    node.getRight().accept(getTraverser());
    getPrinter().print(")");
  }

  public void handle(de.monticore.expressions.bitexpressions._ast.ASTBinaryXorExpression node) {
    getPrinter().print("(");
    node.getLeft().accept(getTraverser());
    getPrinter().stripTrailing();
    getPrinter().print(")");
    getPrinter().print("^");
    getPrinter().print("(");
    node.getRight().accept(getTraverser());
    getPrinter().print(")");
  }

  public void handle(de.monticore.expressions.bitexpressions._ast.ASTBinaryOrOpExpression node) {
    getPrinter().print("(");
    node.getLeft().accept(getTraverser());
    getPrinter().stripTrailing();
    getPrinter().print(")");
    getPrinter().print("|");
    getPrinter().print("(");
    node.getRight().accept(getTraverser());
    getPrinter().print(")");
  }
}
