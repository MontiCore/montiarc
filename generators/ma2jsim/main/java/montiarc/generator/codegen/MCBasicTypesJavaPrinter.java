/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator.codegen;

import com.google.common.base.Preconditions;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCPrimitiveType;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.monticore.types.mcbasictypes._prettyprint.MCBasicTypesPrettyPrinter;
import de.monticore.types3.TypeCheck3;
import org.codehaus.commons.nullanalysis.NotNull;


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
