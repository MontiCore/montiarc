/* (c) https://github.com/MontiCore/monticore */
package arcautomaton._ast;

import arcautomaton.ArcAutomatonMill;
import arcautomaton._symboltable.ArcAutomatonScopesGenitor;
import arcautomaton._symboltable.TestScopesGenitorDelegator;
import arcbasis.ArcBasisAbstractTest;
import arcbasis._ast.ASTComponentType;
import com.google.common.base.Preconditions;
import montiarc.Timing;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@link ArcAutomatonScopesGenitor} is the class under test.
 */
public class ASTArcStatechartTest extends ArcBasisAbstractTest {

  @Override
  @BeforeEach
  public void init() {
    ArcAutomatonMill.globalScope().clear();
    ArcAutomatonMill.reset();
    ArcAutomatonMill.init();
    addBasicTypes2Scope();
  }

  @ParameterizedTest
  @EnumSource(Timing.class)
  public void shouldSetTiming(@NotNull Timing timing) {
    Preconditions.checkNotNull(timing);

    // Given
    ASTComponentType ast = ArcAutomatonMill.componentTypeBuilder().setName("Comp")
      .setHead(ArcAutomatonMill.componentHeadBuilder().build())
      .setBody(ArcAutomatonMill.componentBodyBuilder()
        .addArcElement(ArcAutomatonMill.arcStatechartBuilder()
          .setStereotype(ArcAutomatonMill.stereotypeBuilder()
            .addValues(ArcAutomatonMill.stereoValueBuilder()
              .setName(timing.getName())
              .setContent("")
              .build())
            .build())
          .build())
        .build())
      .build();

    TestScopesGenitorDelegator sgd = new TestScopesGenitorDelegator();

    // When
    sgd.handle(ast);

    // Then
    assertThat(ast.getSymbol().getTiming().isPresent()).isTrue();
    assertThat(ast.getSymbol().getTiming().get()).isEqualTo(timing);
  }
}
