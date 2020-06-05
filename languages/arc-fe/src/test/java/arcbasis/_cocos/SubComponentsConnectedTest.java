/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis._ast.ASTComponentType;
import arcbasis._ast.ASTComponentHead;
import arcbasis._ast.ArcBasisMill;
import arcbasis._symboltable.ArcBasisScope;
import arcbasis._symboltable.ArcBasisSymbolTableCreator;
import arcbasis.util.ArcError;
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
  void shouldDetectWronglyConnectedPorts(ASTComponentType ast, ArcError[] errors) {
    SubComponentsConnected coco = new SubComponentsConnected();
    coco.check(ast);
    this.checkOnlyExpectedErrorsPresent(Log.getFindings(), errors);
  }

  static Stream<Arguments> componentAndErrorCodeProvider() {
    ArcBasisSymbolTableCreator symTab = new ArcBasisSymbolTableCreator(new ArcBasisScope());
    ASTComponentType comp1 = ArcBasisMill.componentTypeBuilder().setName("A")
      .setHead(Mockito.mock(ASTComponentHead.class))
      .setBody(ArcBasisMill.componentBodyBuilder()
        .addArcElement(ArcBasisMill.componentInterfaceBuilder()
          .addPortDeclaration(ArcBasisMill.portDeclarationBuilder()
            .setIncoming(true).setMCType(Mockito.mock(ASTMCType.class))
            .setPortList("i1", "i2")
            .build())
          .addPortDeclaration(ArcBasisMill.portDeclarationBuilder()
            .setOutgoing(true).setMCType(Mockito.mock(ASTMCType.class))
            .setPortList("o1")
            .build())
          .build())
        .build())
      .build();
    symTab.handle(comp1);
    ArcError[] errors1 = new ArcError[] {};
    ASTComponentType comp2 = ArcBasisMill.componentTypeBuilder().setName("B")
      .setHead(Mockito.mock(ASTComponentHead.class))
      .setBody(ArcBasisMill.componentBodyBuilder()
        .addArcElement(ArcBasisMill.componentInterfaceBuilder()
          .addPortDeclaration(ArcBasisMill.portDeclarationBuilder()
            .setIncoming(true).setMCType(Mockito.mock(ASTMCType.class))
            .setPortList("i1")
            .build())
          .addPortDeclaration(ArcBasisMill.portDeclarationBuilder()
            .setOutgoing(true).setMCType(Mockito.mock(ASTMCType.class))
            .setPortList("o1")
            .build())
          .build())
        .addArcElement(ArcBasisMill.componentInstantiationBuilder()
          .setMCType(ArcBasisMill.mCQualifiedTypeBuilder().setMCQualifiedName(
            ArcBasisMill.mCQualifiedNameBuilder().setPartList(Collections.singletonList("A"))
              .build())
            .build())
          .setComponentInstanceList("sub1")
          .build())
        .addArcElement(ArcBasisMill.connectorBuilder().setSource("i1").setTargetList("sub1.i1",
          "sub1.i2", "sub1.o1").build())
        .build())
      .build();
    symTab.handle(comp2);
    ArcError[] errors2 = new ArcError[] { ArcError.OUTGOING_PORT_AS_TARGET };
    ASTComponentType comp3 = ArcBasisMill.componentTypeBuilder().setName("C")
      .setHead(Mockito.mock(ASTComponentHead.class))
      .setBody(ArcBasisMill.componentBodyBuilder()
        .addArcElement(ArcBasisMill.componentInterfaceBuilder()
          .addPortDeclaration(ArcBasisMill.portDeclarationBuilder()
            .setIncoming(true).setMCType(Mockito.mock(ASTMCType.class))
            .setPortList("i1")
            .build())
          .addPortDeclaration(ArcBasisMill.portDeclarationBuilder()
            .setOutgoing(true).setMCType(Mockito.mock(ASTMCType.class))
            .setPortList("o1", "o2")
            .build())
          .build())
        .addArcElement(ArcBasisMill.componentInstantiationBuilder()
          .setMCType(ArcBasisMill.mCQualifiedTypeBuilder().setMCQualifiedName(
            ArcBasisMill.mCQualifiedNameBuilder().setPartList(Collections.singletonList("A"))
              .build())
            .build())
          .setComponentInstanceList("sub1")
          .build())
        .addArcElement(
          ArcBasisMill.connectorBuilder().setSource("i1").setTargetList("sub1.i1").build())
        .addArcElement(
          ArcBasisMill.connectorBuilder().setSource("sub1.o1").setTargetList("o1").build())
        .addArcElement(
          ArcBasisMill.connectorBuilder().setSource("sub1.i2").setTargetList("o2").build())
        .build())
      .build();
    symTab.handle(comp3);
    ArcError[] errors3 = new ArcError[] { ArcError.INCOMING_PORT_AS_SOURCE };
    return Stream.of(Arguments.of(comp1, errors1), Arguments.of(comp2, errors2),
      Arguments.of(comp3, errors3));
  }
}