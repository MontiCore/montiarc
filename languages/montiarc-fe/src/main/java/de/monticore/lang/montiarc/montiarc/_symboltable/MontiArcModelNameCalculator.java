package de.monticore.lang.montiarc.montiarc._symboltable;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

import de.monticore.lang.montiarc.cocos.ComponentNameIsCapitalized;
import de.monticore.lang.montiarc.cocos.PackageLowerCase;
import de.monticore.symboltable.SymbolKind;
import de.se_rwth.commons.Joiners;
import de.se_rwth.commons.Splitters;

/**
 * Helps loading inner components, by mapping their full-qualified names to the full-qualified name
 * of the most outer component of the file the inner one is defined in. This way the SymTab knows
 * which file to load. By convention, package names must be lower-case (see {@link PackageLowerCase}
 * ) and component names must start upper-case (see {@link ComponentNameIsCapitalized}). This ensures,
 * that we can calculate the most outer component, by searching for the first upper-case part of a
 * full-qualified name, e.g.:<br/>
 * a.b.C.D.E -> a.b.C
 *
 * @author Robert Heim, Michael von Wenckstern
 */
public class MontiArcModelNameCalculator
    extends de.monticore.CommonModelNameCalculator {
    
  @Override
  public Set<String> calculateModelNames(final String name, final SymbolKind kind) {
    final Set<String> calculatedModelNames = new LinkedHashSet<>();
    
    if (ComponentSymbol.KIND.isKindOf(kind)) {
      calculatedModelNames.addAll(calculateModelNameForComponent(name));
    }
    else if (PortSymbol.KIND.isKindOf(kind)) {
      calculatedModelNames.addAll(calculateModelNameForPort(name));
    }
    else if (ConnectorSymbol.KIND.isKindOf(kind)) {
      calculatedModelNames.addAll(calculateModelNameForConnector(name));
    }
    else if (ComponentInstanceSymbol.KIND.isKindOf(kind)) {
      calculatedModelNames.addAll(calculateModelNameForComponentInstance(name));
    }
    
    return calculatedModelNames;
  }
  
  protected Set<String> calculateModelNameForComponent(String name) {
    List<String> parts = Splitters.DOT.splitToList(name);
    Set<String> ret = new LinkedHashSet<>();
    
    for (int i = 0; i < parts.size(); i++) {
      char[] c = parts.get(i).toCharArray();
      if (Character.isUpperCase(c[0])) {
        ret.add(Joiners.DOT.join(parts.subList(0, i + 1)));
      }
    }
    
    return Collections.unmodifiableSet(ret);
  }
  
  protected Set<String> calculateModelNameForPort(String name) {
    List<String> parts = Splitters.DOT.splitToList(name);
    if (parts.size() > 1) {
      String modelName = Joiners.DOT.join(parts.subList(0, parts.size() - 1));
      return ImmutableSet.<String> builder()
          .addAll(calculateModelNameForComponent(modelName))
          .build();
    }
    return ImmutableSet.of();
  }
  
  protected Set<String> calculateModelNameForComponentInstance(String name) {
    List<String> parts = Splitters.DOT.splitToList(name);
    if (parts.size() > 1) {
      return calculateModelNameForComponent(Joiners.DOT.join(parts.subList(0, parts.size() - 1)));
    }
    return ImmutableSet.of();
  }
  
  protected Set<String> calculateModelNameForConnector(String name) {
    List<String> parts = Splitters.DOT.splitToList(name);
    if (parts.size() == 1) {
      return calculateModelNameForComponent(Joiners.DOT.join(parts.subList(0, parts.size() - 1)));
    }
    else if (parts.size() >= 2) {
      return ImmutableSet.<String> builder()
          .addAll(
              calculateModelNameForComponent(Joiners.DOT.join(parts.subList(0, parts.size() - 1))))
          .addAll(
              calculateModelNameForComponent(Joiners.DOT.join(parts.subList(0, parts.size() - 2))))
          .build();
    }
    return ImmutableSet.of();
  }
  
}
