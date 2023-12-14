/* (c) https://github.com/MontiCore/monticore */
package arcbasis._symboltable;

import arcbasis.ArcBasisAbstractTest;
import arcbasis.ArcBasisMill;
import arcbasis._ast.ASTArcArgument;
import arcbasis._ast.ASTArcField;
import arcbasis._ast.ASTArcFieldDeclaration;
import arcbasis._ast.ASTArcParameter;
import arcbasis._ast.ASTArcPort;
import arcbasis._ast.ASTComponentBody;
import arcbasis._ast.ASTComponentHead;
import arcbasis._ast.ASTComponentInstance;
import arcbasis._ast.ASTComponentInstantiation;
import arcbasis._ast.ASTComponentType;
import arcbasis._ast.ASTPortDeclaration;
import arcbasis.trafo.SeparateCompInstantiationFromTypeDeclTrafo;
import com.google.common.collect.Lists;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.compsymbols._symboltable.SubcomponentSymbol;
import de.monticore.types.mcbasictypes._ast.ASTConstantsMCBasicTypes;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;

/**
 * Holds tests for the handwritten methods of {@link ArcBasisScopesGenitor}.
 */
public class ArcBasisScopesGenitorTest extends ArcBasisAbstractTest {

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
    Assertions.assertNull(symbol.getType());
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
    this.getSymTab().setCurrentPortDirection(arcbasis.ArcBasisMill.portDirectionBuilder().setIn(true).build());
    ASTArcPort ast = arcbasis.ArcBasisMill.arcPortBuilder().setName("p").build();
    ArcPortSymbol symbol = this.getSymTab().create_Port(ast).buildWithoutType();
    Assertions.assertEquals(ast.getName(), symbol.getName());
    Assertions.assertTrue(symbol.isIncoming());
    Assertions.assertFalse(symbol.isOutgoing());
  }

  @Test
  public void shouldVisitPort() {
    // Given
    ASTArcPort ast = arcbasis.ArcBasisMill.arcPortBuilder().setName("p").build();
    IArcBasisScope scope = ArcBasisMill.scope();
    this.getSymTab().setCurrentPortDirection(arcbasis.ArcBasisMill.portDirectionBuilder().setIn(true).build());
    this.getSymTab().putOnStack(scope);

    // When
    this.getSymTab().visit(ast);

    // Then
    Assertions.assertEquals(scope, ast.getEnclosingScope());
    Assertions.assertFalse(scope.getLocalArcPortSymbols().isEmpty());
    Assertions.assertEquals(1, scope.getLocalArcPortSymbols().size());
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
    SubcomponentSymbol symbol = this.getSymTab().create_ComponentInstance(ast).build();
    Assertions.assertEquals(ast.getName(), symbol.getName());
//    Assertions.assertThrows(IllegalStateException.class, symbol::getType); Todo change upstream behavior
  }

  @Test
  public void shouldInitializeComponentInstance() {
    ASTComponentInstance ast = arcbasis.ArcBasisMill.componentInstanceBuilder().setName("sub")
      .setArcArguments(arcbasis.ArcBasisMill.arcArgumentsBuilder().setArcArgumentsList(
        Arrays.asList(this.argumentMockValues(3))).build()
      ).build();
    IArcBasisScope scope = ArcBasisMill.scope();
    this.getSymTab().putOnStack(scope);
    this.getSymTab().visit(ast);
    this.getSymTab().endVisit(ast);
  }

  @Test
  public void shouldVisitComponentInstance() {
    ASTComponentInstance ast = arcbasis.ArcBasisMill.componentInstanceBuilder().setName("sub")
      .setArcArguments(arcbasis.ArcBasisMill.arcArgumentsBuilder().setArcArgumentsList(
        Arrays.asList(this.argumentMockValues(3))).build()
      ).build();
    IArcBasisScope scope = ArcBasisMill.scope();
    this.getSymTab().putOnStack(scope);
    this.getSymTab().handle(ast);
    Assertions.assertEquals(scope, ast.getEnclosingScope());
    Assertions.assertFalse(scope.getLocalSubcomponentSymbols().isEmpty());
    Assertions.assertEquals(1, scope.getLocalSubcomponentSymbols().size());
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

  /**
   * If a model contains `component Foo fooInst {...}, it is expected to be transformed into two
   * separate statements `component Foo {...}` and `Foo fooInst;`. The symbol table should handle this
   * correctly.
   */
  @Test
  void shouldHandleTransformedDirectComponentInstantiation() {
    // Given
    IArcBasisScope scope = ArcBasisMill.scope();
    this.getSymTab().putOnStack(scope);
    ASTComponentBody body = ArcBasisMill.componentBodyBuilder().build();
    ASTComponentType typeDecl = ArcBasisMill.componentTypeBuilder()
      .setName("Foo")
      .addInstance("fooInst")
      .setHead(ArcBasisMill.componentHeadBuilder().build())
      .setBody(ArcBasisMill.componentBodyBuilder().build())
      .build();
    body.addArcElement(typeDecl);

    // When
    new SeparateCompInstantiationFromTypeDeclTrafo().visit(body);
    this.getSymTab().handle(body);

    // Then
    Assertions.assertAll(
      () -> Assertions.assertEquals(1, scope.getLocalComponentTypeSymbols().size()),
      () -> Assertions.assertEquals(1, scope.getLocalSubcomponentSymbols().size())
    );

    ComponentTypeSymbol typeSym = scope.getLocalComponentTypeSymbols().get(0);
    SubcomponentSymbol instSym = scope.getLocalSubcomponentSymbols().get(0);

    Assertions.assertAll(
      () -> Assertions.assertEquals("Foo", typeSym.getName()),
      () -> Assertions.assertEquals("fooInst", instSym.getName()),
      () -> Assertions.assertEquals(scope, typeSym.getEnclosingScope()),
      () -> Assertions.assertEquals(scope, instSym.getEnclosingScope()),
      () -> Assertions.assertEquals(scope, typeSym.getAstNode().getEnclosingScope()),
      () -> Assertions.assertEquals(scope, instSym.getAstNode().getEnclosingScope())
    );
  }

  protected ASTArcArgument[] argumentMockValues(int length) {
    ASTArcArgument[] values = new ASTArcArgument[length];
    for (int i = 0; i < length; i++) {
      values[i] = Mockito.mock(ASTArcArgument.class);
      values[i].setExpression(Mockito.mock(ASTExpression.class));
    }
    return values;
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