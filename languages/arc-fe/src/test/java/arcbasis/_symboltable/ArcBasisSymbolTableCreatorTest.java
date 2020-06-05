/* (c) https://github.com/MontiCore/monticore */
package arcbasis._symboltable;

import arcbasis._ast.*;
import arcbasis.util.ArcError;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.typesymbols._symboltable.FieldSymbol;
import montiarc.AbstractTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.regex.Pattern;

/**
 * Holds tests for the handwritten methods of {@link ArcBasisSymbolTableCreator}.
 */
public class ArcBasisSymbolTableCreatorTest extends AbstractTest {

  protected ArcBasisSymbolTableCreator symTab;

  @Override
  protected Pattern supplyErrorCodePattern() {
    return ArcError.ERROR_CODE_PATTERN;
  }

  @BeforeEach
  public void SetUpSymTab() {
    this.symTab = ArcBasisSymTabMill.arcBasisSymbolTableCreatorBuilder()
      .addToScopeStack(ArcBasisSymTabMill.arcBasisScopeBuilder().build()).build();
  }

  public ArcBasisSymbolTableCreator getSymTab() {
    return this.symTab;
  }

  public void setSymTab(ArcBasisSymbolTableCreator symTab) {
    this.symTab = symTab;
  }

  @Test
  public void shouldAddComponentSymbolToScope() {
    ComponentTypeSymbol symbol = ArcBasisSymTabMill.componentTypeSymbolBuilder().setName("Comp")
      .setSpannedScope(ArcBasisSymTabMill.arcBasisScopeBuilder().build()).build();
    this.getSymTab().addToScope(symbol);
    Assertions.assertTrue(this.getSymTab().getCurrentScope().isPresent());
    Assertions.assertEquals(1, this.getSymTab().getCurrentScope().get()
      .getComponentTypeSymbols().size());
    Assertions.assertTrue(this.getSymTab().getCurrentScope().get().getComponentTypeSymbols()
      .containsValue(symbol));
  }

  @Test
  public void shouldCreateComponentSymbol() {
    ASTComponentType ast = ArcBasisMill.componentTypeBuilder().setName("Comp")
      .setBody(Mockito.mock(ASTComponentBody.class))
      .setHead(Mockito.mock(ASTComponentHead.class))
      .build();
    ComponentTypeSymbol symbol = this.getSymTab().create_ComponentType(ast);
    Assertions.assertEquals(ast.getName(), symbol.getName());
    Assertions.assertNotNull(symbol.getSpannedScope());
  }

  @Test
  public void shouldVisitComponent() {
    ASTComponentType ast = ArcBasisMill.componentTypeBuilder().setName("Comp")
      .setBody(ArcBasisMill.componentBodyBuilder().build())
      .setHead(ArcBasisMill.componentHeadBuilder().build())
      .build();
    ArcBasisScope scope = ArcBasisSymTabMill.arcBasisScopeBuilder().build();
    this.getSymTab().putOnStack(scope);
    this.getSymTab().visit(ast);
    Assertions.assertEquals(scope, ast.getEnclosingScope());
    Assertions.assertFalse(scope.getComponentTypeSymbols().isEmpty());
    Assertions.assertEquals(1, scope.getComponentTypeSymbols().get("Comp").size());
    Assertions.assertTrue(scope.getComponentTypeSymbols().get("Comp").get(0).getSpannedScope()
      .getComponentTypeSymbols().isEmpty());
  }

  @Test
  public void shouldEndVisitComponent() {
    IArcBasisScope scope = this.getSymTab().getCurrentScope().orElse(null);
    int size = this.getSymTab().getComponentStack().size();
    ASTComponentType ast = ArcBasisMill.componentTypeBuilder().setName("Comp")
      .setBody(ArcBasisMill.componentBodyBuilder().build())
      .setHead(ArcBasisMill.componentHeadBuilder().build())
      .build();
    this.getSymTab().visit(ast);
    this.getSymTab().endVisit(ast);
    Assertions.assertEquals(scope, this.getSymTab().getCurrentScope().orElse(null));
    Assertions.assertEquals(size, this.getSymTab().getComponentStack().size());
  }

  @Test
  public void shouldVisitParameterDeclaration() {
    ASTMCType type = Mockito.mock(ASTMCType.class);
    ASTArcParameter ast = ArcBasisMill.arcParameterBuilder()
      .setName("par").setMCType(type).build();
    ArcBasisScope scope = ArcBasisSymTabMill.arcBasisScopeBuilder().build();
    this.getSymTab().putOnStack(Mockito.mock(ComponentTypeSymbol.class));
    this.getSymTab().putOnStack(scope);
    this.getSymTab().visit(ast);
    Assertions.assertEquals(scope, ast.getEnclosingScope());
    Assertions.assertTrue(this.getSymTab().getCurrentScope().isPresent());
    Assertions.assertEquals(scope, this.getSymTab().getCurrentScope().get());
  }

