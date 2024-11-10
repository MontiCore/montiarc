/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos.util;

import arcbasis._ast.ASTComponentType;
import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis._visitor.ArcBasisVisitor2;
import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import modes._cocos.util.IgnoreASTArcModeHandler;
import montiarc._cocos.MontiArcCoCoChecker;
import montiarc._symboltable.MontiArcComponentTypeSymbol;
import montiarc.check.MontiArcTypeCheck;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc._cocos.util.IgnoreASTArcVarIfHandler;
import variablearc.check.VariableArcTypeCheck;

import java.util.List;

/**
 * A visitor that invokes another traverser on all component variants.
 */
public class MontiArcVariantDispatch extends MontiArcCoCoChecker implements ArcBasisVisitor2 {

  public MontiArcVariantDispatch() {
    super();
    traverser.setArcBasisHandler(new SingleASTVariantComponentTypeHandler());
    traverser.setVariableArcHandler(new IgnoreASTArcVarIfHandler());
    traverser.setModesHandler(new IgnoreASTArcModeHandler());
  }

  @Override
  public void endVisit(@NotNull ASTComponentType node) {
    Preconditions.checkNotNull(node);
    if (!node.isPresentSymbol()) return;

    if (!(node.getSymbol() instanceof MontiArcComponentTypeSymbol) ||
      ((MontiArcComponentTypeSymbol) node.getSymbol()).getVariants().isEmpty()) {
      // Fallback so it is still traversed (in the context of cocos this means the component is still checked)
      VariableArcTypeCheck.setCurrentVariant(node.getSymbol());
      node.accept(getTraverser());
      VariableArcTypeCheck.setCurrentVariant(null);
    }

    List<? extends ComponentTypeSymbol> variants = ((MontiArcComponentTypeSymbol) node.getSymbol()).getVariants();
    for (ComponentTypeSymbol variant : variants) {
      MontiArcTypeCheck.enterContext(variant);
      long findings = Log.getFindingsCount();
      variant.getAstNode().accept(getTraverser());
      findings = Log.getFindingsCount() - findings;
      if (findings > 0 && variants.size() > 1) {
        Log.info(findings + " Error" + (findings > 1 ? "s" : "") + " in " + variant, "↳");
      }
      MontiArcTypeCheck.leaveContext();
    }
  }
}
