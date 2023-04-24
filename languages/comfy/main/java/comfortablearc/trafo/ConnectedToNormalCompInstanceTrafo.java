/* (c) https://github.com/MontiCore/monticore */
package comfortablearc.trafo;

import arcbasis._ast.*;
import arcbasis._visitor.ArcBasisVisitor2;
import arcbasis.trafo.SourcePositionUtil;
import com.google.common.base.Preconditions;
import comfortablearc.ComfortableArcMill;
import comfortablearc._ast.ASTConnectedComponentInstance;
import de.se_rwth.commons.SourcePosition;
import de.se_rwth.commons.logging.Log;
import montiarc.util.ComfortableArcError;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Transforms {@link ASTConnectedComponentInstance} to separate {@link ASTComponentInstance} and {@link ASTConnector}
 * declarations.
 */
public class ConnectedToNormalCompInstanceTrafo implements ArcBasisVisitor2 {

  @Override
  public void visit(@NotNull ASTComponentBody body) {
    Preconditions.checkNotNull(body);

    var instantiationsWithConnectedComps = instantiationToConnectedComps(body);
    for (ASTComponentInstantiation instantiation : instantiationsWithConnectedComps.keySet()) {
      for (ASTConnectedComponentInstance oldInstance : instantiationsWithConnectedComps.get(instantiation)) {

        // Replace old instance with transformed one
        int indexOfInstance = instantiation.getComponentInstanceList().indexOf(oldInstance);
        instantiation.removeComponentInstance(oldInstance);
        instantiation.addComponentInstance(indexOfInstance, transformToNormalInstance(oldInstance));

        // Transform and transfer connectors
        for (ASTConnector connector : oldInstance.getConnectorList()) {
          if (!connector.getSource().isPresentComponent()
            || connector.getSource().getComponent().equals(oldInstance.getName())) {

            connector.getSource().setComponent(oldInstance.getName());
            body.addArcElement(connector);

          } else {
            Log.error(
              ComfortableArcError.CONNECTED_COMPONENT_CONNECTOR_SRC_HAS_COMP_NAME.format(connector.getSourceName()),
              connector.getSource().get_SourcePositionStart(),
              connector.getSource().get_SourcePositionEnd()
            );
          }
        }
      }
    }
  }

  /**
   * @return Every {@link ASTConnectedComponentInstance}, recorded under the {@link ASTComponentInstantiation} in which
   * it appears.
   */
  protected Map<ASTComponentInstantiation, List<ASTConnectedComponentInstance>> instantiationToConnectedComps(
  ASTComponentBody body) {

    return body.streamArcElementsOfType(ASTComponentInstantiation.class)
      .collect(Collectors.toMap(Function.identity(), this::connectedCompsOfInstantiation));
  }

  protected List<ASTConnectedComponentInstance> connectedCompsOfInstantiation(ASTComponentInstantiation instantiation) {
    return instantiation.streamComponentInstances()
      .filter(ASTConnectedComponentInstance.class::isInstance)
      .map(ASTConnectedComponentInstance.class::cast)
      .collect(Collectors.toList());
  }

  protected ASTComponentInstance transformToNormalInstance(ASTConnectedComponentInstance oldInstance) {
    ASTComponentInstanceBuilder newInstanceBuilder = ComfortableArcMill.componentInstanceBuilder()
      .set_SourcePositionStart(oldInstance.get_SourcePositionStart())
      .set_SourcePositionEnd(getEndPositionIgnoringConnectors(oldInstance))
      .set_PreCommentList(oldInstance.get_PreCommentList())
      .set_PostCommentList(oldInstance.get_PostCommentList());

    newInstanceBuilder.setName(oldInstance.getName());

    if (oldInstance.isPresentArcArguments()) {
      newInstanceBuilder.setArcArguments(oldInstance.getArcArguments());
    } else {
      newInstanceBuilder.setArcArgumentsAbsent();
    }

    return newInstanceBuilder.build();
  }

  /**
   * @return the end position for a potential transformed {@link ASTComponentInstance} version of {@code instance}.
   * Therefore, trailing connectors are ignored.
   */
  protected SourcePosition getEndPositionIgnoringConnectors(ASTConnectedComponentInstance instance) {
    if (instance.isPresentArcArguments()) {
      return instance.getArcArguments().get_SourcePositionEnd();
    } else {
      // We can not just call .getName().get_SourcePositionEnd(), as getName() is a String-Token that has no
      // SourcePosition associated with it.
      return SourcePositionUtil.elongate(instance.get_SourcePositionStart(), instance.getName().length());
    }
  }
}
