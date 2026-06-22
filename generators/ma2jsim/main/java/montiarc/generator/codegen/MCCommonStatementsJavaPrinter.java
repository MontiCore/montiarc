/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator.codegen;

import de.monticore.codegen.util.Node2Name;
import de.monticore.prettyprint.CommentPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.statements.mccommonstatements._ast.ASTConstantExpressionSwitchLabel;
import de.monticore.statements.mccommonstatements._ast.ASTDefaultSwitchLabel;
import de.monticore.statements.mccommonstatements._ast.ASTEnhancedForControl;
import de.monticore.statements.mccommonstatements._ast.ASTIfStatement;
import de.monticore.statements.mccommonstatements._ast.ASTSwitchBlockStatementGroup;
import de.monticore.statements.mccommonstatements._ast.ASTSwitchLabel;
import de.monticore.statements.mccommonstatements._ast.ASTSwitchStatement;
import de.monticore.statements.mccommonstatements._prettyprint.MCCommonStatementsPrettyPrinter;
import de.monticore.statements.mcstatementsbasis._ast.ASTMCBlockStatement;
import de.monticore.types3.SymTypeRelations;
import de.monticore.types3.TypeCheck3;
import montiarc.MontiArcMill;

import java.util.List;

public class MCCommonStatementsJavaPrinter extends MCCommonStatementsPrettyPrinter {

  public MCCommonStatementsJavaPrinter(IndentPrinter printer, boolean printComments) {
    super(printer, printComments);
  }

  public void handle(ASTIfStatement node) {
    if (this.isPrintComments()) {
      CommentPrettyPrinter.printPreComments(node, this.getPrinter());
    }

    this.getPrinter().print("if ");
    this.getPrinter().stripTrailing();
    this.getPrinter().print("(");
    node.getCondition().accept(this.getTraverser());
    this.getPrinter().stripTrailing();
    this.getPrinter().print(")");

    // Workaround for single statements that are expended into multiple (like port message sending: assignment + sending)
    if (MontiArcMill.typeDispatcher().isMCCommonStatementsASTMCJavaBlock(node.getThenStatement())) {
      node.getThenStatement().accept(this.getTraverser());
    } else {
      this.getPrinter().print("{");
      node.getThenStatement().accept(this.getTraverser());
      this.getPrinter().print("}");
    }

    if (node.isPresentElseStatement()) {
      this.getPrinter().print("else ");
      if (MontiArcMill.typeDispatcher().isMCCommonStatementsASTMCJavaBlock(node.getElseStatement())) {
        node.getElseStatement().accept(this.getTraverser());
      } else {
        this.getPrinter().print("{");
        node.getElseStatement().accept(this.getTraverser());
        this.getPrinter().print("}");
      }
    }

    if (this.isPrintComments()) {
      CommentPrettyPrinter.printPostComments(node, this.getPrinter());
    }

  }

  @Override
  public void handle(ASTSwitchStatement node) {
    if (this.isPrintComments()) {
      CommentPrettyPrinter.printPreComments(node, this.getPrinter());
    }

    String tmpVar = Node2Name.getName(node);

    this.getPrinter().print("var " + tmpVar + " = ");
    node.getExpression().accept(this.getTraverser());
    this.getPrinter().print(";");
    this.getPrinter().println();

    boolean firstBranch = true;
    List<ASTMCBlockStatement> defaultStatements = null;

    for (ASTSwitchBlockStatementGroup group : node.getSwitchBlockStatementGroupList()) {
      for (ASTSwitchLabel label : group.getSwitchLabelList()) {
        if (label instanceof ASTDefaultSwitchLabel) {
          defaultStatements = group.getMCBlockStatementList();
          continue;
        }

        if (firstBranch) {
          this.getPrinter().print("if (");
        } else {
          this.getPrinter().print("else if (");
        }

        if (label instanceof ASTConstantExpressionSwitchLabel constantLabel &&
          SymTypeRelations.isString(TypeCheck3.typeOf(constantLabel.getConstant()))) {
          // Override for string literals

          constantLabel.getConstant().accept(this.getTraverser());
          this.getPrinter().print(".equals(");
          this.getPrinter().print(tmpVar);
          this.getPrinter().print(")");
        } else {
          this.getPrinter().print(tmpVar);
          this.getPrinter().print(" == ");
          label.accept(this.getTraverser());
        }

        this.getPrinter().print(") ");
        printBlock(group.getMCBlockStatementList());
        this.getPrinter().println();
        firstBranch = false;
      }
    }

    if (defaultStatements != null) {
      if (!firstBranch) {
        this.getPrinter().print("else ");
      }
      printBlock(defaultStatements);
      this.getPrinter().println();
    }

    if (this.isPrintComments()) {
      CommentPrettyPrinter.printPostComments(node, this.getPrinter());
    }
  }

  protected void printBlock(List<ASTMCBlockStatement> statements) {
    this.getPrinter().print("{");
    this.getPrinter().println();
    this.getPrinter().indent();

    for (ASTMCBlockStatement statement : statements) {
      if (MontiArcMill.typeDispatcher().isMCCommonStatementsASTBreakStatement(statement)) continue;
      statement.accept(this.getTraverser());
      this.getPrinter().println();
    }

    this.getPrinter().unindent();
    this.getPrinter().print("}");
  }

  @Override
  public void handle(ASTConstantExpressionSwitchLabel node) {
    node.getConstant().accept(getTraverser());
  }

  @Override
  public void handle(ASTEnhancedForControl node) {
    if (this.isPrintComments()) {
      CommentPrettyPrinter.printPreComments(node, this.getPrinter());
    }

    node.getFormalParameter().accept(this.getTraverser());
    this.getPrinter().print(" : ");
    node.getExpression().accept(this.getTraverser());
    if (SymTypeRelations.isString(TypeCheck3.typeOf(node.getExpression()))) {
      this.getPrinter().print(".toCharArray()");
    }

    if (this.isPrintComments()) {
      CommentPrettyPrinter.printPostComments(node, this.getPrinter());
    }
  }
}
