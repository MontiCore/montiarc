/* (c) https://github.com/MontiCore/monticore */
package variablearc._cocos;

import arcbasis._ast.ASTArcElement;
import arcbasis._ast.ASTComponentHead;
import arcbasis._ast.ASTComponentType;
import arcbasis._symboltable.ComponentInstanceSymbol;
import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis._symboltable.PortSymbol;
import arcbasis.check.TypeExprOfComponent;
import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.types.check.SymTypeExpression;
import de.se_rwth.commons.logging.Log;
import montiarc.util.VariableArcError;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import variablearc.VariableArcAbstractTest;
import variablearc.VariableArcMill;
import variablearc._ast.ASTArcVarIf;

import java.util.Arrays;
import java.util.Collections;

/**
 * Tests for {@link VarIfOmitPortReferences}
 */
public class VarIfOmitPortReferencesTest extends VariableArcAbstractTest {

  protected final static String INDEPENDENT_COMPONENT_NAME = "IndependentComp";
  protected final static String INDEPENDENT_COMPONENT_IN_PORT_NAME = "someIndependentInPort";
  protected final static String INDEPENDENT_COMPONENT_OUT_PORT_NAME = "someIndependentOutPort";
  protected VarIfOmitPortReferences coco;

  protected static ASTExpression nameExpression(@NotNull String name) {
    Preconditions.checkNotNull(name);
    return VariableArcMill.nameExpressionBuilder().setName(name).build();
  }

  @BeforeEach
  protected void setCoCo() {
    coco = new VarIfOmitPortReferences();
  }

  /**
   * Provides a component type symbol with specified name and one in and one out
   * port with specified names. If such a component did not exist in the global
   * scope yet, it is added to it.
   */
  protected ComponentTypeSymbol provideComponentWithInAndOutPort(@NotNull String compName,
                                                                 @NotNull String inPortName,
                                                                 @NotNull String outPortName) {
    Preconditions.checkNotNull(compName);
    Preconditions.checkNotNull(inPortName);
    Preconditions.checkNotNull(outPortName);

    if (VariableArcMill.globalScope().resolveComponentType(compName)
      .isPresent() && VariableArcMill.globalScope()
      .resolveComponentType(compName).get()
      .getIncomingPort(inPortName).isPresent() && VariableArcMill.globalScope()
      .resolveComponentType(compName).get().getOutgoingPort(outPortName)
      .isPresent()
      && VariableArcMill.globalScope().resolveComponentType(compName).get()
      .getAllPorts().size() == 2) {
      return VariableArcMill.globalScope().resolveComponentType(compName).get();
    } else {
      ComponentTypeSymbol comp = VariableArcMill.componentTypeSymbolBuilder()
        .setName(compName).setSpannedScope(VariableArcMill.scope()).build();

      PortSymbol inPort = VariableArcMill.portSymbolBuilder()
        .setName(inPortName).setIncoming(true)
        .setType(Mockito.mock(SymTypeExpression.class)).build();
      comp.getSpannedScope().add(inPort);

      PortSymbol outPort = VariableArcMill.portSymbolBuilder()
        .setName(outPortName).setIncoming(false)
        .setType(Mockito.mock(SymTypeExpression.class)).build();
      comp.getSpannedScope().add(outPort);

      VariableArcMill.globalScope().add(comp);
      VariableArcMill.globalScope().addSubScope(comp.getSpannedScope());

      return comp;
    }
  }

  /**
   * Provides a component type symbol called {@link #INDEPENDENT_COMPONENT_NAME}
   * with an in port of name {@link #INDEPENDENT_COMPONENT_IN_PORT_NAME} and an
   * out port of name {@link #INDEPENDENT_COMPONENT_OUT_PORT_NAME}. This
   * component type is added to the global scope subsequently, if it was not
   * present there before.
   */
  protected ComponentTypeSymbol provideIndependentComponent() {

    if (VariableArcMill.globalScope()
      .resolveComponentType(INDEPENDENT_COMPONENT_NAME).isPresent()) {
      return VariableArcMill.globalScope()
        .resolveComponentType(INDEPENDENT_COMPONENT_NAME).get();
    } else {
      ComponentTypeSymbol independentComp =
        VariableArcMill.componentTypeSymbolBuilder()
          .setName(INDEPENDENT_COMPONENT_NAME)
          .setSpannedScope(VariableArcMill.scope()).build();

      PortSymbol inPort =
        VariableArcMill.portSymbolBuilder()
          .setName(INDEPENDENT_COMPONENT_IN_PORT_NAME).setIncoming(true)
          .setType(Mockito.mock(SymTypeExpression.class)).build();
      independentComp.getSpannedScope().add(inPort);

      PortSymbol outPort =
        VariableArcMill.portSymbolBuilder()
          .setName(INDEPENDENT_COMPONENT_OUT_PORT_NAME).setIncoming(false)
          .setType(Mockito.mock(SymTypeExpression.class))
          .build();
      independentComp.getSpannedScope().add(outPort);

      VariableArcMill.globalScope().add(independentComp);
      VariableArcMill.globalScope()
        .addSubScope(independentComp.getSpannedScope());

      return independentComp;
    }
  }

