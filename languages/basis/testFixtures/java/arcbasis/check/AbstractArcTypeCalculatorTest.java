/* (c) https://github.com/MontiCore/monticore */
package arcbasis.check;

import arcbasis.ArcBasisMill;
import arcbasis.ArcBasisTestBase;
import arcbasis._symboltable.IArcBasisScope;
import arcbasis._symboltable.SymbolService;
import montiarc._symboltable.TransitiveScopeSetter;
import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis.ExpressionsBasisMill;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbolSurrogate;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types3.TypeCheck3;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Collections;
import java.util.stream.Stream;

public abstract class AbstractArcTypeCalculatorTest extends ArcBasisTestBase {

  protected IArcBasisScope scope;
  protected TransitiveScopeSetter scopeSetter;

  protected static Stream<Arguments> expressionProviderForPrimitiveFields() {
    return Stream.of(Arguments.of("a", "int"), Arguments.of("b", "int"), Arguments.of("c", "int"));
  }

  protected static Stream<Arguments> expressionProviderForObjectFields() {
    return Stream.of(Arguments.of("s", "Student"));
  }

  protected abstract IArcBasisScope getScope();

  protected TransitiveScopeSetter getScopeSetter() {
    if (this.scopeSetter == null) {
      this.scopeSetter = new TransitiveScopeSetter();
    }
    return this.scopeSetter;
  }

  protected void setUpScope() {
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
    t.setSuperTypesList(Collections.singletonList(SymTypeExpressionFactory.createTypeObject(this.getScope().resolveType("Role").orElseThrow())));
    s.setSuperTypesList(Collections.singletonList(SymTypeExpressionFactory.createTypeObject(this.getScope().resolveType("Role").orElseThrow())));
    f.setSuperTypesList(Collections.singletonList(SymTypeExpressionFactory.createTypeObject(this.getScope().resolveType("Student").orElseThrow())));
  }

  public void setUpFields() {
    FieldSymbol a = ArcBasisMill.fieldSymbolBuilder().setName("a")
      .setType(SymTypeExpressionFactory.createPrimitive("int")).build();
    FieldSymbol b = ArcBasisMill.fieldSymbolBuilder().setName("b")
      .setType(SymTypeExpressionFactory.createPrimitive("int")).build();
    FieldSymbol c = ArcBasisMill.fieldSymbolBuilder().setName("c")
      .setType(SymTypeExpressionFactory.createPrimitive("int")).build();
    FieldSymbol s = ArcBasisMill.fieldSymbolBuilder().setName("s")
      .setType(SymTypeExpressionFactory.createTypeObject(this.getScope().resolveType("Student").orElseThrow())).build();
    SymbolService.link(scope, a, b, c, s);
  }

  protected void doShouldCalculateType(@NotNull ASTExpression expression, @NotNull String expectedType,
                                       boolean isPrimitive, boolean isGeneric) {
    Preconditions.checkNotNull(expression);
    Preconditions.checkNotNull(expectedType);

    //Given
    this.getScopeSetter().setScope(expression, this.getScope());

    //When
    SymTypeExpression result = TypeCheck3.typeOf(expression);

    //Then
    Assertions.assertFalse(result.isObscureType());
    Assertions.assertEquals(isPrimitive, result.isPrimitive());
    Assertions.assertEquals(isGeneric, result.isGenericType());
    Assertions.assertFalse(result.isTypeVariable());
    Assertions.assertTrue(!(result.getTypeInfo() instanceof OOTypeSymbolSurrogate) ||
      !(((OOTypeSymbolSurrogate) result.getTypeInfo()).lazyLoadDelegate() instanceof  OOTypeSymbolSurrogate));
    Assertions.assertEquals(expectedType, result.print());
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
   * Class under test {@link ArcBasisTypeCheck}.
   */
  @ParameterizedTest
  @MethodSource("expressionProviderForPrimitiveFields")
  public void shouldCalculatePrimitiveType(@NotNull String expression, @NotNull String expectedType) {
    Preconditions.checkNotNull(expression);
    Preconditions.checkNotNull(expectedType);
    doCalculateTypeFromNameExpression(expression, expectedType, true, false);
  }

  /**
   * Class under test {@link ArcBasisTypeCheck}.
   */
  @ParameterizedTest
  @MethodSource("expressionProviderForObjectFields")
  void shouldCalculateObjectType(@NotNull String expression, @NotNull String expectedType) {
    Preconditions.checkNotNull(expression);
    Preconditions.checkNotNull(expectedType);
    doCalculateTypeFromNameExpression(expression, expectedType, false, false);
  }

  /**
   * Class under test {@link ArcBasisTypeCheck}.
   */
  @Test
  void ShouldNotCalculateResult() {
    //Given
    ASTExpression expr = ExpressionsBasisMill.nameExpressionBuilder().setName("d").build();
    expr.setEnclosingScope(this.getScope());

    //When
    SymTypeExpression result = TypeCheck3.typeOf(expr);

    //Then
    Assertions.assertTrue(result.isObscureType());
  }
}
