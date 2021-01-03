/* (c) https://github.com/MontiCore/monticore */
package arcbasis._symboltable;

import arcbasis.AbstractTest;
import arcbasis.ArcBasisMill;
import arcbasis._ast.*;
<<<<<<< HEAD
=======
import de.monticore.expressions.expressionsbasis._ast.ASTArguments;
>>>>>>> bb276d4fcc3784a5352ae1a8711ede81331f4772
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
<<<<<<< HEAD
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
=======
>>>>>>> bb276d4fcc3784a5352ae1a8711ede81331f4772
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
<<<<<<< HEAD
=======
import java.util.Collections;
>>>>>>> bb276d4fcc3784a5352ae1a8711ede81331f4772

/**
 * Holds tests for the handwritten methods of {@link ArcBasisSymbolTableCreator}.
 */
public class ArcBasisSymbolTableCreatorTest extends AbstractTest {

  protected ArcBasisSymbolTableCreator symTab;

  @BeforeEach
  public void SetUpSymTab() {
    this.symTab = ArcBasisMill.arcBasisSymbolTableCreator();
    this.symTab.putOnStack(ArcBasisMill.arcBasisScopeBuilder().build());
  }

  public ArcBasisSymbolTableCreator getSymTab() {
    return this.symTab;
  }

  public void setSymTab(ArcBasisSymbolTableCreator symTab) {
    this.symTab = symTab;
  }

  @Test
  public void shouldAddComponentSymbolToScope() {
    ComponentTypeSymbol symbol = ArcBasisMill.componentTypeSymbolBuilder().setName("Comp")
      .setSpannedScope(ArcBasisMill.arcBasisScopeBuilder().build()).build();
    this.getSymTab().addToScope(symbol);
    Assertions.assertTrue(this.getSymTab().getCurrentScope().isPresent());
    Assertions.assertEquals(1, this.getSymTab().getCurrentScope().get()
      .getComponentTypeSymbols().size());
    Assertions.assertTrue(this.getSymTab().getCurrentScope().get().getComponentTypeSymbols()
      .containsValue(symbol));
  }

  @Test
  public void shouldCreateComponentSymbol() {
    ASTComponentType ast = arcbasis.ArcBasisMill.componentTypeBuilder().setName("Comp")
      .setBody(Mockito.mock(ASTComponentBody.class))
      .setHead(Mockito.mock(ASTComponentHead.class))
      .build();
    ComponentTypeSymbol symbol = this.getSymTab().create_ComponentType(ast);
    Assertions.assertEquals(ast.getName(), symbol.getName());
    Assertions.assertNotNull(symbol.getSpannedScope());
  }

  @Test
  public void shouldVisitComponent() {
    ASTComponentType ast = arcbasis.ArcBasisMill.componentTypeBuilder().setName("Comp")
      .setBody(arcbasis.ArcBasisMill.componentBodyBuilder().build())
      .setHead(arcbasis.ArcBasisMill.componentHeadBuilder().build())
      .build();
    IArcBasisScope scope = ArcBasisMill.arcBasisScopeBuilder().build();
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
    ASTComponentType ast = arcbasis.ArcBasisMill.componentTypeBuilder().setName("Comp")
      .setBody(arcbasis.ArcBasisMill.componentBodyBuilder().build())
      .setHead(arcbasis.ArcBasisMill.componentHeadBuilder().build())
      .build();
    this.getSymTab().visit(ast);
    this.getSymTab().endVisit(ast);
    Assertions.assertEquals(scope, this.getSymTab().getCurrentScope().orElse(null));
    Assertions.assertEquals(size, this.getSymTab().getComponentStack().size());
  }

  @Test
  public void shouldVisitParameterDeclaration() {
    ASTMCType type = Mockito.mock(ASTMCType.class);
    ASTArcParameter ast = arcbasis.ArcBasisMill.arcParameterBuilder()
      .setName("par").setMCType(type).build();
    IArcBasisScope scope = ArcBasisMill.arcBasisScopeBuilder().build();
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
    ASTArcParameter ast = arcbasis.ArcBasisMill.arcParameterBuilder().setName("par")
      .setMCType(type).build();
    VariableSymbol symbol = this.getSymTab().create_ArcParameter(ast);
    Assertions.assertEquals(ast.getName(), symbol.getName());
    Assertions.assertNotNull(symbol.getType());
  }

  @Test
  public void shouldVisitParameter() {
    ASTArcParameter ast = arcbasis.ArcBasisMill.arcParameterBuilder().setName("par")
      .setMCType(Mockito.mock(ASTMCType.class)).build();
    IArcBasisScope scope = ArcBasisMill.arcBasisScopeBuilder().build();
    this.getSymTab().putOnStack(Mockito.mock(ComponentTypeSymbol.class));
    this.getSymTab().putOnStack(scope);
    this.getSymTab().visit(ast);
    Assertions.assertEquals(scope, ast.getEnclosingScope());
    Assertions.assertFalse(scope.getVariableSymbols().isEmpty());
    Assertions.assertEquals(1, scope.getLocalVariableSymbols().size());
  }

