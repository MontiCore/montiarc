/* (c) https://github.com/MontiCore/monticore */
package montiarc.evaluation;

import arcbasis._ast.ASTArcArgument;
import arcbasis.check.TypeExprOfComponent;
import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.literals.mccommonliterals._ast.ASTConstantsMCCommonLiterals;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.compsymbols._symboltable.SubcomponentSymbol;
import de.monticore.types.check.SymTypeExpressionFactory;
import montiarc.MontiArcAbstractTest;
import montiarc.MontiArcMill;
import montiarc._symboltable.IMontiArcScope;
import montiarc.evaluation.util.ASTExpressionSetEnclosingScope;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import variablearc._symboltable.IVariableArcComponentTypeSymbol;
import variablearc._symboltable.IVariableArcScope;
import variablearc._symboltable.VariableArcVariantComponentTypeSymbol;
import variablearc._symboltable.VariableArcVariationPoint;
import variablearc.evaluation.VariationPointSolver;
import variablearc.evaluation.expressions.Expression;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Tests for {@link VariationPointSolver}
 * Focuses on giving back the correct combinations of variations points
 */
public class VariationPointSolverTest extends MontiArcAbstractTest {

  protected static final String originComponentTypeName = "C";
  protected static final String childComponentTypeName = "C2";
  protected static final String childComponentName = "child";

  protected static IVariableArcComponentTypeSymbol createComponentWithVariationPoints(
    List<VariableArcVariationPoint> variationPoints) {
    IMontiArcScope scope = MontiArcMill.scope();

    ASTExpressionSetEnclosingScope scopeSetter = new ASTExpressionSetEnclosingScope(scope);
    variationPoints.forEach(vp -> scopeSetter.setEnclosingScope(vp.getCondition().getAstExpression()));
    scope.add(
      MontiArcMill.variableSymbolBuilder()
        .setName("a")
        .setType(SymTypeExpressionFactory.createPrimitive("boolean"))
        .build());

    scope.add(
      MontiArcMill.variableSymbolBuilder()
        .setName("pInt")
        .setType(SymTypeExpressionFactory.createPrimitive("int"))
        .build());

    IVariableArcComponentTypeSymbol typeSymbol =
      (IVariableArcComponentTypeSymbol) MontiArcMill.componentTypeSymbolBuilder().setName(originComponentTypeName)
        .setSpannedScope(scope)
        .build();

    variationPoints.forEach(typeSymbol::add);

    return typeSymbol;
  }

  protected static IVariableArcComponentTypeSymbol createSubcomponentWithVariationPoints(
    List<VariableArcVariationPoint> variationPoints, List<ASTArcArgument> bindings) {

    // Child setup
    IVariableArcScope scope = MontiArcMill.scope();

    variationPoints.forEach(vp -> vp.getCondition().getAstExpression().setEnclosingScope(scope));
    VariableSymbol parameter =
      MontiArcMill.variableSymbolBuilder()
        .setName("p")
        .setType(SymTypeExpressionFactory.createPrimitive("boolean"))
        .build();
    scope.add(parameter);
    bindings.forEach(e -> e.getExpression().setEnclosingScope(scope));

    IVariableArcComponentTypeSymbol typeSymbol =
      (IVariableArcComponentTypeSymbol) MontiArcMill.componentTypeSymbolBuilder().setName(childComponentTypeName)
        .setSpannedScope(scope)
        .build();
    typeSymbol.getTypeInfo().addParameter(parameter);
    variationPoints.forEach(typeSymbol::add);

    // Parent setup
    IVariableArcScope parentScope = MontiArcMill.scope();
    TypeExprOfComponent typeExprOfComponent = new TypeExprOfComponent(typeSymbol.getTypeInfo());
    typeExprOfComponent.addArcArguments(bindings);
    SubcomponentSymbol instanceSymbol =
      MontiArcMill.subcomponentSymbolBuilder().setName(childComponentName)
        .setType(typeExprOfComponent).build();
    instanceSymbol.getType().bindParams();
    parentScope.add(instanceSymbol);

    return (IVariableArcComponentTypeSymbol) MontiArcMill.componentTypeSymbolBuilder().setName(originComponentTypeName)
      .setSpannedScope(parentScope)
      .build();
  }

  protected static ASTExpression getNumberLiteral(int n) {
    return MontiArcMill.literalExpressionBuilder()
      .setLiteral(MontiArcMill.natLiteralBuilder().setDigits(String.valueOf(n)).build()).build();
  }

  @Test
  public void shouldCreateOrigin() {
    // Given
    IVariableArcComponentTypeSymbol typeSymbol =
      (IVariableArcComponentTypeSymbol) MontiArcMill.componentTypeSymbolBuilder().setName("C")
        .setSpannedScope(MontiArcMill.scope())
        .build();

    // When
    VariationPointSolverTestDelegator variationPointSolver = new VariationPointSolverTestDelegator(typeSymbol);
    variationPointSolver.close();

    // Then
    Assertions.assertEquals(typeSymbol, variationPointSolver.getOrigin());
    Assertions.assertNotNull(variationPointSolver.getExpressionSolver());
  }

