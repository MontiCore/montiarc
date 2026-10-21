<#-- (c) https://github.com/MontiCore/monticore -->
<#-- Assumed variables: ASTMACompilationUnit ast, boolean isTop -->
<#import "/montiarc/generator/ma2jsim/util/MethodNames.ftl" as MethodNames>
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>

/* (c) https://github.com/MontiCore/monticore */
<#if ast.isPresentPackage()>
  ${tc.include("montiarc.generator.Package.ftl", ast.getPackage())}
</#if>

<#assign comp=ast.getArcComponentType().getSymbol()/>
<#-- @ftlvariable name="comp" type="de.monticore.symbols.compsymbols._symboltable.ComponentTypeSymbol" -->

@de.se_rwth.commons.Generated("montiarc.generators.ma2jsim")
public class ${prefixes.deploy()}Mqtt${comp.getName()}<#if isTop>${suffixes.top()}</#if>
  extends montiarc.rte.deploy.MqttDeployment<${comp.getName()}${suffixes.comp()}> {

  public static void main(String[] args){
    new ${prefixes.deploy()}${comp.getName()}(new ${prefixes.deploy()}Mqtt${comp.getName()}()).deploy(args);
  }

  @Override
  protected void setupMqttConnections(${comp.getName()}${suffixes.comp()} component)
    throws org.eclipse.paho.client.mqttv3.MqttException {
    <#list comp.getAllIncomingPorts() as port>
      <#if !port.getType().isGenericType()>
      mqtt.subscribe(getTopic("${comp.getName()}", "${port.getName()}"), str -> deSerializer.deserialize(str, <@Util.getPortTypeReference port.getType()/>)
        .ifPresent(m -> component.${prefixes.port()}${port.getName()}().receive(montiarc.rte.msg.Message.of(m)))
      );
      </#if>
    </#list>
    <#list comp.getAllOutgoingPorts() as port>
      component.${prefixes.port()}${port.getName()}()
        .connect(new montiarc.rte.deploy.mqtt.MqttOutPort<>(mqtt, getTopic("${comp.getName()}", "${port.getName()}"), deSerializer));
    </#list>
  }
}
