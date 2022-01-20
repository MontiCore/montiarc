/* (c) https://github.com/MontiCore/monticore */
package variablearc.check;

import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis._symboltable.PortSymbol;
import arcbasis.check.CompTypeExpression;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import de.monticore.expressions.assignmentexpressions.AssignmentExpressionsMill;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.types.check.SymTypeExpression;
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
import variablearc._symboltable.ArcFeatureSymbol;
import variablearc._symboltable.IVariableArcScope;
import variablearc._symboltable.VariableArcVariationPoint;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class TypeExprOfVariableComponentTest extends AbstractTest {
  protected static ComponentTypeSymbol createComponentWithParams(@NotNull String compName, @NotNull String... varNames) {
    Preconditions.checkArgument(compName != null);
    Preconditions.checkArgument(varNames != null);

    List<VariableSymbol> vars = new ArrayList<>(varNames.length);
    for (String varName : varNames) {
      VariableSymbol var = VariableArcMill.variableSymbolBuilder().setName(varName).build();
      vars.add(var);
    }

    return VariableArcMill.componentTypeSymbolBuilder().setName(compName).setSpannedScope(VariableArcMill.scope()).setParameters(vars).build();
  }

  protected static ComponentTypeSymbol createComponentWithVariationPointAndParams(@NotNull String compName, @NotNull VariableArcVariationPoint variationPoint, @NotNull String... varNames) {
    Preconditions.checkArgument(compName != null);
    Preconditions.checkArgument(varNames != null);

    List<VariableSymbol> vars = new ArrayList<>(varNames.length);
    for (String varName : varNames) {
      VariableSymbol var = VariableArcMill.variableSymbolBuilder().setName(varName).build();
      vars.add(var);
    }

    IVariableArcScope scope = VariableArcMill.scope();

    scope.add(variationPoint);

    return VariableArcMill.componentTypeSymbolBuilder().setName(compName).setSpannedScope(scope).setParameters(vars).build();
  }

  protected static ComponentTypeSymbol createComponentWithVariationPoints(@NotNull String compName, @NotNull VariableArcVariationPoint... variationPoints) {
    Preconditions.checkArgument(compName != null);
    Preconditions.checkArgument(variationPoints != null);

    IVariableArcScope scope = VariableArcMill.scope();

    for (VariableArcVariationPoint variationPoint : variationPoints) {
      scope.add(variationPoint);
    }

    return VariableArcMill.componentTypeSymbolBuilder().setName(compName).setSpannedScope(scope).build();
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

    ASTExpression aExpr = VariableArcMill.nameExpressionBuilder().setName("a").build();
    List<ASTExpression> exprList = Collections.singletonList(aExpr);

    // When
    TypeExprOfVariableComponent compTypeExpr = new TypeExprOfVariableComponent(comp);
    TypeExprOfVariableComponent compTypeExprBound = new TypeExprOfVariableComponent(comp, exprList);

    // Then
    Assertions.assertEquals(compTypeExpr.getTypeInfo(), compTypeExprBound.getTypeInfo());
    List<ASTExpression> returnedBindings = compTypeExprBound.getParameterBindingsAsList();
    Assertions.assertEquals(exprList, returnedBindings);
  }

  @Test
  public void shouldGetParameterBindingsAsListInCorrectOrder() {
    // Given
    ComponentTypeSymbol comp = createComponentWithParams("Comp", "a", "b", "c");

    ASTExpression aExpr = VariableArcMill.nameExpressionBuilder().setName("d").build();
    ASTExpression bExpr = VariableArcMill.nameExpressionBuilder().setName("e").build();
    ASTExpression cExpr = VariableArcMill.nameExpressionBuilder().setName("f").build();
    List<ASTExpression> exprList = Lists.newArrayList(aExpr, bExpr, cExpr);

    // When
    TypeExprOfVariableComponent compTypeExpr = new TypeExprOfVariableComponent(comp, exprList);

    // Then
    List<ASTExpression> returnedBindings = compTypeExpr.getParameterBindingsAsList();
    Assertions.assertEquals(exprList, returnedBindings);
  }

  @Test
  public void shouldGetFeatureBinding() {
    // Given
    ArcFeatureSymbol featureSymbol = VariableArcMill.arcFeatureSymbolBuilder().setName("a").build();
    ComponentTypeSymbol comp = createComponentWithParams("Comp");
    ((IVariableArcScope) comp.getSpannedScope()).add(featureSymbol);

    ASTExpression featureExpression = Mockito.mock(ASTExpression.class);
    ASTExpression variableAssignmentExpr = AssignmentExpressionsMill.assignmentExpressionBuilder()
        .setLeft(VariableArcMill.nameExpressionBuilder().setName("a").build())
        .setOperator(0)
        .setRight(featureExpression)
        .build();

    // When
    TypeExprOfVariableComponent compTypeExpr = new TypeExprOfVariableComponent(comp, Collections.singletonList(variableAssignmentExpr));

    // Then
    Optional<ASTExpression> returnedBinding = compTypeExpr.getBindingFor(featureSymbol);
    Optional<ASTExpression> returnedNameBinding = compTypeExpr.getBindingFor("a");

    Assertions.assertTrue(returnedBinding.isPresent());
    Assertions.assertEquals(featureExpression, returnedBinding.get());
    Assertions.assertTrue(returnedNameBinding.isPresent());
    Assertions.assertEquals(featureExpression, returnedNameBinding.get());
  }

  @Test
  public void shouldCorrectlyBindParametersAndFeatures() {
    // Given parameters and feature assignments in expression list
    ComponentTypeSymbol comp = createComponentWithParams("Comp", "a");
    ((IVariableArcScope) comp.getSpannedScope()).add(VariableArcMill.arcFeatureSymbolBuilder().setName("b").build());

    ASTExpression aExpr = Mockito.mock(ASTExpression.class);
    ASTExpression featureExpression = Mockito.mock(ASTExpression.class);
    ASTExpression featureAssignmentExpr = AssignmentExpressionsMill.assignmentExpressionBuilder()
        .setLeft(VariableArcMill.nameExpressionBuilder().setName("b").build())
        .setOperator(0)
        .setRight(featureExpression)
        .build();
    List<ASTExpression> exprList = Lists.newArrayList(aExpr, featureAssignmentExpr);

    // When
    TypeExprOfVariableComponent compTypeExpr = new TypeExprOfVariableComponent(comp, exprList);

    // Then split up and bind expressions correctly
    List<ASTExpression> returnedBindings = compTypeExpr.getAllBindingsAsList();
    Assertions.assertEquals(exprList.size(), returnedBindings.size());
    Assertions.assertEquals(1, compTypeExpr.getParameterBindingsAsList().size());
    Assertions.assertEquals(aExpr, compTypeExpr.getParameterBindingsAsList().get(0));
    Assertions.assertEquals(1, compTypeExpr.getFeatureBindings().size());
    Assertions.assertEquals(featureExpression, compTypeExpr.getFeatureBindings().values().asList().get(0));
  }

  @ParameterizedTest
  @MethodSource("createTypeExpressionExpectedExceptionProvider")
  public void instantiationShouldTrowException(@NotNull Supplier<TypeExprOfVariableComponent> createType,
      @NotNull Class<Exception> expected) {
    Preconditions.checkNotNull(expected);
    Preconditions.checkNotNull(createType);
    // When && Then
    Assertions.assertThrows(expected, createType::get);
  }

  protected static Stream<Arguments> createTypeExpressionExpectedExceptionProvider() {
    ComponentTypeSymbol comp = createComponentWithParams("Comp", "a");
    ((IVariableArcScope) comp.getSpannedScope()).add(VariableArcMill.arcFeatureSymbolBuilder().setName("b").build());

    ASTExpression aExpr = Mockito.mock(ASTExpression.class);
    ASTExpression featureExpression = Mockito.mock(ASTExpression.class);
    ASTExpression featureAssignmentExpr = AssignmentExpressionsMill.assignmentExpressionBuilder()
        .setLeft(VariableArcMill.nameExpressionBuilder().setName("b").build())
        .setOperator(0)
        .setRight(featureExpression)
        .build();
    ASTExpression featureAssignmentExpr2 = AssignmentExpressionsMill.assignmentExpressionBuilder()
        .setLeft(VariableArcMill.nameExpressionBuilder().setName("c").build())
        .setOperator(0)
        .setRight(featureExpression)
        .build();

    Supplier<TypeExprOfVariableComponent> supplier1 = () -> new TypeExprOfVariableComponent(comp, null);
    Supplier<TypeExprOfVariableComponent> supplier2 = () -> new TypeExprOfVariableComponent(null, Collections.emptyList());

    List<ASTExpression> exprList3 = Lists.newArrayList(featureAssignmentExpr, featureAssignmentExpr2);
    Supplier<TypeExprOfVariableComponent> supplier3 = () -> new TypeExprOfVariableComponent(comp, exprList3);

    List<ASTExpression> exprList4 = Lists.newArrayList(aExpr, featureAssignmentExpr, featureAssignmentExpr2);
    Supplier<TypeExprOfVariableComponent> supplier4 = () -> new TypeExprOfVariableComponent(comp, exprList4);

    return Stream.of(
        Arguments.of(supplier1, NullPointerException.class),
        Arguments.of(supplier2, NullPointerException.class),
        Arguments.of(supplier3, IllegalArgumentException.class),
        Arguments.of(supplier4, IllegalArgumentException.class)
    );
  }

  @Test
  public void shouldGetPortTypeInVariation() {
    // Given
    SymTypeExpression portType = Mockito.mock(SymTypeExpression.class);
    PortSymbol port = VariableArcMill.portSymbolBuilder().setName("a").setType(portType).setIncoming(false).build();

    VariableArcVariationPoint variationPoint = new VariableArcVariationPoint(Mockito.mock(ASTExpression.class));
    variationPoint.add(port);

    ComponentTypeSymbol comp = createComponentWithVariationPointAndParams("Comp", variationPoint);
    comp.getSpannedScope().add(port);

    CompTypeExpression typeExpression = new TypeExprOfVariableComponent(comp);

    // When
    Optional<SymTypeExpression> returnedPortType = typeExpression.getTypeExprOfPort("a");

    // Then
    Assertions.assertTrue(returnedPortType.isPresent());
    Assertions.assertSame(portType, returnedPortType.get());
  }

  @Test
  public void shouldGetMultiplePortTypesInVariations() {
    // Given ports with equal names and different types in different VariationPoints
    SymTypeExpression portType = Mockito.mock(SymTypeExpression.class);
    PortSymbol port = VariableArcMill.portSymbolBuilder().setName("a").setType(portType).setIncoming(false).build();
    SymTypeExpression portType2 = Mockito.mock(SymTypeExpression.class);
    PortSymbol port2 = VariableArcMill.portSymbolBuilder().setName("a").setType(portType2).setIncoming(false).build();

    VariableArcVariationPoint variationPoint = new VariableArcVariationPoint(Mockito.mock(ASTExpression.class));
    variationPoint.add(port);
    VariableArcVariationPoint variationPoint2 = new VariableArcVariationPoint(Mockito.mock(ASTExpression.class));
    variationPoint.add(port2);

    ComponentTypeSymbol comp = createComponentWithVariationPoints("Comp", variationPoint, variationPoint2);
    comp.getSpannedScope().add(port);
    comp.getSpannedScope().add(port2);

    TypeExprOfVariableComponent typeExpression = new TypeExprOfVariableComponent(comp);

    // When
    List<SymTypeExpression> returnedPortTypes = typeExpression.getTypeExprsOfPort("a");

    // Then
    Assertions.assertNotNull(returnedPortTypes);
    Assertions.assertEquals(2, returnedPortTypes.size());
    Assertions.assertTrue(returnedPortTypes.contains(portType));
    Assertions.assertTrue(returnedPortTypes.contains(portType2));
  }

  @Test
  public void shouldFilterOutUnreachableVariationPointsAndPorts() {
    // Given a variation point with a port and unused feature (if not otherwise specified features default to false) meaning the variation point is
    // in an unreachable state
    PortSymbol port = VariableArcMill.portSymbolBuilder().setName("a").setType(Mockito.mock(SymTypeExpression.class)).setIncoming(false).build();

    VariableArcVariationPoint variationPoint = new VariableArcVariationPoint(VariableArcMill.nameExpressionBuilder().setName("feat").build());
    variationPoint.add(port);

    ComponentTypeSymbol comp = createComponentWithVariationPointAndParams("Comp", variationPoint);
    comp.getSpannedScope().add(port);
    ((IVariableArcScope) comp.getSpannedScope()).add(VariableArcMill.arcFeatureSymbolBuilder().setName("feat").build());

    TypeExprOfVariableComponent typeExpression = new TypeExprOfVariableComponent(comp);

    // When
    List<VariableArcVariationPoint> returnedVariationPoints = typeExpression.getVariationPoints();
    Optional<SymTypeExpression> returnedPort = typeExpression.getTypeExprOfPort("a");

    // Then no port nor variation point should be found
    Assertions.assertTrue(returnedVariationPoints.isEmpty());
    Assertions.assertFalse(returnedPort.isPresent());
  }
}
