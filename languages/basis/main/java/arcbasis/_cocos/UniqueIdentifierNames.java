/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis._ast.ASTComponentType;
import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis._symboltable.IArcBasisScope;
import com.google.common.base.Preconditions;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import de.monticore.ast.ASTNode;
import de.monticore.symboltable.ISymbol;
import de.se_rwth.commons.SourcePosition;
import de.se_rwth.commons.logging.Log;
import montiarc.util.ArcError;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.function.BiPredicate;

import static java.util.stream.Collectors.joining;

/**
 * This coco assures that all identifier names within the scope of a component type are unique ([Hab16 B1], [RRW14 U3],
 * [Wo16r MU1]). If your language extends MontiArc and introduces new model elements with identifier names, you can
 * extend this class and overwrite {@link #getAllNameOccurrences(ComponentTypeSymbol)}} which collects all occurrences of
 * identifier names.
 */
public class UniqueIdentifierNames implements ArcBasisASTComponentTypeCoCo {

  @Override
  public void check(@NotNull ASTComponentType astComp) {
    Preconditions.checkNotNull(astComp);
    Preconditions.checkArgument(astComp.isPresentSymbol());

    ComponentTypeSymbol component = astComp.getSymbol();
    Multimap<String, SourcePosition> allNameOccurrences = getAllNameOccurrences(component);
    for(String name : allNameOccurrences.keySet()) {
      Collection<SourcePosition> currentNameOccurrences = allNameOccurrences.get(name);

      boolean allowedExceptionMatches = this.getAllowedExceptions(astComp.getSpannedScope()).stream()
        .anyMatch(predicate -> predicate.test(name, currentNameOccurrences));

      if(currentNameOccurrences.size() > 1 && !allowedExceptionMatches) {
        String positionsPrinted = currentNameOccurrences.stream().map(SourcePosition::toString).collect(joining(", "));
        Log.error(ArcError.UNIQUE_IDENTIFIER_NAMES.format(
          astComp.getSymbol().getFullName(),
          name,
          positionsPrinted
        ), astComp.get_SourcePositionStart(), astComp.get_SourcePositionEnd());
      }
    }
  }

  /**
   * This method collects all identifier names within the scope of a component type. It also registers the
   * {@link SourcePosition}s where the names occur. If you introduce new model elements with identifiers, overwrite this
   * method so that it also returns the source positions of your new identifiers.
   */
  protected Multimap<String, SourcePosition> getAllNameOccurrences(@NotNull ComponentTypeSymbol component) {
    Preconditions.checkNotNull(component);

    Multimap<String, SourcePosition> innerCompNameOccurrences = getInnerComponentNameOccurrences(component);
    Multimap<String, SourcePosition> subCompNameOccurrences = getSubComponentNameOccurrences(component);
    Multimap<String, SourcePosition> portNameOccurrences = getPortNameOccurrences(component);
    Multimap<String, SourcePosition> fieldNameOccurrences = getFieldNameOccurrences(component);
    Multimap<String, SourcePosition> typeParamNameOccurrences = getTypeParameterNameOccurrences(component);

    // merge above
    Multimap<String, SourcePosition> allNameOccurrences = MultimapBuilder.hashKeys().arrayListValues().build();
    innerCompNameOccurrences.forEach(allNameOccurrences::put);
    subCompNameOccurrences.forEach(allNameOccurrences::put);
    portNameOccurrences.forEach(allNameOccurrences::put);
    fieldNameOccurrences.forEach(allNameOccurrences::put);
    typeParamNameOccurrences.forEach(allNameOccurrences::put);

    return allNameOccurrences;
  }

  /**
   * Collects all identifier names of the inner component types within a component type and registers the source code
   * positions where they occur.
   * @param component The component type whose inner component type names should be collected.
   */
  protected Multimap<String, SourcePosition> getInnerComponentNameOccurrences(@NotNull ComponentTypeSymbol component) {
    Preconditions.checkNotNull(component);

    Multimap<String, SourcePosition> nameOccurrences = MultimapBuilder.hashKeys().arrayListValues().build();
    component.getInnerComponents().forEach(
      innerT -> nameOccurrences.put(innerT.getName(), optSourcePosOf(innerT).orElse(new SourcePosition(-1, -1)))
    );
    return nameOccurrences;
  }

