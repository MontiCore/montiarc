/* (c) https://github.com/MontiCore/monticore */
package montiarc._prettyprint;

import de.monticore.expressions.commonexpressions._ast.ASTBooleanAndOpExpression;
import de.monticore.expressions.commonexpressions._ast.ASTBooleanOrOpExpression;
import de.monticore.expressions.commonexpressions._ast.ASTBracketExpression;
import de.monticore.expressions.commonexpressions._ast.ASTConditionalExpression;
import de.monticore.expressions.commonexpressions._ast.ASTDivideExpression;
import de.monticore.expressions.commonexpressions._ast.ASTEqualsExpression;
import de.monticore.expressions.commonexpressions._ast.ASTGreaterEqualExpression;
import de.monticore.expressions.commonexpressions._ast.ASTGreaterThanExpression;
import de.monticore.expressions.commonexpressions._ast.ASTLessEqualExpression;
import de.monticore.expressions.commonexpressions._ast.ASTLessThanExpression;
import de.monticore.expressions.commonexpressions._ast.ASTMinusExpression;
import de.monticore.expressions.commonexpressions._ast.ASTModuloExpression;
import de.monticore.expressions.commonexpressions._ast.ASTMultExpression;
import de.monticore.expressions.commonexpressions._ast.ASTNotEqualsExpression;
import de.monticore.expressions.commonexpressions._ast.ASTPlusExpression;
import de.monticore.expressions.commonexpressions._prettyprint.CommonExpressionsPrettyPrinter;
import de.monticore.prettyprint.CommentPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import org.codehaus.commons.nullanalysis.NotNull;

/**
 * Improved CommonExpressions Pretty Printer that adds spaces around binary operators.
 */
public class MACommonExpressionsPrettyPrinter extends CommonExpressionsPrettyPrinter {

  public MACommonExpressionsPrettyPrinter(@NotNull IndentPrinter printer, boolean printComments) {
    super(printer, printComments);
  }

  @Override
  public void handle(ASTPlusExpression node) {
    if (this.isPrintComments()) {
      CommentPrettyPrinter.printPreComments(node, getPrinter());
    }
    node.getLeft().accept(getTraverser());
    getPrinter().stripTrailing();
    getPrinter().print(" + ");
    node.getRight().accept(getTraverser());
    if (this.isPrintComments()) {
      CommentPrettyPrinter.printPostComments(node, getPrinter());
    }
  }

  @Override
  public void handle(ASTMinusExpression node) {
    if (this.isPrintComments()) {
      CommentPrettyPrinter.printPreComments(node, getPrinter());
    }
    node.getLeft().accept(getTraverser());
    getPrinter().stripTrailing();
    getPrinter().print(" - ");
    node.getRight().accept(getTraverser());
    if (this.isPrintComments()) {
      CommentPrettyPrinter.printPostComments(node, getPrinter());
    }
  }

  @Override
  public void handle(ASTMultExpression node) {
    if (this.isPrintComments()) {
      CommentPrettyPrinter.printPreComments(node, getPrinter());
    }
    node.getLeft().accept(getTraverser());
    getPrinter().stripTrailing();
    getPrinter().print(" * ");
    node.getRight().accept(getTraverser());
    if (this.isPrintComments()) {
      CommentPrettyPrinter.printPostComments(node, getPrinter());
    }
  }

  @Override
  public void handle(ASTDivideExpression node) {
    if (this.isPrintComments()) {
      CommentPrettyPrinter.printPreComments(node, getPrinter());
    }
    node.getLeft().accept(getTraverser());
    getPrinter().stripTrailing();
    getPrinter().print(" / ");
    node.getRight().accept(getTraverser());
    if (this.isPrintComments()) {
      CommentPrettyPrinter.printPostComments(node, getPrinter());
    }
  }

  @Override
  public void handle(ASTModuloExpression node) {
    if (this.isPrintComments()) {
      CommentPrettyPrinter.printPreComments(node, getPrinter());
    }
    node.getLeft().accept(getTraverser());
    getPrinter().stripTrailing();
    getPrinter().print(" % ");
    node.getRight().accept(getTraverser());
    if (this.isPrintComments()) {
      CommentPrettyPrinter.printPostComments(node, getPrinter());
    }
  }

  @Override
  public void handle(ASTEqualsExpression node) {
    if (this.isPrintComments()) {
      CommentPrettyPrinter.printPreComments(node, getPrinter());
    }
    node.getLeft().accept(getTraverser());
    getPrinter().stripTrailing();
    getPrinter().print(" == ");
    node.getRight().accept(getTraverser());
    if (this.isPrintComments()) {
      CommentPrettyPrinter.printPostComments(node, getPrinter());
    }
  }

