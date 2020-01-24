/* (c) https://github.com/MontiCore/monticore */
package arc._symboltable;

import arc._ast.*;
import arc.util.ArcError;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.typesymbols._symboltable.FieldSymbol;
import de.monticore.types.typesymbols._symboltable.TypeVarSymbol;
import montiarc.AbstractTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Holds tests for the handwritten methods of {@link ArcSymbolTableCreator}.
 */
public class ArcSymbolTableCreatorTest extends AbstractTest {

  protected ArcSymbolTableCreator symTab;

  @Override
  protected Pattern supplyErrorCodePattern() {
    return ArcError.ERROR_CODE_PATTERN;
  }

  @BeforeEach
  public void SetUpSymTab() {
    this.symTab = ArcSymTabMill.arcSymbolTableCreatorBuilder()
      .addToScopeStack(ArcSymTabMill.arcScopeBuilder().build()).build();
  }

  public ArcSymbolTableCreator getSymTab() {
    return this.symTab;
  }

  public void setSymTab(ArcSymbolTableCreator symTab) {
    this.symTab = symTab;
  }

  @Test
  public void shouldAddComponentSymbolToScope() {
    ComponentSymbol symbol = ArcSymTabMill.componentSymbolBuilder().setName("Comp")
      .setSpannedScope(ArcSymTabMill.arcScopeBuilder().build()).build();
    this.getSymTab().addToScope(symbol);
    Assertions.assertTrue(this.getSymTab().getCurrentScope().isPresent());
    Assertions.assertEquals(1, this.getSymTab().getCurrentScope().get()
      .getComponentSymbols().size());
    Assertions.assertTrue(this.getSymTab().getCurrentScope().get().getComponentSymbols()
      .containsValue(symbol));
  }

  @Test
  public void shouldCreateComponentSymbol() {
    ASTComponent ast = ArcMill.componentBuilder().setName("Comp")
      .setBody(Mockito.mock(ASTComponentBody.class))
      .setHead(Mockito.mock(ASTComponentHead.class))
      .build();
    ComponentSymbol symbol = this.getSymTab().create_Component(ast);
    Assertions.assertEquals(ast.getName(), symbol.getName());
    Assertions.assertNotNull(symbol.getSpannedScope());
  }

  @Test
  public void shouldVisitComponent() {
    ASTComponent ast = ArcMill.componentBuilder().setName("Comp")
      .setBody(ArcMill.componentBodyBuilder().build())
      .setHead(ArcMill.componentHeadBuilder().build())
      .build();
    ArcScope scope = ArcSymTabMill.arcScopeBuilder().build();
    this.getSymTab().putOnStack(scope);
    this.getSymTab().visit(ast);
    Assertions.assertEquals(scope, ast.getEnclosingScope());
    Assertions.assertFalse(scope.getComponentSymbols().isEmpty());
    Assertions.assertEquals(1, scope.getComponentSymbols().get("Comp").size());
    Assertions.assertTrue(scope.getComponentSymbols().get("Comp").get(0).getSpannedScope()
      .getComponentSymbols().isEmpty());
  }

  @Test
  public void shouldEndVisitComponent() {
    IArcScope scope = this.getSymTab().getCurrentScope().orElse(null);
    int size = this.getSymTab().getComponentStack().size();
    ASTComponent ast = ArcMill.componentBuilder().setName("Comp")
      .setBody(ArcMill.componentBodyBuilder().build())
      .setHead(ArcMill.componentHeadBuilder().build())
      .build();
    this.getSymTab().visit(ast);
    this.getSymTab().endVisit(ast);
    Assertions.assertEquals(scope, this.getSymTab().getCurrentScope().orElse(null));
    Assertions.assertEquals(size, this.getSymTab().getComponentStack().size());
  }

  @Test
  public void shouldVisitParameterDeclaration() {
    ASTMCType type = Mockito.mock(ASTMCType.class);
    ASTArcParameterDeclaration ast = ArcMill.arcParameterDeclarationBuilder()
      .setArcParameter(ArcMill.arcParameterBuilder().setName("par").build())
      .setType(type).build();
    ArcScope scope = ArcSymTabMill.arcScopeBuilder().build();
    this.getSymTab().putOnStack(scope);
    this.getSymTab().visit(ast);
    Assertions.assertEquals(scope, ast.getEnclosingScope());
    Assertions.assertTrue(this.getSymTab().getCurrentScope().isPresent());
    Assertions.assertEquals(scope, this.getSymTab().getCurrentScope().get());
    Assertions.assertTrue(this.getSymTab().getCurrentParameterType().isPresent());
    Assertions.assertEquals(type, this.getSymTab().getCurrentParameterType().get());
  }

