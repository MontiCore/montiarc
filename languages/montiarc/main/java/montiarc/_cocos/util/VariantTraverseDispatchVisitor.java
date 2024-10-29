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
import montiarc._symboltable.MontiArcComponentTypeSymbol;
import montiarc._visitor.MontiArcTraverser;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc._cocos.util.IgnoreASTArcVarIfHandler;
import variablearc.check.VariableArcTypeCheck;

import java.util.ArrayList;
import java.util.List;

/**
 * A visitor that invokes another traverser on all component variants.
 * It is recommended that the traverser uses {@link SingleASTVariantComponentTypeHandler}
 */
public class VariantTraverseDispatchVisitor implements ArcBasisVisitor2 {

  protected List<Class<? extends IVisitor>> cocos = new ArrayList<>();

  @Override
  public void visit(@NotNull ASTComponentType node) {
    Preconditions.checkNotNull(node);
    if (!node.isPresentSymbol()) return;

    if (!(node.getSymbol() instanceof MontiArcComponentTypeSymbol) ||
      ((MontiArcComponentTypeSymbol) node.getSymbol()).getVariants().isEmpty()) {
      // Fallback so it is still traversed (in the context of cocos this means the component is still checked)
      MontiArcTraverser traverser = getTraverser(node.getSymbol());
      VariableArcTypeCheck.setCurrentVariant(node.getSymbol());
      node.accept(traverser);
      VariableArcTypeCheck.setCurrentVariant(null);
    }

    List<? extends ComponentTypeSymbol> variants = ((MontiArcComponentTypeSymbol) node.getSymbol()).getVariants();
    Type4Ast staticMap = AbstractTypeVisitor.tmpMap;
    for (ComponentTypeSymbol variant : variants) {
      AbstractTypeVisitor.tmpMap = new Type4Ast(); // override static map to clear cached results for variants
      MontiArcTraverser traverser = getTraverser(variant);
      VariableArcTypeCheck.setCurrentVariant(variant);
      long findings = Log.getFindingsCount();
      variant.getAstNode().accept(traverser);
      findings = Log.getFindingsCount() - findings;
      if (findings > 0 && variants.size() > 1) {
        Log.info(findings + " Error" + (findings > 1 ? "s" : "") + " in " + variant, "↳");
      }
      VariableArcTypeCheck.setCurrentVariant(null);
    }
    AbstractTypeVisitor.tmpMap = staticMap;
  }

  protected MontiArcTraverser getTraverser(@NotNull ComponentTypeSymbol variant) {
    MontiArcTraverser traverser = MontiArcMill.traverser();
    traverser.setArcBasisHandler(new SingleASTVariantComponentTypeHandler());
    traverser.setVariableArcHandler(new IgnoreASTArcVarIfHandler());
    traverser.setModesHandler(new IgnoreASTArcModeHandler());

    for (Class<? extends IVisitor> c : cocos) {
      try {
        IVisitor coco = null;
        // Select a fitting constructor for the coco
        try {
          coco = c.getDeclaredConstructor().newInstance();
        } catch (Exception ignored) {
        }
        if (coco == null)
          coco = c.getDeclaredConstructor(ComponentTypeSymbol.class).newInstance(variant);
        if (coco instanceof ArcBasisVisitor2) {
          traverser.add4ArcBasis((ArcBasisVisitor2) coco);
        } else if (coco instanceof MCCommonStatementsVisitor2) {
          traverser.add4MCCommonStatements((MCCommonStatementsVisitor2) coco);
        } else if (coco instanceof MCVarDeclarationStatementsVisitor2) {
          traverser.add4MCVarDeclarationStatements((MCVarDeclarationStatementsVisitor2) coco);
        } else if (coco instanceof SCTransitions4CodeVisitor2) {
          traverser.add4SCTransitions4Code((SCTransitions4CodeVisitor2) coco);
        }
      } catch (Exception ignored) {
        Log.warn("Could not add " + c.getSimpleName() + " to traverser for " + variant.getName());
      }
    }

    return traverser;
  }

  public void addVisitor(@NotNull Class<? extends IVisitor> coco) {
    Preconditions.checkNotNull(coco);
    cocos.add(coco);
  }
}
