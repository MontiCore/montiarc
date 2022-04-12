/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator.ma2kotlin.prettyprint;

import de.monticore.expressions.bitexpressions._ast.*;
import de.monticore.expressions.prettyprint.BitExpressionsPrettyPrinter;
import de.monticore.prettyprint.CommentPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;

public class BitExpressionsPrinter extends BitExpressionsPrettyPrinter {

  public BitExpressionsPrinter(IndentPrinter printer) {
    super(printer);
  }

  @Override
  public void handle(ASTLeftShiftExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    node.getLeft().accept(getTraverser());
    getPrinter().print("shl");
    node.getRight().accept(getTraverser());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  @Override
  public void handle(ASTRightShiftExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    node.getLeft().accept(getTraverser());
    getPrinter().print("shr");
    node.getRight().accept(getTraverser());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  @Override
  public void handle(ASTLogicalRightShiftExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    node.getLeft().accept(getTraverser());
    getPrinter().print("ushr");
    node.getRight().accept(getTraverser());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  @Override
  public void handle(ASTBinaryAndExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    node.getLeft().accept(getTraverser());
    getPrinter().print(" and ");
    node.getRight().accept(getTraverser());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  @Override
  public void handle(ASTBinaryXorExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    node.getLeft().accept(getTraverser());
    getPrinter().print(" xor ");
    node.getRight().accept(getTraverser());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  @Override
  public void handle(ASTBinaryOrOpExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    node.getLeft().accept(getTraverser());
    getPrinter().print(" or ");
    node.getRight().accept(getTraverser());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }
}
