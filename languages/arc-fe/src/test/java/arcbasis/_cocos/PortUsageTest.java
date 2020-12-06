package arcbasis._cocos;

import arcbasis.AbstractTest;
import arcbasis.ArcBasisMill;
import arcbasis._ast.ASTComponentHead;
import arcbasis._ast.ASTComponentType;
import arcbasis._symboltable.ArcBasisScope;
import arcbasis._symboltable.ArcBasisSymbolTableCreator;
import arcbasis.util.ArcError;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

import java.util.stream.Stream;

class PortUsageTest extends AbstractTest {

  @ParameterizedTest
  @MethodSource("componentAndErrorCodeProvider")
  void shouldDetectWronglyConnectedPorts(ASTComponentType ast, ArcError[] errors) {
    PortUsage coco = new PortUsage();
    coco.check(ast);
    this.checkOnlyExpectedErrorsPresent(Log.getFindings(), errors);
  }

  static Stream<Arguments> componentAndErrorCodeProvider() {
    ArcBasisSymbolTableCreator symTab = new ArcBasisSymbolTableCreator(new ArcBasisScope());
    ASTComponentType comp1 = ArcBasisMill.componentTypeBuilder().setName("Comp1")
      .setHead(Mockito.mock(ASTComponentHead.class))
      .setBody(ArcBasisMill.componentBodyBuilder()
        .addArcElement(ArcBasisMill.componentInterfaceBuilder()
          .addPortDeclaration(ArcBasisMill.portDeclarationBuilder()
            .setIncoming(true).setMCType(Mockito.mock(ASTMCType.class))
            .setPortList("i1", "i2", "i3")
            .build())
          .addPortDeclaration(ArcBasisMill.portDeclarationBuilder()
            .setOutgoing(true).setMCType(Mockito.mock(ASTMCType.class))
            .setPortList("o1", "o2", "o3")
            .build())
          .build())
        .addArcElement(ArcBasisMill.componentInstantiationBuilder()
          .setMCType(Mockito.mock(ASTMCObjectType.class)).setComponentInstanceList("sub1", "sub2").build())
        .addArcElement(ArcBasisMill.connectorBuilder().setSource("i1").setTargetList("o1", "o2").build())
        .addArcElement(ArcBasisMill.connectorBuilder().setSource("i2").setTargetList("o3", "i3").build())
        .build())
      .build();
    symTab.handle(comp1);
    ASTComponentType comp2 = ArcBasisMill.componentTypeBuilder().setName("Comp2")
      .setHead(Mockito.mock(ASTComponentHead.class))
      .setBody(ArcBasisMill.componentBodyBuilder()
        .addArcElement(ArcBasisMill.componentInterfaceBuilder()
          .addPortDeclaration(ArcBasisMill.portDeclarationBuilder()
            .setIncoming(true).setMCType(Mockito.mock(ASTMCType.class))
            .setPortList("i1")
            .build())
          .addPortDeclaration(ArcBasisMill.portDeclarationBuilder()
            .setOutgoing(true).setMCType(Mockito.mock(ASTMCType.class))
            .setPortList("o1", "o2", "o3")
            .build())
          .build())
        .addArcElement(ArcBasisMill.componentInstantiationBuilder()
          .setMCType(Mockito.mock(ASTMCObjectType.class)).setComponentInstanceList("sub1", "sub2").build())
        .addArcElement(ArcBasisMill.connectorBuilder().setSource("i1").setTargetList("o1").build())
        .addArcElement(ArcBasisMill.connectorBuilder().setSource("o2").setTargetList("o3").build())
        .build())
      .build();
    symTab.handle(comp2);
    return Stream.of(Arguments.of(comp1, new ArcError[] { ArcError.INCOMING_PORT_AS_TARGET }),
      Arguments.of(comp2, new ArcError[] { ArcError.OUTGOING_PORT_AST_SOURCE }));
  }
}