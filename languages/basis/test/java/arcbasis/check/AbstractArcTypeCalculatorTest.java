/* (c) https://github.com/MontiCore/monticore */
package arcbasis.check;

import arcbasis.ArcBasisAbstractTest;
import arcbasis.ArcBasisMill;
import arcbasis._symboltable.IArcBasisScope;
import arcbasis._symboltable.SymbolService;
import arcbasis._symboltable.TransitiveScopeSetter;
import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis.ExpressionsBasisMill;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbolSurrogate;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.TypeCheckResult;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Collections;
import java.util.stream.Stream;

/**
 * Abstract class for test subclasses of {@link IArcTypeCalculator}.
 * <p>
 * Override {@link this#getTypeCalculator()} with method that returns the type
 * calculator under test.
 */
public abstract class AbstractArcTypeCalculatorTest extends ArcBasisAbstractTest {

  protected IArcTypeCalculator typeCalculator;
  protected IArcBasisScope scope;
  protected TransitiveScopeSetter scopeSetter;

  protected static Stream<Arguments> expressionProviderForPrimitiveFields() {
    return Stream.of(Arguments.of("a", "int"), Arguments.of("b", "int"), Arguments.of("c", "int"));
  }

  protected static Stream<Arguments> expressionProviderForObjectFields() {
    return Stream.of(Arguments.of("s", "Student"));
  }

  protected abstract IArcTypeCalculator getTypeCalculator();

  protected abstract IArcBasisScope getScope();

  protected TransitiveScopeSetter getScopeSetter() {
    if (this.scopeSetter == null) {
      this.scopeSetter = new TransitiveScopeSetter();
    }
    return this.scopeSetter;
  }

  protected void setUp() {
    this.scope = ArcBasisMill.artifactScope();
    this.scope.setName("");
    this.setUpTypes();
    this.setUpFields();
    this.getScope().setEnclosingScope(ArcBasisMill.globalScope());
  }

  public void setUpTypes() {
    OOTypeSymbol str = ArcBasisMill.oOTypeSymbolBuilder().setSpannedScope(ArcBasisMill.scope()).setName("String").build();
    OOTypeSymbol p =
      ArcBasisMill.oOTypeSymbolBuilder().setSpannedScope(ArcBasisMill.scope()).setName("Person").build();
    OOTypeSymbol r = ArcBasisMill.oOTypeSymbolBuilder().setSpannedScope(ArcBasisMill.scope()).setName("Role").build();
    OOTypeSymbol t =
      ArcBasisMill.oOTypeSymbolBuilder().setSpannedScope(ArcBasisMill.scope()).setName("Teacher").build();
    OOTypeSymbol s =
      ArcBasisMill.oOTypeSymbolBuilder().setSpannedScope(ArcBasisMill.scope()).setName("Student").build();
    OOTypeSymbol f =
      ArcBasisMill.oOTypeSymbolBuilder().setSpannedScope(ArcBasisMill.scope()).setName("FirstGrader").build();
    SymbolService.link(this.getScope(), p, r, t, s, f, str);
    t.setSuperTypesList(Collections.singletonList(SymTypeExpressionFactory.createTypeObject("Role", this.getScope())));
    s.setSuperTypesList(Collections.singletonList(SymTypeExpressionFactory.createTypeObject("Role", this.getScope())));
    f.setSuperTypesList(Collections.singletonList(SymTypeExpressionFactory.createTypeObject("Student",
      this.getScope())));
  }

  public void setUpFields() {
    FieldSymbol a = ArcBasisMill.fieldSymbolBuilder().setName("a")
      .setType(SymTypeExpressionFactory.createPrimitive("int")).build();
    FieldSymbol b = ArcBasisMill.fieldSymbolBuilder().setName("b")
      .setType(SymTypeExpressionFactory.createPrimitive("int")).build();
    FieldSymbol c = ArcBasisMill.fieldSymbolBuilder().setName("c")
      .setType(SymTypeExpressionFactory.createPrimitive("int")).build();
    FieldSymbol s = ArcBasisMill.fieldSymbolBuilder().setName("s")
      .setType(SymTypeExpressionFactory.createTypeObject("Student", this.getScope())).build();
    SymbolService.link(scope, a, b, c, s);
  }

  protected void doShouldCalculateType(@NotNull ASTExpression expression, @NotNull String expectedType,
                                       boolean isPrimitive, boolean isGeneric) {
    Preconditions.checkNotNull(expression);
    Preconditions.checkNotNull(expectedType);

    //Given
    this.getScopeSetter().setScope(expression, this.getScope());

    //When
    TypeCheckResult result = this.getTypeCalculator().deriveType(expression);

    //Then
    Assertions.assertTrue(result.isPresentResult());
    Assertions.assertEquals(isPrimitive, result.getResult().isPrimitive());
    Assertions.assertEquals(isGeneric, result.getResult().isGenericType());
    Assertions.assertFalse(result.getResult().isTypeVariable());
    Assertions.assertTrue(!(result.getResult().getTypeInfo() instanceof OOTypeSymbolSurrogate) ||
      !(((OOTypeSymbolSurrogate) result.getResult().getTypeInfo()).lazyLoadDelegate() instanceof  OOTypeSymbolSurrogate));
    Assertions.assertEquals(expectedType, result.getResult().print());
  }

  protected void doCalculateTypeFromNameExpression(@NotNull String expression, @NotNull String expectedType,
                                                   boolean isPrimitive, boolean isGeneric) {
    Preconditions.checkNotNull(expression);
    Preconditions.checkNotNull(expectedType);
    doShouldCalculateType(doBuildNameExpression(expression), expectedType,
      isPrimitive, isGeneric);
  }

  protected static ASTNameExpression doBuildNameExpression(@NotNull String expression) {
    Preconditions.checkNotNull(expression);
    return ExpressionsBasisMill.nameExpressionBuilder().setName(expression).build();
  }

  /**
   * Class under test {@link ArcBasisTypeCalculator}.
   */
  @ParameterizedTest
  @MethodSource("expressionProviderForPrimitiveFields")
  public void shouldCalculatePrimitiveType(@NotNull String expression, @NotNull String expectedType) {
    Preconditions.checkNotNull(expression);
    Preconditions.checkNotNull(expectedType);
    doCalculateTypeFromNameExpression(expression, expectedType, true, false);
  }

  /**
   * Class under test {@link ArcBasisTypeCalculator}.
   */
  @ParameterizedTest
  @MethodSource("expressionProviderForObjectFields")
  void shouldCalculateObjectType(@NotNull String expression, @NotNull String expectedType) {
    Preconditions.checkNotNull(expression);
    Preconditions.checkNotNull(expectedType);
    doCalculateTypeFromNameExpression(expression, expectedType, false, false);
  }

  /**
   * Class under test {@link ArcBasisTypeCalculator}.
   */
  @Test
  void ShouldNotCalculateResult() {
    //Given
    ASTExpression expr = ExpressionsBasisMill.nameExpressionBuilder().setName("d").build();
    expr.setEnclosingScope(this.getScope());

    //When
    TypeCheckResult result = this.getTypeCalculator().deriveType(expr);

    //Then
    Assertions.assertTrue(result.isPresentResult());
    Assertions.assertTrue(result.getResult().isObscureType());
  }
}