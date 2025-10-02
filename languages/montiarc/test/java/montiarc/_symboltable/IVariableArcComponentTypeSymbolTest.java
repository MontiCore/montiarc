/* (c) https://github.com/MontiCore/monticore */
package montiarc._symboltable;

import arcbasis._ast.ASTArcArgument;
import arcbasis._ast.ASTArcComponentType;
import de.monticore.symbols.compsymbols._symboltable.PortSymbol;
import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.literals.mccommonliterals._ast.ASTConstantsMCCommonLiterals;
import de.monticore.symbols.compsymbols._symboltable.SubcomponentSymbol;
import de.monticore.types.check.CompKindOfComponentType;
import de.monticore.types.check.SymTypeExpressionFactory;
import montiarc.MontiArcMill;
import montiarc.MontiArcTestBase;
import montiarc._ast.ASTMACompilationUnit;
import montiarc.evaluation.util.ASTExpressionSetEnclosingScope;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import variablearc._symboltable.IVariableArcComponentTypeSymbol;
import variablearc._symboltable.VariableArcVariantComponentTypeSymbol;
import variablearc._symboltable.VariableArcVariationPoint;
import variablearc.evaluation.expressions.Expression;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Tests for {@link IVariableArcComponentTypeSymbol}
 * Focuses on giving back the correct number of Variants (including subcomponent expansion)
 */
public class IVariableArcComponentTypeSymbolTest extends MontiArcTestBase {

  protected static IVariableArcComponentTypeSymbol createComponentWithVariationPoints(List<VariableArcVariationPoint> variationPoints) {
  return createComponentWithVariationPoints("C", variationPoints);
  }

  protected static IVariableArcComponentTypeSymbol createComponentWithVariationPoints(
    String componentTypeName,
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

    ASTArcComponentType astComponentType = Mockito.mock(ASTArcComponentType.class);
    Mockito.when(astComponentType.getBody())
      .thenReturn(MontiArcMill.componentBodyBuilder().setArcElementsList(Collections.emptyList()).build());

    IVariableArcComponentTypeSymbol typeSymbol =
      (IVariableArcComponentTypeSymbol) MontiArcMill.componentTypeSymbolBuilder().setName(componentTypeName)
        .setSpannedScope(scope)
        .setEnclosingScope(MontiArcMill.scope())
        .setAstNode(astComponentType)
        .build();

    typeSymbol.getTypeInfo().addAllParameter(scope.getLocalVariableSymbols());

    variationPoints.forEach(typeSymbol::add);

    return typeSymbol;
  }

  protected static IVariableArcComponentTypeSymbol createComponentWithSubcomponents(
    List<VariableArcVariationPoint> variationPoints, List<SubcomponentSymbol> subcomponents) {
    IVariableArcComponentTypeSymbol component = createComponentWithVariationPoints(variationPoints);
    ASTExpressionSetEnclosingScope scopeSetter = new ASTExpressionSetEnclosingScope(
      (IMontiArcScope) component.getTypeInfo().getSpannedScope());

    for (SubcomponentSymbol subcomponent : subcomponents) {
      component.getTypeInfo().getSpannedScope().add(subcomponent);
      subcomponent.getType().bindParams();
      subcomponent.getType().getArguments().forEach(scopeSetter::setEnclosingScope);
    }

    return component;
  }

  protected static IVariableArcComponentTypeSymbol createComponentWithSubcomponentsAndAddFirstToFirstVP(
    List<VariableArcVariationPoint> variationPoints, List<SubcomponentSymbol> subcomponents) {
    IVariableArcComponentTypeSymbol component = createComponentWithVariationPoints(variationPoints);
    ASTExpressionSetEnclosingScope scopeSetter = new ASTExpressionSetEnclosingScope(
      (IMontiArcScope) component.getTypeInfo().getSpannedScope());

    for (SubcomponentSymbol subcomponent : subcomponents) {
      component.getTypeInfo().getSpannedScope().add(subcomponent);
      subcomponent.getType().bindParams();
      subcomponent.getType().getArguments().forEach(scopeSetter::setEnclosingScope);
    }

    variationPoints.get(0).add(subcomponents.get(0));

    return component;
  }

