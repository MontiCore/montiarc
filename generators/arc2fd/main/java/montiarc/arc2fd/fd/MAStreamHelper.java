/* (c) https://github.com/MontiCore/monticore */
package montiarc.arc2fd.fd;

import arcbasis._ast.ASTArcElement;
import arcbasis._ast.ASTComponentInstance;
import arcbasis._ast.ASTComponentInstantiation;
import arcbasis._ast.ASTComponentType;
import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc._ast.ASTArcConstraintDeclaration;
import variablearc._ast.ASTArcFeature;
import variablearc._ast.ASTArcFeatureDeclaration;
import variablearc._ast.ASTArcVarIf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Helper Class which has some functions to extract things like
 * ASTArcFeatures or ASTExpressions out of a
 * list of ASTArcElements.
 */
public class MAStreamHelper {

  /**
   * Gets a List of all ASTArcFeatures from a List of ASTArcElements
   *
   * @param elements List of Elements to search through
   * @return List of all Features found in the given list of elements
   */
  public static List<ASTArcFeature> getArcFeatureDeclarationsFromElements(@NotNull List<ASTArcElement> elements) {
    Preconditions.checkNotNull(elements);
    Preconditions.checkNotNull(elements);
    return elements.stream()
      .filter(element -> element instanceof ASTArcFeatureDeclaration)
      .map(featureDec -> ((ASTArcFeatureDeclaration) featureDec).getArcFeatureList())
      .flatMap(Collection::stream)
      .collect(Collectors.toList());
  }

  /**
   * Gets a List of all ASTExpression from a List of ASTArcElements
   *
   * @param elements List of Elements to search through
   * @return List of all Expressions found in the given list of elements
   */
  public static List<ASTExpression> getConstraintExpressionsFromArcElements(@NotNull List<ASTArcElement> elements) {
    Preconditions.checkNotNull(elements);
    return elements.stream()
      .filter(element -> element instanceof ASTArcConstraintDeclaration)
      .map(featureDec -> ((ASTArcConstraintDeclaration) featureDec).getExpression())
      .collect(Collectors.toList());
  }

  /**
   * Gets a List of all ASTArcVarIf from a List of ASTArcElements
   *
   * @param elements List of Elements to search through
   * @return List of all If-Else-Statements found in the given list of elements
   */
  public static List<ASTArcVarIf> getVarIfsFromArcElements(@NotNull List<ASTArcElement> elements) {
    Preconditions.checkNotNull(elements);
    return elements.stream()
      .filter(element -> element instanceof ASTArcVarIf)
      .map(featureDec -> (ASTArcVarIf) featureDec)
      .collect(Collectors.toList());
  }

  /**
   * Gets a List of all Inner Components from a List of ASTArcElements
   *
   * @param elements List of Elements to search through
   * @return List of all Inner Component Types found in the given list of
   * elements
   */
  public static List<ASTComponentType> getInnerComponentsFromArcElements(@NotNull List<ASTArcElement> elements) {
    return elements.stream()
      .filter(element -> element instanceof ASTComponentType)
      .map(innerComponent -> (ASTComponentType) innerComponent)
      .collect(Collectors.toList());
  }

  /**
   * Gets a List of all ASTComponentInstance from a List of ASTArcElements
   *
   * @param elements List of Elements to search through
   * @return List of all Component Instances found in the given list of elements
   */
  public static List<ASTComponentInstance> getComponentInstancesFromArcElements(@NotNull List<ASTArcElement> elements) {
    Preconditions.checkNotNull(elements);
    List<ASTComponentInstance> subComponents = new ArrayList<>();
    // Get all Component Instantiations (from "external" components)
    subComponents.addAll(getSubComponentInstantiationsFromArcElements(elements)
      .stream()
      .map(ASTComponentInstantiation::getComponentInstanceList)
      .flatMap(Collection::stream)
      .collect(Collectors.toList()));

    // Also consider all Instantiations of "Nested"-Components (internal,
    // declared inside)
    subComponents.addAll(getInnerComponentsFromArcElements(elements)
      .stream()
      .map(ASTComponentType::getComponentInstanceList)
      .flatMap(Collection::stream)
      .collect(Collectors.toList()));

    return subComponents;
  }

  /**
   * Gets a List of all ASTComponentInstantiation from a List of ASTArcElements
   *
   * @param elements List of Elements to search through
   * @return List of all Component Instantiations found in the given list of
   * elements
   */
  public static List<ASTComponentInstantiation> getSubComponentInstantiationsFromArcElements(@NotNull List<ASTArcElement> elements) {
    Preconditions.checkNotNull(elements);
    return elements.stream()
      .filter(element -> element instanceof ASTComponentInstantiation)
      .map(subComponent -> (ASTComponentInstantiation) subComponent)
      .collect(Collectors.toList());
  }

  /**
   * Gets the corresponding ASTComponentType from a given ASTComponentInstance
   *
   * @param instance Component Instance where we want to extract the type from
   * @return ASTComponentType corresponding to the Instance
   */
  public static ASTComponentType getComponentTypeFromInstance(@NotNull ASTComponentInstance instance) {
    return (ASTComponentType) instance.getSymbol().getType().getTypeInfo().getAstNode();
  }

  /**
   * Extracts all ASTArcElements from an ASTComponentType
   *
   * @param comp Component where we want to get the elements from
   * @return List of all ASTArcElements found in the component
   */
  public static List<ASTArcElement> getArcElements(@NotNull ASTComponentType comp) {
    Preconditions.checkNotNull(comp);
    return comp.getBody().getArcElementList();
  }
}
