/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis._ast.ASTArcParameter;
import arcbasis._ast.ASTComponentType;
import arcbasis._cocos.ArcBasisASTComponentTypeCoCo;
import com.google.common.base.Preconditions;
import de.monticore.ast.ASTNode;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.ocl.setexpressions._ast.ASTSetEnumeration;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.SymTypeOfGenerics;
import de.monticore.types.mccollectiontypes.types3.util.MCCollectionSymTypeFactory;
import de.monticore.types3.SymTypeRelations;
import de.monticore.types3.TypeCheck3;
import de.monticore.umlstereotype._ast.ASTStereoValue;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcMill;
import montiarc.util.MontiArcError;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc._ast.ASTArcFeature;
import variablearc._ast.ASTArcFeatureDeclaration;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Checks if test stereotypes fit to one another and the component signature
 */
public class MaUnitTestConfiguredCorrectly implements ArcBasisASTComponentTypeCoCo {

  @Override
  public void check(@NotNull ASTComponentType node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkArgument(node.isPresentSymbol());

    if (!node.isPresentStereotype() || !node.getStereotype().contains("test"))
      return;

    // Check has no ports
    if (!node.getSymbol().getAllArcPorts().isEmpty()) {
      Log.error(MontiArcError.UNIT_CANNOT_HAVE_PORTS.format());
    }

    int testCount = unitTestCaseCount(node);

    // Check parameter assignments
    if (getStereo(node, "test").isPresent()) {
      checkTestSourceAssignmentMethod(node);
    } else {
      checkValueSourceAssignmentMethod(node);
    }

    // Check feature assignments
    for (ASTArcFeature feature : node.getBody()
      .streamArcElementsOfType(ASTArcFeatureDeclaration.class)
      .flatMap(ASTArcFeatureDeclaration::streamArcFeatures).collect(Collectors.toList())) {
      Optional<ASTStereoValue> stereo = getStereo(node, feature.getName());
      if (stereo.isEmpty()) continue;

      checkTestCountFits(stereo.get(), testCount);
      checkTypeFits(stereo.get(), SymTypeExpressionFactory.createPrimitive("boolean"));
    }

    // Check tick count
    Optional<ASTStereoValue> tickStereo = getStereo(node, "ticks");
    if (tickStereo.isPresent()) {
      checkTestCountFits(tickStereo.get(), testCount);
      checkTypeFits(tickStereo.get(), SymTypeExpressionFactory.createPrimitive("int"));
    }

    // Check expected outcome
    Optional<ASTStereoValue> expectedStereo = getStereo(node, "exception");
    if (expectedStereo.isPresent()) {
      checkTestCountFits(expectedStereo.get(), testCount);
    }
  }

