/* (c) https://github.com/MontiCore/monticore */
package arcbasis._prettyprint;

import arcbasis._ast.ASTArcBasisNode;
import arcbasis._ast.ASTComponentHead;
import arcbasis._ast.ASTComponentInstantiation;
import arcbasis._ast.ASTPortDeclaration;
import com.google.common.base.Preconditions;
import de.monticore.prettyprint.IndentPrinter;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.Iterator;
import java.util.List;

public class ArcBasisPrettyPrinter extends ArcBasisPrettyPrinterTOP {

  public ArcBasisPrettyPrinter(@NotNull IndentPrinter printer, boolean printComments) {
    super(printer, printComments);
  }

  public <T extends ASTArcBasisNode> void acceptSeparatedList(@NotNull List<T> list) {
    if (list.isEmpty()) {
      return;
    }
    Iterator<T> iterator = list.iterator();
    iterator.next().accept(this.getTraverser());
    while (iterator.hasNext()) {
      this.getPrinter().print(", ");
      iterator.next().accept(this.getTraverser());
    }
  }

  @Override
  public void handle(@NotNull ASTComponentHead node) {
    Preconditions.checkNotNull(node);
    if (!node.isEmptyArcParameters()) {
      this.getPrinter().print("(");
      acceptSeparatedList(node.getArcParameterList());
      this.getPrinter().print(")");
    }
    if (node.isPresentParent()) {
      this.getPrinter().print(" extends ");
      node.getParent().accept(this.getTraverser());
    }
  }

  @Override
  public void handle(@NotNull ASTPortDeclaration node) {
    Preconditions.checkNotNull(node);
    node.getPortDirection().accept(this.getTraverser());
    node.getMCType().accept(this.getTraverser());
    this.getPrinter().print(" ");
    acceptSeparatedList(node.getPortList());
  }

  @Override
  public void handle(ASTComponentInstantiation node) {
    Preconditions.checkNotNull(node);
    node.getMCType().accept(this.getTraverser());
    this.getPrinter().print(" ");
    acceptSeparatedList(node.getComponentInstanceList());
    this.getPrinter().println(";");
  }

}