<#-- (c) https://github.com/MontiCore/monticore -->
<#import "/templates/util/Utils.ftl" as Utils>
<#import "/templates/input/InnerInputClass.ftl" as InnerInput>

<#macro printInputClassBody comp compHelper isTOPClass=false>
    <#assign compName = comp.getName()>
    <#assign portsIn = comp.getIncomingPorts()>

    <#list portsIn as port>
        <@Utils.printMember type=compHelper.getRealPortTypeString(port) name=port.getName() visibility="private"/>
    </#list>

  public ${compName}Input<#if isTOPClass>TOP</#if>() {
    <#if comp.isPresentParentComponent()>super();</#if>
  }

    <#if comp.getAllIncomingPorts()?? && (comp.getAllIncomingPorts()?size > 0)>
      public ${compName}Input<#if isTOPClass>TOP</#if> (<#list comp.getAllIncomingPorts() as port>${compHelper.getRealPortTypeString(port)} ${port.getName()}<#sep>, </#sep></#list>) {
        <#if comp.isPresentParentComponent()>
          super(<#list comp.getParent().getTypeInfo().getAllIncomingPorts() as port>${port.getName()}</#list>);
        </#if>
        <#list portsIn as port>
          this.${port.getName()} = ${port.getName()};
        </#list>
      }
    </#if>

    <#list portsIn as port>
      <#assign name = port.getName()>
      <#assign type = compHelper.getRealPortTypeString(port)>
      public void set${name?cap_first}(${type} ${name}) { this.${name} = ${name}; }
      public ${type} get${name?cap_first}() { return this.${name}; }
    </#list>

  @Override
  public String toString() {
  String result = "[";
    <#list portsIn as port>
      result += "${port.getName()}: " + this.${port.getName()} + " ";
    </#list>
  return result + "]";
  }

    <#list comp.getInnerComponents() as innerComp>
      <@InnerInput.printInnerInputClass innerComp=innerComp compHelper=compHelper isTOPClass=isTOPClass/>
    </#list>
</#macro>