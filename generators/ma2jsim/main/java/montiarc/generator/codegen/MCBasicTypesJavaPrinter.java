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

  protected SymTypeExpressionJavaPrinter symTypeExpressionJavaPrinter;

  public MCBasicTypesJavaPrinter(@NotNull IndentPrinter printer,
                                 @NotNull SymTypeExpressionJavaPrinter symTypeExpressionJavaPrinter,
                                 boolean printComments) {
    super(printer, printComments);
    this.symTypeExpressionJavaPrinter = Preconditions.checkNotNull(symTypeExpressionJavaPrinter);
  }

  @Override
  public void handle(@NotNull ASTMCQualifiedName node) {
    Preconditions.checkNotNull(node);
    this.getPrinter().print(symTypeExpressionJavaPrinter.prettyprint(TypeCheck3.symTypeFromAST(node)) + " ");
  }

  @Override
  public void handle(@NotNull ASTMCPrimitiveType node) {
    Preconditions.checkNotNull(node);
    this.getPrinter().print(symTypeExpressionJavaPrinter.prettyprint(TypeCheck3.symTypeFromAST(node)) + " ");
  }
}
