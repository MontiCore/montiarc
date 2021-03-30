/* (c) https://github.com/MontiCore/monticore */
package arcbasis._symboltable;

import arcbasis.AbstractTest;
import arcbasis.ArcBasisMill;
import arcbasis._ast.*;
import com.google.common.collect.Lists;
import de.monticore.expressions.expressionsbasis._ast.ASTArguments;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collections;

/**
 * Holds tests for the handwritten methods of {@link ArcBasisScopesGenitor}.
 */
public class ArcBasisScopesGenitorTest extends AbstractTest {

  protected ArcBasisScopesGenitorTestDelegator symTab;

  @BeforeEach
  public void SetUpSymTab() {
    this.symTab = new ArcBasisScopesGenitorTestDelegator(ArcBasisMill.globalScope());
  }

  /**
   * @return the test subject
   */
  public ArcBasisScopesGenitor getSymTab() {
    return this.symTab.getGenitor();
  }

  @Test
  public void shouldAddComponentSymbolToScope() {
    ComponentTypeSymbol symbol = ArcBasisMill.componentTypeSymbolBuilder().setName("Comp")
      .setSpannedScope(ArcBasisMill.scope()).build();
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
    ComponentTypeSymbol symbol = this.getSymTab().create_ComponentType(ast).build();
    Assertions.assertEquals(ast.getName(), symbol.getName());
    Assertions.assertNotNull(symbol.getSpannedScope());
  }

  @Test
  public void shouldVisitComponent() {
    ASTComponentType ast = arcbasis.ArcBasisMill.componentTypeBuilder().setName("Comp")
      .setBody(arcbasis.ArcBasisMill.componentBodyBuilder().build())
      .setHead(arcbasis.ArcBasisMill.componentHeadBuilder().build())
      .build();
    IArcBasisScope scope = ArcBasisMill.scope();
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
    ASTMCType type = ArcBasisMill.mCQualifiedTypeBuilder()
      .setMCQualifiedName(ArcBasisMill.mCQualifiedNameBuilder()
        .setPartsList(Lists.newArrayList("String"))
        .build())
      .build();
    ASTArcParameter ast = arcbasis.ArcBasisMill.arcParameterBuilder()
      .setName("par").setMCType(type).build();
    IArcBasisScope scope = ArcBasisMill.scope();
    this.getSymTab().putOnStack(Mockito.mock(ComponentTypeSymbol.class));
    this.getSymTab().putOnStack(scope);
    this.getSymTab().visit(ast);
    Assertions.assertEquals(scope, ast.getEnclosingScope());
    Assertions.assertTrue(this.getSymTab().getCurrentScope().isPresent());
    Assertions.assertEquals(scope, this.getSymTab().getCurrentScope().get());
  }

  @Test
  public void shouldCreateParameter() {
    ASTMCType type = ArcBasisMill.mCPrimitiveTypeBuilder().setPrimitive(2).build();
    ASTArcParameter ast = arcbasis.ArcBasisMill.arcParameterBuilder().setName("par")
      .setMCType(type).build();
    VariableSymbol symbol = this.getSymTab().create_ArcParameter(ast).build();
    Assertions.assertEquals(ast.getName(), symbol.getName());
    Assertions.assertNotNull(symbol.getType());
  }

  @Test
  public void shouldVisitParameter() {
    ASTArcParameter ast = arcbasis.ArcBasisMill.arcParameterBuilder().setName("par")
      .setMCType(ArcBasisMill.mCPrimitiveTypeBuilder().setPrimitive(2).build()).build();
    IArcBasisScope scope = ArcBasisMill.scope();
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
      .setMCType(ArcBasisMill.mCPrimitiveTypeBuilder().setPrimitive(2).build()).setIncoming(true)
      .setPortList("p1", "p2", "p3").build();
    IArcBasisScope scope = ArcBasisMill.scope();
    this.getSymTab().putOnStack(scope);
    this.getSymTab().visit(ast);
    Assertions.assertEquals(scope, ast.getEnclosingScope());
    Assertions.assertTrue(this.getSymTab().getCurrentPortType().isPresent());
    Assertions.assertTrue(this.getSymTab().getCurrentPortDirection().isPresent());
  }

