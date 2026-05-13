/* (c) https://github.com/MontiCore/monticore */
package arcbasis._prettyprint;

import arcbasis._ast.ASTArcArgument;
import arcbasis._ast.ASTArcBasisNode;
import arcbasis._ast.ASTArcElement;
import arcbasis._ast.ASTArcField;
import arcbasis._ast.ASTArcFieldDeclaration;
import arcbasis._ast.ASTArcParameter;
import arcbasis._ast.ASTArcParent;
import arcbasis._ast.ASTComponentBody;
import arcbasis._ast.ASTComponentHead;
import arcbasis._ast.ASTComponentInstantiation;
import arcbasis._ast.ASTComponentInterface;
import arcbasis._ast.ASTConnector;
import arcbasis._ast.ASTPortAccess;
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
      this.getPrinter().stripTrailing();
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
    if (!node.getArcParentList().isEmpty()) {
      this.getPrinter().print(" extends ");
      acceptSeparatedList(node.getArcParentList());
    }
  }

  @Override
  public void handle(@NotNull ASTArcParent node) {
    Preconditions.checkNotNull(node);
    node.getType().accept(this.getTraverser());
    if (!node.isEmptyArcArguments()) {
      this.getPrinter().print("(");
      acceptSeparatedList(node.getArcArgumentList());
      this.getPrinter().print(")");
    }
  }

  @Override
  public void handle(@NotNull ASTPortDeclaration node) {
    Preconditions.checkNotNull(node);
    node.getPortDirection().accept(this.getTraverser());
    node.getMCType().accept(this.getTraverser());
    acceptSeparatedList(node.getArcPortList());
  }

  @Override
  public void handle(@NotNull ASTComponentInterface node) {
    Preconditions.checkNotNull(node);
    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPreComments(node, getPrinter());
    }
    Iterator<ASTPortDeclaration> iter = node.getPortDeclarationList().iterator();
    if (iter.hasNext()) {
      getPrinter().print("port ");
      iter.next().accept(getTraverser());
      while (iter.hasNext()) {
        getPrinter().stripTrailing();
        getPrinter().print(",\n");
        getPrinter().print("     ");
        iter.next().accept(getTraverser());
      }
      getPrinter().stripTrailing();
      getPrinter().println(";");
    }
    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPostComments(node, getPrinter());
    }
  }

  @Override
  public void handle(@NotNull ASTComponentInstantiation node) {
    Preconditions.checkNotNull(node);
    node.getMCType().accept(this.getTraverser());
    this.getPrinter().stripTrailing();
    this.getPrinter().print(" ");
    acceptSeparatedList(node.getComponentInstanceList());
    this.getPrinter().stripTrailing();
    this.getPrinter().println(";");
  }

  @Override
  public void handle(@NotNull ASTArcField node) {
    Preconditions.checkNotNull(node);
    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPreComments(node, getPrinter());
    }
    getPrinter().print(node.getName());
    getPrinter().print(" = ");
    node.getInitial().accept(getTraverser());
    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPostComments(node, getPrinter());
    }
  }

  @Override
  public void handle(@NotNull ASTArcFieldDeclaration node) {
    Preconditions.checkNotNull(node);
    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPreComments(node, getPrinter());
    }
    Iterator<ASTArcField> iter = node.getArcFieldList().iterator();
    if (iter.hasNext()) {
      node.getMCType().accept(getTraverser());
      iter.next().accept(getTraverser());
      while (iter.hasNext()) {
        getPrinter().stripTrailing();
        getPrinter().print(", ");
        iter.next().accept(getTraverser());
      }
      getPrinter().stripTrailing();
      getPrinter().println(";");
    }
    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPostComments(node, getPrinter());
    }
  }

  @Override
  public void handle(@NotNull ASTArcParameter node) {
    Preconditions.checkNotNull(node);
    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPreComments(node, getPrinter());
    }
    node.getMCType().accept(getTraverser());
    getPrinter().print(node.getName());
    if (node.isPresentDefault()) {
      getPrinter().print(" = ");
      node.getDefault().accept(getTraverser());
    }
    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPostComments(node, getPrinter());
    }
  }

  @Override
  public void handle(@NotNull ASTArcArgument node) {
    Preconditions.checkNotNull(node);
    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPreComments(node, getPrinter());
    }
    if (node.isPresentName()) {
      getPrinter().print(node.getName());
      getPrinter().print(" = ");
    }
    node.getExpression().accept(getTraverser());
    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPostComments(node, getPrinter());
    }
  }

  @Override
  public void handle(@NotNull ASTConnector node) {
    Preconditions.checkNotNull(node);
    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPreComments(node, getPrinter());
    }
    Iterator<ASTPortAccess> iter = node.getTargetList().iterator();
    if (iter.hasNext()) {
      node.getSource().accept(getTraverser());
      getPrinter().stripTrailing();
      getPrinter().print(" -> ");
      iter.next().accept(getTraverser());
      while (iter.hasNext()) {
        getPrinter().stripTrailing();
        getPrinter().print(", ");
        iter.next().accept(getTraverser());
      }
      getPrinter().stripTrailing();
      getPrinter().println(";");
    }
    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPostComments(node, getPrinter());
    }
  }

  @Override
  public void handle(@NotNull ASTComponentBody node) {
    Preconditions.checkNotNull(node);
    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPreComments(node, getPrinter());
    }

    getPrinter().println("{");
    getPrinter().indent();

    List<ASTArcElement> elements = node.getArcElementList();
    for (int i = 0; i < elements.size(); i++) {
      ASTArcElement element = elements.get(i);
      if (i > 0) {
        ASTArcElement prevElement = elements.get(i - 1);
        if (element.getClass() != prevElement.getClass()) {
          getPrinter().println();
        }
      }
      element.accept(getTraverser());
    }

    getPrinter().unindent();
    getPrinter().println("}");

    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPostComments(node, getPrinter());
    }
  }

}
