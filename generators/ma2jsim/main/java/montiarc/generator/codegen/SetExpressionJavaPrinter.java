/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator.codegen;

import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.ocl.codegen.util.VariableNaming;
import de.monticore.ocl.codegen.visitors.SetExpressionsPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types3.TypeCheck3;
import de.se_rwth.commons.logging.Log;
import org.codehaus.commons.nullanalysis.NotNull;

public class SetExpressionJavaPrinter extends SetExpressionsPrinter {

  protected SymTypeExpressionJavaPrinter symTypeExpressionJavaPrinter;

  public SetExpressionJavaPrinter(@NotNull IndentPrinter printer, @NotNull SymTypeExpressionJavaPrinter symTypeExpressionJavaPrinter) {
    super(printer, new VariableNaming());
    this.symTypeExpressionJavaPrinter = Preconditions.checkNotNull(symTypeExpressionJavaPrinter);
  }

  @Override
  protected void printDerivedInnerType(ASTExpression node) {
    SymTypeExpression innerType = getInnerType(node);
    if (innerType == null) {
      Log.error(INNER_TYPE_NOT_DERIVED_ERROR, node.get_SourcePositionStart());
      return;
    }

    printer.print(symTypeExpressionJavaPrinter.prettyprint(innerType, true));
  }

  @Override
  protected void printDerivedType(ASTExpression node) {
    SymTypeExpression type = TypeCheck3.typeOf(node);
    if (type.isObscureType()) {
      Log.error(NO_TYPE_DERIVED_ERROR, node.get_SourcePositionStart());
      return;
    }

    printer.print(symTypeExpressionJavaPrinter.prettyprint(type, true));
  }

  @Override
  protected String boxType(SymTypeExpression type) {
    return symTypeExpressionJavaPrinter.prettyprint(type, true);
  }
}
