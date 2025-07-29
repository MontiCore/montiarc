/* (c) https://github.com/MontiCore/monticore */
package arcbasis._symboltable;

import arcbasis.ArcBasisMill;
import arcbasis.ArcBasisTestBase;
import de.monticore.symbols.compsymbols._symboltable.PortSymbol;
import de.monticore.symbols.compsymbols._symboltable.PortSymbolBuilder;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;

/**
 * Holds tests for the handwritten methods of {@link PortSymbolBuilder}.
 */
public class ArcArcPortSymbolBuilderTest extends ArcBasisTestBase {

  @Test
  @Disabled
  public void shouldBeValid() {
    PortSymbolBuilder builder = new PortSymbolBuilder();
    builder.setName("in1").setType(mock(SymTypeExpression.class))
      .setIncoming(true).build();
    Assertions.assertTrue(builder.isValid());
  }

  @Test
  public void shouldBeInvalid() {
    PortSymbolBuilder builderWithoutType = new PortSymbolBuilder();
    PortSymbolBuilder builderWithoutName = new PortSymbolBuilder();
    builderWithoutType.setName("out1").setOutgoing(true);
    builderWithoutName.setType(mock(SymTypeExpression.class)).setIncoming(true);
    Assertions.assertFalse(builderWithoutType.isValid());
    Assertions.assertFalse(builderWithoutName.isValid());
  }

  @Test
  public void shouldBuildWithExpectedType() {
    SymTypeExpression typeExpression = SymTypeExpressionFactory.createPrimitive("int");
    PortSymbol symbol = ArcBasisMill.portSymbolBuilder()
      .setName("in2").setType(typeExpression)
      .setIncoming(true)
      .build();
    Assertions.assertEquals(symbol.getType(), typeExpression);
  }
}