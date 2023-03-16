/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis.AbstractTest;
import arcbasis.ArcBasisMill;
import arcbasis._ast.ASTArcElement;
import arcbasis._ast.ASTArcField;
import arcbasis._ast.ASTArcFieldDeclaration;
import arcbasis._ast.ASTArcParameter;
import arcbasis._ast.ASTComponentBody;
import arcbasis._ast.ASTComponentHead;
import arcbasis._ast.ASTComponentInstantiation;
import arcbasis._ast.ASTComponentInterface;
import arcbasis._ast.ASTComponentType;
import arcbasis._ast.ASTPortDeclaration;
import arcbasis._ast.ASTPortDirection;
import arcbasis._symboltable.ArcBasisScopesGenitorDelegator;
import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.se_rwth.commons.logging.Log;
import montiarc.util.ArcError;
import org.apache.commons.lang3.tuple.Pair;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class UniqueIdentifierNamesTest extends AbstractTest {

  protected static UniqueIdentifierNames coco;

  @BeforeEach
  public void setUp() {
    setCoco();
  }

  /** Might be overridden, if a subtype of {@link UniqueIdentifierNames} should be tested. */
  protected void setCoco() {
    coco = new UniqueIdentifierNames();
  }

  protected static Stream<Arguments> provideIdentifiers() {
    ASTComponentType innerComp = simpleCompTypeNamed("unique");
    ASTComponentInstantiation subComp = simpleCompInstNamed("unique");
    ASTComponentInterface port = simplePortNamed("unique");
    ASTArcFieldDeclaration field = simpleFieldNamed("unique");

    return Stream.of(
      Arguments.of(innerComp),
      Arguments.of(subComp),
      Arguments.of(port),
      Arguments.of(field)
      );
  }

  protected static Stream<Arguments> provideDuplicatedIdentifiers() {
    ASTComponentType innerComp = simpleCompTypeNamed("unique");
    ASTComponentType innerComp2 = simpleCompTypeNamed("unique");

    ASTComponentInstantiation subComp = simpleCompInstNamed("unique");
    ASTComponentInstantiation subComp2 = simpleCompInstNamed("unique");

    ASTComponentInterface port = simplePortNamed("unique");
    ASTComponentInterface port2 = simplePortNamed("unique");

    ASTArcFieldDeclaration field = simpleFieldNamed("unique");
    ASTArcFieldDeclaration field2 = simpleFieldNamed("unique");

    List<ASTArcElement> arcEls =
      Arrays.stream(new ASTArcElement[] {
        innerComp, innerComp2,
        subComp, subComp2,
        port, port2,
        field, field2,
      })
      .collect(Collectors.toList());

    return permutePairs(arcEls).stream()
      .map(pair -> Arguments.of(
        Named.of(pair.getLeft().getClass().getSimpleName(), pair.getLeft()),
        Named.of(pair.getRight().getClass().getSimpleName(), pair.getRight()),
        ArcError.UNIQUE_IDENTIFIER_NAMES)
      );
  }

  @ParameterizedTest(name = "{index}: {0} x {1} ")
  @MethodSource("provideDuplicatedIdentifiers")
  public void shouldFindDuplicatedName (
    @NotNull ASTArcElement firstEl,
    @NotNull ASTArcElement secondEl,
    @NotNull ArcError expectedError
  ) {
    Preconditions.checkNotNull(firstEl);
    Preconditions.checkNotNull(secondEl);
    Preconditions.checkNotNull( expectedError);
    Preconditions.checkState(coco != null);

    // Given
    ASTComponentType enclosingComp = ArcBasisMill.componentTypeBuilder()
      .setName("Outer")
      .setBody(ArcBasisMill.componentBodyBuilder()
        .addArcElement(firstEl)
        .addArcElement(secondEl)
        .build()
      )
      .setHead(Mockito.mock(ASTComponentHead.class))
      .build();

    ArcBasisScopesGenitorDelegator symTab = new ArcBasisScopesGenitorDelegator();
    symTab.createFromAST(enclosingComp);

    // When
    coco.check(enclosingComp);

    // Then
    this.checkOnlyExpectedErrorsPresent(expectedError);
  }

  @ParameterizedTest
  @MethodSource("provideIdentifiers")
  public void shouldFindDuplicatedNameWithConfigParam (@NotNull ASTArcElement arcEl) {
    Preconditions.checkNotNull(arcEl);
    Preconditions.checkState(coco != null);

    // Given
    ASTArcParameter configParam = simpleConfigParamNamed("unique");
    ASTComponentType enclosingComp = ArcBasisMill.componentTypeBuilder()
      .setName("Outer")
      .setBody(ArcBasisMill.componentBodyBuilder()
        .addArcElement(arcEl)
        .build()
      )
      .setHead(ArcBasisMill.componentHeadBuilder()
        .addArcParameter(configParam)
        .build()
      )
      .build();

    ArcBasisScopesGenitorDelegator symTab = new ArcBasisScopesGenitorDelegator();
    symTab.createFromAST(enclosingComp);

    // When
    coco.check(enclosingComp);

    // Then
    this.checkOnlyExpectedErrorsPresent(ArcError.UNIQUE_IDENTIFIER_NAMES);
  }

  @Test
  public void shouldFindDuplicateConfigParamNames() {
    Preconditions.checkState(coco != null);

    // Given
    ASTArcParameter configParam = simpleConfigParamNamed("unique");
    ASTArcParameter configParam2 = simpleConfigParamNamed("unique");
    ASTComponentType enclosingComp = ArcBasisMill.componentTypeBuilder()
      .setName("Outer")
      .setBody(Mockito.mock(ASTComponentBody.class))
      .setHead(ArcBasisMill.componentHeadBuilder()
        .addArcParameter(configParam)
        .addArcParameter(configParam2)
        .build()
      )
      .build();

    ArcBasisScopesGenitorDelegator symTab = new ArcBasisScopesGenitorDelegator();
    symTab.createFromAST(enclosingComp);

    // When
    coco.check(enclosingComp);

    // Then
    this.checkOnlyExpectedErrorsPresent(ArcError.UNIQUE_IDENTIFIER_NAMES);
  }

  @Test
  public void shouldNotFindDuplicateNames() {
    Preconditions.checkState(coco != null);

    // Given
    ASTComponentType compType = simpleCompTypeNamed("type");
    ASTComponentInstantiation compInst = simpleCompInstNamed("inst");
    ASTComponentInterface port = simplePortNamed("port");
    ASTArcFieldDeclaration field = simpleFieldNamed("field");
    ASTArcParameter configParam = simpleConfigParamNamed("configParam");

    ASTComponentType enclosingComp = ArcBasisMill.componentTypeBuilder()
      .setName("Outer")
      .setBody(ArcBasisMill.componentBodyBuilder()
        .addArcElement(compType)
        .addArcElement(compInst)
        .addArcElement(port)
        .addArcElement(field)
        .build()
      )
      .setHead(ArcBasisMill.componentHeadBuilder()
        .addArcParameter(configParam)
        .build()
      )
      .build();

    ArcBasisScopesGenitorDelegator symTab = new ArcBasisScopesGenitorDelegator();
    symTab.createFromAST(enclosingComp);

    // When
    coco.check(enclosingComp);

    // Then
    Assertions.assertEquals(0, Log.getErrorCount());
  }

  /** @return An arbitrary {@link ASTComponentType} with the given name. */
  protected static ASTComponentType simpleCompTypeNamed(@NotNull String name) {
    Preconditions.checkNotNull(name);

    return ArcBasisMill.componentTypeBuilder()
      .setName(name)
      .setHead(Mockito.mock(ASTComponentHead.class))
      .setBody(Mockito.mock(ASTComponentBody.class))
      .build();
  }

  /** @return An arbitrary {@link ASTComponentInstantiation} with the given name. */
  protected static ASTComponentInstantiation simpleCompInstNamed(@NotNull String name) {
    Preconditions.checkNotNull(name);

    return ArcBasisMill.componentInstantiationBuilder()
      .setMCType(createQualifiedType("int"))
      .addInstance(name)
      .build();
  }

  /** @return An arbitrary {@link ASTComponentInterface} with the given name. */
  protected static ASTComponentInterface simplePortNamed(@NotNull String name) {
    Preconditions.checkNotNull(name);

    ASTPortDeclaration port =  ArcBasisMill.portDeclarationBuilder()
      .setIncoming(true)
      .setPortDirection(Mockito.mock(ASTPortDirection.class))
      .addPort(name)
      .setMCType(createQualifiedType("int"))
      .build();

    return ArcBasisMill.componentInterfaceBuilder()
      .addPortDeclaration(port)
      .build();
  }

  /** @return An arbitrary {@link ASTArcFieldDeclaration} with the given name. */
  protected static ASTArcFieldDeclaration simpleFieldNamed(@NotNull String name) {
    Preconditions.checkNotNull(name);

    ASTArcField field = ArcBasisMill.arcFieldBuilder()
      .setName(name)
      .setInitial(Mockito.mock(ASTExpression.class))
      .build();

    return ArcBasisMill.arcFieldDeclarationBuilder()
      .setMCType(createQualifiedType("int"))
      .addArcField(field)
      .build();
  }

  /** @return An arbitrary {@link ASTArcParameter} with the given name. */
  protected static ASTArcParameter simpleConfigParamNamed(@NotNull String name) {
    Preconditions.checkNotNull(name);

    return ArcBasisMill.arcParameterBuilder()
      .setName(name)
      .setDefault(Mockito.mock(ASTExpression.class))
      .setMCType(createQualifiedType("int"))
      .build();
  }

  protected static <X> Collection<Pair<X, X>> permutePairs(@NotNull List<X> baseSet) {
    Preconditions.checkNotNull(baseSet);
    Set<Pair<X, X>> pairs = new HashSet<>(baseSet.size());

    for(int i = 0; i < baseSet.size(); i++) {
      for(int j = 0; j < baseSet.size(); j++) {
        if(i != j) {
          pairs.add(Pair.of(baseSet.get(i), baseSet.get(j)));
        }
      }
    }

    return pairs;
  }
}
