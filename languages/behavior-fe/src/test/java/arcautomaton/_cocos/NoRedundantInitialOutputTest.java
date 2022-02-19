/* (c) https://github.com/MontiCore/monticore */
package arcautomaton._cocos;

import arcautomaton.AbstractTest;
import arcautomaton.ArcAutomatonMill;
import arcautomaton._ast.ASTArcStatechart;
import arcautomaton._ast.ASTInitialOutputDeclaration;
import arcbasis._ast.ASTComponentType;
import arcbehaviorbasis.BehaviorError;
import de.monticore.scactions._ast.ASTSCABody;
import de.monticore.scbasis._ast.ASTSCState;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class NoRedundantInitialOutputTest extends AbstractTest {

  @Test
  void shouldFind2UnambiguousInitialOutputs() {
    // Given
    // creating two initialOutputDeclarations that refer to two different states which is allowed.
    ASTComponentType comp = provideComponentTypeWithSymbol("Comp");
    ASTArcStatechart automaton = ArcAutomatonMill.arcStatechartBuilder().build();
    ASTSCState fooState = provideEmptyStateWithSymbol("Foo");
    ASTSCState barState = provideEmptyStateWithSymbol("Bar");
    ASTInitialOutputDeclaration initialFooDec = ArcAutomatonMill.initialOutputDeclarationBuilder()
      .setName("Foo")
      .setSCABody(Mockito.mock(ASTSCABody.class))
      .build();
    ASTInitialOutputDeclaration initialBarDec = ArcAutomatonMill.initialOutputDeclarationBuilder()
      .setName("Bar")
      .setSCABody(Mockito.mock(ASTSCABody.class))
      .build();

    automaton.addSCStatechartElement(fooState);
    automaton.addSCStatechartElement(barState);
    automaton.addSCStatechartElement(initialFooDec);
    automaton.addSCStatechartElement(initialBarDec);

    // When
    NoRedundantInitialOutput coco = new NoRedundantInitialOutput();
    coco.check(automaton);

    // Then
    checkOnlyExpectedErrorsPresent(new BehaviorError[]{});
  }

  @Test
  void shouldFindRedundantInitialOutputs() {
    // Given
    ASTComponentType comp = provideComponentTypeWithSymbol("Comp");
    ASTArcStatechart automaton = ArcAutomatonMill.arcStatechartBuilder().build();
    ASTSCState fooState = provideEmptyStateWithSymbol("Foo");
    ASTInitialOutputDeclaration initialDec1 = ArcAutomatonMill.initialOutputDeclarationBuilder()
      .setName("Foo")
      .setSCABody(Mockito.mock(ASTSCABody.class))
      .build();
    ASTInitialOutputDeclaration initialDec2 = ArcAutomatonMill.initialOutputDeclarationBuilder()
      .setName("Foo")
      .setSCABody(Mockito.mock(ASTSCABody.class))
      .build();

    automaton.addSCStatechartElement(fooState);
    automaton.addSCStatechartElement(initialDec1);
    automaton.addSCStatechartElement(initialDec2);

    // When
    NoRedundantInitialOutput coco = new NoRedundantInitialOutput();
    coco.check(automaton);

    // Then
    checkOnlyExpectedErrorsPresent(BehaviorError.REDUNDANT_INITIAL_DECLARATION);
  }
}
