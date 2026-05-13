/* (c) https://github.com/MontiCore/monticore */
package montiarc._prettyprint;

import com.google.common.base.Preconditions;
import de.monticore.prettyprint.IndentPrinter;
import montiarc._ast.ASTMACompilationUnit;
import org.codehaus.commons.nullanalysis.NotNull;

public class MontiArcPrettyPrinter extends MontiArcPrettyPrinterTOP {

  public MontiArcPrettyPrinter(@NotNull IndentPrinter printer, boolean printComments) {
    super(printer, printComments);
  }

  @Override
  public void handle(@NotNull ASTMACompilationUnit node) {
    Preconditions.checkNotNull(node);
    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPreComments(node, getPrinter());
    }

    if (node.isPresentPackage()) {
      getPrinter().print("package ");
      node.getPackage().accept(getTraverser());
      getPrinter().stripTrailing();
      getPrinter().println(";");
      getPrinter().println();
    }

    node.getImportStatementList().forEach(n -> n.accept(getTraverser()));

    if (!node.getImportStatementList().isEmpty()) {
      getPrinter().println();
    }

    node.getArcComponentType().accept(getTraverser());

    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPostComments(node, getPrinter());
    }
  }
}
