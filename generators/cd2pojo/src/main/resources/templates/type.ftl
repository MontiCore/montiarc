${tc.signature("package", "kind", "type", "super", "typeHelper", "imports")}

package ${package};

import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
<#list imports as import>
import ${import};
</#list>

public ${kind} ${type.getName()} ${super} {

  <#if type.isEnum()>
    <#-- enum -->
    <#list type.getFields() as field>
        ${field.getName()}
        <#if !field?is_last>,<#else>;</#if>
    </#list>

  <#elseif type.isClass()>
    <#-- class -->

    <#-- mandatoryFields are those required in the constructor -->
    <#-- They may originate from attributes or associations with cardinality [1] -->
    <#assign mandatoryFields = []>
    
    <#list type.getFields() as field>
        <#-- attributes -->
        <#assign mandatoryFields = mandatoryFields + [{"name": field.getName(), "type":field.getType()}]>
        private ${field.getType()} ${field.getName()};
        public ${field.getType()} get${field.getName()?cap_first}() {
          return ${field.getName()};
        }
        public void set${field.getName()?cap_first}(${field.getType()} ${field.getName()}) {
          this.${field.getName()} = ${field.getName()};
        }
    </#list>

    <#-- associations -->
    <#list type.getAssociations() as assoc>
      <#assign t=typeHelper.printType(assoc.getTargetType())>
      <#assign n=assoc.getDerivedName()>

      <#if assoc.getTargetCardinality().isMultiple()>
        <#-- [*] -->

        private List<${t}> ${n} = new ArrayList<>();
        public void set${n?cap_first}(List<${t}> ${n}){
            this.${n} = ${n};
        }
        public List<${t}> get${n?cap_first}(){
            return this.${n};
        }
        public void add${n?cap_first}(${t} ${n}){
            this.${n}.add(${n});
        }
        public void remove${n?cap_first}(${t} ${n}){
            this.${n}.remove(${n});
        }

      <#elseif assoc.getTargetCardinality().getMin()==0>
        <#-- [0..1]-->

        <#assign tOpt="Optional<"+t+">">
        private ${tOpt} ${n} = Optional.empty();
        public ${tOpt} get${n?cap_first}() {
          return ${n};
        }
        public void set${n?cap_first}(${t} ${n}) {
          this.${n} = Optional.of(${n});
        }

      <#else>
        <#-- [1] -->

        <#assign mandatoryFields = mandatoryFields + [{"name": n, "type": t}]>
        private ${t} ${n};
        public ${t} get${n?cap_first}() {
          return ${n};
        }
        public void set${n?cap_first}(${t} ${n}) {
          this.${n} = ${n};
        }
      </#if>
    </#list> <#-- /associations -->

    <#-- constructor -->
    public ${type.getName()}(
    <#list mandatoryFields as mandatoryField>
        ${mandatoryField.type} ${mandatoryField.name}
        <#if !mandatoryField?is_last>,</#if>
    </#list>
    ){
       <#list mandatoryFields as mandatoryField>
         this.${mandatoryField.name} = ${mandatoryField.name};
       </#list>
    }
  </#if><#-- /class -->
}
