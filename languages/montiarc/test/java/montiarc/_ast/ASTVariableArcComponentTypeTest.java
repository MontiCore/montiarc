/* (c) https://github.com/MontiCore/monticore */
package montiarc._ast;

import arcbasis._ast.ASTArcComponentType;
import montiarc.MontiArcTestBase;
import org.junit.jupiter.api.Test;
import variablearc._ast.ASTVariableArcComponentType;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * The class under test is {@link ASTVariableArcComponentType}.
 */
class ASTVariableArcComponentTypeTest extends MontiArcTestBase {

  protected static final String MODEL = """
    component C {
      feature f;
      varif (f) {
        port in int i;
      }
      component Inner inner {
        feature g;
        varif (g) {
          port in int j;
        }
      }
    }""";

  @Test
  void shouldParseComponentTypesWithPresence() {
    // Given
    ASTMACompilationUnit ast = compile(MODEL);
    ASTArcComponentType inner = ast.getArcComponentType().getSymbol().getSpannedScope()
      .resolveComponentTypeLocally("Inner").map(type -> (ASTArcComponentType) type.getAstNode()).orElseThrow();

    // When && Then
    assertThat(ast.getArcComponentType()).isInstanceOfSatisfying(ASTVariableArcComponentType.class, component -> {
      assertThat(component.getLocalPresence()).isPresent();
      assertThat(component.getNodePresence()).containsKey(component.getSymbol().getPort("i").orElseThrow().getAstNode());
    });
    assertThat(inner).isInstanceOfSatisfying(ASTVariableArcComponentType.class, component -> {
      assertThat(component.getLocalPresence()).isPresent();
      assertThat(component.getNodePresence()).containsKey(component.getSymbol().getPort("j").orElseThrow().getAstNode());
    });
  }

  @Test
  void shouldNotCopyPresenceOnDeepClone() {
    // Given
    ASTMACompilationUnit ast = compile(MODEL);

    // When
    ASTArcComponentType clone = ast.getArcComponentType().deepClone();

    // Then
    assertThat(clone).isInstanceOfSatisfying(ASTVariableArcComponentType.class, component -> {
      assertThat(component.getLocalPresence()).isEmpty();
      assertThat(component.getNodePresence()).isEmpty();
    });
  }
}
