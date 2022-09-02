/* (c) https://github.com/MontiCore/monticore */
package arcbasis._symboltable;

import arcbasis.AbstractTest;
import arcbasis.ArcBasisMill;
import arcbasis._ast.*;
import com.google.common.collect.Lists;
import de.monticore.expressions.expressionsbasis._ast.ASTArguments;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.types.mcbasictypes._ast.ASTConstantsMCBasicTypes;
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
    this.symTab = new ArcBasisScopesGenitorTestDelegator();
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
    Assertions.assertTrue(symbol.getSpannedScope().isShadowing());
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
    // Given
    ASTMCType type = ArcBasisMill.mCQualifiedTypeBuilder()
      .setMCQualifiedName(ArcBasisMill.mCQualifiedNameBuilder()
        .setPartsList(Lists.newArrayList("String"))
        .build())
      .build();
    ASTArcParameter astParam = arcbasis.ArcBasisMill.arcParameterBuilder()
      .setName("par").setMCType(type).build();
    ComponentTypeSymbol enclosingComp = ArcBasisMill.componentTypeSymbolBuilder()
      .setName("Encl")
      .setSpannedScope(ArcBasisMill.scope())
      .build();
    IArcBasisScope compScope = enclosingComp.getSpannedScope();

    this.getSymTab().putOnStack(enclosingComp);
    this.getSymTab().putOnStack(compScope);

    // When
    this.getSymTab().visit(astParam);

    // Then
    Assertions.assertEquals(compScope, astParam.getEnclosingScope());
    Assertions.assertTrue(this.getSymTab().getCurrentScope().isPresent());
    Assertions.assertEquals(compScope, this.getSymTab().getCurrentScope().get());
    Assertions.assertEquals(1, compScope.getLocalVariableSymbols().size());
    Assertions.assertEquals(1, enclosingComp.getParameters().size());
    Assertions.assertEquals(astParam, compScope.getLocalVariableSymbols().get(0).getAstNode());
    Assertions.assertEquals(astParam, enclosingComp.getParameters().get(0).getAstNode());
  }

  @Test
  public void shouldCreateParameter() {
    ASTMCType type = ArcBasisMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BYTE).build();
    ASTArcParameter ast = arcbasis.ArcBasisMill.arcParameterBuilder().setName("par")
      .setMCType(type).build();
    VariableSymbol symbol = this.getSymTab().create_ArcParameter(ast).build();
    Assertions.assertEquals(ast.getName(), symbol.getName());
    Assertions.assertNull(symbol.getType()); // We set the symbol in the completer
  }

  @Test
  public void shouldVisitParameter() {
    // Given
    ASTArcParameter astParam = arcbasis.ArcBasisMill.arcParameterBuilder().setName("par")
      .setMCType(ArcBasisMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BYTE).build()).build();
    ComponentTypeSymbol enclosingComp = ArcBasisMill.componentTypeSymbolBuilder()
      .setName("Encl")
      .setSpannedScope(ArcBasisMill.scope())
      .build();
    IArcBasisScope compScope = enclosingComp.getSpannedScope();
    this.getSymTab().putOnStack(enclosingComp);
    this.getSymTab().putOnStack(compScope);

    // When
    this.getSymTab().visit(astParam);

    // Then
    Assertions.assertEquals(compScope, astParam.getEnclosingScope());
    Assertions.assertFalse(compScope.getVariableSymbols().isEmpty());
    Assertions.assertFalse(enclosingComp.getParameters().isEmpty());
    Assertions.assertEquals(1, compScope.getLocalVariableSymbols().size());
    Assertions.assertEquals(1, enclosingComp.getParameters().size());
    Assertions.assertEquals(astParam, compScope.getLocalVariableSymbols().get(0).getAstNode());
    Assertions.assertEquals(astParam, enclosingComp.getParameters().get(0).getAstNode());
  }

  @Test
  public void shouldVisitPortDeclaration() {
    ASTPortDeclaration ast = arcbasis.ArcBasisMill.portDeclarationBuilder()
      .setMCType(ArcBasisMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BYTE).build())
      .setIncoming(true)
      .setPortList("p1", "p2", "p3").build();
    IArcBasisScope scope = ArcBasisMill.scope();
    this.getSymTab().putOnStack(scope);
    this.getSymTab().visit(ast);
    Assertions.assertEquals(scope, ast.getEnclosingScope());
    Assertions.assertTrue(this.getSymTab().getCurrentPortDirection().isPresent());
  }

  @Test
  public void shouldEndVisitPortDeclaration() {
    ASTPortDeclaration ast = arcbasis.ArcBasisMill.portDeclarationBuilder()
      .setMCType(ArcBasisMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BYTE).build())
      .setOutgoing(true)
      .setPortList("p1", "p2", "p3").build();
    this.getSymTab().visit(ast);
    this.getSymTab().endVisit(ast);
    Assertions.assertFalse(this.getSymTab().getCurrentPortDirection().isPresent());
  }

  @Test
  public void shouldCreatePort() {
    this.getSymTab().setCurrentPortDirection(arcbasis.ArcBasisMill.portDirectionInBuilder().build());
    ASTPort ast = arcbasis.ArcBasisMill.portBuilder().setName("p").build();
    PortSymbol symbol = this.getSymTab().create_Port(ast).buildWithoutType();
    Assertions.assertEquals(ast.getName(), symbol.getName());
    Assertions.assertNotNull(symbol.getDirection());
  }

  @Test
  public void shouldVisitPort() {
    // Given
    ASTPort ast = arcbasis.ArcBasisMill.portBuilder().setName("p").build();
    IArcBasisScope scope = ArcBasisMill.scope();
    this.getSymTab().setCurrentPortDirection(arcbasis.ArcBasisMill.portDirectionInBuilder().build());
    this.getSymTab().putOnStack(scope);

    // When
    this.getSymTab().visit(ast);

    // Then
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
  }

  @Test
  public void shouldCreateComponentInstance() {
    ASTComponentInstance ast = arcbasis.ArcBasisMill.componentInstanceBuilder().setName("sub").build();
    ComponentInstanceSymbol symbol = this.getSymTab().create_ComponentInstance(ast).build();
    Assertions.assertEquals(ast.getName(), symbol.getName());
    Assertions.assertNotNull(symbol.getArguments());
    Assertions.assertThrows(IllegalStateException.class, symbol::getType);
  }

  @Test
  public void shouldInitializeComponentInstance() {
    ASTComponentInstance ast = arcbasis.ArcBasisMill.componentInstanceBuilder().setName("sub")
      .setArguments(arcbasis.ArcBasisMill.argumentsBuilder()
        .setExpressionsList(Arrays.asList(this.mockValues(3))).build()).build();
    IArcBasisScope scope = ArcBasisMill.scope();
    this.getSymTab().pushCurrentEnclosingScope4Instances(scope);
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
    this.getSymTab().pushCurrentEnclosingScope4Instances(scope);
    this.getSymTab().handle(ast);
    Assertions.assertEquals(scope, ast.getEnclosingScope());
    Assertions.assertFalse(scope.getLocalComponentInstanceSymbols().isEmpty());
    Assertions.assertEquals(1, scope.getLocalComponentInstanceSymbols().size());
    Assertions.assertEquals(3, scope.getComponentInstanceSymbols().get("sub").get(0)
      .getArguments().size());
  }

  @Test
  public void shouldVisitFieldDeclaration() {
    ASTMCType type = ArcBasisMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BYTE).build();
    String[] names = new String[]{"var1", "var2", "var3"};
    ASTArcFieldDeclaration ast = arcbasis.ArcBasisMill.arcFieldDeclarationBuilder()
      .setMCType(type).setArcFieldList(names, this.mockValues(names.length)).build();
    IArcBasisScope scope = ArcBasisMill.scope();
    this.getSymTab().putOnStack(scope);
    this.getSymTab().visit(ast);
    Assertions.assertEquals(scope, ast.getEnclosingScope());
    Assertions.assertTrue(this.getSymTab().getCurrentScope().isPresent());
    Assertions.assertEquals(scope, this.getSymTab().getCurrentScope().get());
  }

  @Test
  public void shouldEndVisitFieldDeclaration() {
    String[] names = new String[]{"var1", "var2", "var3"};
    ASTArcFieldDeclaration ast = arcbasis.ArcBasisMill.arcFieldDeclarationBuilder()
      .setArcFieldList(names, this.mockValues(names.length))
      .setMCType(ArcBasisMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BYTE).build())
      .build();
    this.getSymTab().visit(ast);
    this.getSymTab().endVisit(ast);
  }

  @Test
  public void shouldCreateField() {
    ASTArcField ast = arcbasis.ArcBasisMill.arcFieldBuilder().setName("var")
      .setInitial(Mockito.mock(ASTExpression.class)).build();
    ASTMCType type = ArcBasisMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BYTE).build();
    VariableSymbol symbol = this.getSymTab().create_ArcField(ast).build();
    Assertions.assertEquals(ast.getName(), symbol.getName());
  }

  @Test
  public void shouldVisitField() {
    ASTArcField ast = arcbasis.ArcBasisMill.arcFieldBuilder().setName("var")
      .setInitial(Mockito.mock(ASTExpression.class)).build();
    IArcBasisScope scope = ArcBasisMill.scope();
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
      .setMCType(ArcBasisMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BYTE).build())
      .setComponentInstancesList(Collections.singletonList(instance)).build();
    IArcBasisScope scope = ArcBasisMill.scope();
    this.getSymTab().putOnStack(scope);
    this.getSymTab().pushCurrentEnclosingScope4Instances(scope);
    this.getSymTab().handle(instances);
    Assertions.assertEquals(1, instances.getComponentInstanceList().size());
    Assertions.assertFalse(scope.getComponentInstanceSymbols().get("comp").isEmpty());
    Assertions.assertEquals(1, scope.getComponentInstanceSymbols().get("comp").get(0).getArguments().size());
  }

  @Test
  public void shouldVisitComponentBody() {
    // Given
    IArcBasisScope scope = ArcBasisMill.scope();
    ASTComponentBody compBody = arcbasis.ArcBasisMill.componentBodyBuilder().build();

    // When
    this.getSymTab().putOnStack(scope);
    this.getSymTab().visit(compBody);

    // Then
    Assertions.assertEquals(scope, compBody.getEnclosingScope());
    Assertions.assertTrue(this.getSymTab().getCurrentEnclosingScope4Instances().isPresent());
    Assertions.assertEquals(scope, this.getSymTab().getCurrentEnclosingScope4Instances().get());
  }

  @Test
  public void shouldEndVisitComponentBody() {
    // Given
    IArcBasisScope lowStackScope = ArcBasisMill.scope();
    IArcBasisScope highStackScope = ArcBasisMill.scope();
    ASTComponentBody compBody = arcbasis.ArcBasisMill.componentBodyBuilder().build();

    // When
    this.getSymTab().pushCurrentEnclosingScope4Instances(lowStackScope);
    this.getSymTab().pushCurrentEnclosingScope4Instances(highStackScope);
    this.getSymTab().endVisit(compBody);

    // Then
    Assertions.assertEquals(1, this.getSymTab().getEnclosingScope4InstancesStack().size());
    Assertions.assertEquals(lowStackScope, this.getSymTab().getCurrentEnclosingScope4Instances().get());


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
    public ArcBasisScopesGenitor getGenitor() {
      return symbolTable;
    }
  }
}