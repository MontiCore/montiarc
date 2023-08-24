/* (c) https://github.com/MontiCore/monticore */
package variablearc.evaluation;

import arcbasis._ast.ASTComponentType;
import arcbasis._symboltable.ComponentInstanceSymbol;
import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis.check.CompTypeExpression;
import arcbasis.check.TypeExprOfComponent;
import de.monticore.expressions.expressionsbasis._ast.ASTLiteralExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.literals.mccommonliterals._ast.ASTConstantsMCCommonLiterals;
import de.monticore.types.check.SymTypeExpressionFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import variablearc.VariableArcAbstractTest;
import variablearc.VariableArcMill;
import variablearc._symboltable.IVariableArcScope;
import variablearc._symboltable.VariableArcScopesGenitorP2;
import variablearc._symboltable.IVariableArcComponentTypeSymbol;
import variablearc.evaluation.expressions.Expression;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.eq;

/**
 * Tests for {@link ComponentConverter}
 */
public class ComponentConverterTest extends VariableArcAbstractTest {

  protected static ComponentInstanceSymbol createInstance(String name, ComponentTypeSymbol component) {
    CompTypeExpression typeExpression = new TypeExprOfComponent(component);
    return VariableArcMill.componentInstanceSymbolBuilder().setName(name).setType(typeExpression).build();
  }

  protected static IVariableArcComponentTypeSymbol createComponentTypeSymbolWithVariableConstraint(String varName,
                                                                                                   List<ComponentInstanceSymbol> instanceSymbols) {
    ASTComponentType astComponentType = Mockito.mock(ASTComponentType.class);
    ASTNameExpression expression = getNameExpression(varName);

    IVariableArcScope scope = VariableArcMill.scope();
    expression.setEnclosingScope(scope);
    scope.add(VariableArcMill.variableSymbolBuilder().setName(varName)
      .setType(SymTypeExpressionFactory.createPrimitive("boolean")).build());
    for (ComponentInstanceSymbol instance : instanceSymbols) {
      scope.add(instance);
    }

    IVariableArcComponentTypeSymbol symbol = (IVariableArcComponentTypeSymbol) VariableArcMill.componentTypeSymbolBuilder()
      .setName("C")
      .setSpannedScope(scope)
      .setAstNode(astComponentType)
      .build();
    symbol.setLocalConstraints(new ExpressionSet(new ArrayList<>(Collections.singletonList(new Expression(expression)))));

    return symbol;
  }

  protected static IVariableArcComponentTypeSymbol createComponentTypeSymbolWithTrueConstraint(
    List<ComponentInstanceSymbol> instanceSymbols) {
    ASTComponentType astComponentType = Mockito.mock(ASTComponentType.class);
    IVariableArcScope scope = VariableArcMill.scope();
    for (ComponentInstanceSymbol instance : instanceSymbols) {
      scope.add(instance);
    }

    IVariableArcComponentTypeSymbol symbol = (IVariableArcComponentTypeSymbol) VariableArcMill.componentTypeSymbolBuilder()
      .setName("C")
      .setSpannedScope(scope)
      .setAstNode(astComponentType)
      .build();
    symbol.setLocalConstraints(new ExpressionSet(new ArrayList<>(Collections.singletonList(new Expression(getTrueExpression())))));

    return symbol;
  }

  protected static ASTLiteralExpression getTrueExpression() {
    return VariableArcMill.literalExpressionBuilder()
      .setLiteral(VariableArcMill.booleanLiteralBuilder()
        .setSource(ASTConstantsMCCommonLiterals.TRUE).build()).build();
  }

  protected static ASTNameExpression getNameExpression(String name) {
    return VariableArcMill.nameExpressionBuilder()
      .setName(name)
      .build();
  }

  @Test
  public void convertComponent() {
    // Given
    ComponentConverter converter = new ComponentConverter();
    IVariableArcComponentTypeSymbol component = createComponentTypeSymbolWithTrueConstraint(Collections.emptyList());
    HashSet<ComponentTypeSymbol> visited = new HashSet<>();

    // When
    ExpressionSet exprs = converter.convert(component, visited);

    // Then
    Assertions.assertEquals(1, exprs.getExpressions().size(), "Expression count differs");
    Assertions.assertIterableEquals(Collections.emptyList(), visited, "Visited differs");
    Assertions.assertIterableEquals(Collections.emptyList(), exprs.getNegatedConjunctions(), "Negated expressions are not empty");
    Assertions.assertEquals(Optional.empty(), exprs.getExpressions().get(0).getPrefix(), "Prefix is not empty");
    Assertions.assertTrue(getTrueExpression().deepEquals(exprs.getExpressions().get(0).getAstExpression()), "Expression is not equal to true");
  }

  @Test
  public void convertComponentWithSubcomponent() {
    // Given
    ComponentConverter converter = new ComponentConverter();

    ComponentInstanceSymbol subcomponent = createInstance("comp1", createComponentTypeSymbolWithVariableConstraint("a", Collections.emptyList()).getTypeInfo());

    IVariableArcComponentTypeSymbol component = createComponentTypeSymbolWithTrueConstraint(Collections.singletonList(
      subcomponent
    ));
    HashSet<ComponentTypeSymbol> visited = new HashSet<>();

    VariableArcScopesGenitorP2 scopesGenP2 = new VariableArcScopesGenitorP2();
    for (ComponentInstanceSymbol componentInstanceSymbol : component.getTypeInfo().getSubComponents()) {
      scopesGenP2.visit(componentInstanceSymbol);
    }

    // When
    ExpressionSet exprs = converter.convert(component, visited);

    // Then
    Assertions.assertEquals(2, exprs.getExpressions().size());
    Assertions.assertIterableEquals(Collections.emptyList(), exprs.getNegatedConjunctions());
    Assertions.assertEquals(1, visited.size());
    Assertions.assertTrue(visited.contains(subcomponent.getType().getTypeInfo()));
    // Assert first expression
    Assertions.assertEquals(Optional.empty(), exprs.getExpressions().get(0).getPrefix());
    Assertions.assertTrue(getTrueExpression().deepEquals(exprs.getExpressions().get(0).getAstExpression()));
    // Assert second expression
    Assertions.assertEquals(Optional.of("comp1"), exprs.getExpressions().get(1).getPrefix());
    Assertions.assertTrue(getNameExpression("a").deepEquals(exprs.getExpressions().get(1).getAstExpression()));
  }
}
