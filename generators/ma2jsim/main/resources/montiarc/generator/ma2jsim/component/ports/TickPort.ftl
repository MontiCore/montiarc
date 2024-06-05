<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>
protected montiarc.rte.port.ITimeAwareInPort<?> tickPort = null;
public montiarc.rte.port.ITimeAwareInPort<?> getTickPort() {
  return this.tickPort;
}
