/* (c) https://github.com/MontiCore/monticore */
package variablearc._ast;

import arcbasis._ast.ASTArcComponentType;
import arcbasis._ast.ASTComponentBody;
import arcbasis._ast.ASTComponentHead;
import de.se_rwth.commons.SourcePosition;
import org.junit.jupiter.api.Test;
import variablearc.VariableArcMill;
import variablearc.VariableArcTestBase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;

/**
 * The class under test is {@link ASTVariableArcComponentTypeBuilder}.
 */
class ASTVariableArcComponentTypeBuilderTest extends VariableArcTestBase {

  @Test
  void shouldBeProvidedByMill() {
    // When && Then
    assertThat(VariableArcMill.arcComponentTypeBuilder()).isInstanceOf(ASTVariableArcComponentTypeBuilder.class);
  }

  @Test
  void shouldBuildComponentTypeWithAttributes() {
    // Given
    ASTComponentHead head = VariableArcMill.componentHeadBuilder().build();
    ASTComponentBody body = VariableArcMill.componentBodyBuilder().build();
    SourcePosition start = new SourcePosition(1, 0);
    SourcePosition end = new SourcePosition(2, 1);

    // When
    ASTArcComponentType component = VariableArcMill.arcComponentTypeBuilder()
      .setName("C")
      .setHead(head)
      .setBody(body)
      .addInstance("c")
      .set_SourcePositionStart(start)
      .set_SourcePositionEnd(end)
      .build();

    // Then
    assertThat(component).isInstanceOf(ASTVariableArcComponentType.class);
    assertThat(component.getName()).isEqualTo("C");
    assertThat(component.getHead()).isSameAs(head);
    assertThat(component.getBody()).isSameAs(body);
    assertThat(component.getComponentInstanceList()).singleElement()
      .extracting(instance -> instance.getName()).isEqualTo("c");
    assertThat(component.isPresentStereotype()).isFalse();
    assertThat(component.get_SourcePositionStart()).isEqualTo(start);
    assertThat(component.get_SourcePositionEnd()).isEqualTo(end);
  }

  @Test
  void shouldBuildUncheckedComponentType() {
    // When
    ASTArcComponentType component = VariableArcMill.arcComponentTypeBuilder().uncheckedBuild();

    // Then
    assertThat(component).isInstanceOf(ASTVariableArcComponentType.class);
  }

  @Test
  void shouldRejectIncompleteComponentType() {
    // When && Then
    assertThatIllegalStateException().isThrownBy(() -> VariableArcMill.arcComponentTypeBuilder().setName("C").build());
  }
}
