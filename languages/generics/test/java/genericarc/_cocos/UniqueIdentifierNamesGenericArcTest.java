/* (c) https://github.com/MontiCore/monticore */
package genericarc._cocos;

import arcbasis.ArcBasisMill;
import arcbasis._ast.ASTArcElement;
import arcbasis._ast.ASTArcFieldDeclaration;
import arcbasis._ast.ASTArcParameter;
import arcbasis._ast.ASTComponentBody;
import arcbasis._ast.ASTComponentHead;
import arcbasis._ast.ASTComponentInstantiation;
import arcbasis._ast.ASTComponentInterface;
import arcbasis._ast.ASTComponentType;
import arcbasis._cocos.UniqueIdentifierNamesTest;
import com.google.common.base.Preconditions;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.se_rwth.commons.logging.Log;
import genericarc.GenericArcMill;
import montiarc.util.ArcError;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

public class UniqueIdentifierNamesGenericArcTest extends UniqueIdentifierNamesTest {

  @ParameterizedTest
  @MethodSource("provideIdentifiers")
  public void shouldFindDuplicatedNameWithTypeParam (@NotNull ASTArcElement arcEl) {
    Preconditions.checkNotNull(arcEl);
    Preconditions.checkState(UniqueIdentifierNamesTest.coco != null);

    // Given
    TypeVarSymbol typeParam = simpleTypeParamNamed("unique");
    ASTComponentType enclosingComp = GenericArcMill.componentTypeBuilder()
      .setName("Outer")
      .setBody(GenericArcMill.componentBodyBuilder()
        .addArcElement(arcEl)
        .build()
      )
      .setHead(Mockito.mock(ASTComponentHead.class))
      .build();

    ArcBasisMill.scopesGenitorDelegator().createFromAST(enclosingComp);
    enclosingComp.getSpannedScope().add(typeParam);

    // When
    UniqueIdentifierNamesTest.coco.check(enclosingComp);

    // Then
    this.checkOnlyExpectedErrorsPresent(ArcError.UNIQUE_IDENTIFIER_NAMES);
  }

  @Test
  public void shouldFindDuplicatedNameWithTypeAndConfigParam() {
    Preconditions.checkState(UniqueIdentifierNamesTest.coco != null);

    // Given
    TypeVarSymbol typeParam = simpleTypeParamNamed("unique");
    ASTArcParameter configParam = UniqueIdentifierNamesTest.simpleConfigParamNamed("unique");
    ASTComponentType enclosingComp = GenericArcMill.componentTypeBuilder()
      .setName("Outer")
      .setBody(Mockito.mock(ASTComponentBody.class))
      .setHead(GenericArcMill.componentHeadBuilder()
        .addArcParameter(configParam)
        .build()
      )
      .build();

    ArcBasisMill.scopesGenitorDelegator().createFromAST(enclosingComp);
    enclosingComp.getSpannedScope().add(typeParam);

    // When
    UniqueIdentifierNamesTest.coco.check(enclosingComp);

    // Then
    this.checkOnlyExpectedErrorsPresent(ArcError.UNIQUE_IDENTIFIER_NAMES);
  }

  @Test
  public void shouldFindDuplicatedTypeParamName() {
    Preconditions.checkState(UniqueIdentifierNamesTest.coco != null);

    // Given
    TypeVarSymbol typeParam = simpleTypeParamNamed("unique");
    TypeVarSymbol typeParam2 = simpleTypeParamNamed("unique");
    ASTComponentType enclosingComp = GenericArcMill.componentTypeBuilder()
      .setName("Outer")
      .setBody(Mockito.mock(ASTComponentBody.class))
      .setHead(Mockito.mock(ASTComponentHead.class))
      .build();

    ArcBasisMill.scopesGenitorDelegator().createFromAST(enclosingComp);
    enclosingComp.getSpannedScope().add(typeParam);
    enclosingComp.getSpannedScope().add(typeParam2);

    // When
    UniqueIdentifierNamesTest.coco.check(enclosingComp);

    // Then
    this.checkOnlyExpectedErrorsPresent(ArcError.UNIQUE_IDENTIFIER_NAMES);
  }

  @Test
  public void shouldNotFindDuplicateNamesWithTypeParams() {
    Preconditions.checkState(UniqueIdentifierNamesTest.coco != null);
    // Given
    ASTComponentType compType = UniqueIdentifierNamesTest.simpleCompTypeNamed("type");
    ASTComponentInstantiation compInst = UniqueIdentifierNamesTest.simpleCompInstNamed("inst");
    ASTComponentInterface port = UniqueIdentifierNamesTest.simplePortNamed("port");
    ASTArcFieldDeclaration field = UniqueIdentifierNamesTest.simpleFieldNamed("field");
    ASTArcParameter configParam = UniqueIdentifierNamesTest.simpleConfigParamNamed("configParam");
    TypeVarSymbol typeParam = simpleTypeParamNamed("typeParam");

    ASTComponentType enclosingComp = GenericArcMill.componentTypeBuilder()
      .setName("Outer")
      .setBody(GenericArcMill.componentBodyBuilder()
        .addArcElement(compType)
        .addArcElement(compInst)
        .addArcElement(port)
        .addArcElement(field)
        .build()
      )
      .setHead(GenericArcMill.componentHeadBuilder()
        .addArcParameter(configParam)
        .build()
      )
      .build();

    ArcBasisMill.scopesGenitorDelegator().createFromAST(enclosingComp);
    enclosingComp.getSpannedScope().add(typeParam);

    // When
    UniqueIdentifierNamesTest.coco.check(enclosingComp);

    // Then
    Assertions.assertEquals(0, Log.getErrorCount());
  }

  /* ======================== helpers ================================= */

  protected static TypeVarSymbol simpleTypeParamNamed(@NotNull String name) {
    Preconditions.checkNotNull(name);

    return GenericArcMill.typeVarSymbolBuilder()
      .setName(name)
      .build();
  }
}
