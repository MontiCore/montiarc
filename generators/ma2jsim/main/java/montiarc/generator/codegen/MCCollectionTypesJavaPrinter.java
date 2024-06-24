/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator.codegen;

import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mccollectiontypes._ast.ASTMCListType;
import de.monticore.types.mccollectiontypes._ast.ASTMCMapType;
import de.monticore.types.mccollectiontypes._ast.ASTMCSetType;
import de.monticore.types.mccollectiontypes._prettyprint.MCCollectionTypesPrettyPrinter;
import org.codehaus.commons.nullanalysis.NotNull;

public class MCCollectionTypesJavaPrinter extends MCCollectionTypesPrettyPrinter {

  public MCCollectionTypesJavaPrinter(@NotNull IndentPrinter printer, boolean printComments) {
    super(printer, printComments);
  }

  @Override
  public void handle(ASTMCListType node) {
    this.getPrinter().print("java.util.List<");
    node.getMCTypeArgument().accept(this.getTraverser());
    this.getPrinter().stripTrailing();
    this.getPrinter().print(">");
  }

  @Override
  public void handle(ASTMCMapType node) {
    this.getPrinter().print("java.util.Map<");
    node.getKey().accept(this.getTraverser());
    this.getPrinter().stripTrailing();
    this.getPrinter().print(",");
    node.getValue().accept(this.getTraverser());
    this.getPrinter().stripTrailing();
    this.getPrinter().print(">");
  }

  @Override
  public void handle(ASTMCSetType node) {
    this.getPrinter().print("java.util.Set<");
    node.getMCTypeArgument().accept(this.getTraverser());
    this.getPrinter().stripTrailing();
    this.getPrinter().print(">");
  }
}