  @Test
  public void shouldCreateParameter() {
    ASTMCType type = Mockito.mock(ASTMCType.class);
    ASTArcParameter ast = ArcBasisMill.arcParameterBuilder().setName("par")
      .setMCType(type).build();
    FieldSymbol symbol = this.getSymTab().create_ArcParameter(ast);
    Assertions.assertEquals(ast.getName(), symbol.getName());
    Assertions.assertNotNull(symbol.getType());
  }

  @Test
  public void shouldVisitParameter() {
    ASTArcParameter ast = ArcBasisMill.arcParameterBuilder().setName("par")
      .setMCType(Mockito.mock(ASTMCType.class)).build();
    ArcBasisScope scope = ArcBasisSymTabMill.arcBasisScopeBuilder().build();
    this.getSymTab().putOnStack(Mockito.mock(ComponentTypeSymbol.class));
    this.getSymTab().putOnStack(scope);
    this.getSymTab().visit(ast);
    Assertions.assertEquals(scope, ast.getEnclosingScope());
    Assertions.assertFalse(scope.getFieldSymbols().isEmpty());
    Assertions.assertEquals(1, scope.getLocalFieldSymbols().size());
  }

  @Test
  public void shouldVisitPortDeclaration() {
    ASTPortDeclaration ast = ArcBasisMill.portDeclarationBuilder()
      .setMCType(Mockito.mock(ASTMCType.class)).setIncoming(true)
      .setPortList("p1", "p2", "p3").build();
    ArcBasisScope scope = ArcBasisSymTabMill.arcBasisScopeBuilder().build();
    this.getSymTab().putOnStack(scope);
    this.getSymTab().visit(ast);
    Assertions.assertEquals(scope, ast.getEnclosingScope());
    Assertions.assertTrue(this.getSymTab().getCurrentPortType().isPresent());
    Assertions.assertTrue(this.getSymTab().getCurrentPortDirection().isPresent());
  }

  @Test
  public void shouldEndVisitPortDeclaration() {
    ASTPortDeclaration ast = ArcBasisMill.portDeclarationBuilder()
      .setMCType(Mockito.mock(ASTMCType.class)).setOutgoing(true)
      .setPortList("p1", "p2", "p3").build();
    this.getSymTab().visit(ast);
    this.getSymTab().endVisit(ast);
    Assertions.assertFalse(this.getSymTab().getCurrentPortType().isPresent());
    Assertions.assertFalse(this.getSymTab().getCurrentPortDirection().isPresent());
  }

  @Test
  public void shouldCreatePort() {
    this.getSymTab().setCurrentPortDirection(ArcBasisMill.portDirectionInBuilder().build());
    this.getSymTab().setCurrentPortType(Mockito.mock(ASTMCType.class));
    ASTPort ast = ArcBasisMill.portBuilder().setName("p").build();
    PortSymbol symbol = this.getSymTab().create_Port(ast);
    Assertions.assertEquals(ast.getName(), symbol.getName());
    Assertions.assertNotNull(symbol.getType());
    Assertions.assertNotNull(symbol.getDirection());
  }

  @Test
  public void shouldVisitPort() {
    ASTPort ast = ArcBasisMill.portBuilder().setName("p").build();
    ArcBasisScope scope = ArcBasisSymTabMill.arcBasisScopeBuilder().build();
    this.getSymTab().setCurrentPortDirection(ArcBasisMill.portDirectionInBuilder().build());
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
    ASTComponentInstantiation ast = ArcBasisMill.componentInstantiationBuilder().setMCType(type)
      .setComponentInstanceList("sub1", "sub2", "sub3").build();
    ArcBasisScope scope = ArcBasisSymTabMill.arcBasisScopeBuilder().build();
    this.getSymTab().putOnStack(scope);
    this.getSymTab().visit(ast);
    Assertions.assertEquals(scope, ast.getEnclosingScope());
    Assertions.assertTrue(this.getSymTab().getCurrentScope().isPresent());
    Assertions.assertEquals(scope, this.getSymTab().getCurrentScope().get());
    Assertions.assertTrue(this.getSymTab().getCurrentCompInstanceType().isPresent());
    Assertions.assertEquals(type, this.getSymTab().getCurrentCompInstanceType().get());
  }

