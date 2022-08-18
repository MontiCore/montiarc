<#-- (c) https://github.com/MontiCore/monticore -->

<#import "/templates/util/Utils.ftl" as Utils>

<#macro printSetupRegion comp compHelper>
    <@printSetupMethod2 comp=comp compHelper=compHelper/>

    <@printSetSubcomponentVariablesMethod comp=comp compHelper=compHelper/>
    
    <@printSetPortVariablesMethod comp=comp compHelper=compHelper/>

    <@printInitializeSubcomponentsMethod comp=comp compHelper=compHelper/>
</#macro>


<#macro setupMethodName>setUp</#macro>
<#macro printSetupMethod2 comp compHelper>
  <#assign methodName><@setupMethodName/></#assign>
    public void ${methodName}(<@allPortsSortedAsHeaderParams comp=comp compHelper=compHelper/>) {
      <#if comp.isPresentParentComponent()>
        super.${methodName}();
      </#if>
    <#if comp.isAtomic()>
      <@setPortsMethodName/>(<#list compHelper.getLocalPortSymbolsSorted(comp) as port>${port.getName()}<#sep>, </#list>);
      this.initialize();
    <#else>
      <@setSubcomponentVariablesMethodName/>();
      <@setPortsMethodName/>(<#list compHelper.getLocalPortSymbolsSorted(comp) as port>${port.getName()}<#sep>, </#list>);
      <@initializeSubcomponentsMethodName/>();
    </#if>
    }

    <#if comp.hasPorts()>
      public void ${methodName}() {
        this.${methodName}(<#list comp.getPorts() as port>new <#if port.isDelayed()>Delayed<#else>Undelayed</#if>Port<>()<#sep>, </#list>);
      }
    </#if>
</#macro>


<#macro initializeSubcomponentsMethodName>initializeThisOrSubcomponents</#macro>
<#macro printInitializeSubcomponentsMethod comp compHelper>
  <#assign methodName><@initializeSubcomponentsMethodName/></#assign>
  public void ${methodName}() {
    <#if !comp.isAtomic()>
      // This component is not atomic, so there is nothing to explicitly initialize.
      // We just pass the call down to subcomponents
      <@Utils.forEachSubcomponent comp>.${methodName}();</@Utils.forEachSubcomponent>
    <#else>
      // This component is atomic, so there are no subcomponents to pass the call down to.
      // Instead, we must execute the initialize method
      this.initialize();
    </#if>
  }
</#macro>

<#macro setPortsMethodName>setPorts</#macro>
<#macro printSetPortVariablesMethod comp compHelper>
  <#assign methodName><@setPortsMethodName/></#assign>
  public void ${methodName}(<@allPortsSortedAsHeaderParams comp=comp compHelper=compHelper/>) {
    <#list compHelper.getLocalPortSymbolsSorted(comp)>
      <#items as port>
        this.${port.getName()} = ${port.getName()};
      </#items>
      <#else>
      // this component has no ports
    </#list>
    <#if !comp.isAtomic()>
      //subcomponent port and connectors setup
      <#list compHelper.getVarsForHiddenChannelsMappedToFullPortType(comp) as varName, type>
        ${type} ${varName} = new ${type}();
      </#list>

      <#list compHelper.transformMapMapToSortedListMap(compHelper.mapSubCompNameToPortVariableMap(comp)) as subCompName, paramsList>
        this.${subCompName}.${methodName}(<#list paramsList as param>${param}<#sep>, </#list>);
     </#list>
    </#if>
  }
</#macro>

<#macro setSubcomponentVariablesMethodName>setSubcomponentVariables</#macro>
<#macro printSetSubcomponentVariablesMethod comp compHelper>
    <#assign methodName><@setSubcomponentVariablesMethodName/></#assign>
  public void ${methodName}() {
    <#list comp.getSubComponents() as subcomponent>
      this.${subcomponent.getName()} = new ${compHelper.getSubComponentTypeName(subcomponent)}(<#list compHelper.getParamValues(subcomponent) as param>${param}<#sep>, </#sep></#list>);
      this.${subcomponent.getName()}.setInstanceName(this.getInstanceName() != null ? this.getInstanceName() + ".${subcomponent.getName()}" : "${subcomponent.getName()}");
      this.${subcomponent.getName()}.${methodName}();
    <#else>
      // This component is atomic, so there are no subcomponent variables to set up.
    </#list>
  }
</#macro>

<#macro allPortsSortedAsHeaderParams comp compHelper>
  <#list compHelper.getLocalPortSymbolsSorted(comp) as port>
    Port<${compHelper.getRealPortTypeString(port)}> ${port.getName()}<#sep>, </#sep>
  </#list>
</#macro>