  @ParameterizedTest
  @MethodSource("provideComponentAndExpectedVariationPoints")
  public void getOriginVariationPoints(@NotNull Supplier<IVariableArcComponentTypeSymbol> typeSymbol,
                                       @NotNull Set<Set<VariableArcVariationPoint>> expected) {
    Preconditions.checkNotNull(typeSymbol);
    Preconditions.checkNotNull(expected);
    // Given
    VariationPointSolver variationPointSolver = new VariationPointSolver(typeSymbol.get());

    // When
    Set<Set<VariableArcVariationPoint>> actual = variationPointSolver.getCombinations(null);
    variationPointSolver.close();

    // Then
    Assertions.assertEquals(expected.size(), actual.size());
    for (Set<VariableArcVariationPoint> set : expected) {
      Assertions.assertTrue(actual.contains(set));
    }
  }

  protected static Stream<Arguments> provideComponentAndExpectedVariationPoints() {
    ASTExpression trueExpression = MontiArcMill.literalExpressionBuilder()
      .setLiteral(MontiArcMill.booleanLiteralBuilder()
        .setSource(ASTConstantsMCCommonLiterals.TRUE).build()).build();
    ASTExpression falseExpression = MontiArcMill.literalExpressionBuilder()
      .setLiteral(MontiArcMill.booleanLiteralBuilder()
        .setSource(ASTConstantsMCCommonLiterals.FALSE).build()).build();

    ASTExpression aExpression = MontiArcMill.nameExpressionBuilder()
      .setName("a").build();

    ASTExpression pIntExpression = MontiArcMill.nameExpressionBuilder()
      .setName("pInt").build();

    VariableArcVariationPoint vpFalse = new VariableArcVariationPoint(new Expression(falseExpression));
    VariableArcVariationPoint vpTrue = new VariableArcVariationPoint(new Expression(trueExpression));

    VariableArcVariationPoint vpA = new VariableArcVariationPoint(new Expression(aExpression));

    VariableArcVariationPoint vpFalseParent = new VariableArcVariationPoint(new Expression(falseExpression));
    VariableArcVariationPoint vpTrueChild =
      new VariableArcVariationPoint(new Expression(trueExpression), vpFalseParent);

    VariableArcVariationPoint vpPIntGreater0 = new VariableArcVariationPoint(new Expression(
      MontiArcMill.greaterThanExpressionBuilder().setLeft(pIntExpression).setOperator(">")
        .setRight(getNumberLiteral(0)).build()
    ));
    VariableArcVariationPoint vpPIntGreater1 = new VariableArcVariationPoint(new Expression(
      MontiArcMill.greaterThanExpressionBuilder().setLeft(pIntExpression).setOperator(">")
        .setRight(getNumberLiteral(1)).build()
    ));

    return Stream.of(
      // 1: No variation points -> only empty set
      Arguments.of(
        (Supplier<IVariableArcComponentTypeSymbol>) () -> createComponentWithVariationPoints(Collections.emptyList()),
        Set.of(Collections.emptySet())),
      // 2: Always false variation point
      Arguments.of((Supplier<IVariableArcComponentTypeSymbol>) () -> createComponentWithVariationPoints(List.of(vpFalse)),
        Set.of(Collections.emptySet())),
      // 3: Always true variation point
      Arguments.of((Supplier<IVariableArcComponentTypeSymbol>) () -> createComponentWithVariationPoints(List.of(vpTrue)),
        Set.of(Set.of(vpTrue))),
      // 4: Satisfiable variation point
      Arguments.of((Supplier<IVariableArcComponentTypeSymbol>) () -> createComponentWithVariationPoints(List.of(vpA)),
        Set.of(Collections.emptySet(), Set.of(vpA))),
      // 5: Combination of satisfiable and always included variation point
      Arguments.of(
        (Supplier<IVariableArcComponentTypeSymbol>) () -> createComponentWithVariationPoints(List.of(vpTrue, vpA)),
        Set.of(Set.of(vpTrue), Set.of(vpTrue, vpA))),
      // 6: Always false variation point with always true child variation point
      Arguments.of(
        (Supplier<IVariableArcComponentTypeSymbol>) () -> createComponentWithVariationPoints(
          List.of(vpFalseParent, vpTrueChild)),
        Set.of(Collections.emptySet())),
      // 7: Satisfiable integer condition
      Arguments.of(
        (Supplier<IVariableArcComponentTypeSymbol>) () -> createComponentWithVariationPoints(List.of(vpPIntGreater0)),
        Set.of(Collections.emptySet(), Set.of(vpPIntGreater0))),
      // 8: Implicit dependency i>1 always requires i>0
      Arguments.of((Supplier<IVariableArcComponentTypeSymbol>) () -> createComponentWithVariationPoints(
          List.of(vpPIntGreater0, vpPIntGreater1)),
        Set.of(Collections.emptySet(), Set.of(vpPIntGreater0), Set.of(vpPIntGreater0, vpPIntGreater1)))
    );
  }

