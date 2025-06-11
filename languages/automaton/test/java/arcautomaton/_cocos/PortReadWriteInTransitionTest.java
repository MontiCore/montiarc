/* (c) https://github.com/MontiCore/monticore */
package arcautomaton._cocos;

import arcautomaton.ArcAutomatonTestBase;
import arcautomaton._visitor.ArcAutomatonTraverser;
import arcbasis._cocos.PortReadWriteHandler4ExpressionsBasis;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PortReadWriteInTransitionTest extends ArcAutomatonTestBase {

  @Test
  void shouldConstructObject1() {
    // When
    PortReadWriteInTransition coco = new PortReadWriteInTransition();

    // Then
    assertThat(coco.traverser).isNotNull();
    assertThat(coco.traverser).isInstanceOf(ArcAutomatonTraverser.class);
    assertThat(coco.traverser.getExpressionsBasisHandler()).isPresent();
    assertThat(coco.traverser.getExpressionsBasisHandler().get())
      .isInstanceOf(PortReadWriteHandler4ExpressionsBasis.class);
  }
}
