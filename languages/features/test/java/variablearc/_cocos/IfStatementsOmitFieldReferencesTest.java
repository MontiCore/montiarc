/* (c) https://github.com/MontiCore/monticore */
package variablearc._cocos;

import arcbasis._ast.ASTArcElement;
import arcbasis._ast.ASTComponentHead;
import arcbasis._ast.ASTComponentType;
import arcbasis._symboltable.ComponentTypeSymbol;
import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
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
import variablearc._ast.ASTArcIfStatement;

import java.util.Arrays;
import java.util.Collections;

/**
 * Tests for {@link IfStatementsOmitFieldReferences}
 */
public class IfStatementsOmitFieldReferencesTest extends VariableArcAbstractTest {

  protected IfStatementsOmitFieldReferences coco;

  protected static ASTExpression nameExpression(@NotNull String name) {
    Preconditions.checkNotNull(name);
    return VariableArcMill.nameExpressionBuilder().setName(name).build();
  }

  @BeforeEach
  protected void setCoCo() {
    coco = new IfStatementsOmitFieldReferences();
  }

  /**
   * Provides a component type symbol with specified name and one in and one out
   * port with specified names. If such a component did not exist in the global
   * scope yet, it is added to it.
   */
  protected ComponentTypeSymbol provideComponentWithField(@NotNull String compName,
                                                          @NotNull String fieldName) {
    Preconditions.checkNotNull(compName);
    Preconditions.checkNotNull(fieldName);

    if (VariableArcMill.globalScope().resolveComponentType(compName)
      .isPresent() && VariableArcMill.globalScope()
      .resolveComponentType(compName).get()
      .getFields(fieldName).isPresent()
      && VariableArcMill.globalScope().resolveComponentType(compName).get()
      .getFields().size() == 1) {
      return VariableArcMill.globalScope().resolveComponentType(compName).get();
    } else {
      ComponentTypeSymbol comp = VariableArcMill.componentTypeSymbolBuilder()
        .setName(compName).setSpannedScope(VariableArcMill.scope()).build();

      VariableSymbol field = VariableArcMill.fieldSymbolBuilder()
        .setName(fieldName)
        .setType(Mockito.mock(SymTypeExpression.class)).build();
      comp.getSpannedScope().add(field);

      VariableArcMill.globalScope().add(comp);
      VariableArcMill.globalScope().addSubScope(comp.getSpannedScope());

      return comp;
    }
  }

  /**
   * Provides a component type symbol that has does not reference any ports in
   * the initializer expressions of its fields. If no such component was found
   * in the global scope yet, the component is added to it.
   */
  protected ComponentTypeSymbol provideCompWithoutFieldRef() {
    final String compFieldName = "someField";

    ComponentTypeSymbol comp = provideComponentWithField("WithoutFieldRef", compFieldName);

    ASTArcIfStatement ifStatement = VariableArcMill.arcIfStatementBuilder()
      .setCondition(nameExpression("someCondition"))
      .setThenStatement(Mockito.mock(ASTArcElement.class)).build();

    ASTComponentType compAst = VariableArcMill.componentTypeBuilder()
      .setName("WithoutFieldRef").setHead(Mockito.mock(ASTComponentHead.class))
      .setBody(VariableArcMill.componentBodyBuilder()
        .setArcElementsList(Collections.singletonList(ifStatement)).build())
      .build();
    compAst.setSymbol(comp);
    comp.setAstNode(compAst);

    return comp;
  }

  /**
   * Provides a component type symbol that references its own ports in the
   * expressions of its if statements. If there was no such component before in
   * the global scope, the component is added to it.
   */
  protected ComponentTypeSymbol provideCompWithOwnFieldRef() {
    final String compFieldName = "someField";

    ASTArcIfStatement firstIfStatement =
      VariableArcMill.arcIfStatementBuilder()
        .setCondition(nameExpression(compFieldName))
        .setThenStatement(Mockito.mock(ASTArcElement.class)).build();

    ASTArcIfStatement secondIfStatement = VariableArcMill.arcIfStatementBuilder()
      .setCondition(nameExpression(compFieldName))
      .setThenStatement(Mockito.mock(ASTArcElement.class)).build();

    ComponentTypeSymbol comp = provideComponentWithField("WithOwnFieldRef", compFieldName);

    ASTComponentType compAst = VariableArcMill.componentTypeBuilder()
      .setName("WithOwnFieldRef").setHead(Mockito.mock(ASTComponentHead.class))
      .setBody(VariableArcMill.componentBodyBuilder()
        .setArcElementsList(Arrays.asList(firstIfStatement, secondIfStatement))
        .build()).build();
    compAst.setSymbol(comp);
    comp.setAstNode(compAst);

    return comp;
  }

  @Test
  public void shouldNotFindFieldReference() {
    // Given
    ComponentTypeSymbol comp = provideCompWithoutFieldRef();

    // When
    this.coco.check(comp.getAstNode());

    // Then
    Assertions.assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void shouldFindOwnFieldReference() {
    // Given
    ComponentTypeSymbol comp = provideCompWithOwnFieldRef();

    // When
    this.coco.check(comp.getAstNode());

    // Then
    this.checkOnlyExpectedErrorsPresent(VariableArcError.FIELD_REFERENCE_IN_IF_STATEMENT_ILLEGAL,
      VariableArcError.FIELD_REFERENCE_IN_IF_STATEMENT_ILLEGAL);
  }
}
