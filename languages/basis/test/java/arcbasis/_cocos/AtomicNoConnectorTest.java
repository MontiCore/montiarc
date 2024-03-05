/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis.ArcBasisAbstractTest;
import arcbasis.ArcBasisMill;
import arcbasis._ast.ASTComponentHead;
import arcbasis._ast.ASTComponentInstantiation;
import arcbasis._ast.ASTComponentType;
import arcbasis._ast.ASTConnector;
import com.google.common.base.Preconditions;
import de.monticore.symbols.compsymbols._symboltable.SubcomponentSymbol;
import montiarc.util.ArcError;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

import java.util.stream.Stream;

/**
 * Tests for {@link AtomicNoConnector}
 */
public class AtomicNoConnectorTest extends ArcBasisAbstractTest {

  protected static Stream<Arguments> numberOfConnectorsWithErrorProvider() {
    return Stream.of(
      Arguments.of(0, true, new ArcError[]{}),
      Arguments.of(1, true, new ArcError[]{ArcError.CONNECTORS_IN_ATOMIC}),
      Arguments.of(2, true, new ArcError[]{ArcError.CONNECTORS_IN_ATOMIC, ArcError.CONNECTORS_IN_ATOMIC}),
      Arguments.of(3, true, new ArcError[]{ArcError.CONNECTORS_IN_ATOMIC, ArcError.CONNECTORS_IN_ATOMIC, ArcError.CONNECTORS_IN_ATOMIC}),
      Arguments.of(0, false, new ArcError[]{}),
      Arguments.of(1, false, new ArcError[]{}),
      Arguments.of(2, false, new ArcError[]{}),
      Arguments.of(3, false, new ArcError[]{})
    );
  }

  @ParameterizedTest
  @MethodSource("numberOfConnectorsWithErrorProvider")
  public void testCocoWithNBehaviors(int numberOfConnectors, boolean atomic, @NotNull ArcError... expectedErrors) {
    Preconditions.checkArgument(numberOfConnectors >= 0);
    Preconditions.checkNotNull(expectedErrors);

    // Given
    ASTComponentType compType = ArcBasisMill.componentTypeBuilder()
      .setName("Comp")
      .setBody(ArcBasisMill.componentBodyBuilder().build())
      .setHead(Mockito.mock(ASTComponentHead.class))
      .build();
    for (int i = 0; i < numberOfConnectors; i++) {
      compType.getBody().addArcElement(Mockito.mock(ASTConnector.class));
    }

    if (!atomic) {
      compType.getBody().addArcElement(Mockito.mock(ASTComponentInstantiation.class));
    }

    ArcBasisMill.scopesGenitorDelegator().createFromAST(compType);
    ArcBasisMill.scopesGenitorP2Delegator().createFromAST(compType);
    ArcBasisMill.scopesGenitorP3Delegator().createFromAST(compType);

    if (!atomic) {
      compType.getSpannedScope().add(Mockito.mock(SubcomponentSymbol.class));
    }

    // When
    AtomicNoConnector coco = new AtomicNoConnector();
    coco.check(compType);

    // Then
    checkOnlyExpectedErrorsPresent(expectedErrors);
  }
}