  /**
   * Provides a component type symbol that has does not reference any ports in
   * the initializer expressions of its fields. If no such component was found
   * in the global scope yet, the component is added to it.
   */
  protected ComponentTypeSymbol provideCompWithoutPortRef() {
    final String compInPortName = "someInPort";
    final String compOutPortName = "someOutPort";

    ComponentTypeSymbol comp = provideComponentWithInAndOutPort("WithoutPortRef", compInPortName, compOutPortName);

    ASTArcVarIf varif = VariableArcMill.arcVarIfBuilder()
      .setCondition(nameExpression("someCondition"))
      .setThen(Mockito.mock(ASTArcElement.class)).build();

    ASTComponentType compAst = VariableArcMill.componentTypeBuilder()
      .setName("WithoutPortRef").setHead(Mockito.mock(ASTComponentHead.class))
      .setBody(VariableArcMill.componentBodyBuilder()
        .setArcElementsList(Collections.singletonList(varif)).build())
      .build();
    compAst.setSymbol(comp);
    comp.setAstNode(compAst);


    ComponentInstanceSymbol independentCompInst =
      VariableArcMill.componentInstanceSymbolBuilder()
        .setName("independentComp")
        .setType(new TypeExprOfComponent(provideIndependentComponent()))
        .build();
    comp.getSpannedScope().add(independentCompInst);

    ComponentInstanceSymbol independentCompInst2 =
      VariableArcMill.componentInstanceSymbolBuilder()
        .setName("independentComp2")
        .setType(new TypeExprOfComponent(provideIndependentComponent()))
        .build();
    comp.getSpannedScope().add(independentCompInst2);

    return comp;
  }

  /**
   * Provides a component type symbol that references its own ports in the
   * expressions of its if statements. If there was no such component before in
   * the global scope, the component is added to it.
   */
  protected ComponentTypeSymbol provideCompWithOwnPortRef() {
    final String compInPortName = "anotherInPort";
    final String compOutPortName = "anotherOutPort";

    ASTArcVarIf varif1 =
      VariableArcMill.arcVarIfBuilder()
        .setCondition(nameExpression(compInPortName))
        .setThen(Mockito.mock(ASTArcElement.class)).build();

    ASTArcVarIf varif2 = VariableArcMill.arcVarIfBuilder()
      .setCondition(nameExpression(compOutPortName))
      .setThen(Mockito.mock(ASTArcElement.class)).build();

    ComponentTypeSymbol comp = provideComponentWithInAndOutPort("WithOwnPortRef", compInPortName, compOutPortName);

    ASTComponentType compAst = VariableArcMill.componentTypeBuilder()
      .setName("WithOwnPortRef").setHead(Mockito.mock(ASTComponentHead.class))
      .setBody(VariableArcMill.componentBodyBuilder()
        .setArcElementsList(Arrays.asList(varif1, varif2))
        .build()).build();
    compAst.setSymbol(comp);
    comp.setAstNode(compAst);

    return comp;
  }

  @Test
  public void shouldNotFindPortReference() {
    // Given
    ComponentTypeSymbol comp = provideCompWithoutPortRef();

    // When
    this.coco.check(comp.getAstNode());

    // Then
    Assertions.assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void shouldFindOwnPortReference() {
    // Given
    ComponentTypeSymbol comp = provideCompWithOwnPortRef();

    // When
    this.coco.check(comp.getAstNode());

    // Then
    this.checkOnlyExpectedErrorsPresent(VariableArcError.PORT_REFERENCE_IN_IF_STATEMENT_ILLEGAL, VariableArcError.PORT_REFERENCE_IN_IF_STATEMENT_ILLEGAL);
  }
}
