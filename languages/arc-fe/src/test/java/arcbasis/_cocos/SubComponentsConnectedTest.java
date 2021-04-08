/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis.AbstractTest;
import arcbasis.ArcBasisMill;
import arcbasis._ast.ASTComponentHead;
import arcbasis._ast.ASTComponentType;
import arcbasis._symboltable.ArcBasisScopesGenitorDelegator;
import arcbasis.util.ArcError;
import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.stream.Stream;

/**
 * Holds tests for the handwritten methods of {@link SubComponentsConnected}
 */
public class SubComponentsConnectedTest extends AbstractTest {

  HashMap<String, ASTComponentType> components;

  @ParameterizedTest
  @MethodSource("componentAndErrorCodeProvider")
  void shouldDetectWronglyConnectedPorts(@NotNull String comp, @NotNull ArcError[] errors) {
    Preconditions.checkNotNull(comp);
    Preconditions.checkNotNull(errors);

    //Given
    ASTComponentType ast = components.get(comp);
    SubComponentsConnected coco = new SubComponentsConnected();

    //When
    coco.check(ast);

    //Then
    this.checkOnlyExpectedErrorsPresent(Log.getFindings(), errors);
  }

  @BeforeEach
  public void setUpTest() {
    ArcBasisScopesGenitorDelegator symTab = new ArcBasisScopesGenitorDelegator();
    ASTComponentType comp1 = ArcBasisMill.componentTypeBuilder().setName("A")
      .setHead(Mockito.mock(ASTComponentHead.class))
      .setBody(ArcBasisMill.componentBodyBuilder()
        .addArcElement(ArcBasisMill.componentInterfaceBuilder()
          .addPortDeclaration(ArcBasisMill.portDeclarationBuilder()
            .setIncoming(true).setMCType(ArcBasisMill.mCPrimitiveTypeBuilder().setPrimitive(2).build())
            .setPortList("i1", "i2")
            .build())
          .addPortDeclaration(ArcBasisMill.portDeclarationBuilder()
            .setOutgoing(true).setMCType(ArcBasisMill.mCPrimitiveTypeBuilder().setPrimitive(2).build())
            .setPortList("o1")
            .build())
          .build())
        .build())
      .build();
    symTab.createFromAST(comp1);
    ASTComponentType comp2 = ArcBasisMill.componentTypeBuilder().setName("B")
      .setHead(Mockito.mock(ASTComponentHead.class))
      .setBody(ArcBasisMill.componentBodyBuilder()
        .addArcElement(ArcBasisMill.componentInterfaceBuilder()
          .addPortDeclaration(ArcBasisMill.portDeclarationBuilder()
            .setIncoming(true).setMCType(ArcBasisMill.mCPrimitiveTypeBuilder().setPrimitive(2).build())
            .setPortList("i1")
            .build())
          .addPortDeclaration(ArcBasisMill.portDeclarationBuilder()
            .setOutgoing(true).setMCType(ArcBasisMill.mCPrimitiveTypeBuilder().setPrimitive(2).build())
            .setPortList("o1")
            .build())
          .build())
        .addArcElement(ArcBasisMill.componentInstantiationBuilder()
          .setMCType(createQualifiedType("A"))
          .setComponentInstanceList("sub1")
          .build())
        .addArcElement(ArcBasisMill.connectorBuilder().setSource("i1").setTargetList("sub1.i1",
          "sub1.i2", "sub1.o1").build())
        .build())
      .build();
    symTab.createFromAST(comp2);
    ASTComponentType comp3 = ArcBasisMill.componentTypeBuilder().setName("C")
      .setHead(Mockito.mock(ASTComponentHead.class))
      .setBody(ArcBasisMill.componentBodyBuilder()
        .addArcElement(ArcBasisMill.componentInterfaceBuilder()
          .addPortDeclaration(ArcBasisMill.portDeclarationBuilder()
            .setIncoming(true).setMCType(ArcBasisMill.mCPrimitiveTypeBuilder().setPrimitive(2).build())
            .setPortList("i1")
            .build())
          .addPortDeclaration(ArcBasisMill.portDeclarationBuilder()
            .setOutgoing(true).setMCType(ArcBasisMill.mCPrimitiveTypeBuilder().setPrimitive(2).build())
            .setPortList("o1", "o2")
            .build())
          .build())
        .addArcElement(ArcBasisMill.componentInstantiationBuilder()
          .setMCType(createQualifiedType("A"))
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
    symTab.createFromAST(comp3);
    components = new HashMap<>();
    components.put(comp1.getName(), comp1);
    components.put(comp2.getName(), comp2);
    components.put(comp3.getName(), comp3);
  }

  static Stream<Arguments> componentAndErrorCodeProvider() {
    ArcError[] errors1 = new ArcError[] {};
    ArcError[] errors2 = new ArcError[] { ArcError.OUTGOING_PORT_AS_TARGET };
    ArcError[] errors3 = new ArcError[] { ArcError.INCOMING_PORT_AS_SOURCE };
    return Stream.of(Arguments.of("A", errors1), Arguments.of("B", errors2), Arguments.of("C", errors3));
  }
}