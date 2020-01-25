package arc._cocos;

import arc._ast.ASTComponent;
import arc._ast.ASTComponentHead;
import arc._ast.ASTComponentInstantiation;
import arc._ast.ArcMill;
import arc._symboltable.ArcScope;
import arc._symboltable.ArcSymbolTableCreator;
import arc.util.ArcError;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.se_rwth.commons.logging.Log;
import montiarc.AbstractTest;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

import java.util.regex.Pattern;
import java.util.stream.Stream;

class PortUsageTest extends AbstractTest {

  @Override
  protected Pattern supplyErrorCodePattern() {
    return ArcError.ERROR_CODE_PATTERN;
  }

  @ParameterizedTest
  @MethodSource("componentAndErrorCodeProvider")
  void shouldDetectWronglyConnectedPorts(ASTComponent ast, ArcError[] errors) {
    PortUsage coco = new PortUsage();
    coco.check(ast);
    this.checkOnlyExpectedErrorsPresent(Log.getFindings(), errors);
  }

  static Stream<Arguments> componentAndErrorCodeProvider() {
    ArcSymbolTableCreator symTab = new ArcSymbolTableCreator(new ArcScope());
    ASTComponent comp1 = ArcMill.componentBuilder().setName("Comp1")
      .setHead(Mockito.mock(ASTComponentHead.class))
      .setBody(ArcMill.componentBodyBuilder()
        .addArcElement(ArcMill.componentInterfaceBuilder()
          .addPort(ArcMill.portDeclarationBuilder()
            .setIncoming(true).setType(Mockito.mock(ASTMCType.class))
            .setPortList("i1", "i2", "i3")
            .build())
          .addPort(ArcMill.portDeclarationBuilder()
            .setOutgoing(true).setType(Mockito.mock(ASTMCType.class))
            .setPortList("o1", "o2", "o3")
            .build())
          .build())
        .addArcElement(ArcMill.componentInstantiationBuilder()
          .setType(Mockito.mock(ASTMCObjectType.class)).setInstanceList("sub1", "sub2").build())
        .addArcElement(ArcMill.connectorBuilder().setSource("i1").setTargetList("o1", "o2").build())
        .addArcElement(ArcMill.connectorBuilder().setSource("i2").setTargetList("o3", "i3").build())
        .build())
      .build();
    symTab.handle(comp1);
    ASTComponent comp2 = ArcMill.componentBuilder().setName("Comp2")
      .setHead(Mockito.mock(ASTComponentHead.class))
      .setBody(ArcMill.componentBodyBuilder()
        .addArcElement(ArcMill.componentInterfaceBuilder()
          .addPort(ArcMill.portDeclarationBuilder()
            .setIncoming(true).setType(Mockito.mock(ASTMCType.class))
            .setPortList("i1")
            .build())
          .addPort(ArcMill.portDeclarationBuilder()
            .setOutgoing(true).setType(Mockito.mock(ASTMCType.class))
            .setPortList("o1", "o2", "o3")
            .build())
          .build())
        .addArcElement(ArcMill.componentInstantiationBuilder()
          .setType(Mockito.mock(ASTMCObjectType.class)).setInstanceList("sub1", "sub2").build())
        .addArcElement(ArcMill.connectorBuilder().setSource("i1").setTargetList("o1").build())
        .addArcElement(ArcMill.connectorBuilder().setSource("o2").setTargetList("o3").build())
        .build())
      .build();
    symTab.handle(comp2);
    return Stream.of(Arguments.of(comp1, new ArcError[] { ArcError.INCOMING_PORT_AS_TARGET }),
      Arguments.of(comp2, new ArcError[] { ArcError.OUTGOING_PORT_AST_SOURCE }));
  }
}