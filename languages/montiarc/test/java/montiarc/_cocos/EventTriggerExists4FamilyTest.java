/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcautomaton.ArcAutomatonMill;
import arcautomaton._ast.ASTArcStatechart;
import arcautomaton._ast.ASTMsgEvent;
import arcautomaton._symboltable.IArcAutomatonScope;
import arcbasis._ast.ASTArcComponentType;
import arcbasis._ast.ASTArcElement;
import arcbasis._ast.ASTComponentBody;
import com.google.common.base.Preconditions;
import de.monticore.class2mc.OOClass2MCResolver;
import de.monticore.scbasis._ast.ASTSCStatechartElement;
import de.monticore.scbasis._ast.ASTSCTransition;
import de.monticore.sctransitions4code._ast.ASTTransitionBody;
import de.monticore.symbols.compsymbols._symboltable.PortSymbol;
import de.monticore.symbols.compsymbols._symboltable.PortSymbolBuilder;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcMill;
import montiarc.MontiArcTestBase;
import montiarc._ast.ASTMACompilationUnit;
import montiarc.util.ArcAutomataError;
import montiarc.util.Error;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class EventTriggerExists4FamilyTest extends MontiArcTestBase {

  @BeforeEach
  protected void initSymbols() {
    MontiArcMill.globalScope().addAdaptedTypeSymbolResolver(new OOClass2MCResolver());
    MontiArcMill.globalScope().addAdaptedOOTypeSymbolResolver(new OOClass2MCResolver());
    setUpComponents();
  }

  protected void setUpComponents() {
    compile("package a.b; component A { }");
    compile("package a.b; component B {port in boolean i; port in String mesin;}");
    compile("package a.b; component C { port out boolean o; }");
    compile("package a.b; component D { port in int i; }");
    compile("package a.b; component E { port out int o; }");
    compile("package a.b; component F { port in java.lang.Integer i; }");
    compile("package a.b; component G { port in java.util.List<java.lang.Integer> i; }");
    compile("package a.b; component H { port out java.util.List<java.lang.Integer> o; }");
    compile("package a.b; component I { port in java.lang.Comparable<java.lang.Integer> i; }");
    compile("package a.b; component J { port out java.lang.Comparable<java.lang.Integer> o; }");
    compile("package a.b; component K<T> { port in T i; } ");
    compile("package a.b; component L<T> { port out T o; }");
  }

  private static Stream<Arguments> provideUniqueSenderModel() {
    List<Arguments> componentList = new ArrayList<>();
    Arguments simpleModel = arg("component Test{}");
    componentList.add(simpleModel);
    return componentList.stream();
  }

  @ParameterizedTest
  @MethodSource("provideUniqueSenderModel")
  public void TestModelRuntimeMontiArcCoCos(@NotNull String model) {

    Preconditions.checkNotNull(model);
    MontiArcFullVariantCoCoChecker checker = new MontiArcFullVariantCoCoChecker();
    checker.get4FullVariant().addCoCo(new EventTriggerExists4Family());

    ASTMACompilationUnit mainAST = compile(model);
    checker.checkAll(mainAST);

    String[] test = getLoggedErrorCodes();

    // Then
    assertThat(Log.getErrorCount() == 0);
  }

  private static <T> void addCoCoAs(T coco, Consumer<T> consumer) {
    consumer.accept(coco);
  }

  @ParameterizedTest
  @MethodSource("validParams")
  public void shouldFindEventSymbol(String eventName, String[] portNames) {
    Preconditions.checkNotNull(eventName);
    Preconditions.checkNotNull(portNames);

    //Given
    ASTMsgEvent msgEvent = ArcAutomatonMill.msgEventBuilder().setName(eventName).build();
    msgEvent.setEnclosingScope(createTestScope(portNames));
    ASTTransitionBody transitionBody = ArcAutomatonMill.transitionBodyBuilder().setSCEvent(msgEvent).build();
    ASTSCTransition scTransition = ArcAutomatonMill.sCTransitionBuilder().setSCTBody(transitionBody).uncheckedBuild();
    List<ASTSCStatechartElement> transitionList = new ArrayList<>();
    transitionList.add(scTransition);
    ASTArcStatechart chart = ArcAutomatonMill.arcStatechartBuilder().setSCStatechartElementsList(transitionList).uncheckedBuild();
    List<ASTArcElement> chartElementList = new ArrayList<>();
    chartElementList.add(chart);
    ASTComponentBody body = ArcAutomatonMill.componentBodyBuilder().setArcElementsList(chartElementList).uncheckedBuild();
    ASTArcComponentType compType = ArcAutomatonMill.arcComponentTypeBuilder().setBody(body).uncheckedBuild();
    compType.setEnclosingScope(createTestScope(portNames));
    EventTriggerExists4Family coco = new EventTriggerExists4Family();

    //When
    coco.check(compType);

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
    ASTTransitionBody transitionBody = ArcAutomatonMill.transitionBodyBuilder().setSCEvent(msgEvent).build();
    ASTSCTransition scTransition = ArcAutomatonMill.sCTransitionBuilder().setSCTBody(transitionBody).uncheckedBuild();
    List<ASTSCStatechartElement> transitionList = new ArrayList<>();
    transitionList.add(scTransition);
    ASTArcStatechart chart = ArcAutomatonMill.arcStatechartBuilder().setSCStatechartElementsList(transitionList).uncheckedBuild();
    List<ASTArcElement> chartElementList = new ArrayList<>();
    chartElementList.add(chart);
    ASTComponentBody body = ArcAutomatonMill.componentBodyBuilder().setArcElementsList(chartElementList).uncheckedBuild();
    ASTArcComponentType compType = ArcAutomatonMill.arcComponentTypeBuilder().setBody(body).uncheckedBuild();
    compType.setEnclosingScope(createTestScope(portNames));

    EventTriggerExists4Family coco = new EventTriggerExists4Family();

    //When
    coco.check(compType);

    //Then
    assertThat(Log.getFindings()).as(Log.getFindings().toString()).isNotEmpty();
    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(error));
  }

  protected static Stream<Arguments> validParams() {
    return Stream.of(
      Arguments.arguments("Tick", new String[0]),
      Arguments.arguments("Tick", new String[]{"aPort"}),
      Arguments.arguments("aPort", new String[]{"aPort"}),
      Arguments.arguments("aPort", new String[]{"aPort", "bPort"}),
      Arguments.arguments("aPort", new String[]{"aPort", "aPort"})
    );
  }

  protected static Stream<Arguments> invalidParams() {
    return Stream.of(
      Arguments.arguments("aPort", new String[0], ArcAutomataError.CANT_FIND_MSG_EVENT_SYMBOL),
      Arguments.arguments("aPort", new String[]{"bPort"}, ArcAutomataError.CANT_FIND_MSG_EVENT_SYMBOL)
    );
  }

  protected IArcAutomatonScope createTestScope(String[] portNames) {
    IArcAutomatonScope scope = ArcAutomatonMill.scope();

    for (String portName : portNames) {
      PortSymbol portSymbol = new PortSymbolBuilder()
        .setName(portName)
        .setIncoming(true)
        .setOutgoing(false)
        .setEnclosingScope(scope)
        .setType(SymTypeExpressionFactory.createObscureType())
        .setStronglyCausal(false)
        .build();
      scope.add(portSymbol);
    }

    ArcAutomatonMill.globalScope().addSubScope(scope);
    return scope;
  }
}
