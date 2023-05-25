/* (c) https://github.com/MontiCore/monticore */
package variablearc.evaluation;

import arcbasis._ast.ASTComponentType;
import arcbasis._symboltable.ComponentInstanceSymbol;
import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis.check.CompTypeExpression;
import arcbasis.check.TypeExprOfComponent;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.BoolSort;
import com.microsoft.z3.Context;
import com.microsoft.z3.IntSort;
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

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Stack;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link ComponentConverter}
 */
public class ComponentConverterTest extends VariableArcAbstractTest {

  protected static Context createContext() {
    Context context = Mockito.mock(Context.class);
    BoolSort boolSort = Mockito.mock(BoolSort.class);
    when(context.getBoolSort()).thenReturn(boolSort);
    when(context.getIntSort()).thenReturn(Mockito.mock(IntSort.class));
    BoolExpr trueExpr = Mockito.mock(BoolExpr.class);
    when(context.mkTrue()).thenReturn(trueExpr);
    when(context.mkBool(true)).thenReturn(trueExpr);
    when(context.mkConst(eq("comp1.a"), eq(boolSort))).thenReturn(Mockito.mock(BoolExpr.class));
    return context;
  }

  protected static ComponentInstanceSymbol createInstance(String name, ComponentTypeSymbol component) {
    CompTypeExpression typeExpression = new TypeExprOfComponent(component);
    return VariableArcMill.componentInstanceSymbolBuilder().setName(name).setType(typeExpression).build();
  }

  protected static ComponentTypeSymbol createComponentTypeSymbolWithVariableConstraint(String varName,
                                                                                       List<ComponentInstanceSymbol> instanceSymbols) {
    ASTComponentType astComponentType = Mockito.mock(ASTComponentType.class);
    ASTNameExpression expression = VariableArcMill.nameExpressionBuilder()
      .setName(varName)
      .build();
    Mockito.when(astComponentType.getBody())
      .thenReturn(VariableArcMill.componentBodyBuilder().setArcElementsList(
        List.of(
          VariableArcMill.arcConstraintDeclarationBuilder().setExpression(expression)
            .build()
        )).build());

    IVariableArcScope scope = VariableArcMill.scope();
    expression.setEnclosingScope(scope);
    scope.add(VariableArcMill.variableSymbolBuilder().setName(varName)
      .setType(SymTypeExpressionFactory.createPrimitive("boolean")).build());
    for (ComponentInstanceSymbol instance : instanceSymbols) {
      scope.add(instance);
    }

    return VariableArcMill.componentTypeSymbolBuilder()
      .setName("C")
      .setSpannedScope(scope)
      .setAstNode(astComponentType)
      .build();
  }

  protected static ComponentTypeSymbol createComponentTypeSymbolWithTrueConstraint(
    List<ComponentInstanceSymbol> instanceSymbols) {
    ASTComponentType astComponentType = Mockito.mock(ASTComponentType.class);
    Mockito.when(astComponentType.getBody())
      .thenReturn(VariableArcMill.componentBodyBuilder().setArcElementsList(
        List.of(
          VariableArcMill.arcConstraintDeclarationBuilder().setExpression(VariableArcMill.literalExpressionBuilder()
              .setLiteral(VariableArcMill.booleanLiteralBuilder()
                .setSource(ASTConstantsMCCommonLiterals.TRUE).build()).build())
            .build()
        )).build());

    IVariableArcScope scope = VariableArcMill.scope();
    for (ComponentInstanceSymbol instance : instanceSymbols) {
      scope.add(instance);
    }

    return VariableArcMill.componentTypeSymbolBuilder()
      .setName("C")
      .setSpannedScope(scope)
      .setAstNode(astComponentType)
      .build();
  }

  @Test
  public void createConverter() {
    // Given
    Context context = createContext();

    // When
    InternalComponentConverter converter = new InternalComponentConverter(context);

    // Then
    Assertions.assertEquals(context, converter.getContext());
  }

  @Test
  public void convertComponent() {
    // Given
    Context context = createContext();
    InternalComponentConverter converter = new InternalComponentConverter(context);
    ComponentTypeSymbol component = createComponentTypeSymbolWithTrueConstraint(Collections.emptyList());
    Stack<String> stack = new Stack<>();
    HashSet<ComponentTypeSymbol> visited = new HashSet<>();

    // When
    List<BoolExpr> exprs = converter.convert(component, stack, visited);

    // Then
    Assertions.assertTrue(stack.isEmpty());
    Assertions.assertIterableEquals(List.of(component), visited);
    Assertions.assertIterableEquals(List.of(context.mkTrue()), exprs);
  }

  @Test
  public void convertComponentWithSubcomponent() {
    // Given
    Context context = createContext();
    InternalComponentConverter converter = new InternalComponentConverter(context);

    ComponentInstanceSymbol subcomponent = createInstance("comp1", createComponentTypeSymbolWithVariableConstraint("a", Collections.emptyList()));

    ComponentTypeSymbol component = createComponentTypeSymbolWithTrueConstraint(Collections.singletonList(
      subcomponent
    ));
    Stack<String> stack = new Stack<>();
    HashSet<ComponentTypeSymbol> visited = new HashSet<>();

    VariableArcScopesGenitorP2 scopesGenP2 = new VariableArcScopesGenitorP2();
    for (ComponentInstanceSymbol componentInstanceSymbol : component.getSubComponents()) {
      scopesGenP2.visit(componentInstanceSymbol);
    }

    // When
    List<BoolExpr> exprs = converter.convert(component, stack, visited);

    // Then
    Assertions.assertTrue(stack.isEmpty());
    Assertions.assertEquals(2, visited.size());
    Assertions.assertTrue(visited.contains(component));
    Assertions.assertTrue(visited.contains(subcomponent.getType().getTypeInfo()));
    Assertions.assertIterableEquals(List.of(context.mkTrue(), context.mkConst("comp1.a", context.getBoolSort())), exprs);
  }

  @Test
  public void listToString() {
    // Given
    InternalComponentConverter converter = new InternalComponentConverter(createContext());

    // When
    String res1 = converter.listToString(List.of());
    String res2 = converter.listToString(List.of("A"));
    String res3 = converter.listToString(List.of("A", "b", "CD"));

    // Then
    Assertions.assertEquals("", res1);
    Assertions.assertEquals("A", res2);
    Assertions.assertEquals("A.b.CD", res3);
  }

  protected static class InternalComponentConverter extends ComponentConverter {

    public InternalComponentConverter(Context context) {
      super(context);
    }

    public Context getContext() {
      return context;
    }

    @Override
    public String listToString(List<String> list) {
      return super.listToString(list);
    }
  }
}
