/* (c) https://github.com/MontiCore/monticore */
package arcbasis.check;

import arcbasis.AbstractTest;
import arcbasis.ArcBasisMill;
import arcbasis._symboltable.ArcBasisScope;
import arcbasis._symboltable.IArcBasisScope;
import arcbasis._visitor.ArcBasisInheritanceVisitor;
import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis.ExpressionsBasisMill;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.expressions.expressionsbasis._symboltable.IExpressionsBasisScope;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.typesymbols._symboltable.FieldSymbol;
import de.monticore.types.typesymbols._symboltable.TypeSymbol;
import montiarc.util.check.AbstractArcTypesCalculator;
import montiarc.util.check.IArcTypesCalculator;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Collections;
import java.util.stream.Stream;

/**
 * Abstract class for test subclasses of {@link AbstractArcTypesCalculator}.
 * <p>
 * Override {@link this#getTypesCalculator()} with method that returns the type
 * calculator under test.
 */
public abstract class AbstractArcTypesCalculatorTest extends AbstractTest {

  protected IArcTypesCalculator typesCalculator;
  protected IArcBasisScope scope;
  protected ArcBasisExpressionsScopeSetter scopeSetter;

  protected static Stream<Arguments> expressionProviderForPrimitiveFields() {
    return Stream.of(Arguments.of("a", "int"), Arguments.of("b", "int"), Arguments.of("c", "int"));
  }

  protected static Stream<Arguments> expressionProviderForObjectFields() {
    return Stream.of(Arguments.of("s", "Student"));
  }

  protected abstract IArcTypesCalculator getTypesCalculator();

  protected abstract IArcBasisScope getScope();

  protected ArcBasisExpressionsScopeSetter getScopeSetter() {
    if (this.scopeSetter == null) {
      this.scopeSetter = new ArcBasisExpressionsScopeSetter(this.getScope());
    }
    return this.scopeSetter;
  }

  @BeforeEach
  public void setUpTypes() {
    this.addBasicTypes2Scope(this.getScope());
    TypeSymbol p =
      ArcBasisMill.typeSymbolBuilder().setSpannedScope(new ArcBasisScope()).setName("Person").build();
    TypeSymbol r = ArcBasisMill.typeSymbolBuilder().setSpannedScope(new ArcBasisScope()).setName("Role").build();
    TypeSymbol t =
      ArcBasisMill.typeSymbolBuilder().setSpannedScope(new ArcBasisScope()).setName("Teacher").build();
    TypeSymbol s =
      ArcBasisMill.typeSymbolBuilder().setSpannedScope(new ArcBasisScope()).setName("Student").build();
    TypeSymbol f =
      ArcBasisMill.typeSymbolBuilder().setSpannedScope(new ArcBasisScope()).setName("FirstGrader").build();
    this.add2Scope(this.getScope(), p, r, t, s, f);
    t.setSuperTypeList(Collections.singletonList(SymTypeExpressionFactory.createTypeObject("Role", this.getScope())));
    s.setSuperTypeList(Collections.singletonList(SymTypeExpressionFactory.createTypeObject("Role", this.getScope())));
    f.setSuperTypeList(Collections.singletonList(SymTypeExpressionFactory.createTypeObject("Student",
      this.getScope())));
  }

  @BeforeEach
  public void setUpFields() {
    FieldSymbol a = ArcBasisMill.fieldSymbolBuilder().setName("a")
      .setType(SymTypeExpressionFactory.createTypeConstant("int")).build();
    FieldSymbol b = ArcBasisMill.fieldSymbolBuilder().setName("b")
      .setType(SymTypeExpressionFactory.createTypeConstant("int")).build();
    FieldSymbol c = ArcBasisMill.fieldSymbolBuilder().setName("c")
      .setType(SymTypeExpressionFactory.createTypeConstant("int")).build();
    FieldSymbol s = ArcBasisMill.fieldSymbolBuilder().setName("s")
      .setType(SymTypeExpressionFactory.createTypeObject("Student", this.getScope())).build();
    this.add2Scope(scope, a, b, c, s);
  }

