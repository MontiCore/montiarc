/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis._ast.*;
import arcbasis._symboltable.ArcBasisScope;
import arcbasis._symboltable.ArcBasisSymTabMill;
import arcbasis._symboltable.ArcBasisSymbolTableCreator;
import arcbasis.util.ArcError;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.se_rwth.commons.logging.Log;
import montiarc.AbstractTest;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.regex.Pattern;

/**
 * Holds tests for the handwritten methods of {@link ParameterTypeExists}.
 */
public class ParameterTypeExistsTest extends AbstractTest {

  @Override
  protected Pattern supplyErrorCodePattern() {
    return ArcError.ERROR_CODE_PATTERN;
  }

  @Test
  public void shouldNotFindType() {
    ASTMCQualifiedType type = ArcBasisMill.mCQualifiedTypeBuilder().setMCQualifiedName(
      ArcBasisMill.mCQualifiedNameBuilder().setPartList(Collections.singletonList("Integer"))
        .build())
      .build();
    ASTComponentType ast = ArcBasisMill.componentTypeBuilder().setName("Comp")
      .setBody(Mockito.mock(ASTComponentBody.class))
      .setHead(ArcBasisMill.componentHeadBuilder().setArcParameterList(
        Collections.singletonList(ArcBasisMill.arcParameterBuilder().setName("p").setMCType(type)
          .build()))
        .build())
      .build();
    ArcBasisScope scope = ArcBasisSymTabMill.arcBasisScopeBuilder().build();
    ArcBasisSymbolTableCreator symTab = new ArcBasisSymbolTableCreator(scope);
    symTab.handle(ast);
    ParameterTypeExists coco = new ParameterTypeExists();
    coco.check(ast.getHead().getArcParameter(0));
    this.checkOnlyExpectedErrorsPresent(Log.getFindings(),
      new ArcError[] { ArcError.MISSING_TYPE_OF_PARAMETER });
  }
}