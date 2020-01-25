/* (c) https://github.com/MontiCore/monticore */
package arc._cocos;

import arc._ast.ASTComponent;
import arc._ast.ASTComponentHead;
import arc._ast.ArcMill;
import arc._symboltable.ArcScope;
import arc._symboltable.ArcSymbolTableCreator;
import arc.util.ArcError;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.se_rwth.commons.logging.Log;
import montiarc.AbstractTest;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Holds tests for the handwritten methods of {@link SubComponentsConnected}
 */
public class SubComponentsConnectedTest extends AbstractTest {

  @Override
  protected Pattern supplyErrorCodePattern() {
    return ArcError.ERROR_CODE_PATTERN;
  }

  @ParameterizedTest
  @MethodSource("componentAndErrorCodeProvider")
  void shouldDetectWronglyConnectedPorts(ASTComponent ast, ArcError[] errors) {
    SubComponentsConnected coco = new SubComponentsConnected();
    coco.check(ast);
    this.checkOnlyExpectedErrorsPresent(Log.getFindings(), errors);
  }

  static Stream<Arguments> componentAndErrorCodeProvider() {
    ArcSymbolTableCreator symTab = new ArcSymbolTableCreator(new ArcScope());
    ASTComponent comp1 = ArcMill.componentBuilder().setName("A")
      .setHead(Mockito.mock(ASTComponentHead.class))
      .setBody(ArcMill.componentBodyBuilder()
        .addArcElement(ArcMill.componentInterfaceBuilder()
          .addPort(ArcMill.portDeclarationBuilder()
            .setIncoming(true).setType(Mockito.mock(ASTMCType.class))
            .setPortList("i1", "i2")
            .build())
          .addPort(ArcMill.portDeclarationBuilder()
            .setOutgoing(true).setType(Mockito.mock(ASTMCType.class))
            .setPortList("o1")
            .build())
          .build())
        .build())
      .build();
    symTab.handle(comp1);
    ArcError[] errors1 = new ArcError[] {};
    ASTComponent comp2 = ArcMill.componentBuilder().setName("B")
      .setHead(Mockito.mock(ASTComponentHead.class))
      .setBody(ArcMill.componentBodyBuilder()
        .addArcElement(ArcMill.componentInterfaceBuilder()
          .addPort(ArcMill.portDeclarationBuilder()
            .setIncoming(true).setType(Mockito.mock(ASTMCType.class))
            .setPortList("i1")
            .build())
          .addPort(ArcMill.portDeclarationBuilder()
            .setOutgoing(true).setType(Mockito.mock(ASTMCType.class))
            .setPortList("o1")
            .build())
          .build())
        .addArcElement(ArcMill.componentInstantiationBuilder()
          .setType(ArcMill.mCQualifiedTypeBuilder().setMCQualifiedName(
            ArcMill.mCQualifiedNameBuilder().setPartList(Collections.singletonList("A"))
              .build())
            .build())
          .setInstanceList("sub1")
          .build())
        .addArcElement(ArcMill.connectorBuilder().setSource("i1").setTargetList("sub1.i1",
          "sub1.i2", "sub1.o1").build())
        .build())
      .build();
    symTab.handle(comp2);
    ArcError[] errors2 = new ArcError[] { ArcError.OUTGOING_PORT_AS_TARGET };
    ASTComponent comp3 = ArcMill.componentBuilder().setName("C")
      .setHead(Mockito.mock(ASTComponentHead.class))
      .setBody(ArcMill.componentBodyBuilder()
        .addArcElement(ArcMill.componentInterfaceBuilder()
          .addPort(ArcMill.portDeclarationBuilder()
            .setIncoming(true).setType(Mockito.mock(ASTMCType.class))
            .setPortList("i1")
            .build())
          .addPort(ArcMill.portDeclarationBuilder()
            .setOutgoing(true).setType(Mockito.mock(ASTMCType.class))
            .setPortList("o1", "o2")
            .build())
          .build())
        .addArcElement(ArcMill.componentInstantiationBuilder()
          .setType(ArcMill.mCQualifiedTypeBuilder().setMCQualifiedName(
            ArcMill.mCQualifiedNameBuilder().setPartList(Collections.singletonList("A"))
              .build())
            .build())
          .setInstanceList("sub1")
          .build())
        .addArcElement(ArcMill.connectorBuilder().setSource("i1").setTargetList("sub1.i1").build())
        .addArcElement(ArcMill.connectorBuilder().setSource("sub1.o1").setTargetList("o1").build())
        .addArcElement(ArcMill.connectorBuilder().setSource("sub1.i2").setTargetList("o2").build())
        .build())
      .build();
    symTab.handle(comp3);
    ArcError[] errors3 = new ArcError[] { ArcError.INCOMING_PORT_AS_SOURCE };
    return Stream.of(Arguments.of(comp1, errors1), Arguments.of(comp2, errors2),
      Arguments.of(comp3, errors3));
  }
}