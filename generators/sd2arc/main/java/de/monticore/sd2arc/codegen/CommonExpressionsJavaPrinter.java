/* (c) https://github.com/MontiCore/monticore */
package de.monticore.sd2arc.codegen;

import com.google.common.base.Preconditions;
import de.monticore.expressions.commonexpressions._ast.ASTFieldAccessExpression;
import de.monticore.expressions.commonexpressions._prettyprint.CommonExpressionsPrettyPrinter;
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

import java.util.Optional;

public class CommonExpressionsJavaPrinter extends CommonExpressionsPrettyPrinter {

  protected SDHelper helper = new SDHelper();

  public CommonExpressionsJavaPrinter(@NotNull IndentPrinter printer, boolean printComments) {
    super(printer, printComments);
  }

  @Override
  public void handle(@NotNull ASTFieldAccessExpression node) {
    Preconditions.checkNotNull(node);
    if (this.isPrintComments()) {
      CommentPrettyPrinter.printPreComments(node, getPrinter());
    }

    SymTypeSourceInfo sourceInfo = TypeCheck3.typeOf(node).getSourceInfo();
    if (sourceInfo.getSourceSymbol().isPresent() && sourceInfo.getSourceSymbol().get() instanceof Port2VariableAdapter && ((Port2VariableAdapter) sourceInfo.getSourceSymbol().get()).getAdaptee().isIncoming()) {
      // always use the sending port instead of the receiving one
      Optional<ASTSequenceDiagram> diagram = getDiagram(node.getEnclosingScope());
      if (diagram.isPresent()) {
        String source = helper.getPortSource(diagram.get(), SD4ComponentsMill.prettyPrint(node.getExpression(), false) + "." + node.getName());
        if (source != null && !source.isEmpty()) {
          getPrinter().print(helper.qNameToVarName(source));
          return;
        }
      }
    }

    // default implementation
    node.getExpression().accept(getTraverser());

    getPrinter().stripTrailing();
    if (sourceInfo.getSourceSymbol().isPresent() && sourceInfo.getSourceSymbol().get() instanceof Port2VariableAdapter) {
      getPrinter().print("_");
    } else {
      getPrinter().print(".");
    }
    getPrinter().print(node.getName() + " ");


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
