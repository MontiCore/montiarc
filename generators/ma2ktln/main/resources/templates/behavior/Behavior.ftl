<#-- (c) https://github.com/MontiCore/monticore -->
<#import "/templates/Comments.ftl" as Comment>
<#import "/templates/Header.ftl" as Header>
<#import "/templates/Ports.ftl" as Ports>
<#import "/templates/behavior/ComposedBehavior.ftl" as Composed>
<#import "/templates/behavior/StatechartBehavior.ftl" as Stated>
<#import "/templates/behavior/AtomicBehavior.ftl" as Atomic>
<#-- Prints initializing of the decomposed structure -->
<#-- @ftlvariable name="component" type="arcbasis._symboltable.ComponentTypeSymbol" -->
<#-- @ftlvariable name="util" type="montiarc.generator.ma2kotlin.codegen.TemplateUtilities" -->
<#-- @ftlvariable name="chart" type="arcautomaton._ast.ASTArcStatechart" -->
<#macro init>
    <#if util.getStateTool().getStatechart(component).isPresent()>
        <#list util.getStateTool().getStatechart(component).get().streamInitialStatements().toArray()>
    // initial behavior
            <#items as statement>
${util.printStatement(2, statement)}
            </#items>
            <#if !util.getTiming(component)?starts_with("untimed")>
    tickOutputs()
            </#if>

        </#list>
    <#elseif !util.getComputes(component)?has_content && component.isAtomic()>
    Interface(this, null).also{ behaviorImplementation.initialize(it) }.pushAll()
        <#if !util.getTiming(component)?starts_with("untimed")>
    tickOutputs()
        </#if>

    </#if>
</#macro>
<#macro printBehavior>
  override suspend fun behavior() {
    <@init/>
    ${util.getTiming(component)}Schedule(inputPorts<#if component.isDecomposed()>, subcomponents</#if>).collect { event ->
    <#if component.isAtomic()>
        <@Atomic.printSchedule/>
    <#else>
        <@Composed.printSchedule/>
    </#if>
    }
  }
</#macro>
<#macro printRequiredAttributes>
    <#if util.getStateTool().getStatechart(component).isPresent()>
        <@Stated.printRequiredAttributes/>
    <#elseif !util.getComputes(component)?has_content && component.isAtomic()>

  <@printBehaviorDelegation/>
    </#if>
</#macro>
<#macro printBehaviorDelegation>
  // if there is no statechart or compute, the behavior should be delegated
  private val behaviorImplementation = ${component.getName()}Impl(<#rt>
    <#list component.getParameters() as parameter>
        ${parameter.getName()}Field<#sep>, </#sep><#t>
    </#list>
    )<#lt>

    <@printInterfaceClass/>
</#macro>
<#macro printInterfaceClass>
  /**
   * this class provides context for hand-written behavior and allows access to port values
   */
  class Interface(private val component:IComponent, private val event:${Composed.getMessageType()}?) {
    private val outputs: MutableList< Pair< String, Message>> = java.util.LinkedList()
    /**
     * Sends all values that have been assigned to any output port.
     * It is called after every compute, but may also be called in compute to send multiple Messages
     */
    suspend fun pushAll() {
      outputs.forEach { pair -> component.getOutputPort(pair.first).pushMsg(pair.second) }
      outputs.clear()
    }
  <#list component.getAllIncomingPorts() as port>
      <@Comment.printOf node=port.getAstNode()/>
      <#local valType = util.getTypes().printType(port.getType())>
      <#if !util.getTiming(component) ?starts_with("sync")>
          <#local valType = valType + "?">
    fun isPresent${port.getName()?cap_first}() = event?.isFor(component.getInputPort("${port.getName()}"))?:false
      </#if>
    val ${port.getName()}: ${valType}
      get() = event?.get(component.getInputPort("${port.getName()}"))?.payload as ${valType}
  </#list>
  <#list component.getAllOutgoingPorts() as port>
      <@Comment.printOf node=port.getAstNode()/>
    var ${port.getName()}: ${util.getTypes().printType(port.getType())}
      get() = throw RuntimeException("Port '${port.getName()}' of '${component.getName()}' may only be written, but not read: It is outgoing.")
      set(value) {
        outputs += "${port.getName()}" to Message(value)
      }
  </#list>
  }
</#macro>
