/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator.ma2kotlin.prettyprint;

import de.monticore.prettyprint.CommentPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.statements.mccommonstatements._ast.*;
import de.monticore.statements.prettyprint.MCCommonStatementsPrettyPrinter;

import java.util.stream.Stream;

public class CommonStatementsPrinter extends MCCommonStatementsPrettyPrinter {
  /**
   * A rudimentary helper to change ensure no duplicated names in nested switch-statements.
   * There is currently no mechanism that ensures, that the given names do not interfere with user-variable names.
   * But at the first level, they are java-keywords and deeper levels are rare.
   */
  protected int switchLevel = -1;

  public CommonStatementsPrinter(IndentPrinter printer) {
    super(printer);
  }

  // switch case related:

  @Override
  public void handle(ASTMCJavaBlock a) {
    // omit the braces, because kotlin does no equivalent structure
    // instead add the braces to every statement separately
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    a.getMCBlockStatementList().forEach(m -> m.accept(getTraverser()));
    CommentPrettyPrinter.printPostComments(a, getPrinter());
    // one could use lambda-blocks, but they do not allow breaking out of a loop
    // one could also use if(true){...} but that would be ugly
  }

  @Override
  public void handle(ASTSwitchStatement a) {
    switchLevel++;
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    // java's switch and kotlin's when are not equivalent, thus the statement has to be realized differently
    getPrinter().println("// switch - block");
    getPrinter().println("do {");
    getPrinter().indent();
    getPrinter().print("val " + getSwitch() + " = ");
    a.getExpression().accept(getTraverser());
    getPrinter().println();
    getPrinter().println("var " + getCase() + " = false");
    a.getSwitchBlockStatementGroupList().forEach(this::handle);
    a.getSwitchLabelList().forEach(this::handle);
    getPrinter().unindent();
    getPrinter().println("} while (false)");
    CommentPrettyPrinter.printPostComments(a, getPrinter());
    switchLevel--;
  }

  @Override
  public void handle(ASTSwitchBlockStatementGroup node) {
    node.getSwitchLabelList().forEach(s -> s.accept(getTraverser()));
    getPrinter().println("if (" + getCase() + ") {");
    getPrinter().indent();
    node.getMCBlockStatementList().forEach(s -> s.accept(getTraverser()));
    getPrinter().unindent();
    getPrinter().println("}");
  }

  @Override
  public void handle(ASTConstantExpressionSwitchLabel a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    getPrinter().print(String.format("%s = %s || %s == ", getCase(), getCase(), getSwitch()));
    a.getConstant().accept(getTraverser());
    getPrinter().println();
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  @Override
  public void handle(ASTEnumConstantSwitchLabel a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    getPrinter().print(String.format("%s = %s || %s == ", getCase(), getCase(), getSwitch()));
    getPrinter().println(a.getEnumConstant());
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  // for control

  @Override
  public void handle(ASTDefaultSwitchLabel a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    getPrinter().println("// default:");
    getPrinter().print(getCase());
    getPrinter().println(" = true");
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  @Override
  public void handle(ASTEnhancedForControl a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    a.getFormalParameter().accept(getTraverser());
    getPrinter().print(" in ");
    a.getExpression().accept(getTraverser());
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  @Override
  public void handle(ASTFormalParameter a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    a.getDeclaratorId().accept(getTraverser());
    // type is not needed, but it would be this:
    // getPrinter().print(" :");
    // a.getMCType().accept(getTraverser());
    // modifiers are not possible
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  @Override
  public void handle(ASTForInitByExpressions a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    a.getExpressionList().forEach(e -> e.accept(getTraverser()));
    getPrinter().println();
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  // add braces to other loop-like statements

  @Override
  public void handle(ASTForStatement a) {
    if (a.getForControl() instanceof ASTCommonForControl) {
      // for loop has to be flattened out
      ASTCommonForControl control = (ASTCommonForControl) a.getForControl();
      getPrinter().println("// for loop - initializations");
      control.getForInit().accept(getTraverser());
      getPrinter().println();
      getPrinter().println("// for loop as while loop");
      getPrinter().print("while (");
      control.getCondition().accept(getTraverser());
      getPrinter().println(") {");
      getPrinter().indent();
      getPrinter().println("// for loop - content");
      a.getMCStatement().accept(getTraverser());
      getPrinter().println("// for loop - increment");
      control.getExpressionList().forEach(e -> {
        e.accept(getTraverser());
        getPrinter().println();
      });
      getPrinter().unindent();
      getPrinter().println("}");
    } else {
      super.handle(a);
    }
  }

  @Override
  public void handle(ASTWhileStatement a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    getPrinter().print("while (");
    a.getCondition().accept(getTraverser());
    getPrinter().println(") {");
    getPrinter().indent();
    a.getMCStatement().accept(getTraverser());
    getPrinter().unindent();
    getPrinter().println("}");
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  @Override
  public void handle(ASTDoWhileStatement a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    getPrinter().println("do {");
    getPrinter().indent();
    a.getMCStatement().accept(getTraverser());
    getPrinter().unindent();
    getPrinter().print("} while (");
    a.getCondition().accept(getTraverser());
    getPrinter().println(")");
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  @Override
  public void handle(ASTIfStatement a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    getPrinter().print("if (");
    a.getCondition().accept(getTraverser());
    getPrinter().println(") {");
    getPrinter().indent();
    a.getThenStatement().accept(getTraverser());
    if (a.isPresentElseStatement()) {
      getPrinter().unindent();
      getPrinter().println("} else {");
      getPrinter().indent();
      a.getElseStatement().accept(getTraverser());
    }
    getPrinter().unindent();
    getPrinter().println("}");
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  /**
   * @return a variable name for the calculated value of the switch expression
   */
  protected String getSwitch() {
    return Stream.generate(() -> "ubS").limit(switchLevel).reduce("s", String::concat) + "witch";
  }

  /**
   * @return a variable name for a boolean that marks the switch conditions
   */
  protected String getCase() {
    return Stream.generate(() -> "ub").limit(switchLevel).reduce((s, c) -> s + "S" + c).map(s -> "s" + s + "C").orElse("c") + "ase";
  }
}