  @ParameterizedTest
  @MethodSource("provideSubcomponentAndExpectedVariationPoints")
  public void getSubVariationPoints(@NotNull Supplier<IVariableArcComponentTypeSymbol> origin,
                                    @NotNull Set<Set<VariableArcVariationPoint>> expected) {
    Preconditions.checkNotNull(origin);
    Preconditions.checkNotNull(expected);
    IVariableArcComponentTypeSymbol originSymbol = origin.get();
    Preconditions.checkNotNull(originSymbol);
    Preconditions.checkState(originSymbol.getTypeInfo().getSubcomponents(childComponentName).isPresent());
    // Given
    VariationPointSolver variationPointSolver = new VariationPointSolver(originSymbol);

    // When
    Set<Set<VariableArcVariationPoint>> actual =
      variationPointSolver.getSubComponentVariants(
        (IVariableArcComponentTypeSymbol) originSymbol.getTypeInfo().getSubcomponents(childComponentName).get().getType().getTypeInfo(),
        childComponentName, new HashSet<>(originSymbol.getAllVariationPoints()), null).stream().map(
        VariableArcVariantComponentTypeSymbol::getIncludedVariationPoints).collect(Collectors.toSet());
    variationPointSolver.close();

    // Then
    Assertions.assertEquals(expected.size(), actual.size());
    for (Set<VariableArcVariationPoint> set : expected) {
      Assertions.assertTrue(actual.contains(set));
    }
  }

  protected static Stream<Arguments> provideSubcomponentAndExpectedVariationPoints() {
    ASTExpression trueExpression = MontiArcMill.literalExpressionBuilder()
      .setLiteral(MontiArcMill.booleanLiteralBuilder()
        .setSource(ASTConstantsMCCommonLiterals.TRUE).build()).build();
    ASTExpression falseExpression = MontiArcMill.literalExpressionBuilder()
      .setLiteral(MontiArcMill.booleanLiteralBuilder()
        .setSource(ASTConstantsMCCommonLiterals.FALSE).build()).build();

    ASTExpression pExpression = MontiArcMill.nameExpressionBuilder()
      .setName("p").build();

    ASTExpression aExpression = MontiArcMill.nameExpressionBuilder()
      .setName("a").build();

    VariableArcVariationPoint vpFalse = new VariableArcVariationPoint(new Expression(falseExpression));
    VariableArcVariationPoint vpTrue = new VariableArcVariationPoint(new Expression(trueExpression));

    VariableArcVariationPoint vpA = new VariableArcVariationPoint(new Expression(pExpression));

    ASTArcArgument falseArgument = MontiArcMill.arcArgumentBuilder().setExpression(falseExpression).build();
    ASTArcArgument aArgument = MontiArcMill.arcArgumentBuilder().setExpression(aExpression).build();

    return Stream.of(
      // No variation points -> only empty set
      Arguments.of(
        (Supplier<IVariableArcComponentTypeSymbol>) () -> createSubcomponentWithVariationPoints(Collections.emptyList(),
          Collections.emptyList()),
        Set.of(Collections.emptySet())),
      // Always false variation point
      Arguments.of((Supplier<IVariableArcComponentTypeSymbol>) () -> createSubcomponentWithVariationPoints(List.of(vpFalse),
          Collections.emptyList()),
        Set.of(Collections.emptySet())),
      // Always true variation point
      Arguments.of((Supplier<IVariableArcComponentTypeSymbol>) () -> createSubcomponentWithVariationPoints(List.of(vpTrue),
          Collections.emptyList()),
        Set.of(Set.of(vpTrue))),
      // Subcomponent instantiated with p=false
      Arguments.of((Supplier<IVariableArcComponentTypeSymbol>) () -> createSubcomponentWithVariationPoints(List.of(vpA),
          Collections.singletonList(falseArgument)),
        Set.of(Collections.emptySet())),
      // Subcomponent instantiated with p=true
      Arguments.of((Supplier<IVariableArcComponentTypeSymbol>) () -> createSubcomponentWithVariationPoints(List.of(vpA),
          Collections.singletonList(falseArgument)),
        Set.of(Collections.emptySet())),
      // Subcomponent instantiated with p=a
      Arguments.of((Supplier<IVariableArcComponentTypeSymbol>) () -> createSubcomponentWithVariationPoints(List.of(vpA),
          Collections.singletonList(aArgument)),
        Set.of(Collections.emptySet(), Set.of(vpA)))
    );
  }

  /**
   * grants access to the encapsulated attributes
   */
  protected static class VariationPointSolverTestDelegator extends VariationPointSolver {

    public VariationPointSolverTestDelegator(IVariableArcComponentTypeSymbol origin) {
      super(origin);
    }

    IVariableArcComponentTypeSymbol getOrigin() {
      return this.origin;
    }

    variablearc.evaluation.ExpressionSolver getExpressionSolver() {
      return this.expressionSolver;
    }
  }
}
