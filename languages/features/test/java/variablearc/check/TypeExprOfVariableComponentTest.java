/* (c) https://github.com/MontiCore/monticore */
package variablearc.check;

import arcbasis._ast.ASTComponentBody;
import arcbasis._ast.ASTComponentType;
import arcbasis._symboltable.ComponentTypeSymbol;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import variablearc.AbstractTest;
import variablearc.VariableArcMill;
import variablearc._symboltable.IVariableArcScope;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Tests for {@link TypeExprOfVariableComponent}
 */
public class TypeExprOfVariableComponentTest extends AbstractTest {

  protected static ComponentTypeSymbol createComponentWithParams(@NotNull String compName,
                                                                 @NotNull String... varNames) {
    Preconditions.checkArgument(compName != null);
    Preconditions.checkArgument(varNames != null);

    List<VariableSymbol> vars = new ArrayList<>(varNames.length);
    for (String varName : varNames) {
      VariableSymbol var = VariableArcMill.variableSymbolBuilder()
        .setName(varName).build();
      vars.add(var);
    }

    ASTComponentType astComponentType = Mockito.mock(ASTComponentType.class);
    Mockito.when(astComponentType.getBody()).thenReturn(Mockito.mock(ASTComponentBody.class));

    return VariableArcMill.componentTypeSymbolBuilder().setName(compName)
      .setSpannedScope(VariableArcMill.scope()).setAstNode(astComponentType).setParameters(vars).build();
  }

  protected static Stream<Arguments> createTypeExpressionExpectedExceptionProvider() {
    ComponentTypeSymbol comp = createComponentWithParams("Comp", "a");
    ((IVariableArcScope) comp.getSpannedScope()).add(VariableArcMill.arcFeatureSymbolBuilder()
      .setName("b").build());

    Supplier<TypeExprOfVariableComponent> supplier1 = () -> new TypeExprOfVariableComponent(comp, null);
    Supplier<TypeExprOfVariableComponent> supplier2 =
      () -> new TypeExprOfVariableComponent(null, Collections.emptyList());

    return Stream.of(
      Arguments.of(supplier1, NullPointerException.class),
      Arguments.of(supplier2, NullPointerException.class)
    );
  }

  @BeforeEach
  @Override
  public void init() {
    VariableArcMill.globalScope().clear();
    VariableArcMill.init();
    BasicSymbolsMill.initializePrimitives();
  }

  @Test
  public void shouldAllowLateParametersBinding() {
    // Given
    ComponentTypeSymbol comp = createComponentWithParams("Comp", "a");

    ASTExpression aExpr = VariableArcMill.nameExpressionBuilder().setName("a")
      .build();
    List<ASTExpression> exprList = Collections.singletonList(aExpr);

    // When
    TypeExprOfVariableComponent compTypeExpr = new TypeExprOfVariableComponent(comp);
    TypeExprOfVariableComponent compTypeExprBound = new TypeExprOfVariableComponent(comp, exprList);

    // Then
    Assertions.assertEquals(compTypeExpr.getTypeInfo(), compTypeExprBound.getTypeInfo());
    List<ASTExpression> returnedBindings = compTypeExprBound.getBindingsAsList();
    Assertions.assertEquals(exprList, returnedBindings);
  }

  @Test
  public void shouldGetParameterBindingsAsListInCorrectOrder() {
    // Given
    ComponentTypeSymbol comp = createComponentWithParams("Comp", "a", "b", "c");

    ASTExpression aExpr = VariableArcMill.nameExpressionBuilder().setName("d")
      .build();
    ASTExpression bExpr = VariableArcMill.nameExpressionBuilder().setName("e")
      .build();
    ASTExpression cExpr = VariableArcMill.nameExpressionBuilder().setName("f")
      .build();
    List<ASTExpression> exprList = Lists.newArrayList(aExpr, bExpr, cExpr);

    // When
    TypeExprOfVariableComponent compTypeExpr = new TypeExprOfVariableComponent(comp, exprList);

    // Then
    List<ASTExpression> returnedBindings = compTypeExpr.getBindingsAsList();
    Assertions.assertEquals(exprList, returnedBindings);
  }

  @ParameterizedTest
  @MethodSource("createTypeExpressionExpectedExceptionProvider")
  public void instantiationShouldThrowException(@NotNull Supplier<TypeExprOfVariableComponent> createType,
                                                @NotNull Class<Exception> expected) {
    Preconditions.checkNotNull(expected);
    Preconditions.checkNotNull(createType);
    // When && Then
    Assertions.assertThrows(expected, createType::get);
  }
}
