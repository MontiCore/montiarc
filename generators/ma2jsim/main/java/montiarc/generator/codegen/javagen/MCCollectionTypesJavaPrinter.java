/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator.codegen.javagen;

import com.google.common.base.Preconditions;
import de.monticore.codegen.javagen.SymTypeExpression2JavaConverter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mccollectiontypes._ast.ASTMCListType;
import de.monticore.types.mccollectiontypes._ast.ASTMCMapType;
import de.monticore.types.mccollectiontypes._ast.ASTMCOptionalType;
import de.monticore.types.mccollectiontypes._ast.ASTMCSetType;
import de.monticore.types.mccollectiontypes._prettyprint.MCCollectionTypesPrettyPrinter;
import de.monticore.types3.TypeCheck3;
import org.codehaus.commons.nullanalysis.NotNull;

public class MCCollectionTypesJavaPrinter extends MCCollectionTypesPrettyPrinter {

  public MCCollectionTypesJavaPrinter(@NotNull IndentPrinter printer,
                                      boolean printComments) {
    super(printer, printComments);
  }

  @Override
  public void handle(@NotNull ASTMCListType node) {
    Preconditions.checkNotNull(node);
    this.getPrinter().print(SymTypeExpression2JavaConverter.getJavaTypePrint(TypeCheck3.symTypeFromAST(node)));
  }

  @Override
  public void handle(@NotNull ASTMCMapType node) {
    Preconditions.checkNotNull(node);
    this.getPrinter().print(SymTypeExpression2JavaConverter.getJavaTypePrint(TypeCheck3.symTypeFromAST(node)));
  }

  @Override
  public void handle(@NotNull ASTMCSetType node) {
    Preconditions.checkNotNull(node);
    this.getPrinter().print(SymTypeExpression2JavaConverter.getJavaTypePrint(TypeCheck3.symTypeFromAST(node)));
  }

  @Override
  public void handle(@NotNull ASTMCOptionalType node) {
    Preconditions.checkNotNull(node);
    this.getPrinter().print(SymTypeExpression2JavaConverter.getJavaTypePrint(TypeCheck3.symTypeFromAST(node)));
  }
}
