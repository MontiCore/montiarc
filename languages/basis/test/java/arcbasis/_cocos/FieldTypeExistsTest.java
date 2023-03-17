/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis.AbstractTest;
import arcbasis.ArcBasisMill;
import arcbasis._ast.ASTArcFieldDeclaration;
import arcbasis._ast.ASTComponentHead;
import arcbasis._ast.ASTComponentType;
import arcbasis._symboltable.ArcBasisScopesGenitorDelegator;
import arcbasis._symboltable.IArcBasisArtifactScope;
import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.symboltable.ImportStatement;
import de.monticore.types.mcbasictypes._ast.ASTConstantsMCBasicTypes;
import de.monticore.types.mcbasictypes._ast.ASTMCPrimitiveType;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.se_rwth.commons.logging.Log;
import montiarc.util.ArcError;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Collections;

/**
 * Holds tests for the handwritten methods of {@link FieldTypeExists}.
 */
public class FieldTypeExistsTest extends AbstractTest {

  @Test
  public void shouldFindPrimitiveType() {
    // Given
    ASTMCPrimitiveType type = ArcBasisMill.mCPrimitiveTypeBuilder()
      .setPrimitive(ASTConstantsMCBasicTypes.BOOLEAN)
      .build();
    ASTComponentType comp = createCompWithField("CompA", "a", type);
    ASTArcFieldDeclaration fieldDec = comp.getFieldDeclarations().get(0);
    ArcBasisScopesGenitorDelegator symTab = ArcBasisMill.scopesGenitorDelegator();
    symTab.createFromAST(comp);

    // When
    FieldTypeExists coco = new FieldTypeExists();
    coco.check(fieldDec);

    // Then
    Assertions.assertEquals(0, Log.getErrorCount(), Log.getFindings().toString());
  }

  @Test
  public void shouldFindUnqualifiedType() {
    // Given
    createTypeSymbolInGlobalScope("UQT");
    ASTMCQualifiedType type = createQualifiedType("UQT");
    ASTComponentType compWithField = createCompWithField("CompA", "b", type);
    ASTArcFieldDeclaration fieldDecl = compWithField.getFieldDeclarations().get(0);

    ArcBasisScopesGenitorDelegator symTab = ArcBasisMill.scopesGenitorDelegator();
    symTab.createFromAST(compWithField);

    // When
    FieldTypeExists coco = new FieldTypeExists();
    coco.check(fieldDecl);

    // Then
    Assertions.assertEquals(0, Log.getErrorCount(), Log.getFindings().toString());
  }

  @Test
  public void shouldNotFindUnqualifiedType() {
    // Given
    createTypeSymbolInScope("MyType", "package.name");
    ASTMCQualifiedType type = createQualifiedType("MyType");
    ASTComponentType comp = createCompWithField("NoUQT", "c", type);
    ASTArcFieldDeclaration fieldDec = comp.getFieldDeclarations().get(0);
    ArcBasisScopesGenitorDelegator symTab = ArcBasisMill.scopesGenitorDelegator();
    symTab.createFromAST(comp);

    // When
    FieldTypeExists coco = new FieldTypeExists();
    coco.check(fieldDec);

    // Then
    this.checkOnlyExpectedErrorsPresent(ArcError.MISSING_TYPE);
  }

  @Test
  public void shouldFindQualifiedType() {
    // Given
    createTypeSymbolInScope("MyType", "foopackage");
    ASTMCQualifiedType type = createQualifiedType("foopackage.MyType");
    ASTComponentType comp = createCompWithField("QT", "d", type);
    ASTArcFieldDeclaration fieldDec = comp.getFieldDeclarations().get(0);
    ArcBasisScopesGenitorDelegator symTab = ArcBasisMill.scopesGenitorDelegator();
    symTab.createFromAST(comp);

    // When
    FieldTypeExists coco = new FieldTypeExists();
    coco.check(fieldDec);

    // Then
    Assertions.assertEquals(0, Log.getErrorCount(), Log.getFindings().toString());
  }

  @Test
  public void shouldFindTypeByImport() {
    // Given
    createTypeSymbolInScope("MyType", "foopack");
    ASTMCQualifiedType type = createQualifiedType("MyType");
    ASTComponentType comp = createCompWithField("TbI", "e", type);
    ASTArcFieldDeclaration fieldDec = comp.getFieldDeclarations().get(0);
    IArcBasisArtifactScope scopeWithImports = ArcBasisMill.artifactScope();
    scopeWithImports.setImportsList(Collections.singletonList(new ImportStatement("foopack", true)));
    scopeWithImports.setName("ju");

    ArcBasisScopesGenitorDelegator symTab = ArcBasisMill.scopesGenitorDelegator();
    symTab.createFromAST(comp);
    fieldDec.getMCType().setEnclosingScope(scopeWithImports);
    ArcBasisMill.globalScope().addSubScope(scopeWithImports);

    // When
    FieldTypeExists coco = new FieldTypeExists();
    coco.check(fieldDec);

    // Then
    Assertions.assertEquals(0, Log.getErrorCount(), Log.getFindings().toString());
  }

  @Test
  public void shouldNotFindQualifiedType() {
    // Given
    ASTMCQualifiedType type = createQualifiedType("unknown.Type");
    ASTComponentType comp = createCompWithField("NoQT", "f", type);
    ASTArcFieldDeclaration fieldDec = (ASTArcFieldDeclaration) comp.getBody().getArcElementList().get(0);
    ArcBasisScopesGenitorDelegator symTab = ArcBasisMill.scopesGenitorDelegator();
    symTab.createFromAST(comp);

    // When
    FieldTypeExists coco = new FieldTypeExists();
    coco.check(fieldDec);

    // Then
    this.checkOnlyExpectedErrorsPresent(ArcError.MISSING_TYPE);
  }

  protected static ASTComponentType createCompWithField(@NotNull String compName, @NotNull String fieldName,
                                                        @NotNull ASTMCType fieldType) {
    Preconditions.checkNotNull(compName);
    Preconditions.checkNotNull(fieldName);
    Preconditions.checkNotNull(fieldType);

    ASTArcFieldDeclaration fieldDecl = ArcBasisMill.arcFieldDeclarationBuilder()
      .setMCType(fieldType)
      .addArcField(fieldName, Mockito.mock(ASTExpression.class))
      .build();

    return ArcBasisMill.componentTypeBuilder()
      .setName(compName)
      .setHead(Mockito.mock(ASTComponentHead.class))
      .setBody(ArcBasisMill.componentBodyBuilder()
        .addArcElement(fieldDecl)
        .build())
      .build();
  }
}
