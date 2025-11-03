/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator.codegen;

import com.google.common.base.Preconditions;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument;
import de.monticore.types.mcsimplegenerictypes._ast.ASTMCBasicGenericType;
import de.monticore.types.mcsimplegenerictypes._prettyprint.MCSimpleGenericTypesPrettyPrinter;
import de.monticore.types3.TypeCheck3;
import montiarc.MontiArcMill;
import montiarc._symboltable.IMontiArcScope;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.Iterator;
import java.util.Optional;

public class MCSimpleGenericTypesJavaPrinter extends MCSimpleGenericTypesPrettyPrinter {

  protected SymTypeExpressionJavaPrinter symTypeExpressionJavaPrinter;

  public MCSimpleGenericTypesJavaPrinter(@NotNull IndentPrinter printer,
                                         @NotNull SymTypeExpressionJavaPrinter symTypeExpressionJavaPrinter,
                                         boolean printComments) {
    super(printer, printComments);
    this.symTypeExpressionJavaPrinter = Preconditions.checkNotNull(symTypeExpressionJavaPrinter);
  }

  @Override
  public void handle(@NotNull ASTMCBasicGenericType node) {
    Preconditions.checkNotNull(node);
    this.getPrinter().print(symTypeExpressionJavaPrinter.prettyprint(TypeCheck3.symTypeFromAST(node)));
  }
}
