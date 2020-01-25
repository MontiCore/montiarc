/* (c) https://github.com/MontiCore/monticore */
package arc._cocos;

import arc._ast.*;
import arc._symboltable.ArcScope;
import arc._symboltable.ArcSymTabMill;
import arc._symboltable.ArcSymbolTableCreator;
import arc._symboltable.ComponentSymbol;
import arc.util.ArcError;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.se_rwth.commons.logging.Log;
import montiarc.AbstractTest;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
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
    ASTMCQualifiedType type = ArcMill.mCQualifiedTypeBuilder().setMCQualifiedName(
      ArcMill.mCQualifiedNameBuilder().setPartList(Collections.singletonList("Integer")).build())
      .build();
    ASTComponent ast = ArcMill.componentBuilder().setName("Comp")
      .setBody(Mockito.mock(ASTComponentBody.class))
      .setHead(ArcMill.componentHeadBuilder().setParameterList(
        Collections.singletonList(ArcMill.arcParameterDeclarationBuilder()
          .setType(type).setArcParameter(ArcMill.arcParameterBuilder().setName("p").build())
          .build()))
        .build())
      .build();
    ArcScope scope = ArcSymTabMill.arcScopeBuilder().build();
    ArcSymbolTableCreator symTab = new ArcSymbolTableCreator(scope);
    symTab.handle(ast);
    ParameterTypeExists coco = new ParameterTypeExists();
    coco.check(ast.getHead().getParameter(0).getArcParameter());
    this.checkOnlyExpectedErrorsPresent(Log.getFindings(),
      new ArcError[] { ArcError.MISSING_TYPE_OF_PARAMETER });
  }
}