  @Test
  public void shouldVisitPortDeclaration() {
    ASTPortDeclaration ast = arcbasis.ArcBasisMill.portDeclarationBuilder()
      .setMCType(Mockito.mock(ASTMCType.class)).setIncoming(true)
      .setPortList("p1", "p2", "p3").build();
    IArcBasisScope scope = ArcBasisMill.arcBasisScopeBuilder().build();
    this.getSymTab().putOnStack(scope);
    this.getSymTab().visit(ast);
    Assertions.assertEquals(scope, ast.getEnclosingScope());
    Assertions.assertTrue(this.getSymTab().getCurrentPortType().isPresent());
    Assertions.assertTrue(this.getSymTab().getCurrentPortDirection().isPresent());
  }

  @Test
  public void shouldEndVisitPortDeclaration() {
    ASTPortDeclaration ast = arcbasis.ArcBasisMill.portDeclarationBuilder()
      .setMCType(Mockito.mock(ASTMCType.class)).setOutgoing(true)
      .setPortList("p1", "p2", "p3").build();
    this.getSymTab().visit(ast);
    this.getSymTab().endVisit(ast);
    Assertions.assertFalse(this.getSymTab().getCurrentPortType().isPresent());
    Assertions.assertFalse(this.getSymTab().getCurrentPortDirection().isPresent());
  }

  @Test
  public void shouldCreatePort() {
    this.getSymTab().setCurrentPortDirection(arcbasis.ArcBasisMill.portDirectionInBuilder().build());
    this.getSymTab().setCurrentPortType(Mockito.mock(ASTMCType.class));
    ASTPort ast = arcbasis.ArcBasisMill.portBuilder().setName("p").build();
    PortSymbol symbol = this.getSymTab().create_Port(ast);
    Assertions.assertEquals(ast.getName(), symbol.getName());
    Assertions.assertNotNull(symbol.getType());
    Assertions.assertNotNull(symbol.getDirection());
  }

  @Test
  public void shouldVisitPort() {
    ASTPort ast = arcbasis.ArcBasisMill.portBuilder().setName("p").build();
    IArcBasisScope scope = ArcBasisMill.arcBasisScopeBuilder().build();
    this.getSymTab().setCurrentPortDirection(arcbasis.ArcBasisMill.portDirectionInBuilder().build());
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
    ASTComponentInstantiation ast = arcbasis.ArcBasisMill.componentInstantiationBuilder().setMCType(type)
      .setComponentInstanceList("sub1", "sub2", "sub3").build();
    IArcBasisScope scope = ArcBasisMill.arcBasisScopeBuilder().build();
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
    ASTComponentInstantiation ast = arcbasis.ArcBasisMill.componentInstantiationBuilder()
      .setMCType(Mockito.mock(ASTMCObjectType.class)).setComponentInstanceList("sub1", "sub2", "sub3")
      .build();
    this.getSymTab().visit(ast);
    this.getSymTab().endVisit(ast);
    Assertions.assertFalse(this.getSymTab().getCurrentCompInstanceType().isPresent());
  }

  @Test
  public void shouldCreateComponentInstance() {
    ASTComponentInstance ast = arcbasis.ArcBasisMill.componentInstanceBuilder().setName("sub").build();
    ASTMCObjectType type = Mockito.mock(ASTMCObjectType.class);
    this.getSymTab().setCurrentCompInstanceType(type);
    ComponentInstanceSymbol symbol = this.getSymTab().create_ComponentInstance(ast);
    Assertions.assertEquals(ast.getName(), symbol.getName());
    Assertions.assertNotNull(symbol.getArguments());
  }

  @Test
  public void shouldInitializeComponentInstance() {
    ASTComponentInstance ast = arcbasis.ArcBasisMill.componentInstanceBuilder().setName("sub")
      .setArguments(arcbasis.ArcBasisMill.argumentsBuilder()
        .setExpressionsList(Arrays.asList(this.mockValues(3))).build()).build();
    ASTMCObjectType type = Mockito.mock(ASTMCObjectType.class);
    this.getSymTab().setCurrentCompInstanceType(type);
    ComponentInstanceSymbol symbol = this.getSymTab().create_ComponentInstance(ast);
    this.getSymTab().initialize_ComponentInstance(symbol, ast);
    Assertions.assertEquals(3, symbol.getArguments().size());
  }