  protected static SubcomponentSymbol createInstance(String name, IVariableArcComponentTypeSymbol type,
                                                     List<ASTArcArgument> arguments) {
    CompKindOfComponentType typeExprOfComponent = new CompKindOfComponentType(type.getTypeInfo());
    typeExprOfComponent.addArgument(arguments);
    return MontiArcMill.subcomponentSymbolBuilder().setName(name)
      .setType(typeExprOfComponent).build();
  }

  protected static ASTExpression getNumberLiteral(int n) {
    return MontiArcMill.literalExpressionBuilder()
      .setLiteral(MontiArcMill.natLiteralBuilder().setDigits(String.valueOf(n)).build()).build();
  }

  protected static Stream<Arguments> provideComponentAndExpectedNumberOfVariants() {
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
    VariableArcVariationPoint vpA2 = new VariableArcVariationPoint(new Expression(aExpression));

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

    ASTArcArgument aArgument = MontiArcMill.arcArgumentBuilder().setExpression(aExpression).build();
    ASTArcArgument pIntArgument = MontiArcMill.arcArgumentBuilder().setExpression(pIntExpression).build();

    return Stream.of(
      // 1: No variation points -> 1 variant
      Arguments.of(
        (Supplier<IVariableArcComponentTypeSymbol>) () -> createComponentWithVariationPoints(Collections.emptyList()),
        1),
      // 2: Always false variation point
      Arguments.of((Supplier<IVariableArcComponentTypeSymbol>) () -> createComponentWithVariationPoints(List.of(vpFalse)),
        1),
      // 3: Always true variation point
      Arguments.of((Supplier<IVariableArcComponentTypeSymbol>) () -> createComponentWithVariationPoints(List.of(vpTrue)),
        1),
      // 4: Satisfiable variation point
      Arguments.of((Supplier<IVariableArcComponentTypeSymbol>) () -> createComponentWithVariationPoints(List.of(vpA)),
        2),
      // 5: Combination of satisfiable and always included variation point
      Arguments.of(
        (Supplier<IVariableArcComponentTypeSymbol>) () -> createComponentWithVariationPoints(List.of(vpTrue, vpA)),
        2),
      // 6: Always false variation point with always true child variation point
      Arguments.of(
        (Supplier<IVariableArcComponentTypeSymbol>) () -> createComponentWithVariationPoints(
          List.of(vpFalseParent, vpTrueChild)),
        1),
      // 7: Satisfiable integer condition
      Arguments.of(
        (Supplier<IVariableArcComponentTypeSymbol>) () -> createComponentWithVariationPoints(List.of(vpPIntGreater0)),
        2),
      // 8: Implicit dependency i>1 always requires i>0
      Arguments.of((Supplier<IVariableArcComponentTypeSymbol>) () -> createComponentWithVariationPoints(
          List.of(vpPIntGreater0, vpPIntGreater1)),
        3),
      // 9: Component with subcomponent (both without variants)
      Arguments.of((Supplier<IVariableArcComponentTypeSymbol>) () -> createComponentWithSubcomponents(
          Collections.emptyList(), List.of(
            createInstance("i", createComponentWithVariationPoints(Collections.emptyList()),
              arrayListOf(aArgument, pIntArgument)))),
        1),
      // 10: Component with subcomponent (with subcomponent variants)
      Arguments.of((Supplier<IVariableArcComponentTypeSymbol>) () -> createComponentWithSubcomponents(
          Collections.emptyList(), List.of(
            createInstance("i", createComponentWithVariationPoints(List.of(vpA)), arrayListOf(aArgument, pIntArgument)))),
        2),
      // 11: Component with subcomponent (both with variants)
      Arguments.of((Supplier<IVariableArcComponentTypeSymbol>) () -> createComponentWithSubcomponents(
          List.of(vpPIntGreater0), List.of(
            createInstance("i", createComponentWithVariationPoints(List.of(vpA)), arrayListOf(aArgument, pIntArgument)))),
        4),
      // 12: Component with subcomponent (both with same variation point condition)
      Arguments.of((Supplier<IVariableArcComponentTypeSymbol>) () -> createComponentWithSubcomponents(
          List.of(vpA), List.of(
            createInstance("i", createComponentWithVariationPoints("C2", List.of(vpA2)),
              arrayListOf(aArgument, pIntArgument)))),
        2),
      // 13: Component with subcomponent (both with connected variants)
      Arguments.of((Supplier<IVariableArcComponentTypeSymbol>) () -> createComponentWithSubcomponents(
          List.of(vpPIntGreater0), List.of(
            createInstance("i", createComponentWithVariationPoints("C2", List.of(vpPIntGreater1)),
              arrayListOf(aArgument, pIntArgument)))),
        3),
      // 14: Component with subcomponent (both with variants and subcomponent inside parent variant)
      Arguments.of((Supplier<IVariableArcComponentTypeSymbol>) () -> createComponentWithSubcomponentsAndAddFirstToFirstVP(
          List.of(vpPIntGreater0), List.of(
            createInstance("i", createComponentWithVariationPoints(List.of(vpA)), arrayListOf(aArgument, pIntArgument)))),
        3),
      // 15: Component with subcomponent that has no variation points but a subcomponent with one
      Arguments.of((Supplier<IVariableArcComponentTypeSymbol>) () -> createComponentWithSubcomponents(
          Collections.emptyList(), List.of(
            createInstance("i", createComponentWithSubcomponents(Collections.emptyList(),
                Collections.singletonList(
                  createInstance("j", createComponentWithVariationPoints("C2", List.of(vpA)), arrayListOf(aArgument, pIntArgument)))),
              arrayListOf(aArgument, pIntArgument)))),
        2)
    );
  }

