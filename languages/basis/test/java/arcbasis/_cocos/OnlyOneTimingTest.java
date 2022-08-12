/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis.AbstractTest;
import arcbasis.ArcBasisMill;
import arcbasis.timing.Timing;
import de.monticore.umlstereotype._ast.ASTStereotype;
import montiarc.util.ArcError;
import org.junit.jupiter.api.Test;

import java.util.List;

public class OnlyOneTimingTest extends AbstractTest {

  @Test
  public void testWithMultipleTimings() {
    // Given
    ASTStereotype stereotype = ArcBasisMill.stereotypeBuilder().setValuesList(List.of(
        ArcBasisMill.stereoValueBuilder().setName(Timing.sync().getName()).setContent("").build(),
        ArcBasisMill.stereoValueBuilder().setName(Timing.delayed().getName()).setContent("").build()
    )).build();

    // When
    OnlyOneTiming coco = new OnlyOneTiming();
    coco.check(stereotype);

    // Then
    checkOnlyExpectedErrorsPresent(ArcError.TIMING_MULTIPLE);
  }
}
