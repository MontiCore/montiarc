/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator.util;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@SuppressWarnings("unused")
public class Helper {

  private TypeHelper typeHelper = new TypeHelper();
  private VariantHelper variantHelper = new VariantHelper();
  private BehaviorHelper behaviorHelper = new BehaviorHelper();
  private ModeHelper modeHelper = new ModeHelper();
  private ComponentHelper componentHelper = new ComponentHelper();
  private MaUnitHelper maUnitHelper = new MaUnitHelper();

  public TypeHelper getTypeHelper() {
    return typeHelper;
  }

  public VariantHelper getVariantHelper() {
    return variantHelper;
  }

  public BehaviorHelper getBehaviorHelper() {
    return behaviorHelper;
  }

  public ModeHelper getModeHelper() {
    return modeHelper;
  }

  public ComponentHelper getComponentHelper() {
    return componentHelper;
  }

  public MaUnitHelper getMaUnitHelper(){
    return maUnitHelper;
  }

  public List<Object> asList(Object... args) {
    return List.of(args);
  }

  public <T> List<T> streamToList(Stream<T> stream) {
    return stream.collect(Collectors.toList());
  }
}
