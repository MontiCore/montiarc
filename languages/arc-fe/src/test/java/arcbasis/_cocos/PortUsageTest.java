package arcbasis._cocos;

import arcbasis.AbstractTest;
import arcbasis.ArcBasisMill;
import arcbasis._ast.ASTComponentHead;
import arcbasis._ast.ASTComponentType;
import arcbasis._symboltable.*;
import arcbasis._visitor.ArcBasisTraverser;
import arcbasis._visitor.ArcBasisVisitor;
import arcbasis.util.ArcError;
import com.google.common.collect.LinkedListMultimap;
import de.monticore.ast.ASTNode;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisTraverser;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisVisitor;
import de.monticore.io.paths.ModelPath;
import de.monticore.literals.mcliteralsbasis._visitor.MCLiteralsBasisTraverser;
import de.monticore.literals.mcliteralsbasis._visitor.MCLiteralsBasisVisitor;
import de.monticore.mcbasics._symboltable.IMCBasicsScope;
import de.monticore.mcbasics._visitor.MCBasicsTraverser;
import de.monticore.mcbasics._visitor.MCBasicsVisitor;
import de.monticore.symbols.basicsymbols._symboltable.*;
import de.monticore.symbols.basicsymbols._visitor.BasicSymbolsTraverser;
import de.monticore.symbols.basicsymbols._visitor.BasicSymbolsVisitor;
import de.monticore.symbols.oosymbols._symboltable.*;
import de.monticore.symbols.oosymbols._visitor.OOSymbolsTraverser;
import de.monticore.symbols.oosymbols._visitor.OOSymbolsVisitor;
import de.monticore.symboltable.IScopeSpanningSymbol;
import de.monticore.symboltable.serialization.IDeSer;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mcbasictypes._visitor.MCBasicTypesTraverser;
import de.monticore.types.mcbasictypes._visitor.MCBasicTypesVisitor;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

import java.util.List;
import java.util.Map;
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
    ArcBasisScopesGenitorDelegator symTab = new ArcBasisScopesGenitorDelegator(ArcBasisMill.globalScope());
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
    symTab.createFromAST(comp1);
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
    symTab.createFromAST(comp2);
    return Stream.of(Arguments.of(comp1, new ArcError[] { ArcError.INCOMING_PORT_AS_TARGET }),
      Arguments.of(comp2, new ArcError[] { ArcError.OUTGOING_PORT_AST_SOURCE }));
  }
}