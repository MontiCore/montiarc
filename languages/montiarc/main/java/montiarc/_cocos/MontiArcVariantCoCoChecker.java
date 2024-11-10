/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import com.google.common.base.Preconditions;
import montiarc._cocos.util.MontiArcVariantDispatch;
import org.codehaus.commons.nullanalysis.Nullable;

public class MontiArcVariantCoCoChecker extends MontiArcCoCoChecker {

  protected MontiArcVariantDispatch variantDispatch;

  public MontiArcCoCoChecker get4Variant() {
    return this.variantDispatch;
  }

  public MontiArcVariantCoCoChecker() {
    this(new MontiArcVariantDispatch());
  }

  protected MontiArcVariantCoCoChecker(@Nullable MontiArcVariantDispatch variantDispatch) {
    Preconditions.checkNotNull(variantDispatch);
    this.variantDispatch = variantDispatch;
    this.getTraverser().add4ArcBasis(variantDispatch);
  }
}
