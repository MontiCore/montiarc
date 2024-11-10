/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos.util;

import arcbasis._ast.ASTComponentType;
import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis._visitor.ArcBasisVisitor2;
import com.google.common.base.Preconditions;
import de.monticore.sctransitions4code._visitor.SCTransitions4CodeVisitor2;
import de.monticore.statements.mccommonstatements._visitor.MCCommonStatementsVisitor2;
import de.monticore.statements.mcvardeclarationstatements._visitor.MCVarDeclarationStatementsVisitor2;
import de.monticore.types3.AbstractTypeVisitor;
import de.monticore.types3.Type4Ast;
import de.monticore.visitor.IVisitor;
import de.se_rwth.commons.logging.Log;
import modes._cocos.util.IgnoreASTArcModeHandler;
import montiarc.MontiArcMill;
import montiarc._cocos.MontiArcCoCoCheckerTOP;
import montiarc._symboltable.MontiArcComponentTypeSymbol;
import montiarc._visitor.MontiArcTraverser;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc._cocos.util.IgnoreASTArcVarIfHandler;
import variablearc.check.VariableArcTypeCheck;

import java.util.ArrayList;
import java.util.List;

/**
 * A visitor that invokes another traverser on all component variants.
 */
public class MontiArcVariantDispatch extends MontiArcCoCoCheckerTOP implements ArcBasisVisitor2 {

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
    Type4Ast staticMap = AbstractTypeVisitor.tmpMap;
    for (ComponentTypeSymbol variant : variants) {
      AbstractTypeVisitor.tmpMap = new Type4Ast(); // override static map to clear cached results for variants
      VariableArcTypeCheck.setCurrentVariant(variant);
      long findings = Log.getFindingsCount();
      variant.getAstNode().accept(getTraverser());
      findings = Log.getFindingsCount() - findings;
      if (findings > 0 && variants.size() > 1) {
        Log.info(findings + " Error" + (findings > 1 ? "s" : "") + " in " + variant, "↳");
      }
      VariableArcTypeCheck.setCurrentVariant(null);
    }
    AbstractTypeVisitor.tmpMap = staticMap;
  }
}
