/* (c) https://github.com/MontiCore/monticore */
package arcbasis.check;

import arcbasis.AbstractTest;
import arcbasis.ArcBasisMill;
import arcbasis._symboltable.IArcBasisScope;
import arcbasis._visitor.ArcBasisTraverser;
import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis.ExpressionsBasisMill;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.expressions.expressionsbasis._symboltable.IExpressionsBasisScope;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbolSurrogate;
import de.monticore.types.check.SymTypeExpressionFactory;
import montiarc.util.check.IArcDerive;
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
 * Abstract class for test subclasses of {@link AbstractArcDerive}.
 * <p>
 * Override {@link this#getDerive()} with method that returns the type
 * calculator under test.
 */
public abstract class AbstractArcDeriveTest extends AbstractTest {

  protected IArcDerive derive;
  protected IArcBasisScope scope;
  protected ArcBasisExpressionsScopeSetter scopeSetter;

  protected static Stream<Arguments> expressionProviderForPrimitiveFields() {
    return Stream.of(Arguments.of("a", "int"), Arguments.of("b", "int"), Arguments.of("c", "int"));
  }

  protected static Stream<Arguments> expressionProviderForObjectFields() {
    return Stream.of(Arguments.of("s", "Student"));
  }

  protected abstract IArcDerive getDerive();

  protected abstract IArcBasisScope getScope();

  protected ArcBasisExpressionsScopeSetter getScopeSetter() {
    if (this.scopeSetter == null) {
      this.scopeSetter = new ArcBasisExpressionsScopeSetter(this.getScope());
    }
    return this.scopeSetter;
  }

  @BeforeEach
  public void setUp() {
    this.scope = ArcBasisMill.artifactScope();
    this.scope.setName("");
    this.setUpTypes();
    this.setUpFields();
    this.getScope().setEnclosingScope(ArcBasisMill.globalScope());
  }

  public void setUpTypes() {
    OOTypeSymbol p =
      ArcBasisMill.oOTypeSymbolBuilder().setSpannedScope(ArcBasisMill.scope()).setName("Person").build();
    OOTypeSymbol r = ArcBasisMill.oOTypeSymbolBuilder().setSpannedScope(ArcBasisMill.scope()).setName("Role").build();
    OOTypeSymbol t =
      ArcBasisMill.oOTypeSymbolBuilder().setSpannedScope(ArcBasisMill.scope()).setName("Teacher").build();
    OOTypeSymbol s =
      ArcBasisMill.oOTypeSymbolBuilder().setSpannedScope(ArcBasisMill.scope()).setName("Student").build();
    OOTypeSymbol f =
      ArcBasisMill.oOTypeSymbolBuilder().setSpannedScope(ArcBasisMill.scope()).setName("FirstGrader").build();
    this.add2Scope(this.getScope(), p, r, t, s, f);
    t.setSuperTypesList(Collections.singletonList(SymTypeExpressionFactory.createTypeObject("Role", this.getScope())));
    s.setSuperTypesList(Collections.singletonList(SymTypeExpressionFactory.createTypeObject("Role", this.getScope())));
    f.setSuperTypesList(Collections.singletonList(SymTypeExpressionFactory.createTypeObject("Student",
      this.getScope())));
  }

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
    this.getScopeSetter().handle(expression);
    //When
    expression.accept(this.getDerive().getTraverser());

    //Then
    Assertions.assertTrue(this.getDerive().getResult().isPresent());
    Assertions.assertEquals(isPrimitive, this.getDerive().getResult().get().isTypeConstant());
    Assertions.assertEquals(isGeneric, this.getDerive().getResult().get().isGenericType());
    Assertions.assertFalse(this.getDerive().getResult().get().isTypeVariable());
    Assertions.assertTrue(!(this.getDerive().getResult().get().getTypeInfo() instanceof OOTypeSymbolSurrogate) ||
      !(((OOTypeSymbolSurrogate) this.getDerive().getResult().get().getTypeInfo()).lazyLoadDelegate() instanceof  OOTypeSymbolSurrogate));
    Assertions.assertEquals(expectedType, this.getDerive().getResult().get().print());
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
   * Class under test {@link ArcBasisDerive}.
   */
  @ParameterizedTest
  @MethodSource("expressionProviderForPrimitiveFields")
  void shouldCalculatePrimitiveType(@NotNull String expression, @NotNull String expectedType) {
    Preconditions.checkArgument(expression != null);
    Preconditions.checkArgument(expectedType != null);
    doCalculateTypeFromNameExpression(expression, expectedType, true, false);
  }

  /**
   * Class under test {@link ArcBasisDerive}.
   */
  @ParameterizedTest
  @MethodSource("expressionProviderForObjectFields")
  void shouldCalculateObjectType(@NotNull String expression, @NotNull String expectedType) {
    Preconditions.checkArgument(expression != null);
    Preconditions.checkArgument(expectedType != null);
    doCalculateTypeFromNameExpression(expression, expectedType, false, false);
  }

  /**
   * Class under test {@link ArcBasisDerive}.
   */
  @Test
  void ShouldNotCalculateResult() {
    //Given
    ASTExpression expr = ExpressionsBasisMill.nameExpressionBuilder().setName("d").build();
    expr.setEnclosingScope(this.getScope());

    //When
    expr.accept(this.getDerive().getTraverser());

    //Then
    Assertions.assertFalse(this.getDerive().getResult().isPresent());
  }

  public static class ArcBasisExpressionsScopeSetter implements ArcBasisTraverser {

    protected IExpressionsBasisScope scope;

    public ArcBasisExpressionsScopeSetter(@NotNull IExpressionsBasisScope scope) {
      Preconditions.checkArgument(scope != null);
      this.scope = scope;
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