  @Test
  public void shouldEndVisitComponentInstantiation() {
    ASTComponentInstantiation ast = ArcBasisMill.componentInstantiationBuilder()
      .setMCType(Mockito.mock(ASTMCObjectType.class)).setComponentInstanceList("sub1", "sub2", "sub3")
      .build();
    this.getSymTab().visit(ast);
    this.getSymTab().endVisit(ast);
    Assertions.assertFalse(this.getSymTab().getCurrentCompInstanceType().isPresent());
  }

  @Test
  public void shouldCreateComponentInstance() {
    ASTComponentInstance ast = ArcBasisMill.componentInstanceBuilder().setName("sub").build();
    ASTMCObjectType type = Mockito.mock(ASTMCObjectType.class);
    this.getSymTab().setCurrentCompInstanceType(type);
    ComponentInstanceSymbol symbol = this.getSymTab().create_ComponentInstance(ast);
    Assertions.assertEquals(ast.getName(), symbol.getName());
    Assertions.assertNotNull(symbol.getArguments());
  }

  @Test
  public void shouldInitializeComponentInstance() {
    ASTComponentInstance ast = ArcBasisMill.componentInstanceBuilder().setName("sub")
      .setArcArguments(ArcBasisMill.arcArgumentsBuilder()
        .setExpressionList(Arrays.asList(this.mockValues(3))).build()).build();
    ASTMCObjectType type = Mockito.mock(ASTMCObjectType.class);
    this.getSymTab().setCurrentCompInstanceType(type);
    ComponentInstanceSymbol symbol = this.getSymTab().create_ComponentInstance(ast);
    this.getSymTab().initialize_ComponentInstance(symbol, ast);
    Assertions.assertEquals(3, symbol.getArguments().size());
  }

  @Test
  public void shouldVisitComponentInstance() {
    ASTComponentInstance ast = ArcBasisMill.componentInstanceBuilder().setName("sub")
      .setArcArguments(ArcBasisMill.arcArgumentsBuilder()
        .setExpressionList(Arrays.asList(this.mockValues(3))).build()).build();
    ArcBasisScope scope = ArcBasisSymTabMill.arcBasisScopeBuilder().build();
    this.getSymTab().setCurrentCompInstanceType(Mockito.mock(ASTMCObjectType.class));
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
    String[] names = new String[] { "var1", "var2", "var3" };
    ASTArcFieldDeclaration ast = ArcBasisMill.arcFieldDeclarationBuilder()
      .setMCType(type).setFieldList(names, this.mockValues(names.length)).build();
    ArcBasisScope scope = ArcBasisSymTabMill.arcBasisScopeBuilder().build();
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
    String[] names = new String[] { "var1", "var2", "var3" };
    ASTArcFieldDeclaration ast = ArcBasisMill.arcFieldDeclarationBuilder()
      .setFieldList(names, this.mockValues(names.length))
      .setMCType(Mockito.mock(ASTMCType.class))
      .build();
    this.getSymTab().visit(ast);
    this.getSymTab().endVisit(ast);
    Assertions.assertFalse(this.getSymTab().getCurrentFieldType().isPresent());
  }

  @Test
  public void shouldCreateField() {
    ASTArcField ast = ArcBasisMill.arcFieldBuilder().setName("var")
      .setValue(Mockito.mock(ASTExpression.class)).build();
    ASTMCType type = Mockito.mock(ASTMCType.class);
    this.getSymTab().setCurrentFieldType(type);
    FieldSymbol symbol = this.getSymTab().create_ArcField(ast);
    Assertions.assertEquals(ast.getName(), symbol.getName());
    Assertions.assertNotNull(symbol.getType());
  }

  @Test
  public void shouldVisitField() {
    ASTArcField ast = ArcBasisMill.arcFieldBuilder().setName("var")
      .setValue(Mockito.mock(ASTExpression.class)).build();
    ArcBasisScope scope = ArcBasisSymTabMill.arcBasisScopeBuilder().build();
    this.getSymTab().setCurrentFieldType(Mockito.mock(ASTMCType.class));
    this.getSymTab().putOnStack(scope);
    this.getSymTab().visit(ast);
    Assertions.assertEquals(scope, ast.getEnclosingScope());
    Assertions.assertFalse(scope.getFieldSymbols().isEmpty());
    Assertions.assertEquals(1, scope.getLocalFieldSymbols().size());
  }

  protected ASTExpression[] mockValues(int length) {
    ASTExpression[] values = new ASTExpression[length];
    for (int i = 0; i < length; i++) {
      values[i] = Mockito.mock(ASTExpression.class);
    }
    return values;
  }
}