  @Override
  public void handle(ASTNotEqualsExpression node) {
    if (this.isPrintComments()) {
      CommentPrettyPrinter.printPreComments(node, getPrinter());
    }
    node.getLeft().accept(getTraverser());
    getPrinter().stripTrailing();
    getPrinter().print(" != ");
    node.getRight().accept(getTraverser());
    if (this.isPrintComments()) {
      CommentPrettyPrinter.printPostComments(node, getPrinter());
    }
  }

  @Override
  public void handle(ASTLessThanExpression node) {
    if (this.isPrintComments()) {
      CommentPrettyPrinter.printPreComments(node, getPrinter());
    }
    node.getLeft().accept(getTraverser());
    getPrinter().stripTrailing();
    getPrinter().print(" < ");
    node.getRight().accept(getTraverser());
    if (this.isPrintComments()) {
      CommentPrettyPrinter.printPostComments(node, getPrinter());
    }
  }

  @Override
  public void handle(ASTGreaterThanExpression node) {
    if (this.isPrintComments()) {
      CommentPrettyPrinter.printPreComments(node, getPrinter());
    }
    node.getLeft().accept(getTraverser());
    getPrinter().stripTrailing();
    getPrinter().print(" > ");
    node.getRight().accept(getTraverser());
    if (this.isPrintComments()) {
      CommentPrettyPrinter.printPostComments(node, getPrinter());
    }
  }

  @Override
  public void handle(ASTLessEqualExpression node) {
    if (this.isPrintComments()) {
      CommentPrettyPrinter.printPreComments(node, getPrinter());
    }
    node.getLeft().accept(getTraverser());
    getPrinter().stripTrailing();
    getPrinter().print(" <= ");
    node.getRight().accept(getTraverser());
    if (this.isPrintComments()) {
      CommentPrettyPrinter.printPostComments(node, getPrinter());
    }
  }

  @Override
  public void handle(ASTGreaterEqualExpression node) {
    if (this.isPrintComments()) {
      CommentPrettyPrinter.printPreComments(node, getPrinter());
    }
    node.getLeft().accept(getTraverser());
    getPrinter().stripTrailing();
    getPrinter().print(" >= ");
    node.getRight().accept(getTraverser());
    if (this.isPrintComments()) {
      CommentPrettyPrinter.printPostComments(node, getPrinter());
    }
  }

  @Override
  public void handle(ASTBooleanAndOpExpression node) {
    if (this.isPrintComments()) {
      CommentPrettyPrinter.printPreComments(node, getPrinter());
    }
    node.getLeft().accept(getTraverser());
    getPrinter().stripTrailing();
    getPrinter().print(" && ");
    node.getRight().accept(getTraverser());
    if (this.isPrintComments()) {
      CommentPrettyPrinter.printPostComments(node, getPrinter());
    }
  }

  @Override
  public void handle(ASTBooleanOrOpExpression node) {
    if (this.isPrintComments()) {
      CommentPrettyPrinter.printPreComments(node, getPrinter());
    }
    node.getLeft().accept(getTraverser());
    getPrinter().stripTrailing();
    getPrinter().print(" || ");
    node.getRight().accept(getTraverser());
    if (this.isPrintComments()) {
      CommentPrettyPrinter.printPostComments(node, getPrinter());
    }
  }

  @Override
  public void handle(ASTBracketExpression node) {
    if (this.isPrintComments()) {
      CommentPrettyPrinter.printPreComments(node, getPrinter());
    }
    getPrinter().print("(");
    node.getExpression().accept(getTraverser());
    getPrinter().stripTrailing();
    getPrinter().print(")");
    if (this.isPrintComments()) {
      CommentPrettyPrinter.printPostComments(node, getPrinter());
    }
  }

  @Override
  public void handle(ASTConditionalExpression node) {
    if (this.isPrintComments()) {
      CommentPrettyPrinter.printPreComments(node, getPrinter());
    }
    node.getCondition().accept(getTraverser());
    getPrinter().stripTrailing();
    getPrinter().print(" ? ");
    node.getTrueExpression().accept(getTraverser());
    getPrinter().stripTrailing();
    getPrinter().print(" : ");
    node.getFalseExpression().accept(getTraverser());
    if (this.isPrintComments()) {
      CommentPrettyPrinter.printPostComments(node, getPrinter());
    }
  }
}
