/* (c) https://github.com/MontiCore/monticore */
package variablearc._prettyprint;

import arcbasis._visitor.IFullPrettyPrinter;
import com.google.common.base.Preconditions;
import de.monticore.ast.ASTNode;
import de.monticore.expressions.expressionsbasis._ast.ASTExpressionsBasisNode;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCBasicTypesNode;
import org.codehaus.commons.nullanalysis.NotNull;

public class VariableArcFullPrettyPrinter extends VariableArcFullPrettyPrinterTOP implements IFullPrettyPrinter {

  public VariableArcFullPrettyPrinter(@NotNull IndentPrinter printer) {
    super(Preconditions.checkNotNull(printer));
  }

  public VariableArcFullPrettyPrinter(@NotNull IndentPrinter printer, boolean printComments) {
    super(Preconditions.checkNotNull(printer), printComments);
  }

  @Override
  public String prettyprint(@NotNull ASTExpressionsBasisNode node) {
    Preconditions.checkNotNull(node);
    return this.prettyprint((ASTNode) node);
  }

  @Override
  public String prettyprint(@NotNull ASTMCBasicTypesNode node) {
    Preconditions.checkNotNull(node);
    return this.prettyprint((ASTNode) node);
  }
}

