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
import arcbasis.check.CompTypeExpression;
import arcbasis.check.TypeExprOfComponent;
import arcbasis.util.ArcError;
import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.se_rwth.commons.logging.Log;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class ParameterDefaultValuesOmitPortReferencesTest extends AbstractTest {

  protected final static String INDEPENDENT_COMPONENT_NAME = "IndependentComp";
  protected final static String INDEPENDENT_COMPONENT_IN_PORT_NAME = "someIndependentInPort";
  protected final static String INDEPENDENT_COMPONENT_OUT_PORT_NAME = "someIndependentOutPort";

  /**
   * Provides a component type symbol with specified name and one in and one out port with specified names. The provided
   * component type is also added to the global scope, if it wasn't in there before.
   */
  protected ComponentTypeSymbol provideComponentWithInAndOutPort(
    @NotNull String compName, @NotNull String inPortName, @NotNull String outPortName) {
    Preconditions.checkNotNull(compName);
    Preconditions.checkNotNull(inPortName);
    Preconditions.checkNotNull(outPortName);

    if(
      ArcBasisMill.globalScope().resolveComponentType(compName).isPresent()
        && ArcBasisMill.globalScope().resolveComponentType(compName).get().getIncomingPort(inPortName).isPresent()
        && ArcBasisMill.globalScope().resolveComponentType(compName).get().getOutgoingPort(outPortName).isPresent()
        && ArcBasisMill.globalScope().resolveComponentType(compName).get().getAllPorts().size() == 2
    ) {
      return ArcBasisMill.globalScope().resolveComponentType(compName).get();
    } else {
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

      ArcBasisMill.globalScope().add(comp);
      ArcBasisMill.globalScope().addSubScope(comp.getSpannedScope());

      return comp;
    }
  }

  /**
   * Provides a component type symbol called {@link #INDEPENDENT_COMPONENT_NAME} with an in port of name {@link
   * #INDEPENDENT_COMPONENT_IN_PORT_NAME} and an out port of name {@link #INDEPENDENT_COMPONENT_OUT_PORT_NAME}.
   * If this component type has not been added to the global scope yet, it is added by this method.
   */
  protected ComponentTypeSymbol provideIndependentComponent() {
    return provideComponentWithInAndOutPort(
      INDEPENDENT_COMPONENT_NAME,
      INDEPENDENT_COMPONENT_IN_PORT_NAME,
      INDEPENDENT_COMPONENT_OUT_PORT_NAME
    );
  }

  /**
   * Provides a component type symbol that has does not reference any ports in the default value expressions of it's
   * configuration parameters. It the provided component type symbol has not been added to the global scope yet, it is
   * also added to the global scope by this method.
   */
  protected ComponentTypeSymbol provideCompWithoutPortRef() {
    if(ArcBasisMill.globalScope().resolveComponentType("WithoutPortRef").isPresent()) {
      return ArcBasisMill.globalScope().resolveComponentType("WithoutPortRef").get();
    } else {
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

      VariableSymbol param = ArcBasisMill.variableSymbolBuilder()
        .setName("someParam")
        .setType(Mockito.mock(SymTypeExpression.class))
        .setAstNode(ArcBasisMill.arcParameterBuilder()
          .setName("someParam")
          .setDefault(nameExpression("okayValue"))
          .setMCType(Mockito.mock(ASTMCType.class))
          .build()
        )
        .build();
      comp.getSpannedScope().add(param);
      comp.addParameter(param);

      ComponentInstanceSymbol independentCompInst = ArcBasisMill.componentInstanceSymbolBuilder()
        .setName("independentComp")
        .setType(new TypeExprOfComponent(provideIndependentComponent()))
        .build();
      comp.getSpannedScope().add(independentCompInst);

      ComponentInstanceSymbol independentChildInst = ArcBasisMill.componentInstanceSymbolBuilder()
        .setName("independentComp2")
        .setType(new TypeExprOfComponent(provideIndependentComponent()))
        .build();
      comp.getSpannedScope().add(independentChildInst);

      ArcBasisMill.globalScope().add(comp);
      ArcBasisMill.globalScope().addSubScope(comp.getSpannedScope());

      return comp;
    }
  }

  /**
   * Provides a component type symbol that references it's own ports in the default value expressions of it's
   * configuration parameters. If the provided component type has not been added to the global scope yet, it is also
   * added to the global scope.
   */
  protected ComponentTypeSymbol provideCompWithOwnPortRef() {
    if(ArcBasisMill.globalScope().resolveComponentType("WithOwnPortRef").isPresent()) {
      return ArcBasisMill.globalScope().resolveComponentType("WithOwnPortRef").get();
    } else {
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

      VariableSymbol firstParam = ArcBasisMill.variableSymbolBuilder()
        .setName("firstParam")
        .setType(Mockito.mock(SymTypeExpression.class))
        .setAstNode(ArcBasisMill.arcParameterBuilder()
          .setName("firstParam")
          .setDefault(nameExpression(compInPortName))
          .setMCType(Mockito.mock(ASTMCType.class))
          .build()
        )
        .build();
      comp.getSpannedScope().add(firstParam);
      comp.addParameter(firstParam);

      VariableSymbol secondParam = ArcBasisMill.variableSymbolBuilder()
        .setName("secondParam")
        .setType(Mockito.mock(SymTypeExpression.class))
        .setAstNode(ArcBasisMill.arcParameterBuilder()
          .setName("secondParam")
          .setDefault(nameExpression(compOutPortName))
          .setMCType(Mockito.mock(ASTMCType.class))
          .build()
        )
        .build();
      comp.getSpannedScope().add(secondParam);
      comp.addParameter(secondParam);

      ArcBasisMill.globalScope().add(comp);
      ArcBasisMill.globalScope().addSubScope(comp.getSpannedScope());

      return comp;
    }
  }

  /**
   * Provides a component type symbol that inherits from the one that inherits from the symbol returned by {@link
   * #provideIndependentComponent()}. The component returned by this method accesses the ports of it's parent in the
   * default value expressions of it's configuration parameters. If the provided component type has not been added to
   * the global scope yet, this method also adds it to the global scope.
   */
  protected ComponentTypeSymbol provideCompWithInheritedPortRef(CompTypeExpression parent) {
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

    VariableSymbol firstParam = ArcBasisMill.variableSymbolBuilder()
      .setName("firstParam")
      .setType(Mockito.mock(SymTypeExpression.class))
      .setAstNode(ArcBasisMill.arcParameterBuilder()
        .setName("firstParam")
        .setDefault(nameExpression(INDEPENDENT_COMPONENT_IN_PORT_NAME))
        .setMCType(Mockito.mock(ASTMCType.class))
        .build()
        )
      .build();
    comp.getSpannedScope().add(firstParam);
    comp.addParameter(firstParam);

    VariableSymbol secondParam = ArcBasisMill.variableSymbolBuilder()
      .setName("secondParam")
      .setType(Mockito.mock(SymTypeExpression.class))
      .setAstNode(ArcBasisMill.arcParameterBuilder()
        .setName("secondParam")
        .setDefault(nameExpression(INDEPENDENT_COMPONENT_OUT_PORT_NAME))
        .setMCType(Mockito.mock(ASTMCType.class))
        .build()
      )
      .build();
    comp.getSpannedScope().add(secondParam);
    comp.addParameter(secondParam);

    ArcBasisMill.globalScope().add(comp);
    ArcBasisMill.globalScope().addSubScope(comp.getSpannedScope());

    return comp;
  }

  @Test
  public void shouldNotFindPortReference() {
    // Given
    ComponentTypeSymbol comp = provideCompWithoutPortRef();

    // When
    ParameterDefaultValuesOmitPortReferences coco = new ParameterDefaultValuesOmitPortReferences();
    coco.check(comp.getAstNode());

    // Then
    Assertions.assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void shouldFindOwnPortReference() {
    // Given
    ComponentTypeSymbol comp = provideCompWithOwnPortRef();

    // When
    ParameterDefaultValuesOmitPortReferences coco = new ParameterDefaultValuesOmitPortReferences();
    coco.check(comp.getAstNode());

    // Then

    this.checkOnlyExpectedErrorsPresent(
      ArcError.PORT_REFERENCE_IN_PARAMETER_DEFAULT_VALUE_ILLEGAL,
      ArcError.PORT_REFERENCE_IN_PARAMETER_DEFAULT_VALUE_ILLEGAL);
  }

  @Test
  public void shouldFindInheritedPortReference() {
    // Given
    IArcBasisScope scope = ArcBasisMill.scope();
    ComponentTypeSymbol superComp = provideIndependentComponent();
    ComponentTypeSymbol comp = provideCompWithInheritedPortRef(new TypeExprOfComponent(superComp));
    scope.add(superComp);
    scope.add(comp);
    scope.addSubScope(superComp.getSpannedScope());
    scope.addSubScope(comp.getSpannedScope());

    // When
    ParameterDefaultValuesOmitPortReferences coco = new ParameterDefaultValuesOmitPortReferences();
    coco.check(comp.getAstNode());

    // Then
    this.checkOnlyExpectedErrorsPresent(
      ArcError.PORT_REFERENCE_IN_PARAMETER_DEFAULT_VALUE_ILLEGAL,
      ArcError.PORT_REFERENCE_IN_PARAMETER_DEFAULT_VALUE_ILLEGAL);
  }

  protected static ASTExpression nameExpression(@NotNull String name) {
    Preconditions.checkNotNull(name);
    return ArcBasisMill.nameExpressionBuilder().setName(name).build();
  }
}
