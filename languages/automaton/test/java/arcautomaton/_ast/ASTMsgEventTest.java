/* (c) https://github.com/MontiCore/monticore */
package arcautomaton._ast;

import arcautomaton.ArcAutomatonMill;
import arcautomaton._symboltable.IArcAutomatonScope;
import arcautomaton._visitor.ArcAutomatonTraverser;
import arcbasis.ArcBasisAbstractTest;
import arcbasis.ArcBasisMill;
import arcbasis._symboltable.ArcPortSymbol;
import arcbasis._symboltable.ArcPortSymbolBuilder;
import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import montiarc.util.ArcAutomataError;
import montiarc.util.Error;
import org.junit.jupiter.api.AfterEach;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

public class ASTMsgEventTest extends ArcBasisAbstractTest {
  
  @BeforeAll
  public static void init() {
    LogStub.init();
    Log.enableFailQuick(false);
    ArcBasisMill.globalScope().clear();
    ArcBasisMill.reset();
    ArcBasisMill.init();
    ArcAutomatonMill.reset();
    ArcAutomatonMill.init();
  }
  
  @Override
  public void setUp() { }
  
  @AfterEach
  public void tearDown() {
    Log.clearFindings();
  }
  
  @ParameterizedTest
  @MethodSource("validParams")
  public void shouldFindEventSymbol(String eventName, boolean initializeTick, String[] portNames) {
    Preconditions.checkNotNull(eventName);
    Preconditions.checkNotNull(portNames);
    
    //Given
    if(initializeTick) ArcAutomatonMill.initializeTick();
    ASTMsgEvent msgEvent = ArcAutomatonMill.msgEventBuilder().setName(eventName).build();
    msgEvent.setEnclosingScope(createTestScope(portNames));
  
    ArcAutomatonTraverser traverser = ArcAutomatonMill.traverser();
    traverser.add4ArcAutomaton(ArcAutomatonMill.scopesGenitorP2());
    
    //When
    msgEvent.accept(traverser);
    
    //Then
    Assertions.assertThat(Log.getFindingsCount()).as(Log.getFindings().toString()).isEqualTo(0);
    Assertions.assertThat(msgEvent.getEventSymbol()).isNotNull();
  }
  
  @ParameterizedTest
  @MethodSource("invalidParams")
  public void shouldProduceError(String eventName, boolean initializeTick, String[] portNames, Error error) {
    Preconditions.checkNotNull(eventName);
    Preconditions.checkNotNull(portNames);
  
    //Given
    if(initializeTick) ArcAutomatonMill.initializeTick();
    ASTMsgEvent msgEvent = ArcAutomatonMill.msgEventBuilder().setName(eventName).build();
    msgEvent.setEnclosingScope(createTestScope(portNames));
  
    ArcAutomatonTraverser traverser = ArcAutomatonMill.traverser();
    traverser.add4ArcAutomaton(ArcAutomatonMill.scopesGenitorP2());
  
    //When
    msgEvent.accept(traverser);
  
    //Then
    Assertions.assertThat(Log.getFindings()).as(Log.getFindings().toString()).isNotEmpty();
    Assertions.assertThat(this.collectErrorCodes(Log.getFindings())).as(Log.getFindings().toString())
        .containsExactlyElementsOf(List.of(error.getErrorCode()));
  }
  
  protected static Stream<Arguments> validParams() {
    return Stream.of(
        Arguments.arguments("Tick", true, new String[0]),
        Arguments.arguments("Tick", true, new String[] {"aPort"}),
        Arguments.arguments("aPort", true, new String[] {"aPort"}),
        Arguments.arguments("aPort", true, new String[] {"aPort", "bPort"}),
        Arguments.arguments("aPort", true, new String[] {"aPort", "aPort"}),
        Arguments.arguments("Tick", false, new String[] {"Tick", "aPort"}),
        Arguments.arguments("aPort", false, new String[] {"Tick", "aPort"})
    );
  }
  
  protected static Stream<Arguments> invalidParams() {
    return Stream.of(
        Arguments.arguments("Tick", false, new String[0], ArcAutomataError.MSG_EVENT_WITHOUT_SYMBOL),
        Arguments.arguments("Tick", false, new String[] {"aPort"}, ArcAutomataError.MSG_EVENT_WITHOUT_SYMBOL),
        Arguments.arguments("aPort", true, new String[0], ArcAutomataError.MSG_EVENT_WITHOUT_SYMBOL),
        Arguments.arguments("aPort", false, new String[0], ArcAutomataError.MSG_EVENT_WITHOUT_SYMBOL),
        Arguments.arguments("aPort", true, new String[] {"bPort"}, ArcAutomataError.MSG_EVENT_WITHOUT_SYMBOL),
        Arguments.arguments("aPort", false, new String[] {"bPort"}, ArcAutomataError.MSG_EVENT_WITHOUT_SYMBOL)
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
