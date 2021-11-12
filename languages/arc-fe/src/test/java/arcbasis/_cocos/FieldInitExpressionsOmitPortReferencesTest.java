/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis.AbstractTest;
import arcbasis.ArcBasisMill;
import arcbasis._ast.ASTComponentBody;
import arcbasis._ast.ASTComponentHead;
import arcbasis._ast.ASTComponentType;
import arcbasis._symboltable.ComponentInstanceSymbol;
import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis._symboltable.IArcBasisScope;
import arcbasis._symboltable.PortSymbol;
import arcbasis.check.CompSymTypeExpression;
import arcbasis.check.SymTypeOfComponent;
import arcbasis.util.ArcError;
import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.se_rwth.commons.logging.Log;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Tests for {@link FieldInitExpressionsOmitPortReferences}
 */
public class FieldInitExpressionsOmitPortReferencesTest extends AbstractTest {

  protected FieldInitExpressionsOmitPortReferences coco;

  protected final static String INDEPENDENT_COMPONENT_NAME = "IndependentComp";
  protected final static String INDEPENDENT_COMPONENT_IN_PORT_NAME = "someIndependentInPort";
  protected final static String INDEPENDENT_COMPONENT_OUT_PORT_NAME = "someIndependentOutPort";
  protected final static String INDEPENDENT_CHILD_NAME = "IndependentChild";

  @BeforeEach
  protected void setCoCo() {
    coco = new FieldInitExpressionsOmitPortReferences();
  }

  /**
   * Provides a component type symbol with specified name and one in and one out port with specified names.
   */
  protected ComponentTypeSymbol provideComponentWithInAndOutPort(
    @NotNull String compName, @NotNull String inPortName, @NotNull String outPortName) {
    Preconditions.checkNotNull(compName);
    Preconditions.checkNotNull(inPortName);
    Preconditions.checkNotNull(outPortName);

    ComponentTypeSymbol comp = ArcBasisMill.componentTypeSymbolBuilder()
      .setName(compName)
      .setSpannedScope(ArcBasisMill.scope())
      .build();

    PortSymbol inPort = ArcBasisMill.portSymbolBuilder()
      .setName(inPortName)
      .setIncoming(true)
      .setType(Mockito.mock(SymTypeExpression.class))
      .build();
    comp.getSpannedScope().add(inPort);

    PortSymbol outPort = ArcBasisMill.portSymbolBuilder()
      .setName(outPortName)
      .setIncoming(false)
      .setType(Mockito.mock(SymTypeExpression.class))
      .build();
    comp.getSpannedScope().add(outPort);

    return comp;
  }

  /**
   * Provides a component type symbol called {@link #INDEPENDENT_COMPONENT_NAME} with an in port of name {@link
   * #INDEPENDENT_COMPONENT_IN_PORT_NAME} and an out port of name {@link #INDEPENDENT_COMPONENT_OUT_PORT_NAME}.
   */
  protected ComponentTypeSymbol provideIndependentComponent() {
    ComponentTypeSymbol independentComp = ArcBasisMill.componentTypeSymbolBuilder()
      .setName(INDEPENDENT_COMPONENT_NAME)
      .setSpannedScope(ArcBasisMill.scope())
      .build();

    PortSymbol inPort = ArcBasisMill.portSymbolBuilder()
      .setName(INDEPENDENT_COMPONENT_IN_PORT_NAME)
      .setIncoming(true)
      .setType(Mockito.mock(SymTypeExpression.class))
      .build();
    independentComp.getSpannedScope().add(inPort);

    PortSymbol outPort = ArcBasisMill.portSymbolBuilder()
      .setName(INDEPENDENT_COMPONENT_OUT_PORT_NAME)
      .setIncoming(false)
      .setType(Mockito.mock(SymTypeExpression.class))
      .build();
    independentComp.getSpannedScope().add(outPort);

    return independentComp;
  }

  /**
   * Provides a component type symbol that has does not reference any ports in the initializer expressions of it's
   * fields.
   */
  protected ComponentTypeSymbol provideCompWithoutPortRef() {
    final String compInPortName = "someInPort";
    final String compOutPortName = "someOutPort";

    ComponentTypeSymbol comp = provideComponentWithInAndOutPort(
      "WithoutPortRef", compInPortName, compOutPortName);

    ASTComponentType compAst = ArcBasisMill.componentTypeBuilder()
      .setName("WithoutPortRef")
      .setHead(Mockito.mock(ASTComponentHead.class))
      .setBody(Mockito.mock(ASTComponentBody.class))
      .build();
    compAst.setSymbol(comp);
    comp.setAstNode(compAst);

    VariableSymbol field = ArcBasisMill.variableSymbolBuilder()
      .setName("someField")
      .setType(Mockito.mock(SymTypeExpression.class))
      .setAstNode(ArcBasisMill.arcFieldBuilder()
        .setName("someField")
        .setInitial(nameExpression("okayValue"))
        .build()
      )
      .build();
    comp.getSpannedScope().add(field);

    ComponentInstanceSymbol independentCompInst = ArcBasisMill.componentInstanceSymbolBuilder()
      .setName("independentComp")
      .setType(ArcBasisMill.componentTypeSymbolSurrogateBuilder()
        .setName(INDEPENDENT_COMPONENT_NAME)
        .setEnclosingScope(comp.getSpannedScope())
        .build()
      )
      .build();
    comp.getSpannedScope().add(independentCompInst);

    ComponentInstanceSymbol independentChildInst = ArcBasisMill.componentInstanceSymbolBuilder()
      .setName("independentChild")
      .setType(ArcBasisMill.componentTypeSymbolSurrogateBuilder()
        .setName(INDEPENDENT_CHILD_NAME)
        .setEnclosingScope(comp.getSpannedScope())
        .build()
      )
      .build();
    comp.getSpannedScope().add(independentChildInst);

    return comp;
  }

