/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import montiarc.util.ArcError;
import com.google.common.base.Preconditions;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import montiarc.MontiArcMill;
import montiarc.util.Error;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

public class ComponentInstantiationRespectsGenericTypeBoundsTest extends AbstractCoCoTest {

  protected static final String PACKAGE = "componentInstantiationRespectsGenericTypeBounds";

  @Override
  protected String getPackage() {
    return PACKAGE;
  }

  @Override
  protected void registerCoCos(@NotNull MontiArcCoCoChecker checker) {
    Preconditions.checkNotNull(checker).addCoCo(new ComponentInstantiationRespectsGenericTypeBounds());
  }

  @BeforeEach
  public void prepareModels() {
    loadFish();
    loadComparable();
    loadListAndLinkedList();
    loadPersonAndSubtypes();

    loadComponentsToInstantiate();
  }

  public void loadComponentsToInstantiate() {
    // loading helper models into the symboltable
    this.parseAndCreateAndCompleteSymbols("GenericComp.arc");
    this.parseAndCreateAndCompleteSymbols("CompWithoutTypeArgs.arc");
  }

  /**
   * Creates the following types and adds them to the global scope: {@code List<T>} and {@code LinkedList<U> extends
   * List<U>}
   */
  public void loadListAndLinkedList() {
    OOTypeSymbol listSym = MontiArcMill.oOTypeSymbolBuilder()
      .setName("List")
      .setSpannedScope(MontiArcMill.scope())
      .build();
    listSym.addTypeVarSymbol(MontiArcMill.typeVarSymbolBuilder().setName("T").build());
    MontiArcMill.globalScope().add(listSym);
    MontiArcMill.globalScope().addSubScope(listSym.getSpannedScope());

    TypeVarSymbol uTypeVar = MontiArcMill.typeVarSymbolBuilder().setName("U").build();
    OOTypeSymbol linkedListSym = MontiArcMill.oOTypeSymbolBuilder()
      .setName("LinkedList")
      .setSpannedScope(MontiArcMill.scope())
      .build();
    linkedListSym.addTypeVarSymbol(uTypeVar);
    MontiArcMill.globalScope().add(linkedListSym);
    MontiArcMill.globalScope().addSubScope(linkedListSym.getSpannedScope());

    // setting the parent of LinkedList<U> to List<U>
    SymTypeExpression uExpr = SymTypeExpressionFactory.createTypeObject(uTypeVar);
    SymTypeExpression listOfU = SymTypeExpressionFactory.createGenerics(listSym, Arrays.asList(uExpr));
    linkedListSym.addSuperTypes(listOfU);
  }

  /**
   * Creates the type "Fish" and adds it to the global scope.
   */
  public void loadFish() {
    OOTypeSymbol fishSym = MontiArcMill.oOTypeSymbolBuilder()
      .setName("Fish")
      .setSpannedScope(MontiArcMill.scope())
      .build();
    MontiArcMill.globalScope().add(fishSym);
    MontiArcMill.globalScope().addSubScope(fishSym.getSpannedScope());
  }

  /**
   * Creates the type {@code Comparable<V>} and adds it to the global scope.
   */
  public void loadComparable() {
    OOTypeSymbol comparableSym = MontiArcMill.oOTypeSymbolBuilder()
      .setName("Comparable")
      .setSpannedScope(MontiArcMill.scope())
      .build();
    comparableSym.addTypeVarSymbol(MontiArcMill.typeVarSymbolBuilder().setName("V").build());
    MontiArcMill.globalScope().add(comparableSym);
    MontiArcMill.globalScope().addSubScope(comparableSym.getSpannedScope());
  }

  /**
   * Creates the following types and adds them to the global scope: {@code Person}, {@code Student extends Person}, and
   * {@code GlassStudent extends Student & Comparable<Student>}. Note that the type {@code Comparable<V>} must be
   * resolvable before this method is called.
   */
  public void loadPersonAndSubtypes() {
    Optional<OOTypeSymbol> comparableSym = MontiArcMill.globalScope().resolveOOType("Comparable");
    Preconditions.checkState(comparableSym.isPresent(), "Type Comparable<V> is not resolvable in test " +
      "set-up. You may have executed the set-up in the wrong order.");

    OOTypeSymbol personSym = MontiArcMill.oOTypeSymbolBuilder()
      .setName("Person")
      .setSpannedScope(MontiArcMill.scope())
      .build();
    MontiArcMill.globalScope().add(personSym);
    MontiArcMill.globalScope().addSubScope(personSym.getSpannedScope());

    OOTypeSymbol studentSym = MontiArcMill.oOTypeSymbolBuilder()
      .setName("Student")
      .setSpannedScope(MontiArcMill.scope())
      .build();
    MontiArcMill.globalScope().add(studentSym);
    MontiArcMill.globalScope().addSubScope(studentSym.getSpannedScope());
    studentSym.addSuperTypes(SymTypeExpressionFactory.createTypeObject(personSym));
    SymTypeExpression studentExpr = SymTypeExpressionFactory.createTypeObject(studentSym);

    OOTypeSymbol glassStudentSym = MontiArcMill.oOTypeSymbolBuilder()
      .setName("GlassStudent")
      .setSpannedScope(MontiArcMill.scope())
      .build();
    MontiArcMill.globalScope().add(glassStudentSym);
    MontiArcMill.globalScope().addSubScope(glassStudentSym.getSpannedScope());
    glassStudentSym.addSuperTypes(studentExpr);
    glassStudentSym.addSuperTypes(SymTypeExpressionFactory
      .createGenerics(comparableSym.get(), Arrays.asList(studentExpr))
    );
  }

  @ParameterizedTest
  @ValueSource(strings = {"CompatibleBounds.arc"})
  public void shouldCorrectlyBindTypeParams(@NotNull String model) {
    testModel(model);
  }

  protected static Stream<Arguments> modelAndExpectedErrorsProvider() {
    return Stream.of(
      arg("IncompatibleBounds.arc",
        ArcError.TYPE_ARG_IGNORES_UPPER_BOUND, ArcError.TYPE_ARG_IGNORES_UPPER_BOUND,
        ArcError.TYPE_ARG_IGNORES_UPPER_BOUND, ArcError.TYPE_ARG_IGNORES_UPPER_BOUND,
        ArcError.TYPE_ARG_IGNORES_UPPER_BOUND, ArcError.TYPE_ARG_IGNORES_UPPER_BOUND,
        ArcError.TYPE_ARG_IGNORES_UPPER_BOUND, ArcError.TYPE_ARG_IGNORES_UPPER_BOUND,
        ArcError.TYPE_ARG_IGNORES_UPPER_BOUND, ArcError.TYPE_ARG_IGNORES_UPPER_BOUND,
        ArcError.TYPE_ARG_IGNORES_UPPER_BOUND, ArcError.TYPE_ARG_IGNORES_UPPER_BOUND,
        ArcError.TYPE_ARG_IGNORES_UPPER_BOUND, ArcError.TYPE_ARG_IGNORES_UPPER_BOUND
      )
    );
  }

  @ParameterizedTest
  @MethodSource("modelAndExpectedErrorsProvider")
  public void shouldViolateTypeParameterBounds(@NotNull String model, @NotNull Error... errors) {
    Preconditions.checkNotNull(model);
    Preconditions.checkNotNull(errors);
    testModel(model, errors);
  }
}
