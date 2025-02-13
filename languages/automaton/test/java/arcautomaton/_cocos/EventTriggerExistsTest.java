/* (c) https://github.com/MontiCore/monticore */
package arcautomaton._cocos;

import arcautomaton.ArcAutomatonMill;
import arcautomaton.ArcAutomatonTestBase;
import arcautomaton._ast.ASTMsgEvent;
import arcautomaton._symboltable.IArcAutomatonScope;
import arcautomaton._visitor.ArcAutomatonTraverser;
import arcbasis._symboltable.ArcPortSymbol;
import arcbasis._symboltable.ArcPortSymbolBuilder;
import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import montiarc.util.ArcAutomataError;
import montiarc.util.Error;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Collections;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class EventTriggerExistsTest extends ArcAutomatonTestBase {

  @ParameterizedTest
  @MethodSource("validParams")
  public void shouldFindEventSymbol(String eventName, String[] portNames) {
    Preconditions.checkNotNull(eventName);
    Preconditions.checkNotNull(portNames);
    
    //Given
    ASTMsgEvent msgEvent = ArcAutomatonMill.msgEventBuilder().setName(eventName).build();
    msgEvent.setEnclosingScope(createTestScope(portNames));

    EventTriggerExists coco = new EventTriggerExists();
    
    //When
    coco.check(msgEvent);
    
    //Then
    assertThat(Log.getFindingsCount()).as(Log.getFindings().toString()).isEqualTo(0);
  }
  
  @ParameterizedTest
  @MethodSource("invalidParams")
  public void shouldProduceError(String eventName, String[] portNames, Error error) {
    Preconditions.checkNotNull(eventName);
    Preconditions.checkNotNull(portNames);
  
    //Given
    ASTMsgEvent msgEvent = ArcAutomatonMill.msgEventBuilder().setName(eventName).build();
    msgEvent.setEnclosingScope(createTestScope(portNames));

    EventTriggerExists coco = new EventTriggerExists();

    //When
    coco.check(msgEvent);
  
    //Then
    assertThat(Log.getFindings()).as(Log.getFindings().toString()).isNotEmpty();
    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(error));
  }
  
  protected static Stream<Arguments> validParams() {
    return Stream.of(
        Arguments.arguments("Tick", new String[0]),
        Arguments.arguments("Tick", new String[] {"aPort"}),
        Arguments.arguments("aPort", new String[] {"aPort"}),
        Arguments.arguments("aPort", new String[] {"aPort", "bPort"}),
        Arguments.arguments("aPort", new String[] {"aPort", "aPort"})
    );
  }
  
  protected static Stream<Arguments> invalidParams() {
    return Stream.of(
        Arguments.arguments("aPort", new String[0], ArcAutomataError.MSG_EVENT_WITHOUT_SYMBOL),
        Arguments.arguments("aPort", new String[] {"bPort"}, ArcAutomataError.MSG_EVENT_WITHOUT_SYMBOL)
    );
  }
  
  protected IArcAutomatonScope createTestScope(String[] portNames) {
    IArcAutomatonScope scope = ArcAutomatonMill.scope();
    
    for(String portName: portNames) {
      ArcPortSymbol portSymbol = new ArcPortSymbolBuilder()
          .setName(portName)
          .setIncoming(true)
          .setOutgoing(false)
          .setEnclosingScope(scope)
          .buildWithoutType();
      scope.add(portSymbol);
    }
    
    ArcAutomatonMill.globalScope().addSubScope(scope);
    return scope;
  }
}