  protected void checkTestSourceAssignmentMethod(@NotNull ASTComponentType node) {
    Preconditions.checkArgument(getStereo(node, "test").isPresent());

    Optional<ASTSetEnumeration> testDefinition = getStereo(node, "test")
      .filter(ASTStereoValue::isPresentExpression)
      .map(ASTStereoValue::getExpression)
      .filter(MontiArcMill.typeDispatcher()::isSetExpressionsASTSetEnumeration)
      .map(MontiArcMill.typeDispatcher()::asSetExpressionsASTSetEnumeration);

    if (testDefinition.isEmpty())
      Log.error(MontiArcError.UNIT_TEST_SOURCE_MISCONFIGURED.format());

    if (testDefinition.isPresent()) {
      for (int testIndex = 0; testIndex < testDefinition.get()
        .getSetCollectionItemList().size(); testIndex++) {
        if (MontiArcMill.typeDispatcher()
          .isSetExpressionsASTSetValueItem(testDefinition.get()
            .getSetCollectionItem(testIndex))
          && MontiArcMill.typeDispatcher()
          .isSetExpressionsASTSetEnumeration(MontiArcMill.typeDispatcher()
            .asSetExpressionsASTSetValueItem(testDefinition.get()
              .getSetCollectionItem(testIndex)).getExpression())
          && MontiArcMill.typeDispatcher()
          .asSetExpressionsASTSetEnumeration(MontiArcMill.typeDispatcher()
            .asSetExpressionsASTSetValueItem(testDefinition.get()
              .getSetCollectionItem(testIndex))
            .getExpression())
          .isList()) {
          ASTSetEnumeration testCaseDefinitionList = MontiArcMill.typeDispatcher()
            .asSetExpressionsASTSetEnumeration(MontiArcMill.typeDispatcher()
              .asSetExpressionsASTSetValueItem(testDefinition.get()
                .getSetCollectionItem(testIndex)).getExpression());
          for (int i = 0; i < testCaseDefinitionList.getSetCollectionItemList().size(); i++) {
            if (!MontiArcMill.typeDispatcher()
              .isSetExpressionsASTSetValueItem(testCaseDefinitionList
                .getSetCollectionItem(i))) {
              Log.error(MontiArcError.UNIT_TEST_CASE_PARAMETER_MISCONFIGURED.format(testIndex, i),
                testCaseDefinitionList.getSetCollectionItem(i).get_SourcePositionStart(),
                testCaseDefinitionList.getSetCollectionItem(i).get_SourcePositionEnd());
              continue;
            }
            if (i >= node.getHead().getArcParameterList().size()) {
              continue;
            }

            // Check type fits
            ASTExpression assignment = MontiArcMill.typeDispatcher()
              .asSetExpressionsASTSetValueItem(testCaseDefinitionList
                .getSetCollectionItem(i)).getExpression();
            SymTypeExpression parameterType = node.getHead().getArcParameter(i).getSymbol().getType();
            checkTypeFits(node.getHead().getArcParameter(i).getName(), assignment, parameterType, TypeCheck3.typeOf(assignment, parameterType));
          }

          // Parameter count fits
          if (testCaseDefinitionList.getSetCollectionItemList().size() > node.getHead().getArcParameterList().size()) {
            Log.error(MontiArcError.UNIT_TOO_MANY_ARGUMENTS.format(testIndex),
              testCaseDefinitionList.get_SourcePositionStart(), testCaseDefinitionList.get_SourcePositionEnd());
          }
          if (testCaseDefinitionList.getSetCollectionItemList().size() < node.getHead().streamArcParameters()
            .filter(p -> !p.isPresentDefault()).count()) {
            String missingParameters = node.getHead().streamArcParameters()
              .filter(p -> !p.isPresentDefault())
              .skip(testCaseDefinitionList.getSetCollectionItemList().size())
              .reduce("", (a, b) -> a + b.getName() + ", ", String::concat);
            Log.error(MontiArcError.UNIT_MISSING_ARGUMENTS.format(testIndex, missingParameters),
              testCaseDefinitionList.get_SourcePositionStart(), testCaseDefinitionList.get_SourcePositionEnd());
          }
        } else {
          Log.error(MontiArcError.UNIT_TEST_CASE_MISCONFIGURED.format(testIndex),
            testDefinition.get().getSetCollectionItem(testIndex).get_SourcePositionStart(),
            testDefinition.get().getSetCollectionItem(testIndex).get_SourcePositionEnd());
        }
      }

      // Check enugh test cases defined
      if (unitTestCaseCount(node) > testDefinition.get().getSetCollectionItemList().size()) {
        Log.error(MontiArcError.UNIT_TEST_COUNT_MISMATCH.format(unitTestCaseCount(node), testDefinition.get().getSetCollectionItemList().size()),
          testDefinition.get().get_SourcePositionStart(),
          testDefinition.get().get_SourcePositionEnd());
      }
    }

    // Check not using parameter assignment method
    for (ASTArcParameter parameter : node.getHead().getArcParameterList()) {
      if (getStereo(node, parameter.getName()).isPresent()) {
        Log.error(MontiArcError.UNIT_TEST_SOURCE_AND_VALUE_SOURCE.format(parameter.getName()),
          getStereo(node, parameter.getName()).get().get_SourcePositionStart(),
          getStereo(node, parameter.getName()).get().get_SourcePositionEnd());
      }
    }
  }

  protected void checkValueSourceAssignmentMethod(@NotNull ASTComponentType node) {
    int testCount = unitTestCaseCount(node);

    // Check parameter assignments
    for (ASTArcParameter parameter : node.getHead().getArcParameterList()) {
      Optional<ASTStereoValue> stereo = getStereo(node, parameter.getName());
      if (stereo.isEmpty()) {
        if (!parameter.isPresentDefault())
          Log.error(MontiArcError.UNIT_MISSING_ARGUMENT.format(parameter.getName()),
            node.getStereotype().get_SourcePositionStart(),
            node.getStereotype().get_SourcePositionEnd());
        continue;
      }

      checkTestCountFits(stereo.get(), testCount);
      checkTypeFits(stereo.get(), parameter.getSymbol().getType());
    }
  }

  protected Optional<ASTStereoValue> getStereo(@NotNull ASTComponentType node, @NotNull String name) {
    Preconditions.checkNotNull(node);
    Preconditions.checkNotNull(name);

    List<ASTStereoValue> stereos = node.getStereotype().getValuesList().stream()
      .filter(s -> Objects.equals(s.getName(), name))
      .filter(ASTStereoValue::isPresentExpression)
      .collect(Collectors.toList());
    if (stereos.isEmpty()) {
      return Optional.empty();
    }
    if (stereos.size() > 1) {
      Log.warn(MontiArcError.UNIT_DUPLICATE_ARGUMENTS.format(name),
        stereos.get(stereos.size() - 1).get_SourcePositionStart(),
        stereos.get(stereos.size() - 1).get_SourcePositionStart());
    }

    return Optional.of(stereos.get(0));
  }

