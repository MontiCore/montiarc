/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator.codegen.javagen;

import de.monticore.codegen.javagen.JavaGenVisitorState;
import de.monticore.codegen.javagen.SymTypeExpression2JavaConverter;
import de.monticore.codegen.util.Node2Name;
import de.monticore.statements.mccommonstatements._ast.ASTConstantExpressionSwitchLabel;
import de.monticore.statements.mccommonstatements._ast.ASTDefaultSwitchLabel;
import de.monticore.statements.mccommonstatements._ast.ASTEnhancedForControl;
import de.monticore.statements.mccommonstatements._ast.ASTSwitchBlockStatementGroup;
import de.monticore.statements.mccommonstatements._ast.ASTSwitchLabel;
import de.monticore.statements.mccommonstatements._ast.ASTSwitchStatement;
import de.monticore.statements.mccommonstatements.codegen.javagen.MCCommonStatementsJavaGenVisitor;
import de.monticore.statements.mcstatementsbasis._ast.ASTMCBlockStatement;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types3.SymTypeRelations;
import de.monticore.types3.TypeCheck3;

import java.util.List;

public class MACommonStatementsJavaGenVisitor extends MCCommonStatementsJavaGenVisitor {

  public MACommonStatementsJavaGenVisitor(JavaGenVisitorState state) {
    super(state);
  }

  @Override
  public void traverse(ASTSwitchStatement node) {
    String tmpVar = Node2Name.getName(node);

    this.getPrinter().print("var " + tmpVar + " = ");
    node.getExpression().accept(this.getTraverser());
    state.endStatement();

    boolean firstBranch = true;
    List<ASTMCBlockStatement> defaultStatements = null;

    for (ASTSwitchBlockStatementGroup group : node.getSwitchBlockStatementGroupList()) {
      for (ASTSwitchLabel label : group.getSwitchLabelList()) {
        if (label instanceof ASTDefaultSwitchLabel) {
          defaultStatements = group.getMCBlockStatementList();
          continue;
        }

        if (firstBranch) {
          this.getPrinter().print("if ");
        } else {
          this.getPrinter().print("else if ");
        }
        state.startParentheses();

        if (label instanceof ASTConstantExpressionSwitchLabel constantLabel &&
          SymTypeRelations.isStringOrSubType(TypeCheck3.typeOf(constantLabel.getConstant()))) {
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

        state.endParentheses();
        printBlock(group.getMCBlockStatementList());
        firstBranch = false;
      }
    }

    if (defaultStatements != null) {
      if (!firstBranch) {
        this.getPrinter().print("else ");
      }
      printBlock(defaultStatements);
    }
  }

  @Override
  public void traverse(ASTConstantExpressionSwitchLabel node) {
    node.getConstant().accept(getTraverser());
  }

  protected void printBlock(List<ASTMCBlockStatement> statements) {
    state.startStatementBlock();
    for (ASTMCBlockStatement statement : statements) {
      statement.accept(this.getTraverser());
    }
    state.endStatementBlock();
  }

  @Override
  public void traverse(ASTEnhancedForControl node) {
    SymTypeExpression expressionType = TypeCheck3.typeOf(node.getExpression());
    SymTypeExpression variableType = TypeCheck3.symTypeFromAST(node.getFormalParameter().getMCType());

    node.getFormalParameter().accept(this.getTraverser());
    this.getPrinter().print(" : ");
    state.startParentheses();
    if (SymTypeRelations.isStringOrSubType(expressionType)) {
      node.getExpression().accept(this.getTraverser());
      state.endParentheses();
      this.getPrinter().print(".toCharArray()");
    } else if (expressionType.isArrayType()) {
      node.getExpression().accept(getTraverser());
      state.endParentheses();
    } else {
      this.getPrinter().print("(");
      this.getPrinter().print("java.lang.Iterable<");
      this.getPrinter().print(SymTypeExpression2JavaConverter.getBoxedJavaTypePrint(variableType));
      this.getPrinter().print(">) (java.lang.Iterable<?>) (");
      node.getExpression().accept(this.getTraverser());
      state.endParentheses();
      state.endParentheses();
    }
  }
}
