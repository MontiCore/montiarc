/* (c) https://github.com/MontiCore/monticore */
package arcbasis.check;

import de.monticore.types.check.FullSynthesizeCompKindFromMCSimpleGenericTypes;

public class ArcBasisFullSynthesizeCompKind extends FullSynthesizeCompKindFromMCSimpleGenericTypes {

  @Override
  public void init() {
    super.init();
    ArcBasisSynthesizeCompKindFromMCBasicTypes synFromBasic = new ArcBasisSynthesizeCompKindFromMCBasicTypes(resultWrapper);
    ArcBasisSynthesizeCompKindFromMCCollectionTypes synFromCollection = new ArcBasisSynthesizeCompKindFromMCCollectionTypes(resultWrapper);

    traverser.setMCBasicTypesHandler(synFromBasic);
    traverser.setMCCollectionTypesHandler(synFromCollection);
  }
}
