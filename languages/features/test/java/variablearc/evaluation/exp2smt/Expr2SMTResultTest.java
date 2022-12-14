/* (c) https://github.com/MontiCore/monticore */
package variablearc.evaluation.exp2smt;

import com.microsoft.z3.ArithExpr;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Expr;
import com.microsoft.z3.IntExpr;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import variablearc.AbstractTest;

/**
 * Tests for {@link Expr2SMTResult}
 */
public class Expr2SMTResultTest extends AbstractTest {

  @Test
  public void shouldNotGetValue() {
    // Given
    Expr2SMTResult result = new Expr2SMTResult();

    // When
    result.clear();

    // Then
    Assertions.assertAll(() -> {
          Assertions.assertTrue(result.getValue().isEmpty());
          Assertions.assertTrue(result.getValueAsArith().isEmpty());
          Assertions.assertTrue(result.getValueAsInt().isEmpty());
          Assertions.assertTrue(result.getValueAsBool().isEmpty());
        }
    );
  }

  @Test
  public void shouldGetValue() {
    // Given
    Expr<?> expr = Mockito.mock(Expr.class);
    Expr2SMTResult result = new Expr2SMTResult();

    // When
    result.setValue(expr);

    // Then
    Assertions.assertAll(() -> {
          Assertions.assertTrue(result.getValue().isPresent());
          Assertions.assertEquals(result.getValue().get(), expr);
          Assertions.assertTrue(result.getValueAsArith().isEmpty());
          Assertions.assertTrue(result.getValueAsInt().isEmpty());
          Assertions.assertTrue(result.getValueAsBool().isEmpty());
        }
    );
  }

  @Test
  public void shouldGetBooleanValue() {
    // Given
    Expr<?> expr = Mockito.mock(BoolExpr.class);
    Expr2SMTResult result = new Expr2SMTResult();

    // When
    result.setValue(expr);

    // Then
    Assertions.assertAll(() -> {
          Assertions.assertTrue(result.getValue().isPresent());
          Assertions.assertEquals(result.getValue().get(), expr);
          Assertions.assertTrue(result.getValueAsBool().isPresent());
          Assertions.assertEquals(result.getValueAsBool().get(), expr);
          Assertions.assertTrue(result.getValueAsArith().isEmpty());
          Assertions.assertTrue(result.getValueAsInt().isEmpty());
        }
    );
  }

  @Test
  public void shouldGetArithmeticValue() {
    // Given
    Expr<?> expr = Mockito.mock(ArithExpr.class);
    Expr2SMTResult result = new Expr2SMTResult();

    // When
    result.setValue(expr);

    // Then
    Assertions.assertAll(() -> {
          Assertions.assertTrue(result.getValue().isPresent());
          Assertions.assertEquals(result.getValue().get(), expr);
          Assertions.assertTrue(result.getValueAsArith().isPresent());
          Assertions.assertEquals(result.getValueAsArith().get(), expr);
          Assertions.assertTrue(result.getValueAsBool().isEmpty());
          Assertions.assertTrue(result.getValueAsInt().isEmpty());
        }
    );
  }

  @Test
  public void shouldGetIntegerValue() {
    // Given
    Expr<?> expr = Mockito.mock(IntExpr.class);
    Expr2SMTResult result = new Expr2SMTResult();

    // When
    result.setValue(expr);

    // Then
    Assertions.assertAll(() -> {
          Assertions.assertTrue(result.getValue().isPresent());
          Assertions.assertEquals(result.getValue().get(), expr);
          Assertions.assertTrue(result.getValueAsArith().isPresent());
          Assertions.assertEquals(result.getValueAsArith().get(), expr);
          Assertions.assertTrue(result.getValueAsInt().isPresent());
          Assertions.assertEquals(result.getValueAsInt().get(), expr);
          Assertions.assertTrue(result.getValueAsBool().isEmpty());
        }
    );
  }

}
