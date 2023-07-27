/* (c) https://github.com/MontiCore/monticore */
package variablearc._cocos.util;

import arcbasis._ast.ASTComponentType;
import arcbasis._visitor.ArcBasisVisitor2;
import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc.VariableArcMill;
import variablearc._symboltable.VariableArcVariationPoint;
import variablearc._symboltable.VariableComponentTypeSymbol;
import variablearc._symboltable.VariantComponentTypeSymbol;
import variablearc._visitor.VariableArcTraverser;
import variablearc.evaluation.expressions.Expression;

import java.util.List;

/**
 * A visitor that invokes another traverser on all component variants.
 * It is recommended that the traverser uses {@link SingleASTVariantComponentTypeHandler}
 */
public class VariantTraverseDispatchVisitor implements ArcBasisVisitor2 {

  protected VariableArcTraverser traverser;

  public VariantTraverseDispatchVisitor(VariableArcTraverser traverser) {
    this.traverser = traverser;
  }

  @Override
  public void visit(@NotNull ASTComponentType node) {
    Preconditions.checkNotNull(node);
    if (!node.isPresentSymbol()) return;

    if (!(node.getSymbol() instanceof VariableComponentTypeSymbol) ||
      ((VariableComponentTypeSymbol) node.getSymbol()).getVariants().isEmpty()) {
      // Fallback so it is still traversed (in the context of cocos this means the component is still checked)
      node.accept(traverser);
    }

    List<VariantComponentTypeSymbol> variants = ((VariableComponentTypeSymbol) node.getSymbol()).getVariants();
    for (VariantComponentTypeSymbol variant : variants) {
      long findings = Log.getFindingsCount();
      variant.getAstNode().accept(traverser);
      if (findings != Log.getFindingsCount() && variants.size() > 1) {
        Log.info("Error in variant (" + variant.getIncludedVariationPoints().stream().map(VariableArcVariationPoint::getCondition).map(Expression::print).reduce((a, b) -> a + ", " + b).orElse("") + ")", "↳ Variability");
      }
    }
  }
}
