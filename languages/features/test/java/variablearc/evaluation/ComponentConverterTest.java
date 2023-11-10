/* (c) https://github.com/MontiCore/monticore */
package variablearc.evaluation;

import arcbasis._ast.ASTArcParameter;
import arcbasis._ast.ASTComponentBody;
import arcbasis._ast.ASTComponentType;
import arcbasis._symboltable.ArcBasisScopesGenitorP2;
import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis.check.CompTypeExpression;
import arcbasis.check.TypeExprOfComponent;
import de.monticore.expressions.expressionsbasis._ast.ASTLiteralExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.literals.mccommonliterals._ast.ASTConstantsMCCommonLiterals;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.compsymbols._symboltable.SubcomponentSymbol;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.mcbasictypes._ast.ASTConstantsMCBasicTypes;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import variablearc.VariableArcAbstractTest;
import variablearc.VariableArcMill;
import variablearc._symboltable.IVariableArcComponentTypeSymbol;
import variablearc._symboltable.IVariableArcScope;
import variablearc.evaluation.expressions.AssignmentExpression;
import variablearc.evaluation.expressions.Expression;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

/**
 * Tests for {@link ComponentConverter}
 */
public class ComponentConverterTest extends VariableArcAbstractTest {

  protected static SubcomponentSymbol createInstance(String name, ComponentTypeSymbol component) {
    CompTypeExpression typeExpression = new TypeExprOfComponent(component);
    return VariableArcMill.subcomponentSymbolBuilder().setName(name).setType(typeExpression).build();
  }

  protected static IVariableArcComponentTypeSymbol createComponentTypeSymbolWithVariableConstraint(String varName,
                                                                                                   List<SubcomponentSymbol> instanceSymbols) {
    ASTArcParameter parameter = VariableArcMill.arcParameterBuilder()
      .setName("p1")
      .setDefault(getTrueExpression())
      .setMCType(VariableArcMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BOOLEAN).build())
      .build();
    ASTComponentType astComponentType = VariableArcMill.componentTypeBuilder()
      .setName("C")
      .setHead(VariableArcMill.componentHeadBuilder().setArcParametersList(Collections.singletonList(parameter)).build())
      .setBody(Mockito.mock(ASTComponentBody.class))
      .build();
    ASTNameExpression expression = getNameExpression(varName);

    IVariableArcScope scope = VariableArcMill.scope();
    expression.setEnclosingScope(scope);
    scope.add(VariableArcMill.variableSymbolBuilder().setName(varName)
      .setType(SymTypeExpressionFactory.createPrimitive("boolean")).build());
    VariableSymbol parameterSymbol = VariableArcMill.variableSymbolBuilder().setName("p1")
      .setType(SymTypeExpressionFactory.createPrimitive("boolean")).build();
    parameter.setSymbol(parameterSymbol);
    scope.add(parameterSymbol);
    for (SubcomponentSymbol instance : instanceSymbols) {
      scope.add(instance);
    }

    IVariableArcComponentTypeSymbol symbol = (IVariableArcComponentTypeSymbol) VariableArcMill.componentTypeSymbolBuilder()
      .setName("C")
      .setSpannedScope(scope)
      .setAstNode(astComponentType)
      .setParameters(Collections.singletonList(parameterSymbol))
      .build();
    symbol.setLocalConstraints(new ExpressionSet(new ArrayList<>(Collections.singletonList(new Expression(expression)))));

    return symbol;
  }

  protected static IVariableArcComponentTypeSymbol createComponentTypeSymbolWithTrueConstraint(
    List<SubcomponentSymbol> instanceSymbols) {
    IVariableArcScope scope = VariableArcMill.scope();
    for (SubcomponentSymbol instance : instanceSymbols) {
      scope.add(instance);
    }

    IVariableArcComponentTypeSymbol symbol = (IVariableArcComponentTypeSymbol) VariableArcMill.componentTypeSymbolBuilder()
      .setName("C")
      .setSpannedScope(scope)
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

    SubcomponentSymbol subcomponent = createInstance("comp1", createComponentTypeSymbolWithVariableConstraint("a", Collections.emptyList()).getTypeInfo());

    IVariableArcComponentTypeSymbol component = createComponentTypeSymbolWithTrueConstraint(Collections.singletonList(
      subcomponent
    ));
    HashSet<ComponentTypeSymbol> visited = new HashSet<>();

    ArcBasisScopesGenitorP2 scopesGenP2 = new ArcBasisScopesGenitorP2();
    for (SubcomponentSymbol subcomponentSymbol : component.getTypeInfo().getSubcomponents()) {
      scopesGenP2.visit(subcomponentSymbol);
    }

    // When
    ExpressionSet exprs = converter.convert(component, visited);

    // Then
    Assertions.assertEquals(3, exprs.getExpressions().size());
    Assertions.assertIterableEquals(Collections.emptyList(), exprs.getNegatedConjunctions());
    Assertions.assertEquals(1, visited.size());
    Assertions.assertTrue(visited.contains(subcomponent.getType().getTypeInfo()));
    // Assert first expression
    Assertions.assertEquals(Optional.empty(), exprs.getExpressions().get(0).getPrefix());
    Assertions.assertTrue(getTrueExpression().deepEquals(exprs.getExpressions().get(0).getAstExpression()));
    // Assert second expression (parameter)
    Assertions.assertEquals(Optional.of("comp1"), exprs.getExpressions().get(1).getPrefix());
    Assertions.assertTrue(exprs.getExpressions().get(1) instanceof AssignmentExpression);
    Assertions.assertTrue(getTrueExpression().deepEquals(exprs.getExpressions().get(1).getAstExpression()));
    Assertions.assertEquals("p1", ((AssignmentExpression) exprs.getExpressions().get(1)).getVariable().getName());
    // Assert third expression
    Assertions.assertEquals(Optional.of("comp1"), exprs.getExpressions().get(2).getPrefix());
    Assertions.assertTrue(getNameExpression("a").deepEquals(exprs.getExpressions().get(2).getAstExpression()));
  }
}
