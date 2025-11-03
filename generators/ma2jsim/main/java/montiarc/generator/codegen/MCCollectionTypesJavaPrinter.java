/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator.codegen;

import com.google.common.base.Preconditions;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mccollectiontypes._ast.ASTMCListType;
import de.monticore.types.mccollectiontypes._ast.ASTMCMapType;
import de.monticore.types.mccollectiontypes._ast.ASTMCOptionalType;
import de.monticore.types.mccollectiontypes._ast.ASTMCSetType;
import de.monticore.types.mccollectiontypes._prettyprint.MCCollectionTypesPrettyPrinter;
import de.monticore.types3.TypeCheck3;
import org.codehaus.commons.nullanalysis.NotNull;

public class MCCollectionTypesJavaPrinter extends MCCollectionTypesPrettyPrinter {

  protected SymTypeExpressionJavaPrinter symTypeExpressionJavaPrinter;

  public MCCollectionTypesJavaPrinter(@NotNull IndentPrinter printer,
                                      @NotNull SymTypeExpressionJavaPrinter symTypeExpressionJavaPrinter,
                                      boolean printComments) {
    super(printer, printComments);
    this.symTypeExpressionJavaPrinter = Preconditions.checkNotNull(symTypeExpressionJavaPrinter);
  }

  @Override
  public void handle(@NotNull ASTMCListType node) {
    Preconditions.checkNotNull(node);
    this.getPrinter().print(symTypeExpressionJavaPrinter.prettyprint(TypeCheck3.symTypeFromAST(node)));
  }

  @Override
  public void handle(@NotNull ASTMCMapType node) {
    Preconditions.checkNotNull(node);
    this.getPrinter().print(symTypeExpressionJavaPrinter.prettyprint(TypeCheck3.symTypeFromAST(node)));
  }

  @Override
  public void handle(@NotNull ASTMCSetType node) {
    Preconditions.checkNotNull(node);
    this.getPrinter().print(symTypeExpressionJavaPrinter.prettyprint(TypeCheck3.symTypeFromAST(node)));
  }

  @Override
  public void handle(@NotNull ASTMCOptionalType node) {
    Preconditions.checkNotNull(node);
    this.getPrinter().print(symTypeExpressionJavaPrinter.prettyprint(TypeCheck3.symTypeFromAST(node)));
  }
}
