/* (c) https://github.com/MontiCore/monticore */
package variablearc.evaluation;

import com.microsoft.z3.BoolSort;
import com.microsoft.z3.Context;
import com.microsoft.z3.IntSort;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import variablearc.VariableArcAbstractTest;

import static org.mockito.Mockito.when;

/**
 * Tests for {@link VariableArcDeriveSMTExpr}
 */
public class VariableArcDeriveSMTExprTest extends VariableArcAbstractTest {

  protected static Context createContext() {
    Context context = Mockito.mock(Context.class);
    when(context.getBoolSort()).thenReturn(Mockito.mock(BoolSort.class));
    when(context.getIntSort()).thenReturn(Mockito.mock(IntSort.class));
    return context;
  }

  @Test
  public void setup() {
    // Given
    Context context = createContext();

    // When
    VariableArcDeriveSMTExpr derive = new VariableArcDeriveSMTExpr(context);

    // Then
    Assertions.assertEquals(context, derive.getContext());
    Assertions.assertNotNull(derive.getResult());
    Assertions.assertTrue(derive.getResult().getValue().isEmpty());
    Assertions.assertNotNull(derive.getSortDerive());
    Assertions.assertNotNull(derive.getTraverser());
    Assertions.assertEquals("", derive.getPrefix());
  }

  @Test
  public void setPrefix() {
    final String PREFIX = "test.test";
    // Given
    VariableArcDeriveSMTExpr derive = new VariableArcDeriveSMTExpr(createContext());

    // When
    derive.setPrefix(PREFIX);

    // Then
    Assertions.assertEquals(PREFIX, derive.getPrefix());
  }
}