  protected void doShouldCalculateType(@NotNull ASTExpression expression, @NotNull String expectedType,
                                       boolean isPrimitive, boolean isGeneric) {
    Preconditions.checkArgument(expression != null);
    Preconditions.checkArgument(expectedType != null);
    //Given
    expression.accept(this.getScopeSetter());

    //When
    this.getTypesCalculator().calculateType(expression);

    //Then
    Assertions.assertTrue(this.getTypesCalculator().getResult().isPresent());
    Assertions.assertEquals(isPrimitive, this.getTypesCalculator().getResult().get().isPrimitive());
    Assertions.assertEquals(isGeneric, this.getTypesCalculator().getResult().get().isGenericType());
    Assertions.assertFalse(this.getTypesCalculator().getResult().get().isTypeVariable());
    Assertions.assertTrue(this.getTypesCalculator().getResult().get().isTypeInfoLoadable());
    Assertions.assertEquals(expectedType, this.getTypesCalculator().getResult().get().print());
  }

  protected void doCalculateTypeFromNameExpression(@NotNull String expression, @NotNull String expectedType,
                                                   boolean isPrimitive, boolean isGeneric) {
    Preconditions.checkArgument(expression != null);
    Preconditions.checkArgument(expectedType != null);
    doShouldCalculateType(this.doBuildNameExpression(expression), expectedType,
      isPrimitive, isGeneric);
  }

  protected ASTNameExpression doBuildNameExpression(@NotNull String expression) {
    Preconditions.checkArgument(expression != null);
    return ExpressionsBasisMill.nameExpressionBuilder().setName(expression).build();
  }

  /**
   * Method under test {@link ArcBasisTypesCalculator#calculateType(ASTExpression)}.
   */
  @ParameterizedTest
  @MethodSource("expressionProviderForPrimitiveFields")
  void shouldCalculatePrimitiveType(@NotNull String expression, @NotNull String expectedType) {
    Preconditions.checkArgument(expression != null);
    Preconditions.checkArgument(expectedType != null);
    doCalculateTypeFromNameExpression(expression, expectedType, true, false);
  }

  /**
   * Method under test {@link ArcBasisTypesCalculator#calculateType(ASTExpression)}.
   */
  @ParameterizedTest
  @MethodSource("expressionProviderForObjectFields")
  void shouldCalculateObjectType(@NotNull String expression, @NotNull String expectedType) {
    Preconditions.checkArgument(expression != null);
    Preconditions.checkArgument(expectedType != null);
    doCalculateTypeFromNameExpression(expression, expectedType, false, false);
  }

  /**
   * Method under test {@link ArcBasisTypesCalculator#calculateType(ASTExpression)}.
   */
  @Test
  void ShouldNotCalculateResult() {
    //Given
    ASTExpression expr = ExpressionsBasisMill.nameExpressionBuilder().setName("d").build();
    expr.setEnclosingScope(this.getScope());

    //When
    this.getTypesCalculator().calculateType(expr);

    //Then
    Assertions.assertFalse(this.getTypesCalculator().getResult().isPresent());
  }

  public static class ArcBasisExpressionsScopeSetter implements ArcBasisInheritanceVisitor {

    protected IExpressionsBasisScope scope;

    public ArcBasisExpressionsScopeSetter(@NotNull IExpressionsBasisScope scope) {
      Preconditions.checkArgument(scope != null);
      this.scope = scope;
    }

    @Override
    public ArcBasisExpressionsScopeSetter getRealThis() {
      return this;
    }

    public IExpressionsBasisScope getScope() {
      return this.scope;
    }

    public void setScope(@NotNull IExpressionsBasisScope scope) {
      Preconditions.checkArgument(scope != null);
      this.scope = scope;
    }

    @Override
    public void visit(@NotNull ASTExpression node) {
      Preconditions.checkArgument(node != null);
      Preconditions.checkState(this.getScope() != null);
      Preconditions.checkArgument(node.getEnclosingScope() == null ||
        node.getEnclosingScope() == this.getScope());
      node.setEnclosingScope(this.getScope());
    }
  }
}