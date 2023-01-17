/* (c) https://github.com/MontiCore/monticore */
package variablearc._symboltable;

import arcbasis._symboltable.ComponentInstanceSymbol;
import arcbasis._symboltable.ComponentTypeSymbol;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import org.codehaus.commons.nullanalysis.Nullable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.mockito.Mockito;
import variablearc.AbstractTest;
import variablearc.VariableArcMill;
import variablearc._visitor.VariableArcTraverser;
import variablearc.check.TypeExprOfVariableComponent;

import java.util.Arrays;
import java.util.List;

/**
 * Tests for {@link VariableArcSymbolTableCompleter}
 */
public class VariableArcSymbolTableCompleterTest extends AbstractTest {

  protected VariableArcSymbolTableCompleter completer;

  protected VariableArcSymbolTableCompleter getCompleter() {
    return this.completer;
  }

  protected void setUpCompleter() {
    this.completer = VariableArcMill.symbolTableCompleter();
  }

  @BeforeEach
  @Override
  public void init() {
    super.init();
    this.setUpCompleter();
  }

  /**
   * Method under test {@link VariableArcSymbolTableCompleter#setTraverser(VariableArcTraverser)}
   */
  @Test
  public void shouldSetTraverser() {
    // Given
    VariableArcTraverser traverser = VariableArcMill.traverser();

    // When
    this.getCompleter().setTraverser(traverser);

    // Then
    Assertions.assertEquals(traverser, this.getCompleter().getTraverser());
  }

  /**
   * Method under test {@link VariableArcSymbolTableCompleter#setTraverser(VariableArcTraverser)}
   *
   * @param traverser the traverser to set (null)
   */
  @ParameterizedTest
  @NullSource
  public void setTraverserShouldThrowNullPointerException(@Nullable VariableArcTraverser traverser) {
    // When && Then
    Assertions.assertThrows(NullPointerException.class, () -> this.getCompleter()
      .setTraverser(traverser));
  }

  /**
   * Method under test {@link VariableArcSymbolTableCompleter#visit(ComponentInstanceSymbol)}
   */
  @Test
  public void shouldVisitComponentInstanceSymbol() {
    // Given
    IVariableArcScope scope = VariableArcMill.scope();
    VariableSymbol parameter1 = VariableArcMill.variableSymbolBuilder()
      .setName("a").setEnclosingScope(scope).build();
    scope.add(parameter1);
    VariableSymbol parameter2 = VariableArcMill.variableSymbolBuilder()
      .setName("b").setEnclosingScope(scope).build();
    scope.add(parameter2);
    ComponentTypeSymbol component = VariableArcMill.componentTypeSymbolBuilder()
      .setParameters(Arrays.asList(parameter1, parameter2)) // List.of produces an
      .setName("C")
      .setSpannedScope(scope)
      .build();
    TypeExprOfVariableComponent typeExpr = new TypeExprOfVariableComponent(component);

    List<ASTExpression> bindings = Arrays.asList(Mockito.mock(ASTExpression.class), Mockito.mock(ASTExpression.class));
    ComponentInstanceSymbol symbol = VariableArcMill.componentInstanceSymbolBuilder()
      .setArguments(bindings).setType(typeExpr).setName("c1").build();

    // When
    getCompleter().visit(symbol);

    // Then
    Assertions.assertNotEquals(typeExpr, symbol.getType());
    Assertions.assertEquals(typeExpr.getTypeInfo(), symbol.getType()
      .getTypeInfo());
    Assertions.assertTrue(symbol.getType() instanceof TypeExprOfVariableComponent);
    Assertions.assertIterableEquals(bindings, ((TypeExprOfVariableComponent) symbol.getType()).getBindingsAsList());
  }
}
