/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator.ma2kotlin.prettyprint;

import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.javaclassexpressions._ast.*;
import de.monticore.expressions.prettyprint.JavaClassExpressionsPrettyPrinter;
import de.monticore.prettyprint.CommentPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;

import java.util.List;

public class JavaClassExpressionsPrinter extends JavaClassExpressionsPrettyPrinter {
  ASTExtTypeExt arrayType = null;

  public JavaClassExpressionsPrinter(IndentPrinter printer) {
    super(printer);
  }

  @Override
  public void handle(ASTSuperExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    getPrinter().print("super@");
    node.getExpression().accept(getTraverser());
    node.getSuperSuffix().accept(getTraverser());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  @Override
  public void handle(ASTClassExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    getPrinter().print("Class.forName(\"");
    node.getExtReturnType().accept(getTraverser());
    getPrinter().print("\")");
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  @Override
  public void handle(ASTTypeCastExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    Preconditions.checkNotNull(node);
    node.getExpression().accept(getTraverser());
    getPrinter().print(" as ");
    node.getExtType().accept(getTraverser());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  /**
   * overridden by {@link #handle(ASTGenericInvocationExpression)}
   */
  @Override
  public void handle(ASTGenericInvocationSuffix node) {
    throw new IllegalStateException();
  }

  @Override
  public void handle(ASTGenericInvocationExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    // print left side
    node.getExpression().accept(getTraverser());
    getPrinter().print(".");
    // print method name (or super/this for constructor calls)
    ASTGenericInvocationSuffix method = node.getPrimaryGenericInvocationExpression().getGenericInvocationSuffix();
    getPrinter().print(method.isSuper() ? "super" : method.isThis() ? "this" : method.getName());
    // print generic arguments
    String comma = "<";
    for (ASTExtTypeArgumentExt type : node.getPrimaryGenericInvocationExpression().getExtTypeArgumentList()) {
      getPrinter().print(comma);
      comma = ",";
      type.accept(getTraverser());
    }
    getPrinter().print(">");
    // print round-bracket-arguments
    if (method.isSuper()) {
      method.getSuperSuffix().accept(getTraverser());
    } else {
      method.getArguments().accept(getTraverser());
    }
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  /**
   * overridden by {@link #handle(ASTGenericInvocationExpression)}
   */
  @Override
  public void handle(ASTPrimaryGenericInvocationExpression node) {
    throw new IllegalStateException();
  }

  @Override
  public void handle(ASTInstanceofExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    node.getExpression().accept(getTraverser());
    getPrinter().print(" is ");
    node.getExtType().accept(getTraverser());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  @Override
  public void handle(ASTThisExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    getPrinter().print("this@");
    node.getExpression().accept(getTraverser());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  @Override
  public void handle(ASTArrayCreator a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    arrayType = a.getExtType();
    a.getArrayDimensionSpecifier().accept(getTraverser());
    arrayType = null;
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  @Override
  public void handle(ASTArrayDimensionByExpression a) {
    // = new int[6][5][4][][]
    // = Array(6){Array(5){arrayOfNulls<Array<Array<Int>>>(4)}}
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    // copy array type to local variable, because you never know what happens in a deeper branch of the visit-tree
    ASTExtTypeExt type = Preconditions.checkNotNull(this.arrayType);
    List<ASTExpression> sizes = a.getExpressionList();
    List<ASTExpression> firstSizes = a.getExpressionList().subList(0, sizes.size() - 1);
    for (ASTExpression size : firstSizes) {
      getPrinter().print("Array(");
      size.accept(getTraverser());
      getPrinter().print(") {");
    }
    getPrinter().print("arrayOfNulls<");
    for (String brace : a.getDimList()) {
      getPrinter().print("Array<");
    }
    type.accept(getTraverser());
    for (String brace : a.getDimList()) {
      getPrinter().print(">");
    }
    getPrinter().print("(");
    sizes.get(firstSizes.size()).accept(getTraverser());
    getPrinter().print(")");
    for (ASTExpression size : firstSizes) {
      getPrinter().print("}");
    }
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  @Override
  public void handle(ASTCreatorExpression a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    // kotlin does not use "new"
    getPrinter().print(" ");
    a.getCreator().accept(getTraverser());
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }
}
