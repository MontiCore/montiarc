/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator.dse;

import com.google.common.base.Preconditions;
import de.monticore.expressions.commonexpressions._ast.ASTCallExpression;
import de.monticore.expressions.commonexpressions._ast.ASTFieldAccessExpression;
import de.monticore.expressions.commonexpressions._prettyprint.CommonExpressionsPrettyPrinter;
import de.monticore.prettyprint.CommentPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.symbols.oosymbols._symboltable.MethodSymbol;
import de.monticore.symboltable.ISymbol;
import montiarc.generator.helper.dse.ComponentHelperDseValue;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.Optional;

public class DseCommonExpressionsJavaPrinter extends CommonExpressionsPrettyPrinter {
  ComponentHelperDseValue componentHelperDseValue;

  public DseCommonExpressionsJavaPrinter(@NotNull IndentPrinter printer, boolean printComments) {
    super(printer, printComments);
    this.componentHelperDseValue = new ComponentHelperDseValue();
  }

  @Override
  public void handle(@NotNull ASTCallExpression node) {
    Preconditions.checkNotNull(node);
    if (this.isPrintComments()) {
      CommentPrettyPrinter.printPreComments(node, getPrinter());
    }

    Optional<ISymbol> symbol = node.getDefiningSymbol();
    if (symbol.isPresent()
      && symbol.get() instanceof MethodSymbol
      && ((MethodSymbol) symbol.get()).isIsConstructor()
    ) {
      Preconditions.checkState(node.getExpression() instanceof ASTFieldAccessExpression);
      this.getPrinter().print("new ");
      // At the moment we need to call the Name of the class to instantiate as a qualification
      // before the constructor
      // call in MontiArc models. But Java does not do this, so we have to strip this qualification.
      // See https://git.rwth-aachen.de/monticore/montiarc/core/-/merge_requests/677
      ((ASTFieldAccessExpression) node.getExpression()).getExpression().accept(getTraverser());
    }
    else {
      node.getExpression().accept(getTraverser());
    }
    node.getArguments().accept(getTraverser());
    if (this.isPrintComments()) {
      CommentPrettyPrinter.printPostComments(node, getPrinter());
    }
  }

  @Override
  public void handle(de.monticore.expressions.commonexpressions._ast.ASTPlusExpression node) {
    if (this.isPrintComments()) {
      CommentPrettyPrinter.printPreComments(node, getPrinter());
    }
    getPrinter().stripTrailing();

    getPrinter().print("ctx.mkAdd(");
    node.getLeft().accept(getTraverser());

    getPrinter().print(",");

    node.getRight().accept(getTraverser());
    getPrinter().print(")");

    if (this.isPrintComments()) {
      CommentPrettyPrinter.printPostComments(node, getPrinter());
    }
  }

  @Override
  public void handle(de.monticore.expressions.commonexpressions._ast.ASTMinusExpression node) {
    if (this.isPrintComments()) {
      CommentPrettyPrinter.printPreComments(node, getPrinter());
    }
    getPrinter().stripTrailing();

    getPrinter().print("ctx.mkSub(");
    node.getLeft().accept(getTraverser());

    getPrinter().print(",");

    node.getRight().accept(getTraverser());
    getPrinter().print(")");

    if (this.isPrintComments()) {
      CommentPrettyPrinter.printPostComments(node, getPrinter());
    }
  }

  @Override
  public void handle(de.monticore.expressions.commonexpressions._ast.ASTMultExpression node) {
    if (this.isPrintComments()) {
      CommentPrettyPrinter.printPreComments(node, getPrinter());
    }
    getPrinter().stripTrailing();

    getPrinter().print("ctx.mkMul(");
    node.getLeft().accept(getTraverser());

    getPrinter().print(",");

    node.getRight().accept(getTraverser());
    getPrinter().print(")");


    if (this.isPrintComments()) {
      CommentPrettyPrinter.printPostComments(node, getPrinter());
    }
  }

  @Override
  public void handle(de.monticore.expressions.commonexpressions._ast.ASTDivideExpression node) {
    if (this.isPrintComments()) {
      CommentPrettyPrinter.printPreComments(node, getPrinter());
    }
    getPrinter().stripTrailing();

    getPrinter().print("ctx.mkDiv(");
    node.getLeft().accept(getTraverser());

    getPrinter().print(",");

    node.getRight().accept(getTraverser());
    getPrinter().print(")");

    if (this.isPrintComments()) {
      CommentPrettyPrinter.printPostComments(node, getPrinter());
    }
  }

