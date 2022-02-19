/* (c) https://github.com/MontiCore/monticore */
package arcautomaton._cocos;

import arcautomaton.AbstractTest;
import arcautomaton.ArcAutomatonMill;
import arcautomaton._ast.ASTArcStatechart;
import arcautomaton._ast.ASTInitialOutputDeclaration;
import arcautomaton._symboltable.ArcAutomatonScope;
import arcbasis._ast.ASTComponentType;
import arcbehaviorbasis.BehaviorError;
import de.monticore.scactions._ast.ASTSCABody;
import de.monticore.scbasis._ast.ASTSCState;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class InitialStatesResolvableTest extends AbstractTest {
  @Test
  void shouldFindUnresolvableInitialState() {
    // Given
    ASTComponentType comp = provideComponentTypeWithSymbol("Comp");
    ASTArcStatechart automaton = ArcAutomatonMill.arcStatechartBuilder().build();
    ASTSCState fooState = provideEmptyStateWithSymbol("Foo");
    ASTInitialOutputDeclaration initialDec = ArcAutomatonMill.initialOutputDeclarationBuilder()
      .setName("Bar")
      .setSCABody(Mockito.mock(ASTSCABody.class))
      .build();

    automaton.addSCStatechartElement(fooState);
    automaton.addSCStatechartElement(initialDec);

    ((ArcAutomatonScope) comp.getSpannedScope()).add(fooState.getSymbol());
    automaton.setEnclosingScope(comp.getSpannedScope());
    fooState.setEnclosingScope((ArcAutomatonScope) comp.getSpannedScope());
    initialDec.setEnclosingScope(comp.getSpannedScope());

    // When
    InitialStatesResolvable coco = new InitialStatesResolvable();
    coco.check(initialDec);

    // Then
    checkOnlyExpectedErrorsPresent(BehaviorError.INITIAL_STATE_REFERENCE_MISSING);
  }

  @Test
  void shouldFindResolvableInitialState() {
    // Given
    ASTComponentType comp = provideComponentTypeWithSymbol("Comp");
    ASTArcStatechart automaton = ArcAutomatonMill.arcStatechartBuilder().build();
    ASTSCState fooState = provideEmptyStateWithSymbol("Foo");
    ASTInitialOutputDeclaration initialDec = ArcAutomatonMill.initialOutputDeclarationBuilder()
      .setName("Foo")
      .setSCABody(Mockito.mock(ASTSCABody.class))
      .build();

    automaton.addSCStatechartElement(fooState);
    automaton.addSCStatechartElement(initialDec);

    ((ArcAutomatonScope) comp.getSpannedScope()).add(fooState.getSymbol());
    automaton.setEnclosingScope(comp.getSpannedScope());
    fooState.setEnclosingScope((ArcAutomatonScope) comp.getSpannedScope());
    initialDec.setEnclosingScope(comp.getSpannedScope());

    // When
    InitialStatesResolvable coco = new InitialStatesResolvable();
    coco.check(initialDec);

    // Then
    checkOnlyExpectedErrorsPresent(new BehaviorError[]{});
  }
}
