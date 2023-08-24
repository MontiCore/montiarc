/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis._cocos.ArcBasisASTComponentTypeCoCo;
import arcbasis._cocos.ArcBasisASTConnectorCoCo;
import com.google.common.base.Preconditions;
import montiarc.MontiArcMill;
import montiarc._visitor.MontiArcTraverser;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc._cocos.util.IgnoreASTArcVarIfHandler;
import montiarc._cocos.util.SingleASTVariantComponentTypeHandler;
import montiarc._cocos.util.VariantTraverseDispatchVisitor;

public class MontiArcCoCoChecker extends MontiArcCoCoCheckerTOP {

  protected MontiArcTraverser variantTraverser;

  public MontiArcCoCoChecker() {
    super();
    variantTraverser = MontiArcMill.traverser();
    variantTraverser.setArcBasisHandler(new SingleASTVariantComponentTypeHandler());
    variantTraverser.setVariableArcHandler(new IgnoreASTArcVarIfHandler());

    getTraverser().add4ArcBasis(new VariantTraverseDispatchVisitor(variantTraverser));
  }

  /**
   * Adds a CoCo that is checked on all Variants of a component
   *
   * @param coco that will be checked
   */
  public void addVariantCoCo(@NotNull ArcBasisASTComponentTypeCoCo coco) {
    Preconditions.checkNotNull(coco);

    variantTraverser.add4ArcBasis(coco);
  }

  /**
   * Adds a CoCo that is checked on all Variants of a component
   *
   * @param coco that will be checked
   */
  public void addVariantCoCo(@NotNull ArcBasisASTConnectorCoCo coco) {
    Preconditions.checkNotNull(coco);

    variantTraverser.add4ArcBasis(coco);
  }
}
