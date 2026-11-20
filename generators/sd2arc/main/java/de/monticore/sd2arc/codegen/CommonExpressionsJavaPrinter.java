/* (c) https://github.com/MontiCore/monticore */
package de.monticore.sd2arc.codegen;

import com.google.common.base.Preconditions;
import de.monticore.expressions.commonexpressions._ast.ASTFieldAccessExpression;
import de.monticore.expressions.commonexpressions._ast.ASTQualifiedNameExpression;
import de.monticore.expressions.commonexpressions._prettyprint.CommonExpressionsPrettyPrinter;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.lang.sd4components.SD4ComponentsMill;
import de.monticore.lang.sd4components._symboltable.SD4ComponentsArtifactScope;
import de.monticore.lang.sdbasis._ast.ASTSequenceDiagram;
import de.monticore.prettyprint.CommentPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.symbols.compsymbols._symboltable.Port2VariableAdapter;
import de.monticore.symboltable.IScope;
import de.monticore.types.check.SymTypeSourceInfo;
import de.monticore.types3.TypeCheck3;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.List;
import java.util.Optional;

public class CommonExpressionsJavaPrinter extends CommonExpressionsPrettyPrinter {

  protected SDHelper helper = new SDHelper();

  public CommonExpressionsJavaPrinter(@NotNull IndentPrinter printer, boolean printComments) {
    super(printer, printComments);
  }

  @Override
  public void handle(@NotNull ASTFieldAccessExpression node) {
    Preconditions.checkNotNull(node);
    this.printAccess(node, SD4ComponentsMill.prettyPrint(node.getExpression(), false), node.getName(),
      () -> node.getExpression().accept(getTraverser()));
  }

  @Override
  public void handle(@NotNull ASTQualifiedNameExpression node) {
    Preconditions.checkNotNull(node);
    List<String> parts = node.getMCQualifiedName().getPartsList();
    if (parts.size() == 1) {
      super.handle(node);
      return;
    }
    String qualifier = String.join(".", parts.subList(0, parts.size() - 1));
    this.printAccess(node, qualifier, parts.get(parts.size() - 1), () -> getPrinter().print(qualifier));
  }

  /**
   * prints accesses of form qualifier.name, e.g., ports of subcomponents
   */
  protected void printAccess(@NotNull ASTExpression node, @NotNull String qualifier, @NotNull String name,
                             @NotNull Runnable printQualifier) {
    if (this.isPrintComments()) {
      CommentPrettyPrinter.printPreComments(node, getPrinter());
    }

    SymTypeSourceInfo sourceInfo = TypeCheck3.typeOf(node).getSourceInfo();
    if (sourceInfo.getSourceSymbol().isPresent() && sourceInfo.getSourceSymbol().get() instanceof Port2VariableAdapter && ((Port2VariableAdapter) sourceInfo.getSourceSymbol().get()).getAdaptee().isIncoming()) {
      // always use the sending port instead of the receiving one
      Optional<ASTSequenceDiagram> diagram = getDiagram(node.getEnclosingScope());
      if (diagram.isPresent()) {
        String source = helper.getPortSource(diagram.get(), qualifier + "." + name);
        if (source != null && !source.isEmpty()) {
          getPrinter().print(helper.qNameToVarName(source));
          return;
        }
      }
    }

    // default implementation
    printQualifier.run();

    getPrinter().stripTrailing();
    if (sourceInfo.getSourceSymbol().isPresent() && sourceInfo.getSourceSymbol().get() instanceof Port2VariableAdapter) {
      getPrinter().print("_");
    } else {
      getPrinter().print(".");
    }
    getPrinter().print(name + " ");


    if (this.isPrintComments()) {
      CommentPrettyPrinter.printPostComments(node, getPrinter());
    }
  }

  protected Optional<ASTSequenceDiagram> getDiagram(IScope scope) {
    if (scope instanceof SD4ComponentsArtifactScope)
      return Optional.of((ASTSequenceDiagram) ((SD4ComponentsArtifactScope) scope).getLocalDiagramSymbols().get(0).getAstNode());
    if (scope.getEnclosingScope().equals(SD4ComponentsMill.globalScope()))
      return Optional.empty();

    return getDiagram(scope.getEnclosingScope());
  }
}
