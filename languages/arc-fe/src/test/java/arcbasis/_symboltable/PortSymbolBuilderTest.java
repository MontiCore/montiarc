/* (c) https://github.com/MontiCore/monticore */
package arcbasis._symboltable;

import arcbasis._ast.ArcBasisMill;
import arcbasis.util.ArcError;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.typesymbols._symboltable.*;
import montiarc.AbstractTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;

import java.util.regex.Pattern;

/**
 * Holds tests for the handwritten methods of {@link PortSymbolBuilder}.
 */
public class PortSymbolBuilderTest extends AbstractTest {

  @Override
  protected Pattern supplyErrorCodePattern() {
    return ArcError.ERROR_CODE_PATTERN;
  }

  @Test
  public void shouldBeValid() {
    PortSymbolBuilder builder = new PortSymbolBuilder();
    builder.setName("in1").setType(mock(SymTypeExpression.class))
      .setDirection(ArcBasisMill.portDirectionInBuilder().build());
    Assertions.assertTrue(builder.isValid());
  }

  @Test
  public void shouldBeInvalid() {
    PortSymbolBuilder builderWithoutType = new PortSymbolBuilder();
    PortSymbolBuilder builderWithoutName = new PortSymbolBuilder();
    PortSymbolBuilder builderWithoutDirection = new PortSymbolBuilder();
    builderWithoutType.setName("out1")
      .setDirection(ArcBasisMill.portDirectionOutBuilder().build());
    builderWithoutName.setType(mock(SymTypeExpression.class))
      .setDirection(ArcBasisMill.portDirectionInBuilder().build());
    builderWithoutDirection.setName("in1").setType(mock(SymTypeExpression.class));
    Assertions.assertFalse(builderWithoutType.isValid());
    Assertions.assertFalse(builderWithoutName.isValid());
    Assertions.assertFalse(builderWithoutDirection.isValid());
  }

  @Test
  public void shouldBuildWithExpectedType() {
    TypeSymbolLoader type = TypeSymbolsSymTabMill.typeSymbolLoaderBuilder().setName("A").build();
    SymTypeExpression typeExpression = SymTypeExpressionFactory.createTypeObject(type);
    PortSymbol symbol = ArcBasisSymTabMill.portSymbolBuilder()
      .setName("in2").setType(typeExpression)
      .setDirection(ArcBasisMill.portDirectionInBuilder().build())
      .build();
    Assertions.assertEquals(symbol.getType(), typeExpression);
  }
}