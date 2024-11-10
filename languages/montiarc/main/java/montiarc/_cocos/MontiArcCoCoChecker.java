/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import com.google.common.base.Preconditions;
import montiarc._cocos.util.MontiArcVariantDispatch;
import org.codehaus.commons.nullanalysis.NotNull;

public class MontiArcCoCoChecker extends MontiArcCoCoCheckerTOP {

  protected MontiArcVariantDispatch variantDispatch;

  public MontiArcCoCoCheckerTOP get4Variant() {
    return this.variantDispatch;
  }

  public MontiArcCoCoChecker() {
    this(new MontiArcVariantDispatch());
  }

  protected MontiArcCoCoChecker(@NotNull MontiArcVariantDispatch variantDispatch) {
    Preconditions.checkNotNull(variantDispatch);
    this.variantDispatch = variantDispatch;
    this.getTraverser().add4ArcBasis(variantDispatch);
  }
}