  @Test
  public void shouldEndVisitPortDeclaration() {
    ASTPortDeclaration ast = arcbasis.ArcBasisMill.portDeclarationBuilder()
      .setMCType(ArcBasisMill.mCPrimitiveTypeBuilder().setPrimitive(2).build()).setOutgoing(true)
      .setPortList("p1", "p2", "p3").build();
    this.getSymTab().visit(ast);
    this.getSymTab().endVisit(ast);
    Assertions.assertFalse(this.getSymTab().getCurrentPortType().isPresent());
    Assertions.assertFalse(this.getSymTab().getCurrentPortDirection().isPresent());
  }

  @Test
  public void shouldCreatePort() {
    this.getSymTab().setCurrentPortDirection(arcbasis.ArcBasisMill.portDirectionInBuilder().build());
    this.getSymTab().setCurrentPortType(ArcBasisMill.mCPrimitiveTypeBuilder().setPrimitive(2).build());
    ASTPort ast = arcbasis.ArcBasisMill.portBuilder().setName("p").build();
    PortSymbol symbol = this.getSymTab().create_Port(ast).build();
    Assertions.assertEquals(ast.getName(), symbol.getName());
    Assertions.assertNotNull(symbol.getType());
    Assertions.assertNotNull(symbol.getDirection());
  }

  @Test
  public void shouldVisitPort() {
    ASTPort ast = arcbasis.ArcBasisMill.portBuilder().setName("p").build();
    IArcBasisScope scope = ArcBasisMill.scope();
    this.getSymTab().setCurrentPortDirection(arcbasis.ArcBasisMill.portDirectionInBuilder().build());
    this.getSymTab().setCurrentPortType(ArcBasisMill.mCPrimitiveTypeBuilder().setPrimitive(2).build());
    this.getSymTab().putOnStack(scope);
    this.getSymTab().visit(ast);
    Assertions.assertEquals(scope, ast.getEnclosingScope());
    Assertions.assertFalse(scope.getLocalPortSymbols().isEmpty());
    Assertions.assertEquals(1, scope.getLocalPortSymbols().size());
  }

  @Test
  public void shouldVisitComponentInstantiation() {
    ASTMCObjectType type = ArcBasisMill.mCQualifiedTypeBuilder()
      .setMCQualifiedName(ArcBasisMill.mCQualifiedNameBuilder()
        .setPartsList(Lists.newArrayList("String"))
        .build())
      .build();
    ASTComponentInstantiation ast = arcbasis.ArcBasisMill.componentInstantiationBuilder().setMCType(type)
      .setComponentInstanceList("sub1", "sub2", "sub3").build();
    IArcBasisScope scope = ArcBasisMill.scope();
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
      .setMCType(ArcBasisMill.mCQualifiedTypeBuilder()
      .setMCQualifiedName(ArcBasisMill.mCQualifiedNameBuilder()
        .setPartsList(Lists.newArrayList("String"))
        .build())
      .build()).setComponentInstanceList("sub1", "sub2", "sub3")
      .build();
    this.getSymTab().visit(ast);
    this.getSymTab().endVisit(ast);
    Assertions.assertFalse(this.getSymTab().getCurrentCompInstanceType().isPresent());
  }

  @Test
  public void shouldCreateComponentInstance() {
    ASTComponentInstance ast = arcbasis.ArcBasisMill.componentInstanceBuilder().setName("sub").build();
    ASTMCObjectType type = ArcBasisMill.mCQualifiedTypeBuilder()
      .setMCQualifiedName(ArcBasisMill.mCQualifiedNameBuilder()
        .setPartsList(Lists.newArrayList("String"))
        .build())
      .build();
    this.getSymTab().setCurrentCompInstanceType(type);
    ComponentInstanceSymbol symbol = this.getSymTab().create_ComponentInstance(ast).build();
    Assertions.assertEquals(ast.getName(), symbol.getName());
    Assertions.assertNotNull(symbol.getArguments());
  }

  @Test
  public void shouldInitializeComponentInstance() {
    ASTComponentInstance ast = arcbasis.ArcBasisMill.componentInstanceBuilder().setName("sub")
      .setArguments(arcbasis.ArcBasisMill.argumentsBuilder()
        .setExpressionsList(Arrays.asList(this.mockValues(3))).build()).build();
    ASTMCObjectType type = ArcBasisMill.mCQualifiedTypeBuilder()
      .setMCQualifiedName(ArcBasisMill.mCQualifiedNameBuilder()
        .setPartsList(Lists.newArrayList("String"))
        .build())
      .build();
    this.getSymTab().setCurrentCompInstanceType(type);
    this.getSymTab().visit(ast);
    this.getSymTab().endVisit(ast);
    Assertions.assertEquals(3, ast.getSymbol().getArguments().size());
  }

