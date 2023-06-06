/* (c) https://github.com/MontiCore/monticore */
package variablearc.evaluation;

import com.microsoft.z3.BoolSort;
import com.microsoft.z3.Context;
import com.microsoft.z3.FPSort;
import com.microsoft.z3.IntSort;
import com.microsoft.z3.RealSort;
import com.microsoft.z3.Sort;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.types.check.SymTypeExpressionFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import variablearc.VariableArcAbstractTest;
import variablearc.check.VariableArcTypeCalculator;

import java.util.Optional;

import static org.mockito.Mockito.when;

/**
 * Tests for {@link VariableArcDeriveSMTSort}
 */
public class VariableArcSortCalculatorTest extends VariableArcAbstractTest {

  protected static Context createContext() {
    Context context = Mockito.mock(Context.class);
    when(context.getBoolSort()).thenReturn(Mockito.mock(BoolSort.class));
    when(context.getIntSort()).thenReturn(Mockito.mock(IntSort.class));
    when(context.getRealSort()).thenReturn(Mockito.mock(RealSort.class));
    return context;
  }

  @Test
  public void shouldDeriveBool() {
    // Given
    Context context = createContext();
    VariableArcDeriveSMTSort calculator = new VariableArcDeriveSMTSort(new VariableArcTypeCalculator());

    // When
    Optional<Sort> sort = calculator.toSort(context, SymTypeExpressionFactory.createPrimitive(BasicSymbolsMill.BOOLEAN));

    // Then
    Assertions.assertTrue(sort.isPresent());
    Assertions.assertTrue(sort.get() instanceof BoolSort);
  }

  @Test
  public void shouldDeriveInt() {
    // Given
    Context context = createContext();
    VariableArcDeriveSMTSort calculator = new VariableArcDeriveSMTSort(new VariableArcTypeCalculator());

    // When
    Optional<Sort> sortByte = calculator.toSort(context, SymTypeExpressionFactory.createPrimitive(BasicSymbolsMill.BYTE));
    Optional<Sort> sortInt = calculator.toSort(context, SymTypeExpressionFactory.createPrimitive(BasicSymbolsMill.INT));
    Optional<Sort> sortShort = calculator.toSort(context, SymTypeExpressionFactory.createPrimitive(BasicSymbolsMill.SHORT));
    Optional<Sort> sortChar = calculator.toSort(context, SymTypeExpressionFactory.createPrimitive(BasicSymbolsMill.CHAR));
    Optional<Sort> sortLong = calculator.toSort(context, SymTypeExpressionFactory.createPrimitive(BasicSymbolsMill.LONG));

    // Then
    Assertions.assertAll(() -> {
      Assertions.assertTrue(sortByte.isPresent());
      Assertions.assertTrue(sortByte.get() instanceof IntSort);
      Assertions.assertTrue(sortInt.isPresent());
      Assertions.assertTrue(sortInt.get() instanceof IntSort);
      Assertions.assertTrue(sortShort.isPresent());
      Assertions.assertTrue(sortShort.get() instanceof IntSort);
      Assertions.assertTrue(sortChar.isPresent());
      Assertions.assertTrue(sortChar.get() instanceof IntSort);
      Assertions.assertTrue(sortLong.isPresent());
      Assertions.assertTrue(sortLong.get() instanceof IntSort);
    });
  }

  @Test
  public void shouldDeriveFloat() {
    // Given
    Context context = createContext();
    VariableArcDeriveSMTSort calculator = new VariableArcDeriveSMTSort(new VariableArcTypeCalculator());

    // When
    Optional<Sort> sort = calculator.toSort(context, SymTypeExpressionFactory.createPrimitive(BasicSymbolsMill.FLOAT));

    // Then
    Assertions.assertTrue(sort.isPresent());
    Assertions.assertTrue(sort.get() instanceof RealSort);
  }

  @Test
  public void shouldDeriveDouble() {
    // Given
    Context context = createContext();
    VariableArcDeriveSMTSort calculator = new VariableArcDeriveSMTSort(new VariableArcTypeCalculator());

    // When
    Optional<Sort> sort = calculator.toSort(context, SymTypeExpressionFactory.createPrimitive(BasicSymbolsMill.DOUBLE));

    // Then
    Assertions.assertTrue(sort.isPresent());
    Assertions.assertTrue(sort.get() instanceof RealSort);
  }
}
