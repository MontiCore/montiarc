<#-- (c) https://github.com/MontiCore/monticore -->
<#import "/templates/Comments.ftl" as Comment>
<#import "/templates/behavior/Behavior.ftl" as Behavior>
<#import "/templates/behavior/ComposedBehavior.ftl" as Events>
<#import "/templates/Ports.ftl" as Port>
<#import "/templates/Header.ftl" as Header>
<#-- Prints initializing of the decomposed structure -->
<#-- @ftlvariable name="component" type="arcbasis._symboltable.ComponentTypeSymbol" -->
<#-- @ftlvariable name="util" type="montiarc.generator.ma2kotlin.codegen.TemplateUtilities" -->
<#macro attributes>
    <#list component.getFields()?filter(e -> !component.getParameter(e.getName()).isPresent())>
        // fields
        <#items as field>
            <@addField field=field/>
        </#items>

    </#list>
    <#if util.getModeTool().streamModes(component.getAstNode()).count() != 0>
        <@addModeAutomaton/>

    </#if>
</#macro>
<#macro init>
    <#if component.hasPorts()>
    // ports
    </#if>
    <#list component.getIncomingPorts() as port>
        <@addPort inout="In" port=port/>
    </#list>
    <#list component.getOutgoingPorts() as port>
        <@addPort inout="Out" port=port/>
    </#list>
    <#list component.getSubComponents()>

    // subcomponents
        <#items as subComponent>
            <@addStaticSubcomponent subcomponent = subComponent/>
        </#items>
    </#list>
    <#list component.getAstNode().getConnectors()>

    // connectors
        <#items as connector>
            <@addConnector connector=connector postfix=""/>
        </#items>
    </#list>
    <#list util.getModeTool().streamModes(component.getAstNode()).toArray()>

    // modes
        <#items as mode>
            <@addMode mode=mode/>
        </#items>
    // transitions
        <#list util.getModeTransitions(component) as transition>
            <@addTransition transition=transition/>
        </#list>

    // embrace initial mode
    reconfigure(modeAutomaton.currentMode)
    </#list>
</#macro>
<#macro addPort inout port>
    <@Comment.printOf node=port.getAstNode()/>
    this.add${inout}Port(Port.make<${util.getTypes().printType(port.getType())}>("${port.getName()}"))
</#macro>
<#macro addStaticSubcomponent subcomponent>
    <@Comment.printOf node=subcomponent.getAstNode()/>
    component(<@createSubcomponent subcomponent=subcomponent/>)
</#macro>
<#macro addDynamicSubcomponent subcomponent permanent>
    <@Comment.printOf node=subcomponent.getAstNode()/>
    create(<@createSubcomponent subcomponent=subcomponent/>, permanent = ${permanent})
</#macro>
<#macro createSubcomponent subcomponent>
${subcomponent.getType().getTypeInfo().getName()}("${subcomponent.getName()}"<#rt>
    <#list subcomponent.getArguments() as parameter>
      , ${util.printExpression(parameter)}<#t>
    </#list>
    )<#t>
</#macro>
<#macro addConnector connector postfix>
    <@Comment.printOf node=connector/>
    <#list connector.getTargetList() as target>
    connect(<@Port.printAccess port=connector.getSource()/>, <@Port.printAccess port=target/>${postfix})
    </#list>
</#macro>
<#macro addMode mode>
    <#list mode.getDeclarations() as node>
        <@Comment.printOf node=node/>
    </#list>
    modeAutomaton.addMode("${mode.getName()}"<#rt>
    <#if mode.isInitial(component)>
        , initial = true<#t>
    </#if>
      ) {<#lt>
      // remove non-static elements from other modes
      removeTemporaryElements()

      // add the modes elements and connectors
        <#list mode.getSubComponents(util.getNull()) as subComponent>
${"  "}<@addDynamicSubcomponent subcomponent = subComponent permanent="false"/><#rt>
        </#list>
        <#list mode.getPorts(util.getNull())?filter(p-> p.isIncoming()) as port>
${"  "}<@addPort inout="Temporary" port=port/><#rt>
        </#list>
        <#list mode.getConnectors(util.getNull()) as connector>
${"  "}<@addConnector connector=connector postfix=", permanent = false"/><#rt>
        </#list>
    }

</#macro>
<#macro addModeAutomaton>
  // properties for modes
  private val guard = object : IGuardInterface {
    override val subcomponents
      get() = this@${component.getName()}.subcomponents
    override val inputPorts
      get() = this@${component.getName()}.inputPorts
    val event: ${Events.getMessageType()}
      get() = lastEvent?: throw RuntimeException("There are ne events for ${component.getName()} $instanceName yet")
    override var lastEvent: ${Events.getMessageType()}? = null
  }
  private val modeAutomaton = ModeAutomaton(guard)
</#macro>
<#macro addField field>
    <#local node=field.getAstNode()>
    <@Comment.printOf node=node/>
  private var ${field.getName()}Field : ${util.getTypes().printType(field.getType())} = ${util.printExpression(node.getInitial())}
</#macro>
<#macro addTransition transition>
    <@Comment.printOf node=transition/>
    modeAutomaton.addTransition("${transition.getSourceName()}", "${transition.getTargetName()}"<#rt>
    <#if util.getStateTool().getReaction(transition).isPresent()>
        , reaction = {{<#lt>
        ${util.printStatement(3, util.getStateTool().getReaction(transition).get())}<#lt>
    }}<#rt>
    </#if>
    <#list util.getStateTool().getTriggers(transition)>
        , trigger = setOf(<#t>
        <#items as port>
            <@Port.printAccess port=port/>
        </#items>
        )<#t>
    </#list>
    <#lt>) {
    <#if util.getStateTool().getGuard(transition).isPresent()>
      (${util.printExpression(util.getStateTool().getGuard(transition).get())})!!
    <#else>
      true
    </#if>
    }
</#macro>