  /**
   * Collects all identifier names of the subcomponents of a component type and registers the source code positions
   * where they occur.
   */
  protected Multimap<String, SourcePosition> getSubComponentNameOccurrences(@NotNull ComponentTypeSymbol component) {
    Preconditions.checkNotNull(component);

    Multimap<String, SourcePosition> nameOccurrences = MultimapBuilder.hashKeys().arrayListValues().build();
    component.getSubComponents().forEach(
      inst -> nameOccurrences.put(inst.getName(), optSourcePosOf(inst).orElse(new SourcePosition(-1, -1)))
    );
    return nameOccurrences;
  }

  /**
   * Collects all identifier names of the ports of a component type and registers the source code positions where they
   * occur.
   */
  protected Multimap<String, SourcePosition> getPortNameOccurrences(@NotNull ComponentTypeSymbol component) {
    Preconditions.checkNotNull(component);

    Multimap<String, SourcePosition> nameOccurrences = MultimapBuilder.hashKeys().arrayListValues().build();
    component.getPorts().forEach(
      port -> nameOccurrences.put(port.getName(), optSourcePosOf(port).orElse(new SourcePosition(-1, -1)))
    );
    return nameOccurrences;
  }

  /**
   * Collects all identifier names of fields and configuration parameters of a component type and registers the source
   * code positions where they occur.
   */
  protected Multimap<String, SourcePosition> getFieldNameOccurrences(@NotNull ComponentTypeSymbol component) {
    Preconditions.checkNotNull(component);

    Multimap<String, SourcePosition> nameOccurrences = MultimapBuilder.hashKeys().arrayListValues().build();
    component.getFields().forEach(
      field -> nameOccurrences.put(field.getName(), optSourcePosOf(field).orElse(new SourcePosition(-1, -1)))
    );
    return nameOccurrences;
  }

  /**
   * Collects all identifier names of the type parameters of a component type and registers the source code positions
   * where they occur.
   */
  protected Multimap<String, SourcePosition> getTypeParameterNameOccurrences(@NotNull ComponentTypeSymbol component) {
    Preconditions.checkNotNull(component);

    Multimap<String, SourcePosition> nameOccurrences = MultimapBuilder.hashKeys().arrayListValues().build();
    component.getTypeParameters().forEach(
      tParam -> nameOccurrences.put(tParam.getName(), optSourcePosOf(tParam).orElse(new SourcePosition(-1, -1)))
    );
    return nameOccurrences;
  }


  /**
   * Defines exceptions when it is okay that there are multiple occurrences of the same name. To add your own
   * exceptions, overwrite this method.
   * @param scope The scope in which all the names were found. May be used to check whether there is an allowed
   *              exception to the rule that there should not be name duplicates.
   */
  protected Collection<BiPredicate<String, Collection<SourcePosition>>> getAllowedExceptions(@NotNull IArcBasisScope scope) {
    HashSet<BiPredicate<String, Collection<SourcePosition>>> allowedExceptions = new HashSet<>();
    // This method currently does nothing but is a possible extension point to declare Exceptions.

    return allowedExceptions;
  }

  /**
   * When a symbol has an {@link ASTNode} and the ASTNode has a {@link SourcePosition}, that SourcePosition is returned
   * contained in an optional. Otherwise, an empty Optional is returned.
   */
  protected static Optional<SourcePosition> optSourcePosOf(@NotNull ISymbol sym) {
    Preconditions.checkNotNull(sym);

    if(!sym.isPresentAstNode()) {
      return Optional.empty();
    }
    ASTNode node = sym.getAstNode();

    if(!node.isPresent_SourcePositionStart()) {
      return Optional.empty();
    }
    return Optional.of(node.get_SourcePositionStart());
  }
}
