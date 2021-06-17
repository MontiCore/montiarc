/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos.util;

import arcbasis._symboltable.ComponentInstanceSymbol;
import arcbasis._symboltable.ComponentTypeSymbol;
import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.visitor.ITraverser;
import de.se_rwth.commons.SourcePosition;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Represents a method that can be used to find references to certain ports as defined by {@link PortReference}s in
 * {@link ASTExpression}s.
 */
@FunctionalInterface
public interface IPortReferenceInExpressionExtractor {

  /**
   * @param traverser The traverser to use while searching for port references
   * @see IPortReferenceInExpressionExtractor
   */
  HashMap<PortReference, SourcePosition> findPortReferences(@NotNull ASTExpression expr,
                                                            @NotNull HashSet<PortReference> portReferencesToLookFor,
                                                            @NotNull ITraverser traverser);

  /**
   * Holds simple String information about the reference to a port. As this type only records the simple name of the
   * referenced port and optionally an instance name to which the port belongs, this class is not intended to retrieve
   * the symbols or AST nodes to which an object of this class refers. Use {@link arcbasis._ast.ASTPortAccess} for this
   * purpose instead.
   */
  class PortReference {

    protected final String portName;
    protected final String instanceName;

    public PortReference(@NotNull String portName) {
      Preconditions.checkNotNull(portName);
      this.portName = portName;
      this.instanceName = null;
    }

    public PortReference(@NotNull String instanceName, @NotNull String portName) {
      Preconditions.checkNotNull(instanceName);
      Preconditions.checkNotNull(portName);
      this.portName = portName;
      this.instanceName = instanceName;
    }

    public String getPortName() {
      return this.portName;
    }

    public Optional<String> getInstanceName() {
      return Optional.ofNullable(this.instanceName);
    }

    @Override
    public String toString() {
      return this.getInstanceName().map(s -> this.portName + "." + s).orElse(this.portName);
    }

    /**
     * Creates {@link PortReference}s that correspond to the ports of the given component type.
     */
    public static Collection<PortReference> ofComponentTypePorts(@NotNull ComponentTypeSymbol comp) {
      Preconditions.checkNotNull(comp);

      return comp.getAllPorts().stream()
        .map(port -> new PortReference(port.getName()))
        .collect(Collectors.toSet());
    }

    /**
     * Creates {@link PortReference}s that correspond to the ports of the subcomponents of the given component type,
     * qualified by their instance names.
     */
    public static Collection<PortReference> ofSubComponentPorts(@NotNull ComponentTypeSymbol comp) {
      Preconditions.checkNotNull(comp);
      Collection<PortReference> allSubCompPorts = new HashSet<>();

      for (ComponentInstanceSymbol subComp : comp.getSubComponents()) {
        Preconditions.checkState(subComp.getType() != null);
        subComp.getType().getAllPorts().stream()
          .map(port -> new PortReference(subComp.getName(), port.getName()))
          .forEach(allSubCompPorts::add);
      }
      return allSubCompPorts;
    }
  }
}
