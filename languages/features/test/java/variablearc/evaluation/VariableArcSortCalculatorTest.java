/* (c) https://github.com/MontiCore/monticore */
package variablearc.evaluation;

import com.microsoft.z3.BoolSort;
import com.microsoft.z3.Context;
import com.microsoft.z3.IntSort;
import com.microsoft.z3.RealSort;
import com.microsoft.z3.Sort;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.types.check.SymTypeExpressionFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import variablearc.VariableArcTestBase;

import java.util.Optional;

import static org.mockito.Mockito.when;

/**
 * Tests for {@link VariableArcDeriveSMTSort}
 */
public class VariableArcSortCalculatorTest extends VariableArcTestBase {

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
    VariableArcDeriveSMTSort calculator = new VariableArcDeriveSMTSort(context);

    // When
    Optional<Sort> sort = calculator.toSort(SymTypeExpressionFactory.createPrimitive(BasicSymbolsMill.BOOLEAN));

    // Then
    Assertions.assertTrue(sort.isPresent());
    Assertions.assertInstanceOf(BoolSort.class, sort.get());
  }

  @Test
  public void shouldDeriveInt() {
    // Given
    Context context = createContext();
    VariableArcDeriveSMTSort calculator = new VariableArcDeriveSMTSort(context);

    // When
    Optional<Sort> sortByte = calculator.toSort(SymTypeExpressionFactory.createPrimitive(BasicSymbolsMill.BYTE));
    Optional<Sort> sortInt = calculator.toSort(SymTypeExpressionFactory.createPrimitive(BasicSymbolsMill.INT));
    Optional<Sort> sortShort = calculator.toSort(SymTypeExpressionFactory.createPrimitive(BasicSymbolsMill.SHORT));
    Optional<Sort> sortChar = calculator.toSort(SymTypeExpressionFactory.createPrimitive(BasicSymbolsMill.CHAR));
    Optional<Sort> sortLong = calculator.toSort(SymTypeExpressionFactory.createPrimitive(BasicSymbolsMill.LONG));

    // Then
    Assertions.assertTrue(sortByte.isPresent());
    Assertions.assertInstanceOf(IntSort.class, sortByte.get());
    Assertions.assertTrue(sortInt.isPresent());
    Assertions.assertInstanceOf(IntSort.class, sortInt.get());
    Assertions.assertTrue(sortShort.isPresent());
    Assertions.assertInstanceOf(IntSort.class, sortShort.get());
    Assertions.assertTrue(sortChar.isPresent());
    Assertions.assertInstanceOf(IntSort.class, sortChar.get());
    Assertions.assertTrue(sortLong.isPresent());
    Assertions.assertInstanceOf(IntSort.class, sortLong.get());
  }

  @Test
  public void shouldDeriveFloat() {
    // Given
    Context context = createContext();
    VariableArcDeriveSMTSort calculator = new VariableArcDeriveSMTSort(context);

    // When
    Optional<Sort> sort = calculator.toSort(SymTypeExpressionFactory.createPrimitive(BasicSymbolsMill.FLOAT));

    // Then
    Assertions.assertTrue(sort.isPresent());
    Assertions.assertInstanceOf(RealSort.class, sort.get());
  }

  @Test
  public void shouldDeriveDouble() {
    // Given
    Context context = createContext();
    VariableArcDeriveSMTSort calculator = new VariableArcDeriveSMTSort(context);

    // When
    Optional<Sort> sort = calculator.toSort(SymTypeExpressionFactory.createPrimitive(BasicSymbolsMill.DOUBLE));

    // Then
    Assertions.assertTrue(sort.isPresent());
    Assertions.assertInstanceOf(RealSort.class, sort.get());
  }
}
