/* (c) https://github.com/MontiCore/monticore */
package arc._cocos;

import arc._ast.ASTArcFieldDeclaration;
import arc._ast.ArcMill;
import arc._symboltable.ArcScope;
import arc._symboltable.ArcSymTabMill;
import arc._symboltable.ArcSymbolTableCreator;
import arc.util.ArcError;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.se_rwth.commons.logging.Log;
import montiarc.AbstractTest;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.regex.Pattern;

/**
 * Holds tests for the handwritten methods of {@link FieldTypeExists}.
 */
public class FieldTypeExistsTest extends AbstractTest {

  @Override
  protected Pattern supplyErrorCodePattern() {
    return ArcError.ERROR_CODE_PATTERN;
  }

  @Test
  public void shouldNotFindType() {
    ASTMCQualifiedType type = ArcMill.mCQualifiedTypeBuilder().setMCQualifiedName(
      ArcMill.mCQualifiedNameBuilder().setPartList(Collections.singletonList("Integer")).build())
      .build();
    ASTArcFieldDeclaration ast = ArcMill.arcFieldDeclarationBuilder()
      .setType(type).setFieldList("p1", "p2", "p3").build();
    ArcScope scope = ArcSymTabMill.arcScopeBuilder().build();
    ArcSymbolTableCreator symTab = new ArcSymbolTableCreator(scope);
    symTab.handle(ast);
    FieldTypeExists coco = new FieldTypeExists();
    coco.check(ast.getField(0));
    this.checkOnlyExpectedErrorsPresent(Log.getFindings(),
      new ArcError[] { ArcError.MISSING_TYPE_OF_FIELD });
  }
}