  @Test
  public void shouldEndVisitParameterDeclaration() {
    ASTArcParameterDeclaration ast = ArcMill.arcParameterDeclarationBuilder()
      .setArcParameter(ArcMill.arcParameterBuilder().setName("par").build())
      .setType(Mockito.mock(ASTMCType.class)).build();
    this.getSymTab().visit(ast);
    this.getSymTab().endVisit(ast);
    Assertions.assertFalse(this.getSymTab().getCurrentParameterType().isPresent());
  }

  @Test
  public void shouldCreateParameter() {
    ASTArcParameter ast = ArcMill.arcParameterBuilder().setName("par").build();
    ASTMCType type = Mockito.mock(ASTMCType.class);
    this.getSymTab().setCurrentParameterType(type);
    FieldSymbol symbol = this.getSymTab().create_ArcParameter(ast);
    Assertions.assertEquals(ast.getName(), symbol.getName());
    Assertions.assertNotNull(symbol.getType());
  }

  @Test
  public void shouldVisitParameter() {
    ASTArcParameter ast = ArcMill.arcParameterBuilder().setName("par").build();
    ArcScope scope = ArcSymTabMill.arcScopeBuilder().build();
    this.getSymTab().setCurrentParameterType(Mockito.mock(ASTMCType.class));
    this.getSymTab().putOnStack(Mockito.mock(ComponentSymbol.class));
    this.getSymTab().putOnStack(scope);
    this.getSymTab().visit(ast);
    Assertions.assertEquals(scope, ast.getEnclosingScope());
    Assertions.assertFalse(scope.getFieldSymbols().isEmpty());
    Assertions.assertEquals(1, scope.getLocalFieldSymbols().size());
  }

  @Test
  public void shouldCreateTypeParameter() {
    List<ASTMCType> bounds = Arrays.asList(Mockito.mock(ASTMCType.class),
      Mockito.mock(ASTMCType.class));
    ASTArcTypeParameter ast = ArcMill.arcTypeParameterBuilder().setName("T")
      .setUpperBoundList(bounds).build();
    TypeVarSymbol symbol = this.getSymTab().create_ArcTypeParameter(ast);
    Assertions.assertEquals(ast.getName(), symbol.getName());
    Assertions.assertEquals(2, symbol.getUpperBoundList().size());
  }

  @Test
  public void shouldVisitTypeParameter() {
    ASTArcTypeParameter ast = ArcMill.arcTypeParameterBuilder().setName("T").build();
    ArcScope scope = ArcSymTabMill.arcScopeBuilder().build();
    this.getSymTab().putOnStack(Mockito.mock(ComponentSymbol.class));
    this.getSymTab().putOnStack(scope);
    this.getSymTab().visit(ast);
    Assertions.assertEquals(scope, ast.getEnclosingScope());
    Assertions.assertFalse(scope.getLocalTypeVarSymbols().isEmpty());
    Assertions.assertEquals(1, scope.getLocalTypeVarSymbols().size());
  }

  @Test
  public void shouldVisitPortDeclaration() {
    ASTPortDeclaration ast = ArcMill.portDeclarationBuilder()
      .setType(Mockito.mock(ASTMCType.class)).setIncoming(true)
      .setPortList("p1", "p2", "p3").build();
    ArcScope scope = ArcSymTabMill.arcScopeBuilder().build();
    this.getSymTab().putOnStack(scope);
    this.getSymTab().visit(ast);
    Assertions.assertEquals(scope, ast.getEnclosingScope());
    Assertions.assertTrue(this.getSymTab().getCurrentPortType().isPresent());
    Assertions.assertTrue(this.getSymTab().getCurrentPortDirection().isPresent());
  }

  @Test
  public void shouldEndVisitPortDeclaration() {
    ASTPortDeclaration ast = ArcMill.portDeclarationBuilder()
      .setType(Mockito.mock(ASTMCType.class)).setOutgoing(true)
      .setPortList("p1", "p2", "p3").build();
    this.getSymTab().visit(ast);
    this.getSymTab().endVisit(ast);
    Assertions.assertFalse(this.getSymTab().getCurrentPortType().isPresent());
    Assertions.assertFalse(this.getSymTab().getCurrentPortDirection().isPresent());
  }

