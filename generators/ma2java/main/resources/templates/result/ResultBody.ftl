<#-- (c) https://github.com/MontiCore/monticore -->
<#import "/templates/util/Utils.ftl" as Utils>
<#import "/templates/result/InnerResultClass.ftl" as InnerResult>

<#macro printResultClassBody comp compHelper isTOPClass=false>
    <#assign compName = comp.getName()>
    <#assign portsOut = comp.getOutgoingPorts()>

    <#list portsOut as port>
        <@Utils.printMember type=compHelper.getRealPortTypeString(port) name=port.getName() visibility="private"/>
    </#list>

  public ${compName}Result<#if isTOPClass>TOP</#if>() {
    <#if comp.isPresentParentComponent()>super();</#if>
  }

    <#if comp.getAllOutgoingPorts()?? && (comp.getAllOutgoingPorts()?size > 0)>
      public ${compName}Result<#if isTOPClass>TOP</#if> (<#list comp.getAllOutgoingPorts() as port>${compHelper.getRealPortTypeString(port)} ${port.getName()}<#sep>, </#sep></#list>) {
        <#if comp.isPresentParentComponent()>
          super(<#list comp.getParent().getTypeInfo().getAllOutgoingPorts() as port>${port.getName()}</#list>);
        </#if>
        <#list portsOut as port>
          this.${port.getName()} = ${port.getName()};
        </#list>
      }
    </#if>

    <#list portsOut as port>
        <#assign name = port.getName()>
        <#assign type = compHelper.getRealPortTypeString(port)>
      public void set${name?cap_first}(${type} ${name}) { this.${name} = ${name}; }
      public ${type} get${name?cap_first}() { return this.${name}; }
    </#list>

  @Override
  public String toString() {
  String result = "[";
    <#list portsOut as port>
      result += "${port.getName()}: " + this.${port.getName()} + " ";
    </#list>
  return result + "]";
  }

    public ${compName}Result<@Utils.printFormalTypeParameters comp=comp/> merge(${compName}Result<@Utils.printFormalTypeParameters comp=comp/> other) {
    <#list comp.getAllOutgoingPorts() as port>
        <#assign name = port.getName()>
        if (other.get${name?cap_first}() != null) this.set${name?cap_first}(other.get${name?cap_first}());
    </#list>
        return this;
    }

    <#list comp.getInnerComponents() as innerComp>
        <@InnerResult.printInnerResultClass innerComp=innerComp compHelper=compHelper isTOPClass=isTOPClass/>
    </#list>
</#macro>