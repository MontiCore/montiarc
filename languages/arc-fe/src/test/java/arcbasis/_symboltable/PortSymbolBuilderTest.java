/* (c) https://github.com/MontiCore/monticore */
package arcbasis._symboltable;

import arcbasis.AbstractTest;
import arcbasis.ArcBasisMill;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.typesymbols.TypeSymbolsMill;
import de.monticore.types.typesymbols._symboltable.TypeSymbolLoader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;

/**
 * Holds tests for the handwritten methods of {@link PortSymbolBuilder}.
 */
public class PortSymbolBuilderTest extends AbstractTest {

  @Test
  public void shouldBeValid() {
    PortSymbolBuilder builder = new PortSymbolBuilder();
    builder.setName("in1").setType(mock(SymTypeExpression.class))
      .setDirection(arcbasis.ArcBasisMill.portDirectionInBuilder().build());
    Assertions.assertTrue(builder.isValid());
  }

  @Test
  public void shouldBeInvalid() {
    PortSymbolBuilder builderWithoutType = new PortSymbolBuilder();
    PortSymbolBuilder builderWithoutName = new PortSymbolBuilder();
    PortSymbolBuilder builderWithoutDirection = new PortSymbolBuilder();
    builderWithoutType.setName("out1")
      .setDirection(arcbasis.ArcBasisMill.portDirectionOutBuilder().build());
    builderWithoutName.setType(mock(SymTypeExpression.class))
      .setDirection(arcbasis.ArcBasisMill.portDirectionInBuilder().build());
    builderWithoutDirection.setName("in1").setType(mock(SymTypeExpression.class));
    Assertions.assertFalse(builderWithoutType.isValid());
    Assertions.assertFalse(builderWithoutName.isValid());
    Assertions.assertFalse(builderWithoutDirection.isValid());
  }

  @Test
  public void shouldBuildWithExpectedType() {
    TypeSymbolLoader type = TypeSymbolsMill.typeSymbolLoaderBuilder().setName("A").build();
    SymTypeExpression typeExpression = SymTypeExpressionFactory.createTypeObject(type);
    PortSymbol symbol = ArcBasisMill.portSymbolBuilder()
      .setName("in2").setType(typeExpression)
      .setDirection(arcbasis.ArcBasisMill.portDirectionInBuilder().build())
      .build();
    Assertions.assertEquals(symbol.getType(), typeExpression);
  }
}