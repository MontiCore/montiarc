/* (c) https://github.com/MontiCore/monticore */
package variablearc._cocos.arcbasis;

import arcbasis._symboltable.ComponentTypeSymbol;
import com.google.common.base.Preconditions;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import de.se_rwth.commons.SourcePosition;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc._symboltable.IVariableArcScope;

public class UniqueIdentifier extends arcbasis._cocos.UniqueIdentifier {

  /**
   * This method collects all identifier names within the scope of a component type. It also registers the
   * {@link SourcePosition}s where the names occur. If you introduce new model elements with identifiers, overwrite this
   * method so that it also returns the source positions of your new identifiers.
   */
  @Override
  protected Multimap<String, SourcePosition> getAllNameOccurrences(@NotNull ComponentTypeSymbol component) {
    Preconditions.checkNotNull(component);

    Multimap<String, SourcePosition> featureNameOccurrences = getFeatureNameOccurrences(component);

    // merge above
    Multimap<String, SourcePosition> allNameOccurrences = super.getAllNameOccurrences(component);
    featureNameOccurrences.forEach(allNameOccurrences::put);

    return allNameOccurrences;
  }

  /**
   * Collects all identifier names of features within a component type and registers the source code
   * positions where they occur.
   *
   * @param component The component type whose inner component type names should be collected.
   */
  protected Multimap<String, SourcePosition> getFeatureNameOccurrences(@NotNull ComponentTypeSymbol component) {
    Preconditions.checkNotNull(component);

    Multimap<String, SourcePosition> nameOccurrences = MultimapBuilder.hashKeys().arrayListValues().build();

    ((IVariableArcScope) component.getSpannedScope()).getLocalArcFeatureSymbols().forEach(
      innerT -> nameOccurrences.put(innerT.getName(), optSourcePosOf(innerT).orElse(new SourcePosition(-1, -1)))
    );
    return nameOccurrences;
  }
}
