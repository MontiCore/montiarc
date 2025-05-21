<#-- (c) https://github.com/MontiCore/monticore -->
<#-- Assumed variables: ASTMACompilationUnit ast, boolean isTop -->
<#import "/montiarc/generator/ma2jsim/util/MethodNames.ftl" as MethodNames>
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>

/* (c) https://github.com/MontiCore/monticore */
<#if ast.isPresentPackage()>
  ${tc.include("montiarc.generator.Package.ftl", ast.getPackage())}
</#if>

<#assign comp=variant!ast.getArcComponentType().getSymbol()/>
<#-- @ftlvariable name="comp" type=" arcbasis._symboltable.ArcComponentTypeSymbol" -->

public class ${prefixes.deploy()}Mqtt${comp.getName()}<#if isTop>${suffixes.top()}</#if>
  extends montiarc.rte.deploy.MqttDeployment<${comp.getName()}${suffixes.comp()}> {

  public static void main(String[] args){
    new ${prefixes.deploy()}Mqtt${comp.getName()}().deploy(args);
  }

  @Override
  public ${comp.getName()}${suffixes.comp()} buildComponent() {
    return new ${comp.getName()}${suffixes.comp()}${suffixes.builder()}("${comp.getName()}")
      <#if variant??>
        <#list variant.getFeatureSymbolBooleanMap() as feature, value>
      .${prefixes.setterMethod()}${prefixes.feature()}${feature.getName()}(${value?c})
        </#list>
      </#if>
    .build();
  }

  @Override
  protected void connect(${comp.getName()}${suffixes.comp()} component, montiarc.rte.deploy.mqtt.SimpleMqtt mqtt)
    throws org.eclipse.paho.client.mqttv3.MqttException {
    <#list comp.getAllIncomingPorts() as port>
      <#if !port.getType().isGenericType()>
      mqtt.subscribe(getTopic("${comp.getName()}", "${port.getName()}${helper.portVariantSuffix(comp.getAstNode(), port)}"), str -> deserialize(str, <@Util.getPortTypeString port.getType()/>.class)
        .ifPresent(m -> component.${prefixes.port()}${port.getName()}${helper.portVariantSuffix(comp.getAstNode(), port)}().receive(montiarc.rte.msg.Message.of(m)))
      );
      </#if>
    </#list>
    <#list comp.getAllOutgoingPorts() as port>
      component.${prefixes.port()}${port.getName()}${helper.portVariantSuffix(comp.getAstNode(), port)}()
        .connect(new montiarc.rte.deploy.mqtt.MqttPort<>(mqtt, getTopic("${comp.getName()}", "${port.getName()}${helper.portVariantSuffix(comp.getAstNode(), port)}"), m -> serialize(m)));
    </#list>
  }
}
