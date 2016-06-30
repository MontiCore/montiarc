package de.monticore.lang.montiarc.montiarc._symboltable;

import java.util.LinkedHashSet;
import java.util.Set;

import de.monticore.symboltable.MutableScope;
import de.monticore.symboltable.Symbol;
import de.monticore.symboltable.resolving.ResolvingFilter;
import de.se_rwth.commons.logging.Log;

/**
 * Created by Michael von Wenckstern on 23.05.2016.
 *
 * @author Michael von Wenckstern
 */
public class MontiArcExpandedComponentInstanceSymbolCreator {

  protected LinkedHashSet<ComponentSymbol> topComponents = new LinkedHashSet<>();

  public MontiArcExpandedComponentInstanceSymbolCreator() {
  }

  /**
   * @param topComponent this is the scope where the top-level component is defined in
   */
  public void createInstances(ComponentSymbol topComponent) {
    if (!topComponents.add(topComponent)) {
      Log.info("instances for top component + " + topComponent.getFullName() +
              " is already created",
          MontiArcExpandedComponentInstanceSymbolCreator.class.toString());
      return;
    }

    if (!topComponent.getFormalTypeParameters().isEmpty()) {
      Log.info("expanded component instance is not created, b/c top level has"
              + " generic parameters and can, therefore, not be instantiated",
          MontiArcExpandedComponentInstanceSymbolCreator.class.toString());
      return;
    }

    final Set<ResolvingFilter<? extends Symbol>> filters =
        topComponent.getSpannedScope().getResolvingFilters();

    // make first letter to lower case
    // this is needed so that you can differentiate between ComponentDefinition.Port
    // and between ComponentInstance.Port (ComponentInstance has small letter and
    // ComponentDefinition has capital letter)
    String name = topComponent.getName();
    if (name.length() > 1) {
      name = Character.toLowerCase(name.charAt(0)) + name.substring(1);
    }
    else {
      name = Character.toLowerCase(name.charAt(0)) + "";
    }

    ExpandedComponentInstanceBuilder builder =
        createInstance(topComponent, filters)
            .setName(name);

    final ExpandedComponentInstanceSymbol sym = builder.addResolvingFilters(filters).build();
    ((MutableScope) topComponent.getEnclosingScope()).add(sym);
  }

  protected ExpandedComponentInstanceBuilder createInstance(ComponentSymbol cmp, final Set<ResolvingFilter<? extends Symbol>> filters) {
    // TODO resolve generics and parameters
    //    System.err.println("create instance for: " + cmp.getName() + " [" + cmp.getFullName() + "]");
    ExpandedComponentInstanceBuilder builder =
        ExpandedComponentInstanceSymbol.builder()
            .setSymbolReference(new ComponentSymbolReference(cmp.getName(),
                cmp.getEnclosingScope()))
            .addPorts(cmp.getPorts())
            .addConnectors(cmp.getConnectors());

    // add sub components
    for (ComponentInstanceSymbol inst : cmp.getSubComponents()) {
      //      System.err.println("would create now: " + inst.getName() + "[" + inst.getComponentType().getFullName() + "]");
      builder.addSubComponent(
          createInstance(inst.getComponentType(), filters)
              .setName(inst.getName())
              .addActualTypeArguments(inst.getComponentType().getFormalTypeParameters(),
                  inst.getComponentType().getActualTypeArguments()).addResolvingFilters(filters).build());
    }

    // add inherited ports and sub components
    for (ComponentSymbol superCmp = cmp;
         superCmp.getSuperComponent().isPresent();
         superCmp = superCmp.getSuperComponent().get()) {
      builder.addPortsIfNameDoesNotExists(
          superCmp.getSuperComponent().get().getPorts(),
          superCmp.getSuperComponent().get().getFormalTypeParameters(),
          superCmp.getSuperComponent().get().getActualTypeArguments());
      builder.addConnectorsIfNameDoesNotExists(superCmp.getSuperComponent().get().getConnectors());
      superCmp.getSuperComponent().get().getSubComponents().stream().forEachOrdered(
          inst -> builder.addSubComponentIfNameDoesNotExists(
              createInstance(inst.getComponentType(), filters).setName(inst.getName())
                  .addActualTypeArguments(inst.getComponentType().getFormalTypeParameters(),
                      inst.getComponentType().getActualTypeArguments())
                  .addResolvingFilters(filters).build())
      );
    }

    return builder;
  }

}
