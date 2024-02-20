/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import com.google.common.base.Preconditions;
import de.monticore.visitor.IVisitor;
import montiarc._cocos.util.VariantTraverseDispatchVisitor;
import org.codehaus.commons.nullanalysis.NotNull;

public class MontiArcCoCoChecker extends MontiArcCoCoCheckerTOP {

  protected VariantTraverseDispatchVisitor variantVisitor;

  public MontiArcCoCoChecker() {
    super();
    variantVisitor = new VariantTraverseDispatchVisitor();
    getTraverser().add4ArcBasis(variantVisitor);
  }

  /**
   * Adds a CoCo that is checked on all Variants of a component
   *
   * @param coco that will be checked
   */
  public void addVariantCoCo(@NotNull Class<? extends IVisitor> coco) {
    Preconditions.checkNotNull(coco);

    variantVisitor.addVisitor(coco);
  }
}
