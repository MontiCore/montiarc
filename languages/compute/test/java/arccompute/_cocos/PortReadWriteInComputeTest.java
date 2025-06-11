/* (c) https://github.com/MontiCore/monticore */
package arccompute._cocos;

import arcbasis._cocos.PortReadWriteHandler4ExpressionsBasis;
import arccompute._visitor.ArcComputeTraverser;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PortReadWriteInComputeTest extends ArcComputeTestBase {

  @Test
  void shouldConstructObject1() {
    // When
    PortReadWriteInCompute coco = new PortReadWriteInCompute();

    // Then
    assertThat(coco.traverser).isNotNull();
    assertThat(coco.traverser).isInstanceOf(ArcComputeTraverser.class);
    assertThat(coco.traverser.getExpressionsBasisHandler()).isPresent();
    assertThat(coco.traverser.getExpressionsBasisHandler().get())
      .isInstanceOf(PortReadWriteHandler4ExpressionsBasis.class);
  }
}