  @Test
  public void shouldVisitComponentInstance() {
    ASTComponentInstance ast = arcbasis.ArcBasisMill.componentInstanceBuilder().setName("sub")
      .setArguments(arcbasis.ArcBasisMill.argumentsBuilder()
        .setExpressionsList(Arrays.asList(this.mockValues(3))).build()).build();
    IArcBasisScope scope = ArcBasisMill.scope();
    this.getSymTab().setCurrentCompInstanceType(ArcBasisMill.mCQualifiedTypeBuilder()
      .setMCQualifiedName(ArcBasisMill.mCQualifiedNameBuilder()
        .setPartsList(Lists.newArrayList("String"))
        .build())
      .build());
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
    ASTMCType type = ArcBasisMill.mCPrimitiveTypeBuilder().setPrimitive(2).build();
    String[] names = new String[]{"var1", "var2", "var3"};
    ASTArcFieldDeclaration ast = arcbasis.ArcBasisMill.arcFieldDeclarationBuilder()
      .setMCType(type).setArcFieldList(names, this.mockValues(names.length)).build();
    IArcBasisScope scope = ArcBasisMill.scope();
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
      .setMCType(ArcBasisMill.mCPrimitiveTypeBuilder().setPrimitive(2).build())
      .build();
    this.getSymTab().visit(ast);
    this.getSymTab().endVisit(ast);
    Assertions.assertFalse(this.getSymTab().getCurrentFieldType().isPresent());
  }

  @Test
  public void shouldCreateField() {
    ASTArcField ast = arcbasis.ArcBasisMill.arcFieldBuilder().setName("var")
      .setInitial(Mockito.mock(ASTExpression.class)).build();
    ASTMCType type = ArcBasisMill.mCPrimitiveTypeBuilder().setPrimitive(2).build();
    this.getSymTab().setCurrentFieldType(type);
    VariableSymbol symbol = this.getSymTab().create_ArcField(ast).build();
    Assertions.assertEquals(ast.getName(), symbol.getName());
    Assertions.assertNotNull(symbol.getType());
  }

  @Test
  public void shouldVisitField() {
    ASTArcField ast = arcbasis.ArcBasisMill.arcFieldBuilder().setName("var")
      .setInitial(Mockito.mock(ASTExpression.class)).build();
    IArcBasisScope scope = ArcBasisMill.scope();
    this.getSymTab().setCurrentFieldType(ArcBasisMill.mCPrimitiveTypeBuilder().setPrimitive(2).build());
    this.getSymTab().putOnStack(scope);
    this.getSymTab().visit(ast);
    Assertions.assertEquals(scope, ast.getEnclosingScope());
    Assertions.assertFalse(scope.getVariableSymbols().isEmpty());
    Assertions.assertEquals(1, scope.getLocalVariableSymbols().size());
  }

  @Test
  public void shouldAddArgumentsOnlyOnce() {
    ASTArguments args = ArcBasisMill.argumentsBuilder()
      .setExpressionsList(Collections.singletonList(Mockito.mock(ASTExpression.class))).build();
    ASTComponentInstance instance = ArcBasisMill.componentInstanceBuilder().setName("comp").setArguments(args).build();
    ASTComponentInstantiation instances = ArcBasisMill.componentInstantiationBuilder()
      .setMCType(ArcBasisMill.mCPrimitiveTypeBuilder().setPrimitive(2).build()).setComponentInstancesList(Collections.singletonList(instance)).build();
    IArcBasisScope scope = ArcBasisMill.scope();
    this.getSymTab().putOnStack(scope);
    this.getSymTab().handle(instances);
    Assertions.assertEquals(1, instances.getComponentInstanceList().size());
    Assertions.assertFalse(scope.getComponentInstanceSymbols().get("comp").isEmpty());
    Assertions.assertEquals(1, scope.getComponentInstanceSymbols().get("comp").get(0).getArguments().size());
  }

  protected ASTExpression[] mockValues(int length) {
    ASTExpression[] values = new ASTExpression[length];
    for (int i = 0; i < length; i++) {
      values[i] = Mockito.mock(ASTExpression.class);
    }
    return values;
  }

  /**
   * grants access to the usually capsulated used arcbasis genitor
   */
  private static class ArcBasisScopesGenitorTestDelegator extends ArcBasisScopesGenitorDelegator {
    public ArcBasisScopesGenitorTestDelegator(IArcBasisGlobalScope scope) {
      super(scope);
    }

    public ArcBasisScopesGenitor getGenitor() {
      return symbolTable;
    }
  }
}