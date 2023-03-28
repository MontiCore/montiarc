/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis._cocos.PortHeritageTypeFits;
import com.google.common.base.Preconditions;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.TypeRelations;
import montiarc.MontiArcMill;
import montiarc.util.ArcError;
import montiarc.util.Error;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

/**
 * Tests {@link  PortHeritageTypeFits}
 */
public class PortHeritageTypeFitsTest extends AbstractCoCoTest {

  @Override
  @BeforeEach
  public void setUp() {
    super.setUp();
    loadOOTypes();
    loadUtilityComponents();
  }

  public void loadOOTypes() {
    loadString();
    loadList();
    loadPersonAndStudent();
  }

  public void loadString() {
    OOTypeSymbol listSym = MontiArcMill.oOTypeSymbolBuilder()
      .setName("String")
      .setSpannedScope(MontiArcMill.scope())
      .build();
    MontiArcMill.globalScope().add(listSym);
    MontiArcMill.globalScope().addSubScope(listSym.getSpannedScope());
  }

  public void loadList() {
    OOTypeSymbol listSym = MontiArcMill.oOTypeSymbolBuilder()
      .setName("List")
      .setSpannedScope(MontiArcMill.scope())
      .build();
    listSym.addTypeVarSymbol(MontiArcMill.typeVarSymbolBuilder().setName("T").build());
    MontiArcMill.globalScope().add(listSym);
    MontiArcMill.globalScope().addSubScope(listSym.getSpannedScope());
  }

  public void loadPersonAndStudent() {
    OOTypeSymbol personSym = MontiArcMill.oOTypeSymbolBuilder()
      .setName("Person")
      .setSpannedScope(MontiArcMill.scope())
      .build();
    MontiArcMill.globalScope().add(personSym);
    MontiArcMill.globalScope().addSubScope(personSym.getSpannedScope());

    OOTypeSymbol studentSym = MontiArcMill.oOTypeSymbolBuilder()
      .setName("Student")
      .setSpannedScope(MontiArcMill.scope())
      .addSuperTypes(SymTypeExpressionFactory.createTypeObject(personSym))
      .build();
    MontiArcMill.globalScope().add(studentSym);
    MontiArcMill.globalScope().addSubScope(studentSym.getSpannedScope());
  }

  public void loadUtilityComponents() {
    // loading super components into the symboltable
    this.parseAndCreateAndCompleteSymbols("superComponents/MultipleIncomingAndOutgoingPorts.arc");
    this.parseAndCreateAndCompleteSymbols("superComponents/GenericIncomingAndOutgoingPort.arc");
  }

  /**
   * This method that facilitates stating arguments for parameterized tests. By using an elliptical parameter this
   * method removes the need to explicitly create arrays.
   *
   * @param model  model to test
   * @param errors all expected errors
   */
  protected static Arguments arg(@NotNull String model, @NotNull Error... errors) {
    Preconditions.checkNotNull(model);
    Preconditions.checkNotNull(errors);

    return Arguments.of(model, errors);
  }

  protected static Stream<Arguments> modelAndExpectedErrorsProvider() {
    return Stream.of(
      arg("NoPortOverriding.arc"),
      arg("CorrectPortOverriding.arc"),
      arg("CorrectGenericPortOverriding.arc"),
      arg("CorrectNestedPortOverriding.arc"),
      arg("IncorrectPortOverriding.arc",
        ArcError.IN_PORT_HERITAGE_TYPE_MISMATCH,
        ArcError.IN_PORT_HERITAGE_TYPE_MISMATCH,
        ArcError.IN_PORT_HERITAGE_TYPE_MISMATCH,
        ArcError.IN_PORT_HERITAGE_TYPE_MISMATCH,
        ArcError.OUT_PORT_HERITAGE_TYPE_MISMATCH,
        ArcError.IN_PORT_HERITAGE_TYPE_MISMATCH,
        ArcError.PORT_HERITAGE_DIRECTION_MISMATCH,
        ArcError.OUT_PORT_HERITAGE_TYPE_MISMATCH,
        ArcError.OUT_PORT_HERITAGE_TYPE_MISMATCH
      ),
      arg("IncorrectGenericPortOverriding.arc",
        ArcError.IN_PORT_HERITAGE_TYPE_MISMATCH,
        ArcError.OUT_PORT_HERITAGE_TYPE_MISMATCH
      ),
      arg("IncorrectNestedPortOverriding.arc",
        ArcError.IN_PORT_HERITAGE_TYPE_MISMATCH,
        ArcError.IN_PORT_HERITAGE_TYPE_MISMATCH,
        ArcError.IN_PORT_HERITAGE_TYPE_MISMATCH,
        ArcError.IN_PORT_HERITAGE_TYPE_MISMATCH,
        ArcError.OUT_PORT_HERITAGE_TYPE_MISMATCH,
        ArcError.IN_PORT_HERITAGE_TYPE_MISMATCH,
        ArcError.PORT_HERITAGE_DIRECTION_MISMATCH,
        ArcError.OUT_PORT_HERITAGE_TYPE_MISMATCH,
        ArcError.OUT_PORT_HERITAGE_TYPE_MISMATCH
      )
    );
  }

  @Override
  protected String getPackage() {
    return "correctSymbolTypeInheritance";
  }

  @Override
  protected void registerCoCos(MontiArcCoCoChecker checker) {
    checker.addCoCo(new PortHeritageTypeFits(new TypeRelations()));
  }

  @ParameterizedTest
  @MethodSource("modelAndExpectedErrorsProvider")
  public void shouldFind(@NotNull String model, @NotNull Error... errors) {
    Preconditions.checkNotNull(model);
    Preconditions.checkNotNull(errors);

    testModel(model, errors);
  }
}
