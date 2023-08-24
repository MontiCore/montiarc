/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos.util;

import arcbasis._ast.ASTComponentType;
import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis._visitor.ArcBasisVisitor2;
import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import montiarc._symboltable.MontiArcComponentTypeSymbol;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc._visitor.VariableArcTraverser;

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

    if (!(node.getSymbol() instanceof MontiArcComponentTypeSymbol) ||
      ((MontiArcComponentTypeSymbol) node.getSymbol()).getVariants().isEmpty()) {
      // Fallback so it is still traversed (in the context of cocos this means the component is still checked)
      node.accept(traverser);
    }

    List<? extends ComponentTypeSymbol> variants = ((MontiArcComponentTypeSymbol) node.getSymbol()).getVariants();
    for (ComponentTypeSymbol variant : variants) {
      long findings = Log.getFindingsCount();
      variant.getAstNode().accept(traverser);
      findings = Log.getFindingsCount() - findings;
      if (findings > 0 && variants.size() > 1) {
        Log.info(findings + " Error" + (findings > 1 ? "s" : "") + " in " + variant, "↳");
      }
    }
  }
}