  @Test
  public void shouldVisitComponentInstance() {
    ASTComponentInstance ast = arcbasis.ArcBasisMill.componentInstanceBuilder().setName("sub")
      .setArguments(arcbasis.ArcBasisMill.argumentsBuilder()
        .setExpressionsList(Arrays.asList(this.mockValues(3))).build()).build();
<<<<<<< HEAD
    ArcBasisScope scope = ArcBasisMill.arcBasisScopeBuilder().build();
=======
    IArcBasisScope scope = ArcBasisMill.arcBasisScopeBuilder().build();
>>>>>>> bb276d4fcc3784a5352ae1a8711ede81331f4772
    this.getSymTab().setCurrentCompInstanceType(Mockito.mock(ASTMCObjectType.class));
    this.getSymTab().putOnStack(scope);
    this.getSymTab().handle(ast);
    Assertions.assertEquals(scope, ast.getEnclosingScope());
    Assertions.assertFalse(scope.getLocalComponentInstanceSymbols().isEmpty());
    Assertions.assertEquals(1, scope.getLocalComponentInstanceSymbols().size());
    Assertions.assertEquals(3, scope.getComponentInstanceSymbols().get("sub").get(0)
      .getArguments().size());
  }

  @Test
  public void shouldVisitFieldDeclaration() {
    ASTMCType type = Mockito.mock(ASTMCType.class);
    String[] names = new String[]{"var1", "var2", "var3"};
    ASTArcFieldDeclaration ast = arcbasis.ArcBasisMill.arcFieldDeclarationBuilder()
      .setMCType(type).setArcFieldList(names, this.mockValues(names.length)).build();
    IArcBasisScope scope = ArcBasisMill.arcBasisScopeBuilder().build();
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
    String[] names = new String[]{"var1", "var2", "var3"};
    ASTArcFieldDeclaration ast = arcbasis.ArcBasisMill.arcFieldDeclarationBuilder()
      .setArcFieldList(names, this.mockValues(names.length))
      .setMCType(Mockito.mock(ASTMCType.class))
      .build();
    this.getSymTab().visit(ast);
    this.getSymTab().endVisit(ast);
    Assertions.assertFalse(this.getSymTab().getCurrentFieldType().isPresent());
  }

  @Test
  public void shouldCreateField() {
    ASTArcField ast = arcbasis.ArcBasisMill.arcFieldBuilder().setName("var")
      .setInitial(Mockito.mock(ASTExpression.class)).build();
    ASTMCType type = Mockito.mock(ASTMCType.class);
    this.getSymTab().setCurrentFieldType(type);
    VariableSymbol symbol = this.getSymTab().create_ArcField(ast);
    Assertions.assertEquals(ast.getName(), symbol.getName());
    Assertions.assertNotNull(symbol.getType());
  }

  @Test
  public void shouldVisitField() {
    ASTArcField ast = arcbasis.ArcBasisMill.arcFieldBuilder().setName("var")
      .setInitial(Mockito.mock(ASTExpression.class)).build();
    IArcBasisScope scope = ArcBasisMill.arcBasisScopeBuilder().build();
    this.getSymTab().setCurrentFieldType(Mockito.mock(ASTMCType.class));
    this.getSymTab().putOnStack(scope);
    this.getSymTab().visit(ast);
    Assertions.assertEquals(scope, ast.getEnclosingScope());
    Assertions.assertFalse(scope.getVariableSymbols().isEmpty());
    Assertions.assertEquals(1, scope.getLocalVariableSymbols().size());
<<<<<<< HEAD
=======
  }

  @Test
  public void shouldAddArgumentsOnlyOnce() {
    ASTArguments args = ArcBasisMill.argumentsBuilder()
      .setExpressionsList(Collections.singletonList(Mockito.mock(ASTExpression.class))).build();
    ASTComponentInstance instance = ArcBasisMill.componentInstanceBuilder().setName("comp").setArguments(args).build();
    ASTComponentInstantiation instances = ArcBasisMill.componentInstantiationBuilder()
      .setMCType(Mockito.mock(ASTMCType.class)).setComponentInstancesList(Collections.singletonList(instance)).build();
    IArcBasisScope scope = ArcBasisMill.arcBasisScopeBuilder().build();
    this.getSymTab().putOnStack(scope);
    this.getSymTab().handle(instances);
    Assertions.assertEquals(1, instances.getComponentInstanceList().size());
    Assertions.assertFalse(scope.getComponentInstanceSymbols().get("comp").isEmpty());
    Assertions.assertEquals(1, scope.getComponentInstanceSymbols().get("comp").get(0).getArguments().size());
>>>>>>> bb276d4fcc3784a5352ae1a8711ede81331f4772
  }

  protected ASTExpression[] mockValues(int length) {
    ASTExpression[] values = new ASTExpression[length];
    for (int i = 0; i < length; i++) {
      values[i] = Mockito.mock(ASTExpression.class);
    }
    return values;
  }
}