  protected void checkTestCountFits(@NotNull ASTStereoValue stereo, int testCount) {
    Preconditions.checkNotNull(stereo);
    if (MontiArcMill.typeDispatcher().isSetExpressionsASTSetEnumeration(stereo.getExpression())
      && MontiArcMill.typeDispatcher().asSetExpressionsASTSetEnumeration(stereo.getExpression()).isList()
      && MontiArcMill.typeDispatcher().asSetExpressionsASTSetEnumeration(stereo.getExpression()).getSetCollectionItemList().size() != testCount) {
      Log.error(MontiArcError.UNIT_TEST_COUNT_MISMATCH.format(testCount, MontiArcMill.typeDispatcher().asSetExpressionsASTSetEnumeration(stereo.getExpression()).getSetCollectionItemList().size()),
        stereo.get_SourcePositionStart(), stereo.get_SourcePositionEnd());
    }
  }

  /**
   * Checks if the type of the value source stereo expression(s) fit the parameter/feature/timing type
   */
  protected void checkTypeFits(@NotNull ASTStereoValue stereo, @NotNull SymTypeExpression type) {
    Preconditions.checkNotNull(stereo);
    Preconditions.checkNotNull(type);

    if (MontiArcMill.typeDispatcher().isSetExpressionsASTSetEnumeration(stereo.getExpression())
      && MontiArcMill.typeDispatcher().asSetExpressionsASTSetEnumeration(stereo.getExpression()).isList()) {

      // The stereo value should be of type List<X> where X is the type of the
      // corresponding parameter. We create the target type for the stereo
      // value here to check against it.
      SymTypeOfGenerics targetType = MCCollectionSymTypeFactory.createList(type);

      checkTypeFits(stereo.getName(), stereo.getExpression(), targetType, TypeCheck3.typeOf(stereo.getExpression(), targetType) );

    } else {
      checkTypeFits(stereo.getName(), stereo, type, TypeCheck3.typeOf(stereo.getExpression(), type));
    }
  }

  protected void checkTypeFits(@NotNull String name, @NotNull ASTNode node, @NotNull SymTypeExpression assignee, @NotNull SymTypeExpression assigner) {
    Preconditions.checkNotNull(node);
    Preconditions.checkNotNull(assignee);
    Preconditions.checkNotNull(assigner);

    if (assigner.isObscureType()) {
      Log.debug(node.get_SourcePositionStart() + ": Skip execution of CoCo, could not calculate the stereotype's type.", this.getClass().getCanonicalName());
    } else if (!SymTypeRelations.isCompatible(assignee, assigner)) {
      Log.error(MontiArcError.UNIT_TYPE_MISMATCH.format(name, assignee.printFullName(), assigner.printFullName()), node.get_SourcePositionStart(), node.get_SourcePositionEnd());
    }
  }

  /**
   * @return the number of test cases defined by the component
   */
  protected int unitTestCaseCount(@NotNull ASTComponentType node) {
    Preconditions.checkNotNull(node);
    if (!node.isPresentStereotype()) return 0;

    Set<String> names = node.getHead().getArcParameterList().stream().map(ASTArcParameter::getName).collect(Collectors.toSet());
    names.addAll(node.getBody().streamArcElementsOfType(ASTArcFeatureDeclaration.class).flatMap(ASTArcFeatureDeclaration::streamArcFeatures).map(ASTArcFeature::getName).collect(Collectors.toSet()));
    names.add("ticks");

    return Math.max(
      node.getStereotype().getValuesList().stream()
        .filter(sv -> names.contains(sv.getName()))
        .filter(ASTStereoValue::isPresentExpression)
        .filter(stereo -> MontiArcMill.typeDispatcher().isSetExpressionsASTSetEnumeration(stereo.getExpression()) && MontiArcMill.typeDispatcher().asSetExpressionsASTSetEnumeration(stereo.getExpression()).isList())
        .map(stereo -> MontiArcMill.typeDispatcher().asSetExpressionsASTSetEnumeration(stereo.getExpression()).getSetCollectionItemList().size())
        .reduce(1, Math::max),
      getStereo(node, "test").map(stereo -> {
        if (stereo.isPresentExpression() && MontiArcMill.typeDispatcher().isSetExpressionsASTSetEnumeration(stereo.getExpression()))
          return MontiArcMill.typeDispatcher().asSetExpressionsASTSetEnumeration(stereo.getExpression()).getSetCollectionItemList().size();
        else return 1;
      }).orElse(1)
    );
  }
}