  @Test
  public void shouldCreatePort() {
    this.getSymTab().setCurrentPortDirection("in");
    this.getSymTab().setCurrentPortType(Mockito.mock(ASTMCType.class));
    ASTPort ast = ArcMill.portBuilder().setName("p").build();
    PortSymbol symbol = this.getSymTab().create_Port(ast);
    Assertions.assertEquals(ast.getName(), symbol.getName());
    Assertions.assertNotNull(symbol.getType());
    Assertions.assertNotNull(symbol.getDirection());
  }

  @Test
  public void shouldVisitPort() {
    ASTPort ast = ArcMill.portBuilder().setName("p").build();
    ArcScope scope = ArcSymTabMill.arcScopeBuilder().build();
    this.getSymTab().setCurrentPortDirection("in");
    this.getSymTab().setCurrentPortType(Mockito.mock(ASTMCType.class));
    this.getSymTab().putOnStack(scope);
    this.getSymTab().visit(ast);
    Assertions.assertEquals(scope, ast.getEnclosingScope());
    Assertions.assertFalse(scope.getLocalPortSymbols().isEmpty());
    Assertions.assertEquals(1, scope.getLocalPortSymbols().size());
  }

  @Test
  public void shouldVisitComponentInstantiation() {
    ASTMCObjectType type = Mockito.mock(ASTMCObjectType.class);
    List<ASTExpression> arguments = Arrays.asList(Mockito.mock(ASTExpression.class),
      Mockito.mock(ASTExpression.class), Mockito.mock(ASTExpression.class));
    ASTComponentInstantiation ast = ArcMill.componentInstantiationBuilder().setType(type)
      .setInstanceList("sub1", "sub2", "sub3").setArgumentList(arguments).build();
    ArcScope scope = ArcSymTabMill.arcScopeBuilder().build();
    this.getSymTab().putOnStack(scope);
    this.getSymTab().visit(ast);
    Assertions.assertEquals(scope, ast.getEnclosingScope());
    Assertions.assertTrue(this.getSymTab().getCurrentScope().isPresent());
    Assertions.assertEquals(scope, this.getSymTab().getCurrentScope().get());
    Assertions.assertTrue(this.getSymTab().getCurrentCompInstanceType().isPresent());
    Assertions.assertEquals(type, this.getSymTab().getCurrentCompInstanceType().get());
    Assertions.assertTrue(this.getSymTab().getCurrentCompInstanceArguments().isPresent());
    Assertions.assertEquals(3, this.getSymTab().getCurrentCompInstanceArguments().get().size());
  }

  @Test
  public void shouldEndVisitComponentInstantiation() {
    List<ASTExpression> arguments = Arrays.asList(Mockito.mock(ASTExpression.class),
      Mockito.mock(ASTExpression.class), Mockito.mock(ASTExpression.class));
    ASTComponentInstantiation ast = ArcMill.componentInstantiationBuilder()
      .setType(Mockito.mock(ASTMCObjectType.class)).setInstanceList("sub1", "sub2", "sub3")
      .setArgumentList(arguments).build();
    this.getSymTab().visit(ast);
    this.getSymTab().endVisit(ast);
    Assertions.assertFalse(this.getSymTab().getCurrentCompInstanceType().isPresent());
    Assertions.assertFalse(this.getSymTab().getCurrentCompInstanceArguments().isPresent());
  }

  @Test
  public void shouldCreateComponentInstance() {
    ASTComponentInstance ast = ArcMill.componentInstanceBuilder().setName("sub").build();
    ASTMCObjectType type = Mockito.mock(ASTMCObjectType.class);
    List<ASTExpression> arguments = Arrays.asList(Mockito.mock(ASTExpression.class),
      Mockito.mock(ASTExpression.class), Mockito.mock(ASTExpression.class));
    this.getSymTab().setCurrentCompInstanceType(type);
    this.getSymTab().setCurrentCompInstanceArguments(arguments);
    ComponentInstanceSymbol symbol = this.getSymTab().create_ComponentInstance(ast);
    Assertions.assertEquals(ast.getName(), symbol.getName());
    Assertions.assertNotNull(symbol.getArguments());
  }

