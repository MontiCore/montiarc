/* (c) https://github.com/MontiCore/monticore */
package variablearc._cocos.util;

import arcbasis._symboltable.ComponentTypeSymbol;
import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.visitor.ITraverser;
import de.se_rwth.commons.SourcePosition;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc._symboltable.ArcFeature2VariableAdapter;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.stream.Collectors;

/**
 * Represents a method that can be used to find references to certain ports as defined by {@link FieldReference}s in
 * {@link ASTExpression}s.
 */
@FunctionalInterface
public interface IFieldReferenceInExpressionExtractor {

  /**
   * @param traverser The traverser to use while searching for port references
   * @see IFieldReferenceInExpressionExtractor
   */
  HashMap<FieldReference, SourcePosition> findFieldReferences(@NotNull ASTExpression expr,
                                                              @NotNull HashSet<FieldReference> fieldReferencesToLookFor,
                                                              @NotNull ITraverser traverser);

  /**
   * Holds simple String information about the reference to a port. As this type only records the simple name of the
   * referenced port and optionally an instance name to which the port belongs, this class is not intended to retrieve
   * the symbols or AST nodes to which an object of this class refers. Use {@link arcbasis._ast.ASTPortAccess} for this
   * purpose instead.
   */
  class FieldReference {

    protected final String fieldName;

    public FieldReference(@NotNull String fieldName) {
      Preconditions.checkNotNull(fieldName);
      this.fieldName = fieldName;
    }

    public String getFieldName() {
      return this.fieldName;
    }


    @Override
    public String toString() {
      return this.fieldName;
    }

    /**
     * Creates {@link FieldReference}s that correspond to the ports of the given component type.
     */
    public static Collection<FieldReference> ofComponentTypeFields(@NotNull ComponentTypeSymbol comp) {
      Preconditions.checkNotNull(comp);

      return comp.getFields().stream()
        .filter(e -> !comp.getParameters().contains(e))
        .filter(e -> !(e instanceof ArcFeature2VariableAdapter))
        .map(field -> new FieldReference(field.getName()))
        .collect(Collectors.toSet());
    }
  }
}
