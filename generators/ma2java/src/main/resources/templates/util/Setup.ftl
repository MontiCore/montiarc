<#-- (c) https://github.com/MontiCore/monticore -->

<#import "/templates/util/Utils.ftl" as Utils>

<#-- Prints the setup() method for both atomic and composed components. -->
<#macro printSetupMethod comp compHelper>
  @Override
  public void setUp() {
  <#if comp.isPresentParentComponent()>
    <#lt>    super.setUp();
  </#if>
  <#t>
  <#if !comp.isAtomic()>
      <#assign subComponents = comp.getSubComponents()>
      <#list subComponents as subcomponent>
        <#lt>    this.${subcomponent.getName()} = new ${compHelper.getSubComponentTypeName(subcomponent)}(<#list compHelper.getParamValues(subcomponent) as param>${param}<#sep>, </#sep></#list>);
        <#lt>    this.${subcomponent.getName()}.setUp();
      </#list>
  </#if>

    // incoming ports setup
    <#assign portsIn = comp.getIncomingPorts()>
    <#list portsIn as port>
        <#lt>    this.${port.getName()} = new Port<${compHelper.getRealPortTypeString(port)}>();
    </#list>

    // outgoing ports setup
    <#assign portsOut = comp.getOutgoingPorts()>
    <#list portsOut as port>
        <#lt>    this.${port.getName()} = new Port<${compHelper.getRealPortTypeString(port)}>();
    </#list>

    // set up connectors
    <#list comp.getAstNode().getConnectors() as connector>
        <#list compHelper.getConnectorSetupCalls(connector) as call>
        <#lt>    ${call}
        </#list>
    </#list>

    <#if comp.isAtomic()>
      <#lt>    this.initialize();
    </#if>
  }
</#macro>


<#macro printSetupRegion comp compHelper>
    <@printSetupMethod2 comp=comp compHelper=compHelper/>

    <@printSetSubcomponentVariablesMethod comp=comp compHelper=compHelper/>
    
    <@printSetPortVariablesMethod comp=comp compHelper=compHelper/>

    <@printInitializeSubcomponentsMethod comp=comp compHelper=compHelper/>

    <@printConnectOutgoingPortForwardsMethod comp=comp compHelper=compHelper/>

    <@printConnectHiddenChannelsMethod comp=comp compHelper=compHelper/>

    <@printConnectIncomingPortForwardsMethod comp=comp compHelper=compHelper/>
</#macro>


<#macro setupMethodName>setUp</#macro>
<#macro printSetupMethod2 comp compHelper>
  <#assign methodName><@setupMethodName/></#assign>
    @Override
    public void ${methodName}() {
      <#if comp.isPresentParentComponent()>
        super.${methodName}();
      </#if>
    <#if comp.isAtomic()>
      <@setPortVariablesMethodName/>();
      this.initialize();
    <#else>
      <@setSubcomponentVariablesMethodName/>();
      <@setPortVariablesMethodName/>();
      <@initializeSubcomponentsMethodName/>();
      <@connectOutgoingPortForwardsMethodName/>();
      <@connectHiddenChannelsMethodName/>();
      <@connectIncomingPortForwardsMethodName/>();
    </#if>
    }
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


<#macro connectOutgoingPortForwardsMethodName>connectOutgoingPortForwardsRecursively</#macro>
<#macro printConnectOutgoingPortForwardsMethod comp compHelper>
    <#assign methodName><@connectOutgoingPortForwardsMethodName/></#assign>
    public void ${methodName}() {
      <@Utils.forEachSubcomponent comp>.${methodName}();</@Utils.forEachSubcomponent>

      <#list compHelper.getOutgoingPortForwards(comp) as connector>
        <#list compHelper.getConnectorSetupCalls(connector) as call>${call}</#list>
      </#list>
    }
</#macro>


<#macro connectHiddenChannelsMethodName>connectHiddenChannelsRecursively</#macro>
<#macro printConnectHiddenChannelsMethod comp compHelper>
  <#assign methodName><@connectHiddenChannelsMethodName/></#assign>
  public void ${methodName}() {
    <@Utils.forEachSubcomponent comp>.${methodName}();</@Utils.forEachSubcomponent>

    <#list compHelper.getHiddenChannels(comp) as connector>
      <#list compHelper.getConnectorSetupCalls(connector) as call>${call}</#list>

    </#list>
  }  
</#macro>


<#macro connectIncomingPortForwardsMethodName>connectIncomingPortForwardsRecursively</#macro>
<#macro printConnectIncomingPortForwardsMethod comp compHelper>
  <#assign methodName><@connectIncomingPortForwardsMethodName/></#assign>
  public void ${methodName}() {
    <#list compHelper.getIncomingPortForwards(comp) as connector>
        <#list compHelper.getConnectorSetupCalls(connector) as call>${call}</#list>
    </#list>

    <@Utils.forEachSubcomponent comp>.${methodName}();</@Utils.forEachSubcomponent>
  }
</#macro>


<#macro setPortVariablesMethodName>setPortVariables</#macro>
<#macro printSetPortVariablesMethod comp compHelper>
  <#assign methodName><@setPortVariablesMethodName/></#assign>
  public void ${methodName}() {
    // incoming ports, if present, are either connected later or set to empty in the init method.

    <#list comp.getOutgoingPorts()>
      // outgoing ports setup
      <#items as port>
        this.${port.getName()} = new Port<${compHelper.getRealPortTypeString(port)}>();
      </#items>
      <#else>
      // this component has no outgoing ports
    </#list>
    <#if !comp.isAtomic()>

    //subcomponent ports setup
    <@Utils.forEachSubcomponent comp>.${methodName}();</@Utils.forEachSubcomponent>
    </#if>
  }
</#macro>


<#macro setSubcomponentVariablesMethodName>setSubcomponentVariables</#macro>
<#macro printSetSubcomponentVariablesMethod comp compHelper>
    <#assign methodName><@setSubcomponentVariablesMethodName/></#assign>
  public void ${methodName}() {
    <#list comp.getSubComponents() as subcomponent>
      this.${subcomponent.getName()} = new ${compHelper.getSubComponentTypeName(subcomponent)}(<#list compHelper.getParamValues(subcomponent) as param>${param}<#sep>, </#sep></#list>);
      this.${subcomponent.getName()}.${methodName}();
    <#else>
      // This component is atomic, so there are no subcomponent variables to set up.
    </#list>
  }
</#macro>