  @Override
  public void handle(de.monticore.expressions.commonexpressions._ast.ASTEqualsExpression node) {
    if (this.isPrintComments()) {
      CommentPrettyPrinter.printPreComments(node, getPrinter());
    }
    getPrinter().stripTrailing();

    getPrinter().print("ctx.mkEq(");
    node.getLeft().accept(getTraverser());

    getPrinter().print(",");

    node.getRight().accept(getTraverser());
    getPrinter().print(")");

    if (this.isPrintComments()) {
      CommentPrettyPrinter.printPostComments(node, getPrinter());
    }
  }

  @Override
  public void handle(de.monticore.expressions.commonexpressions._ast.ASTLogicalNotExpression node) {

    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPreComments(node, getPrinter());
    }
    getPrinter().stripTrailing();

    getPrinter().print("ctx.mkNot(");
    node.getExpression().accept(getTraverser());
    getPrinter().print(")");

    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPostComments(node, getPrinter());
    }
  }

  @Override
  public void handle(de.monticore.expressions.commonexpressions._ast.ASTLessEqualExpression node) {
    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPreComments(node, getPrinter());
    }
    getPrinter().stripTrailing();

    getPrinter().print("ctx.mkLe(");
    node.getLeft().accept(getTraverser());

    getPrinter().print(",");
    node.getRight().accept(getTraverser());
    getPrinter().print(")");

    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPostComments(node, getPrinter());
    }
  }

  @Override
  public void handle(de.monticore.expressions.commonexpressions._ast.ASTGreaterEqualExpression node) {
    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPreComments(node, getPrinter());
    }
    getPrinter().stripTrailing();

    getPrinter().print("ctx.mkGe(");
    node.getLeft().accept(getTraverser());

    getPrinter().print(",");
    node.getRight().accept(getTraverser());
    getPrinter().print(")");

    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPostComments(node, getPrinter());
    }
  }

  @Override
  public void handle(de.monticore.expressions.commonexpressions._ast.ASTLessThanExpression node) {
    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPreComments(node, getPrinter());
    }

    getPrinter().stripTrailing();

    getPrinter().print("ctx.mkLt(");
    node.getLeft().accept(getTraverser());

    getPrinter().print(",");
    node.getRight().accept(getTraverser());
    getPrinter().print(")");

    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPostComments(node, getPrinter());
    }
  }

  @Override
  public void handle(de.monticore.expressions.commonexpressions._ast.ASTGreaterThanExpression node) {
    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPreComments(node, getPrinter());
    }

    getPrinter().stripTrailing();

    getPrinter().print("ctx.mkGt(");
    node.getLeft().accept(getTraverser());

    getPrinter().print(",");
    node.getRight().accept(getTraverser());
    getPrinter().print(")");

    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPostComments(node, getPrinter());
    }
  }

  @Override
  public void handle(de.monticore.expressions.commonexpressions._ast.ASTNotEqualsExpression node) {
    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPreComments(node, getPrinter());
    }
    getPrinter().stripTrailing();

    getPrinter().print("ctx.mkNot(");
    getPrinter().print("ctx.mkEq(");
    node.getLeft().accept(getTraverser());

    getPrinter().print(",");

    node.getRight().accept(getTraverser());
    getPrinter().print(")");
    getPrinter().print(")");

    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPostComments(node, getPrinter());
    }
  }

  @Override
  public void handle(de.monticore.expressions.commonexpressions._ast.ASTBooleanAndOpExpression node) {
    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPreComments(node, getPrinter());
    }
    getPrinter().stripTrailing();

    getPrinter().print("ctx.mkAnd(");
    node.getLeft().accept(getTraverser());

    getPrinter().print(",");
    node.getRight().accept(getTraverser());
    getPrinter().print(")");

    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPostComments(node, getPrinter());
    }
  }

  @Override
  public void handle(de.monticore.expressions.commonexpressions._ast.ASTBooleanOrOpExpression node) {
    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPreComments(node, getPrinter());
    }

    getPrinter().stripTrailing();

    getPrinter().print("ctx.mkOr()");
    node.getLeft().accept(getTraverser());

    getPrinter().print(",");
    node.getRight().accept(getTraverser());
    getPrinter().print(")");

    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPostComments(node, getPrinter());
    }
  }
}