  /**
   * Provides a component type symbol that references it's own ports in the initializer expressions of it's fields.
   */
  protected ComponentTypeSymbol provideCompWithOwnPortRef() {
    final String compInPortName = "anotherInPort";
    final String compOutPortName = "anotherOutPort";

    ComponentTypeSymbol comp = provideComponentWithInAndOutPort(
      "WithOwnPortRef", compInPortName, compOutPortName);

    ASTComponentType compAst = ArcBasisMill.componentTypeBuilder()
      .setName("WithOwnPortRef")
      .setHead(Mockito.mock(ASTComponentHead.class))
      .setBody(Mockito.mock(ASTComponentBody.class))
      .build();
    compAst.setSymbol(comp);
    comp.setAstNode(compAst);

    VariableSymbol firstField = ArcBasisMill.variableSymbolBuilder()
      .setName("firstField")
      .setType(Mockito.mock(SymTypeExpression.class))
      .setAstNode(ArcBasisMill.arcFieldBuilder()
        .setName("firstField")
        .setInitial(nameExpression(compInPortName))
        .build()
      )
      .build();
    comp.getSpannedScope().add(firstField);

    VariableSymbol secondField = ArcBasisMill.variableSymbolBuilder()
      .setName("secondField")
      .setType(Mockito.mock(SymTypeExpression.class))
      .setAstNode(ArcBasisMill.arcFieldBuilder()
        .setName("secondField")
        .setInitial(nameExpression(compOutPortName))
        .build()
      )
      .build();
    comp.getSpannedScope().add(secondField);

    return comp;
  }

  /**
   * Provides a component type symbol that inherits from the one that inherits from the symbol returned by {@link
   * #provideIndependentComponent()}. Note that this relationship is only realized by a surrogate. If you want to have
   * access to the parent, you must put the parent into a scope that is reachable from the component returned by this
   * method. The component returned by this method accesses the ports of it's parent in initializer expressions for it's
   * fields.
   */
  protected ComponentTypeSymbol provideCompWithInheritedPortRef(CompSymTypeExpression parent) {
    ComponentTypeSymbol comp = ArcBasisMill.componentTypeSymbolBuilder()
      .setName("WithInheritedPortRef")
      .setSpannedScope(ArcBasisMill.scope())
      .build();

    ASTComponentType compAst = ArcBasisMill.componentTypeBuilder()
      .setName("WithInheritedPortRef")
      .setHead(Mockito.mock(ASTComponentHead.class))
      .setBody(Mockito.mock(ASTComponentBody.class))
      .build();
    compAst.setSymbol(comp);
    comp.setAstNode(compAst);

    comp.setParent(parent);

    VariableSymbol firstField = ArcBasisMill.variableSymbolBuilder()
      .setName("firstField")
      .setType(Mockito.mock(SymTypeExpression.class))
      .setAstNode(ArcBasisMill.arcFieldBuilder()
        .setName("firstField")
        .setInitial(nameExpression(INDEPENDENT_COMPONENT_IN_PORT_NAME))
        .build()
      )
      .build();
    comp.getSpannedScope().add(firstField);

    VariableSymbol secondField = ArcBasisMill.variableSymbolBuilder()
      .setName("secondField")
      .setType(Mockito.mock(SymTypeExpression.class))
      .setAstNode(ArcBasisMill.arcFieldBuilder()
        .setName("secondField")
        .setInitial(nameExpression(INDEPENDENT_COMPONENT_OUT_PORT_NAME))
        .build()
      )
      .build();
    comp.getSpannedScope().add(secondField);

    return comp;
  }

  @Test
  public void shouldNotFindPortReference() {
    // Given
    IArcBasisScope scope = ArcBasisMill.scope();
    ComponentTypeSymbol comp = provideCompWithoutPortRef();
    scope.add(comp);
    scope.addSubScope(comp.getSpannedScope());

    // When
    this.coco.check(comp.getAstNode());

    // Then
    Assertions.assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void shouldFindOwnPortReference() {
    // Given
    IArcBasisScope scope = ArcBasisMill.scope();
    ComponentTypeSymbol comp = provideCompWithOwnPortRef();
    scope.add(comp);
    scope.addSubScope(comp.getSpannedScope());

    // When
    this.coco.check(comp.getAstNode());

    // Then

    this.checkOnlyExpectedErrorsPresent(
      ArcError.PORT_REFERENCE_IN_FIELD_INIT_EXPRESSION_ILLEGAL,
      ArcError.PORT_REFERENCE_IN_FIELD_INIT_EXPRESSION_ILLEGAL);
  }

  @Test
  public void shouldFindInheritedPortReference() {
    // Given
    IArcBasisScope scope = ArcBasisMill.scope();
    ComponentTypeSymbol superComp = provideIndependentComponent();
    ComponentTypeSymbol comp = provideCompWithInheritedPortRef(new SymTypeOfComponent(superComp));
    scope.add(superComp);
    scope.add(comp);
    scope.addSubScope(superComp.getSpannedScope());
    scope.addSubScope(comp.getSpannedScope());

    // When
    this.coco.check(comp.getAstNode());

    // Then
    this.checkOnlyExpectedErrorsPresent(
      ArcError.PORT_REFERENCE_IN_FIELD_INIT_EXPRESSION_ILLEGAL,
      ArcError.PORT_REFERENCE_IN_FIELD_INIT_EXPRESSION_ILLEGAL);
  }


  protected static ASTExpression nameExpression(@NotNull String name) {
    Preconditions.checkNotNull(name);
    return ArcBasisMill.nameExpressionBuilder().setName(name).build();
  }
}