  @ParameterizedTest
  @MethodSource("provideComponentAndExpectedNumberOfVariants")
  public void getOriginVariationPoints(@NotNull Supplier<IVariableArcComponentTypeSymbol> typeSymbol,
                                       @NotNull int expectedNumberOfVariants) {
    Preconditions.checkNotNull(typeSymbol);

    // When
    List<VariableArcVariantComponentTypeSymbol> variants = typeSymbol.get().getVariableArcVariants();

    // Then
    Assertions.assertEquals(expectedNumberOfVariants, variants.size());
  }

  @SafeVarargs
  static protected <E> ArrayList<E> arrayListOf(E... elements) {
    return new ArrayList<>(List.of(elements));
  }

  /**
   * Should not visit inner compTypeDefinition when creating the variant.
   * See #851
   */
  @Test
  public void shouldNotVisitInnerCompTypeDefinitions() {
    // Given
    ASTMACompilationUnit compilationUnit = compile("component c1 {" +
      "port in int i;" +
      "port out int o;" +
      "component Inner {" +
      "port out int o;" +
      "<<delayed>> compute {}" +
      "}}");


    PortSymbol portSymbol = compilationUnit.getArcComponentType().getSymbol().getPort("o").orElseThrow(() -> new IllegalStateException("Port 'o' not found."));
    PortSymbol portVariantSymbol = ((MontiArcComponentTypeSymbol) compilationUnit.getArcComponentType().getSymbol()).getVariants().get(0).getPort("o").orElseThrow(() -> new IllegalStateException("Port 'o' not found."));

    // Then
    Assertions.assertEquals(portSymbol.getStronglyCausal(), portVariantSymbol.getStronglyCausal());
  }
}
