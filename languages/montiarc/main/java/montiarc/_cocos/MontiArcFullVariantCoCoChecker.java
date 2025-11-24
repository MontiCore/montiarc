/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import com.google.common.base.Preconditions;
import montiarc._cocos.util.MontiArcFullVariantDispatch;
import org.codehaus.commons.nullanalysis.Nullable;

public class MontiArcFullVariantCoCoChecker extends MontiArcCoCoChecker{
  protected MontiArcFullVariantDispatch variantDispatch;

  public MontiArcCoCoChecker get4FullVariant() {
    return this.variantDispatch;
  }

  public MontiArcFullVariantCoCoChecker() {
    this(new MontiArcFullVariantDispatch());
  }

  protected MontiArcFullVariantCoCoChecker(@Nullable MontiArcFullVariantDispatch variantDispatch) {
    Preconditions.checkNotNull(variantDispatch);
    this.variantDispatch = variantDispatch;
    this.getTraverser().add4ArcBasis(variantDispatch);
  }
}
