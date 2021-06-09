/* (c) https://github.com/MontiCore/monticore */
package genericarc._cocos;

import arcbasis.ArcBasisMill;
import arcbasis._ast.*;
import arcbasis._cocos.UniqueIdentifierNamesTest;
import arcbasis.util.ArcError;
import com.google.common.base.Preconditions;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.se_rwth.commons.logging.Log;
import genericarc.GenericArcMill;
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
    Preconditions.checkArgument(arcEl != null);
    Preconditions.checkState(coco != null);

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
    enclosingComp.getSymbol().addTypeParameter(typeParam);

    // When
    coco.check(enclosingComp);

    // Then
    this.checkOnlyExpectedErrorsPresent(ArcError.UNIQUE_IDENTIFIER_NAMES);
  }

  @Test
  public void shouldFindDuplicatedNameWithTypeAndConfigParam() {
    Preconditions.checkState(coco != null);

    // Given
    TypeVarSymbol typeParam = simpleTypeParamNamed("unique");
    ASTArcParameter configParam = simpleConfigParamNamed("unique");
    ASTComponentType enclosingComp = GenericArcMill.componentTypeBuilder()
      .setName("Outer")
      .setBody(Mockito.mock(ASTComponentBody.class))
      .setHead(GenericArcMill.componentHeadBuilder()
        .addArcParameter(configParam)
        .build()
      )
      .build();

    ArcBasisMill.scopesGenitorDelegator().createFromAST(enclosingComp);
    enclosingComp.getSymbol().addTypeParameter(typeParam);

    // When
    coco.check(enclosingComp);

    // Then
    this.checkOnlyExpectedErrorsPresent(ArcError.UNIQUE_IDENTIFIER_NAMES);
  }

  @Test
  public void shouldFindDuplicatedTypeParamName() {
    Preconditions.checkState(coco != null);

    // Given
    TypeVarSymbol typeParam = simpleTypeParamNamed("unique");
    TypeVarSymbol typeParam2 = simpleTypeParamNamed("unique");
    ASTComponentType enclosingComp = GenericArcMill.componentTypeBuilder()
      .setName("Outer")
      .setBody(Mockito.mock(ASTComponentBody.class))
      .setHead(Mockito.mock(ASTComponentHead.class))
      .build();

    ArcBasisMill.scopesGenitorDelegator().createFromAST(enclosingComp);
    enclosingComp.getSymbol().addTypeParameter(typeParam);
    enclosingComp.getSymbol().addTypeParameter(typeParam2);

    // When
    coco.check(enclosingComp);

    // Then
    this.checkOnlyExpectedErrorsPresent(ArcError.UNIQUE_IDENTIFIER_NAMES);
  }

  @Test
  public void shouldNotFindDuplicateNamesWithTypeParams() {
    Preconditions.checkState(coco != null);
    // Given
    ASTComponentType compType = simpleCompTypeNamed("type");
    ASTComponentInstantiation compInst = simpleCompInstNamed("inst");
    ASTComponentInterface port = simplePortNamed("port");
    ASTArcFieldDeclaration field = simpleFieldNamed("field");
    ASTArcParameter configParam = simpleConfigParamNamed("configParam");
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
    enclosingComp.getSymbol().addTypeParameter(typeParam);

    // When
    coco.check(enclosingComp);

    // Then
    Assertions.assertEquals(0, Log.getErrorCount());
  }

  /* ======================== helpers ================================= */

  protected static TypeVarSymbol simpleTypeParamNamed(@NotNull String name) {
    Preconditions.checkArgument(name != null);

    return GenericArcMill.typeVarSymbolBuilder()
      .setName(name)
      .build();
  }
}
