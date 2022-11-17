/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis.AbstractTest;
import arcbasis.ArcBasisMill;
import arcbasis._ast.ASTComponentHead;
import arcbasis._ast.ASTComponentType;
import arcbasis._symboltable.ArcBasisScopesGenitorDelegator;
import de.monticore.types.mcbasictypes._ast.ASTConstantsMCBasicTypes;
import montiarc.util.ArcError;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

import java.util.stream.Stream;

class PortsConnectedTest extends AbstractTest {

  @ParameterizedTest
  @MethodSource("componentAndErrorCodeProvider")
  void shouldDetectWronglyConnectedPorts(ASTComponentType ast, ArcError[] errors) {
    PortsConnected coco = new PortsConnected();
    coco.check(ast);
    this.checkOnlyExpectedErrorsPresent(errors);
  }

  static Stream<Arguments> componentAndErrorCodeProvider() {
    ArcBasisScopesGenitorDelegator symTab = ArcBasisMill.scopesGenitorDelegator();
    ASTComponentType comp1 = ArcBasisMill.componentTypeBuilder().setName("Comp1")
      .setHead(Mockito.mock(ASTComponentHead.class))
      .setBody(ArcBasisMill.componentBodyBuilder()
        .addArcElement(ArcBasisMill.componentInterfaceBuilder()
          .addPortDeclaration(ArcBasisMill.portDeclarationBuilder()
            .setIncoming(true)
            .setMCType(ArcBasisMill.mCPrimitiveTypeBuilder()
              .setPrimitive(ASTConstantsMCBasicTypes.BYTE)
              .build())
            .setPortList("i1", "i2", "i3", "i4")
            .build())
          .addPortDeclaration(ArcBasisMill.portDeclarationBuilder()
            .setOutgoing(true)
            .setMCType(ArcBasisMill.mCPrimitiveTypeBuilder()
              .setPrimitive(ASTConstantsMCBasicTypes.BYTE)
              .build())
            .setPortList("o1", "o2", "o3")
            .build())
          .build())
        .addArcElement(ArcBasisMill.componentInstantiationBuilder()
          .setMCType(ArcBasisMill.mCPrimitiveTypeBuilder()
            .setPrimitive(ASTConstantsMCBasicTypes.BYTE)
            .build())
          .setComponentInstanceList("sub1", "sub2").build())
        .addArcElement(ArcBasisMill.connectorBuilder().setSource("i1").setTargetList("o1", "o2").build())
        .addArcElement(ArcBasisMill.connectorBuilder().setSource("i2").setTargetList("o3", "i3").build())
        .build())
      .build();
    symTab.createFromAST(comp1);
    ASTComponentType comp2 = ArcBasisMill.componentTypeBuilder().setName("Comp2")
      .setHead(Mockito.mock(ASTComponentHead.class))
      .setBody(ArcBasisMill.componentBodyBuilder()
        .addArcElement(ArcBasisMill.componentInterfaceBuilder()
          .addPortDeclaration(ArcBasisMill.portDeclarationBuilder()
            .setIncoming(true)
            .setMCType(ArcBasisMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BYTE).build())
            .setPortList("i1")
            .build())
          .addPortDeclaration(ArcBasisMill.portDeclarationBuilder()
            .setOutgoing(true)
            .setMCType(ArcBasisMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BYTE).build())
            .setPortList("o1", "o2", "o3", "04")
            .build())
          .build())
        .addArcElement(ArcBasisMill.componentInstantiationBuilder()
          .setMCType(ArcBasisMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BYTE).build())
          .setComponentInstanceList("sub1", "sub2").build())
        .addArcElement(ArcBasisMill.connectorBuilder().setSource("i1").setTargetList("o1").build())
        .addArcElement(ArcBasisMill.connectorBuilder().setSource("o2").setTargetList("o3").build())
        .build())
      .build();
    symTab.createFromAST(comp2);
    return Stream.of(Arguments.of(comp1, new ArcError[] { ArcError.INCOMING_PORT_NO_FORWARD }),
      Arguments.of(comp2, new ArcError[] { ArcError.OUTGOING_PORT_NO_FORWARD }));
  }
}
