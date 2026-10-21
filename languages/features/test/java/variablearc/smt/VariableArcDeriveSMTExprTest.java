/* (c) https://github.com/MontiCore/monticore */
package variablearc.smt;

import com.microsoft.z3.BoolSort;
import com.microsoft.z3.Context;
import com.microsoft.z3.IntSort;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import variablearc.VariableArcMill;
import variablearc.VariableArcTestBase;
import variablearc.smt.exp2smt.IDeriveSMTExpr;

import static org.mockito.Mockito.when;

/**
 * Tests for {@link VariableArcDeriveSMTExpr}
 */
public class VariableArcDeriveSMTExprTest extends VariableArcTestBase {

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
  }

  @Test
  public void shouldNameUnknownsByExpression() {
    // Given
    VariableArcDeriveSMTExpr derive = new VariableArcDeriveSMTExpr(createContext());
    ASTExpression first = VariableArcMill.nameExpressionBuilder().setName("a").build();
    ASTExpression second = VariableArcMill.nameExpressionBuilder().setName("b").build();

    // When
    String firstName = derive.unknownName(first);
    String secondName = derive.unknownName(second);

    // Then
    Assertions.assertEquals(first.get_SourcePositionStart(), second.get_SourcePositionStart());
    Assertions.assertNotEquals(firstName, secondName);
    Assertions.assertEquals(firstName, derive.unknownName(first));
    Assertions.assertTrue(firstName.contains(IDeriveSMTExpr.UNKNOWN_MARKER));
  }
}
