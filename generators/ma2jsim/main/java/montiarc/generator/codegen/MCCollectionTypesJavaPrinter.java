/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator.codegen;

import com.google.common.base.Preconditions;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mccollectiontypes._ast.ASTMCListType;
import de.monticore.types.mccollectiontypes._ast.ASTMCMapType;
import de.monticore.types.mccollectiontypes._ast.ASTMCSetType;
import de.monticore.types.mccollectiontypes._prettyprint.MCCollectionTypesPrettyPrinter;
import org.codehaus.commons.nullanalysis.NotNull;

public class MCCollectionTypesJavaPrinter extends MCCollectionTypesPrettyPrinter {

  protected CodeGenContext context;

  public MCCollectionTypesJavaPrinter(@NotNull IndentPrinter printer,
                                      @NotNull CodeGenContext context,
                                      boolean printComments) {
    super(printer, printComments);
    this.context =  Preconditions.checkNotNull(context);
  }

  protected CodeGenContext getContext() {
    return this.context;
  }

  @Override
  public void handle(@NotNull ASTMCListType node) {
    Preconditions.checkNotNull(node);
    getContext().setInGenericTypeExpression(true);
    this.getPrinter().print("java.util.List<");
    node.getMCTypeArgument().accept(this.getTraverser());
    this.getPrinter().stripTrailing();
    this.getPrinter().print(">");
    getContext().setInGenericTypeExpression(false);
  }

  @Override
  public void handle(@NotNull ASTMCMapType node) {
    Preconditions.checkNotNull(node);
    getContext().setInGenericTypeExpression(true);
    this.getPrinter().print("java.util.Map<");
    node.getKey().accept(this.getTraverser());
    this.getPrinter().stripTrailing();
    this.getPrinter().print(",");
    node.getValue().accept(this.getTraverser());
    this.getPrinter().stripTrailing();
    this.getPrinter().print(">");
    getContext().setInGenericTypeExpression(false);
  }

  @Override
  public void handle(@NotNull ASTMCSetType node) {
    Preconditions.checkNotNull(node);
    getContext().setInGenericTypeExpression(true);
    this.getPrinter().print("java.util.Set<");
    node.getMCTypeArgument().accept(this.getTraverser());
    this.getPrinter().stripTrailing();
    this.getPrinter().print(">");
    getContext().setInGenericTypeExpression(false);
  }
}
