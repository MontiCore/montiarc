/* (c) https://github.com/MontiCore/monticore */
package comfortablearc._ast;

/**
 * Extends the {@link ASTFullyConnectedComponentInstantiationBuilderTOP} with
 * utility functions for easy construction of
 * {@link ASTFullyConnectedComponentInstantiation} nodes.
 */
public class ASTFullyConnectedComponentInstantiationBuilder extends ASTFullyConnectedComponentInstantiationBuilderTOP {

  @Override
  public ASTFullyConnectedComponentInstantiationBuilder setComponentInstance(int index, String instance) {
    super.setComponentInstance(index, instance);
    return this.realBuilder;
  }

  @Override
  public ASTFullyConnectedComponentInstantiationBuilder setComponentInstanceList(String... instances) {
    super.setComponentInstanceList(instances);
    return this.realBuilder;
  }

  @Override
  public ASTFullyConnectedComponentInstantiationBuilder addInstance(String instance) {
    super.addInstance(instance);
    return this.realBuilder;
  }

  @Override
  public ASTFullyConnectedComponentInstantiationBuilder addAllInstances(String... instances) {
    super.addAllInstances(instances);
    return this.realBuilder;
  }

  @Override
  public ASTFullyConnectedComponentInstantiationBuilder addInstance(int index, String instance) {
    super.addInstance(index, instance);
    return this.realBuilder;
  }

  @Override
  public ASTFullyConnectedComponentInstantiationBuilder addAllInstances(int index, String... instances) {
    super.addAllInstances(index, instances);
    return this.realBuilder;
  }
}