  @Test
  public void shouldInitializeComponentInstance() {
    ASTComponentInstance ast = ArcMill.componentInstanceBuilder().setName("sub").build();
    ASTMCObjectType type = Mockito.mock(ASTMCObjectType.class);
    List<ASTExpression> arguments = Arrays.asList(Mockito.mock(ASTExpression.class),
      Mockito.mock(ASTExpression.class), Mockito.mock(ASTExpression.class));
    this.getSymTab().setCurrentCompInstanceType(type);
    this.getSymTab().setCurrentCompInstanceArguments(arguments);
    ComponentInstanceSymbol symbol = this.getSymTab().create_ComponentInstance(ast);
    this.getSymTab().initialize_ComponentInstance(symbol, ast);
    Assertions.assertEquals(3, symbol.getArguments().size());
  }

  @Test
  public void shouldVisitComponentInstance() {
    ASTComponentInstance ast = ArcMill.componentInstanceBuilder().setName("sub").build();
    ArcScope scope = ArcSymTabMill.arcScopeBuilder().build();
    List<ASTExpression> arguments = Arrays.asList(Mockito.mock(ASTExpression.class),
      Mockito.mock(ASTExpression.class), Mockito.mock(ASTExpression.class));
    this.getSymTab().setCurrentCompInstanceType(Mockito.mock(ASTMCObjectType.class));
    this.getSymTab().setCurrentCompInstanceArguments(arguments);
    this.getSymTab().putOnStack(scope);
    this.getSymTab().visit(ast);
    Assertions.assertEquals(scope, ast.getEnclosingScope());
    Assertions.assertFalse(scope.getLocalComponentInstanceSymbols().isEmpty());
    Assertions.assertEquals(1, scope.getLocalComponentInstanceSymbols().size());
    Assertions.assertEquals(3, scope.getComponentInstanceSymbols().get("sub").get(0)
      .getArguments().size());
  }

  @Test
  public void shouldVisitFieldDeclaration() {
    ASTMCType type = Mockito.mock(ASTMCType.class);
    ASTArcFieldDeclaration ast = ArcMill.arcFieldDeclarationBuilder()
      .setType(type).setFieldList("var1", "var2", "var3").build();
    ArcScope scope = ArcSymTabMill.arcScopeBuilder().build();
    this.getSymTab().putOnStack(scope);
    this.getSymTab().visit(ast);
    Assertions.assertEquals(scope, ast.getEnclosingScope());
    Assertions.assertTrue(this.getSymTab().getCurrentScope().isPresent());
    Assertions.assertEquals(scope, this.getSymTab().getCurrentScope().get());
    Assertions.assertTrue(this.getSymTab().getCurrentFieldType().isPresent());
    Assertions.assertEquals(type, this.getSymTab().getCurrentFieldType().get());
  }

  @Test
  public void shouldEndVisitFieldDeclaration() {
    ASTArcFieldDeclaration ast = ArcMill.arcFieldDeclarationBuilder()
      .setFieldList("var1", "var2", "var3").setType(Mockito.mock(ASTMCType.class)).build();
    this.getSymTab().visit(ast);
    this.getSymTab().endVisit(ast);
    Assertions.assertFalse(this.getSymTab().getCurrentFieldType().isPresent());
  }

  @Test
  public void shouldCreateField() {
    ASTArcField ast = ArcMill.arcFieldBuilder().setName("var").build();
    ASTMCType type = Mockito.mock(ASTMCType.class);
    this.getSymTab().setCurrentFieldType(type);
    FieldSymbol symbol = this.getSymTab().create_ArcField(ast);
    Assertions.assertEquals(ast.getName(), symbol.getName());
    Assertions.assertNotNull(symbol.getType());
  }

  @Test
  public void shouldVisitField() {
    ASTArcField ast = ArcMill.arcFieldBuilder().setName("var").build();
    ArcScope scope = ArcSymTabMill.arcScopeBuilder().build();
    this.getSymTab().setCurrentFieldType(Mockito.mock(ASTMCType.class));
    this.getSymTab().putOnStack(scope);
    this.getSymTab().visit(ast);
    Assertions.assertEquals(scope, ast.getEnclosingScope());
    Assertions.assertFalse(scope.getFieldSymbols().isEmpty());
    Assertions.assertEquals(1, scope.getLocalFieldSymbols().size());
  }
}