/* (c) https://github.com/MontiCore/monticore */
package arcautomaton._cocos;

import arcautomaton.AbstractTest;
import arcautomaton.ArcAutomatonMill;
import arcautomaton._ast.ASTArcStatechart;
import arcbasis.util.ArcError;
import de.monticore.scbasis._ast.ASTSCSAnte;
import de.monticore.scbasis._ast.ASTSCState;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class NoRedundantInitialOutputTest extends AbstractTest {

  @Test
  void shouldFind2UnambiguousInitialOutputs() {
    // Given
    // creating two initial states with init-outputs which is allowed.
    ASTArcStatechart automaton = ArcAutomatonMill.arcStatechartBuilder().build();
    ASTSCState fooState = provideEmptyStateWithSymbol("Foo");
    ASTSCState barState = provideEmptyStateWithSymbol("Bar");
    fooState.getSCModifier().setInitial(true);
    barState.getSCModifier().setInitial(true);

    ASTSCSAnte initialFooDec = ArcAutomatonMill.anteActionBuilder()
      .setMCBlockStatementsList(new ArrayList<>())
      .build();
    ASTSCSAnte initialBarDec = ArcAutomatonMill.anteActionBuilder()
      .setMCBlockStatementsList(new ArrayList<>())
      .build();

    fooState.setSCSAnte(initialFooDec);
    barState.setSCSAnte(initialBarDec);
    automaton.addSCStatechartElement(fooState);
    automaton.addSCStatechartElement(barState);

    // When
    NoRedundantInitialOutput coco = new NoRedundantInitialOutput();
    coco.check(automaton);

    // Then
    checkOnlyExpectedErrorsPresent(new ArcError[]{});
  }

  @Test
  void shouldFindRedundantInitialOutputs() {
    // Given
    ASTArcStatechart automaton = ArcAutomatonMill.arcStatechartBuilder().build();
    ASTSCState fooState1 = provideEmptyStateWithSymbol("Foo");
    ASTSCState fooState2 = provideEmptyStateWithSymbol("Foo");
    fooState1.getSCModifier().setInitial(true);
    fooState2.getSCModifier().setInitial(true);

    ASTSCSAnte initialFooDec1 = ArcAutomatonMill.anteActionBuilder()
      .setMCBlockStatementsList(new ArrayList<>())
      .build();
    ASTSCSAnte initialFooDec2 = ArcAutomatonMill.anteActionBuilder()
      .setMCBlockStatementsList(new ArrayList<>())
      .build();

    fooState1.setSCSAnte(initialFooDec1);
    fooState2.setSCSAnte(initialFooDec2);
    automaton.addSCStatechartElement(fooState1);
    automaton.addSCStatechartElement(fooState2);

    // When
    NoRedundantInitialOutput coco = new NoRedundantInitialOutput();
    coco.check(automaton);

    // Then
    checkOnlyExpectedErrorsPresent(ArcError.REDUNDANT_INITIAL_DECLARATION);
  }
}
