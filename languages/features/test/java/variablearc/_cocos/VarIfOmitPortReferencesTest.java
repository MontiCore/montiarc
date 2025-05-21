/* (c) https://github.com/MontiCore/monticore */
package variablearc._cocos;

import arcbasis._ast.ASTArcComponentType;
import arcbasis._ast.ASTArcElement;
import arcbasis._ast.ASTComponentHead;
import arcbasis._symboltable.ArcComponentTypeSymbol;
import de.monticore.symbols.compsymbols._symboltable.PortSymbol;
import arcbasis.check.TypeExprOfComponent;
import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.symbols.compsymbols._symboltable.SubcomponentSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.se_rwth.commons.logging.Log;
import montiarc.util.VariableArcError;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import variablearc.VariableArcMill;
import variablearc.VariableArcTestBase;
import variablearc._ast.ASTArcVarIf;

import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link VarIfOmitPortReferences}
 */
public class VarIfOmitPortReferencesTest extends VariableArcTestBase {

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
  protected ArcComponentTypeSymbol provideComponentWithInAndOutPort(@NotNull String compName,
                                                                    @NotNull String inPortName,
                                                                    @NotNull String outPortName) {
    Preconditions.checkNotNull(compName);
    Preconditions.checkNotNull(inPortName);
    Preconditions.checkNotNull(outPortName);

    if (VariableArcMill.globalScope().resolveArcComponentType(compName)
      .isPresent() && VariableArcMill.globalScope()
      .resolveArcComponentType(compName).get()
      .getIncomingPort(inPortName).isPresent() && VariableArcMill.globalScope()
      .resolveArcComponentType(compName).get().getOutgoingPort(outPortName)
      .isPresent()
      && VariableArcMill.globalScope().resolveArcComponentType(compName).get()
      .getAllPorts().size() == 2) {
      return VariableArcMill.globalScope().resolveArcComponentType(compName).get();
    } else {
      ArcComponentTypeSymbol comp = VariableArcMill.arcComponentTypeSymbolBuilder()
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
  protected ArcComponentTypeSymbol provideIndependentComponent() {

    if (VariableArcMill.globalScope()
      .resolveArcComponentType(INDEPENDENT_COMPONENT_NAME).isPresent()) {
      return VariableArcMill.globalScope()
        .resolveArcComponentType(INDEPENDENT_COMPONENT_NAME).get();
    } else {
      ArcComponentTypeSymbol independentComp =
        VariableArcMill.arcComponentTypeSymbolBuilder()
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
  protected ArcComponentTypeSymbol provideCompWithoutPortRef() {
    final String compInPortName = "someInPort";
    final String compOutPortName = "someOutPort";

    ArcComponentTypeSymbol comp = provideComponentWithInAndOutPort("WithoutPortRef", compInPortName, compOutPortName);

    ASTArcVarIf varif = VariableArcMill.arcVarIfBuilder()
      .setCondition(nameExpression("someCondition"))
      .setThen(Mockito.mock(ASTArcElement.class)).build();

    ASTArcComponentType compAst = VariableArcMill.arcComponentTypeBuilder()
      .setName("WithoutPortRef").setHead(Mockito.mock(ASTComponentHead.class))
      .setBody(VariableArcMill.componentBodyBuilder()
        .setArcElementsList(Collections.singletonList(varif)).build())
      .build();
    compAst.setSymbol(comp);
    comp.setAstNode(compAst);


    SubcomponentSymbol independentCompInst =
      VariableArcMill.subcomponentSymbolBuilder()
        .setName("independentComp")
        .setType(new TypeExprOfComponent(provideIndependentComponent()))
        .build();
    comp.getSpannedScope().add(independentCompInst);

    SubcomponentSymbol independentCompInst2 =
      VariableArcMill.subcomponentSymbolBuilder()
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
  protected ArcComponentTypeSymbol provideCompWithOwnPortRef() {
    final String compInPortName = "anotherInPort";
    final String compOutPortName = "anotherOutPort";

    ASTArcVarIf varif1 =
      VariableArcMill.arcVarIfBuilder()
        .setCondition(nameExpression(compInPortName))
        .setThen(Mockito.mock(ASTArcElement.class)).build();

    ASTArcVarIf varif2 = VariableArcMill.arcVarIfBuilder()
      .setCondition(nameExpression(compOutPortName))
      .setThen(Mockito.mock(ASTArcElement.class)).build();

    ArcComponentTypeSymbol comp = provideComponentWithInAndOutPort("WithOwnPortRef", compInPortName, compOutPortName);

    ASTArcComponentType compAst = VariableArcMill.arcComponentTypeBuilder()
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
    ArcComponentTypeSymbol comp = provideCompWithoutPortRef();

    // When
    this.coco.check(comp.getAstNode());

    // Then
    Assertions.assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void shouldFindOwnPortReference() {
    // Given
    ArcComponentTypeSymbol comp = provideCompWithOwnPortRef();

    // When
    this.coco.check(comp.getAstNode());

    // Then
    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(
        getErrorCodes(
          VariableArcError.PORT_REFERENCE_IN_IF_STATEMENT_ILLEGAL,
          VariableArcError.PORT_REFERENCE_IN_IF_STATEMENT_ILLEGAL
        )
      );
  }
}
