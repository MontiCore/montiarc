/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator.codegen.javagen;

import com.google.common.base.Preconditions;
import de.monticore.codegen.javagen.SymTypeExpression2JavaConverter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcsimplegenerictypes._ast.ASTMCBasicGenericType;
import de.monticore.types.mcsimplegenerictypes._prettyprint.MCSimpleGenericTypesPrettyPrinter;
import de.monticore.types3.TypeCheck3;
import org.codehaus.commons.nullanalysis.NotNull;

public class MCSimpleGenericTypesJavaPrinter extends MCSimpleGenericTypesPrettyPrinter {

  public MCSimpleGenericTypesJavaPrinter(@NotNull IndentPrinter printer,
                                         boolean printComments) {
    super(printer, printComments);
  }

  @Override
  public void handle(@NotNull ASTMCBasicGenericType node) {
    Preconditions.checkNotNull(node);
    this.getPrinter().print(SymTypeExpression2JavaConverter.getJavaTypePrint(TypeCheck3.symTypeFromAST(node)));
  }
}
