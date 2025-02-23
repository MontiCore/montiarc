/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator.codegen;

import com.google.common.base.Preconditions;
import de.monticore.prettyprint.CommentPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.types.mcbasictypes._ast.ASTMCPrimitiveType;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.monticore.types.mcbasictypes._prettyprint.MCBasicTypesPrettyPrinter;
import de.monticore.types3.TypeCheck3;
import montiarc.MontiArcMill;
import org.codehaus.commons.nullanalysis.NotNull;

import static de.monticore.types.mcbasictypes._ast.ASTConstantsMCBasicTypes.BOOLEAN;
import static de.monticore.types.mcbasictypes._ast.ASTConstantsMCBasicTypes.BYTE;
import static de.monticore.types.mcbasictypes._ast.ASTConstantsMCBasicTypes.CHAR;
import static de.monticore.types.mcbasictypes._ast.ASTConstantsMCBasicTypes.DOUBLE;
import static de.monticore.types.mcbasictypes._ast.ASTConstantsMCBasicTypes.FLOAT;
import static de.monticore.types.mcbasictypes._ast.ASTConstantsMCBasicTypes.INT;
import static de.monticore.types.mcbasictypes._ast.ASTConstantsMCBasicTypes.LONG;
import static de.monticore.types.mcbasictypes._ast.ASTConstantsMCBasicTypes.SHORT;

public class MCBasicTypesJavaPrinter extends MCBasicTypesPrettyPrinter {

  protected CodeGenContext context;

  public MCBasicTypesJavaPrinter(@NotNull IndentPrinter printer,
                                 @NotNull CodeGenContext context,
                                 boolean printComments) {
    super(printer, printComments);
    this.context = Preconditions.checkNotNull(context);
  }

  protected CodeGenContext getContext() {
    return this.context;
  }

  @Override
  public void handle(@NotNull ASTMCQualifiedName node) {
    Preconditions.checkNotNull(node);
    if (!node.isQualified()) {
      TypeSymbol type = TypeCheck3.symTypeFromAST(node).getTypeInfo();
      if (MontiArcMill.typeDispatcher().isOOSymbolsOOType(type)) {
        getPrinter().print(type.getFullName() + " ");
        return;
      }
    }
    super.handle(node);
  }

  @Override
  public void handle(@NotNull ASTMCPrimitiveType node) {
    Preconditions.checkNotNull(node);
    if (!getContext().isInGenericTypeExpression()) {
      super.handle(node);
    } else {
      // Box primitives when InGenericTypeExpression
      if (this.isPrintComments()) {
        CommentPrettyPrinter.printPreComments(node, this.getPrinter());
      }

      if (node.getPrimitive() == BOOLEAN) {
        getPrinter().print("Boolean ");
      } else if (node.getPrimitive() == BYTE) {
        getPrinter().print("Byte ");
      } else if (node.getPrimitive() == SHORT) {
        getPrinter().print("Short ");
      } else if (node.getPrimitive() == INT) {
        getPrinter().print("Integer ");
      } else if (node.getPrimitive() == LONG) {
        getPrinter().print("Long ");
      } else if (node.getPrimitive() == CHAR) {
        getPrinter().print("Character ");
      } else if (node.getPrimitive() == FLOAT) {
        getPrinter().print("Float ");
      } else if (node.getPrimitive() == DOUBLE) {
        getPrinter().print("Double ");
      }

      if (this.isPrintComments()) {
        CommentPrettyPrinter.printPostComments(node, this.getPrinter());
      }
    }
  }
}
