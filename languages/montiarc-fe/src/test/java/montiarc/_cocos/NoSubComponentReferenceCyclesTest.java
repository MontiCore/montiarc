/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis._ast.ASTComponentType;
import arcbasis._cocos.NoSubComponentReferenceCycles;
import arcbasis._symboltable.ComponentInstanceSymbol;
import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis.util.ArcError;
import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcMill;
import montiarc._ast.ASTMACompilationUnit;
import montiarc.util.Error;
import org.apache.commons.io.FilenameUtils;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

public class NoSubComponentReferenceCyclesTest extends AbstractCoCoTest {

  protected final static String PACKAGE = "noSubComponentReferenceCycles";

  @Override
  protected String getPackage() { return PACKAGE; }

  @Override
  protected void registerCoCos(@NotNull MontiArcCoCoChecker checker) {
    Preconditions.checkNotNull(checker);
    checker.addCoCo(new NoSubComponentReferenceCycles());
  }

  protected static Stream<Arguments> modelAndExpectedErrorsProvider() {
    return Stream.of(
      arg("LongCycle1.arc", ArcError.NO_SUBCOMPONENT_CYCLE, ArcError.NO_SUBCOMPONENT_CYCLE, ArcError.NO_SUBCOMPONENT_CYCLE),
      arg("WithDirectSelfReference.arc", ArcError.NO_SUBCOMPONENT_CYCLE),
      arg("WithNestedSubCompRefCycle.arc", ArcError.NO_SUBCOMPONENT_CYCLE, ArcError.NO_SUBCOMPONENT_CYCLE),
      arg("WithNestedSubCompRefCycle2.arc", ArcError.NO_SUBCOMPONENT_CYCLE, ArcError.NO_SUBCOMPONENT_CYCLE, ArcError.NO_SUBCOMPONENT_CYCLE),
      arg("WithSubCompRefCycle.arc", ArcError.NO_SUBCOMPONENT_CYCLE, ArcError.NO_SUBCOMPONENT_CYCLE)
    );
  }

  @ParameterizedTest
  @MethodSource("modelAndExpectedErrorsProvider")
  public void shouldFindSubComponentRefCycles(@NotNull String model, @NotNull Error... errors) {
    Preconditions.checkNotNull(model);
    Preconditions.checkNotNull(errors);

    //Given
    Stream<ASTComponentType> artifacts = this.parseAndLoadAllSymbolsStream(this.getPackage() + "." + model);

    //When
    artifacts.forEach(artifact -> this.getChecker().checkAll(artifact));

    //Then
    this.checkOnlyExpectedErrorsPresent(errors);
  }

  @ParameterizedTest
  @ValueSource(strings = {"DummyWithoutCycle.arc", "WithoutSubCompRefCycle.arc"})
  public void shouldNotFindSubComponentRefCycles(@NotNull String model) {
    Preconditions.checkNotNull(model);

    //Given
    Stream<ASTComponentType> artifacts = this.parseAndLoadAllSymbolsStream(this.getPackage() + "." + model);

    //When
    artifacts.forEach(unit -> this.getChecker().checkAll(unit));

    //Then
    Assertions.assertEquals(Collections.emptyList(), Log.getFindings());
  }

  protected Stream<ASTComponentType> parseAndLoadAllSymbolsStream(@NotNull String model) {
    Preconditions.checkNotNull(model);
    // parse and load symbols
    Path path = Paths.get(RELATIVE_MODEL_PATH, MODEL_PATH, this.getPackage());
    this.getCLI().loadSymbols(MontiArcMill.globalScope().getFileExt(), path);
    Collection<ASTMACompilationUnit> asts = this.getCLI().parse(".arc", path);
    this.getCLI().createSymbolTable(asts);
    this.getCLI().completeSymbolTable(asts);
    // find top-component
    Optional<ComponentTypeSymbol> topComponent =
      MontiArcMill.globalScope().resolveComponentType(FilenameUtils.removeExtension(model));
    Preconditions.checkState(topComponent.isPresent());
    // find all related component types
    Set<ComponentTypeSymbol> types = new HashSet<>();
    collectSubComponents(topComponent.get(), types);
    // return one ast node per type so it can be checked
    return types.stream()
      // ignore inner components, because the checker can find them when checking their outer component
      .filter(type -> !type.isInnerComponent())
      .map(ComponentTypeSymbol::getAstNode);
  }

  /**
   * Recursively travels through a components sub components and their subcomponents, to find all component types that
   * are part of this model.
   *
   * @param typeSymbol top component type
   * @param bucket     set that will contain all used types after finishing this call
   */
  protected void collectSubComponents(ComponentTypeSymbol typeSymbol, Set<ComponentTypeSymbol> bucket) {
    Stream.of(typeSymbol)
      // ignore components that are already known
      .filter(bucket::add)
      // get direct subtypes
      .map(ComponentTypeSymbol::getSubComponents)
      .flatMap(List::stream)
      .map(ComponentInstanceSymbol::getType)
      // recursion call
      .forEach(type -> collectSubComponents(type, bucket));
  }
}