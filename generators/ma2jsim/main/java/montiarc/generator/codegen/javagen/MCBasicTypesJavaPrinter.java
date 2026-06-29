/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator.codegen.javagen;

import com.google.common.base.Preconditions;
import de.monticore.codegen.javagen.SymTypeExpression2JavaConverter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCPrimitiveType;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.monticore.types.mcbasictypes._prettyprint.MCBasicTypesPrettyPrinter;
import de.monticore.types3.TypeCheck3;
import org.codehaus.commons.nullanalysis.NotNull;

public class MCBasicTypesJavaPrinter extends MCBasicTypesPrettyPrinter {

  public MCBasicTypesJavaPrinter(@NotNull IndentPrinter printer,
                                 boolean printComments) {
    super(printer, printComments);
  }

  @Override
  public void handle(@NotNull ASTMCQualifiedName node) {
    Preconditions.checkNotNull(node);
    this.getPrinter().print(SymTypeExpression2JavaConverter.getJavaTypePrint(TypeCheck3.symTypeFromAST(node)) + " ");
  }

  @Override
  public void handle(@NotNull ASTMCPrimitiveType node) {
    Preconditions.checkNotNull(node);
    this.getPrinter().print(SymTypeExpression2JavaConverter.getJavaTypePrint(TypeCheck3.symTypeFromAST(node)) + " ");
  }
}
