/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis.AbstractTest;
import arcbasis.ArcBasisMill;
import arcbasis._ast.ASTArcParameter;
import arcbasis._ast.ASTComponentBody;
import arcbasis._ast.ASTComponentType;
import arcbasis._symboltable.ArcBasisScopesGenitorDelegator;
import arcbasis._symboltable.IArcBasisArtifactScope;
import de.monticore.types.mcbasictypes._ast.ASTConstantsMCBasicTypes;
import montiarc.util.ArcError;
import com.google.common.base.Preconditions;
import de.monticore.symboltable.ImportStatement;
import de.monticore.types.mcbasictypes._ast.ASTMCPrimitiveType;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.se_rwth.commons.logging.Log;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.Optional;

/**
 * Holds tests for the handwritten methods of {@link ParameterTypeExists}.
 */
public class ParameterTypeExistsTest extends AbstractTest {

  @Test
  public void shouldFindPrimitiveType() {
    // Given
    ASTMCPrimitiveType type = ArcBasisMill.mCPrimitiveTypeBuilder()
      .setPrimitive(ASTConstantsMCBasicTypes.BOOLEAN)
      .build();
    ASTComponentType comp = createCompWithParam("CompA", "p1", type);
    ASTArcParameter paramDec = getFirstParamDeclaration(comp).get();
    ArcBasisScopesGenitorDelegator symTab = ArcBasisMill.scopesGenitorDelegator();
    symTab.createFromAST(comp);

    // When
    ParameterTypeExists coco = new ParameterTypeExists();
    coco.check(paramDec);

    // Then
    Assertions.assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void shouldFindUnqualifiedType() {
    // Given
    createTypeSymbolInGlobalScope("MyType");
    ASTMCQualifiedType type = createQualifiedType("MyType");
    ASTComponentType comp = createCompWithParam("CompA", "p1", type);
    ASTArcParameter param = getFirstParamDeclaration(comp).get();

    ArcBasisScopesGenitorDelegator symTab = ArcBasisMill.scopesGenitorDelegator();
    symTab.createFromAST(comp);

    // When
    ParameterTypeExists coco = new ParameterTypeExists();
    coco.check(param);

    // Then
    Assertions.assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void shouldNotFindUnqualifiedType() {
    // Given
    createTypeSymbolInScope("MyType", "package.name");
    ASTMCQualifiedType type = createQualifiedType("MyType");
    ASTComponentType comp = createCompWithParam("CompA", "p1", type);
    ASTArcParameter paramDec = getFirstParamDeclaration(comp).get();
    ArcBasisScopesGenitorDelegator symTab = ArcBasisMill.scopesGenitorDelegator();
    symTab.createFromAST(comp);

    // When
    ParameterTypeExists coco = new ParameterTypeExists();
    coco.check(paramDec);

    // Then
    this.checkOnlyExpectedErrorsPresent(ArcError.MISSING_TYPE);
  }

  @Test
  public void shouldFindQualifiedType() {
    // Given
    createTypeSymbolInScope("MyType", "foopackage");
    ASTMCQualifiedType type = createQualifiedType("foopackage.MyType");
    ASTComponentType comp = createCompWithParam("CompA", "p1", type);
    ASTArcParameter paramDec = getFirstParamDeclaration(comp).get();
    ArcBasisScopesGenitorDelegator symTab = ArcBasisMill.scopesGenitorDelegator();
    symTab.createFromAST(comp);

    // When
    ParameterTypeExists coco = new ParameterTypeExists();
    coco.check(paramDec);

    // Then
    Assertions.assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void shouldFindTypeByImport() {
    // Given
    createTypeSymbolInScope("MyType", "foopack");
    ASTMCQualifiedType type = createQualifiedType("MyType");
    ASTComponentType comp = createCompWithParam("CompA", "p1", type);
    ASTArcParameter paramDec = getFirstParamDeclaration(comp).get();
    IArcBasisArtifactScope scopeWithImports = ArcBasisMill.artifactScope();
    scopeWithImports.setImportsList(Collections.singletonList(new ImportStatement("foopack", true)));
    scopeWithImports.setName("ju");

    ArcBasisScopesGenitorDelegator symTab = ArcBasisMill.scopesGenitorDelegator();
    symTab.createFromAST(comp);
    paramDec.getMCType().setEnclosingScope(scopeWithImports);
    ArcBasisMill.globalScope().addSubScope(scopeWithImports);

    // When
    ParameterTypeExists coco = new ParameterTypeExists();
    coco.check(paramDec);

    // Then
    Assertions.assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void shouldNotFindQualifiedType() {
    // Given
    ASTMCQualifiedType type = createQualifiedType("unknown.Type");
    ASTComponentType comp = createCompWithParam("CompA", "p1", type);
    ASTArcParameter paramDec = getFirstParamDeclaration(comp).get();
    ArcBasisScopesGenitorDelegator symTab = ArcBasisMill.scopesGenitorDelegator();
    symTab.createFromAST(comp);

    // When
    ParameterTypeExists coco = new ParameterTypeExists();
    coco.check(paramDec);

    // Then
    this.checkOnlyExpectedErrorsPresent(ArcError.MISSING_TYPE);
  }

  protected static ASTComponentType createCompWithParam(@NotNull String compName, @NotNull String paramName,
                                                        @NotNull ASTMCType paramType) {
    Preconditions.checkNotNull(compName);
    Preconditions.checkNotNull(paramName);
    Preconditions.checkNotNull(paramType);

    ASTArcParameter param = ArcBasisMill.arcParameterBuilder()
      .setMCType(paramType)
      .setName(paramName)
      .build();

    ASTComponentType comp = ArcBasisMill.componentTypeBuilder()
      .setName(compName)
      .setHead(ArcBasisMill.componentHeadBuilder().addArcParameter(param).build())
      .setBody(Mockito.mock(ASTComponentBody.class))
      .build();

    return comp;
  }

  protected static Optional<ASTArcParameter> getFirstParamDeclaration(@NotNull ASTComponentType comp) {
    Preconditions.checkNotNull(comp);

    return comp.getHead().isEmptyArcParameters() ?
      Optional.empty()
      : Optional.of(comp.getHead().getArcParameter(0));
  }
}