/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis.ArcBasisMill;
import arcbasis.ArcBasisTestBase;

import arcbasis._ast.ASTArcComponentType;
import de.monticore.expressions.expressionsbasis._ast.*;
import de.se_rwth.commons.logging.LogStub;
import de.monticore.literals.mccommonliterals._ast.ASTNatLiteral;
import de.monticore.types.mcbasictypes._ast.ASTConstantsMCBasicTypes;
import montiarc.util.ArcError;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CheckNoFieldDependencyCyclesTest extends ArcBasisTestBase {

  @BeforeEach
  void initLogging() {
    LogStub.init();
    LogStub.clearFindings();
    LogStub.enableFailQuick(false);
  }

  @Test
  void shouldReportCycle() {
    // a = b
    ASTExpression refB = ArcBasisMill.nameExpressionBuilder()
      .setName("b")
      .build();
    var declA = ArcBasisMill.arcFieldDeclarationBuilder()
      .setMCType(ArcBasisMill.mCPrimitiveTypeBuilder()
        .setPrimitive(ASTConstantsMCBasicTypes.INT)
        .build())
      .addArcField(
        ArcBasisMill.arcFieldBuilder()
          .setName("a")
          .setInitial(refB)
          .build()
      )
      .build();

    // b = a
    ASTExpression refA = ArcBasisMill.nameExpressionBuilder()
      .setName("a")
      .build();
    var declB = ArcBasisMill.arcFieldDeclarationBuilder()
      .setMCType(ArcBasisMill.mCPrimitiveTypeBuilder()
        .setPrimitive(ASTConstantsMCBasicTypes.INT)
        .build())
      .addArcField(
        ArcBasisMill.arcFieldBuilder()
          .setName("b")
          .setInitial(refA)
          .build()
      )
      .build();

    // component contening A and B
    ASTArcComponentType comp = ArcBasisMill.arcComponentTypeBuilder()
      .setName("CyclicAssignment")
      .setHead(ArcBasisMill.componentHeadBuilder().build())
      .setBody(
        ArcBasisMill.componentBodyBuilder()
          .addArcElement(declA)
          .addArcElement(declB)
          .build()
      )
      .build();

    ArcBasisMill.scopesGenitorDelegator().createFromAST(comp);
    ArcBasisMill.scopesGenitorP2Delegator().createFromAST(comp);
    ArcBasisMill.scopesGenitorP3Delegator().createFromAST(comp);

    new CheckNoFieldDependencyCycles() { }.check(comp);

    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(ArcError.CIRCULAR_FIELDS_DEPENDENCY));
  }

  @Test
  void shouldNotReportWhenNoCycle() {
    // littéral 0
    ASTNatLiteral nat0 = ArcBasisMill.natLiteralBuilder()
      .setDigits("0")
      .build();
    ASTLiteralExpression zero = ArcBasisMill.literalExpressionBuilder()
      .setLiteral(nat0)
      .build();

    // x = 0
    var declX = ArcBasisMill.arcFieldDeclarationBuilder()
      .setMCType(ArcBasisMill.mCPrimitiveTypeBuilder()
        .setPrimitive(ASTConstantsMCBasicTypes.INT)
        .build())
      .addArcField(
        ArcBasisMill.arcFieldBuilder()
          .setName("x")
          .setInitial(zero)
          .build()
      )
      .build();

    // y = x
    ASTNameExpression refX = ArcBasisMill.nameExpressionBuilder()
      .setName("x")
      .build();
    var declY = ArcBasisMill.arcFieldDeclarationBuilder()
      .setMCType(ArcBasisMill.mCPrimitiveTypeBuilder()
        .setPrimitive(ASTConstantsMCBasicTypes.INT)
        .build())
      .addArcField(
        ArcBasisMill.arcFieldBuilder()
          .setName("y")
          .setInitial(refX)
          .build()
      )
      .build();

    ASTArcComponentType comp = ArcBasisMill.arcComponentTypeBuilder()
      .setName("NoCycle")
      .setHead(ArcBasisMill.componentHeadBuilder().build())
      .setBody(
        ArcBasisMill.componentBodyBuilder()
          .addArcElement(declX)
          .addArcElement(declY)
          .build()
      )
      .build();

    ArcBasisMill.scopesGenitorDelegator().createFromAST(comp);
    ArcBasisMill.scopesGenitorP2Delegator().createFromAST(comp);
    ArcBasisMill.scopesGenitorP3Delegator().createFromAST(comp);

    new CheckNoFieldDependencyCycles() { }.check(comp);

    assertThat(LogStub.getFindings()).isEmpty();
  }
}
