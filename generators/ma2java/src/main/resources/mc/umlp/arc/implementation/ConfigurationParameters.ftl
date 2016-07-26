${tc.params("String paramType", "String paramName")}


        /** The configuration parameter ${paramName}. */
        private ${paramType} ${paramName};
        
        /**
         * @return the configuration parameter ${paramName}
         */
        protected ${paramType} get${paramName?cap_first}() {
            ${paramType} _param = this.${paramName};
            <#-- ${op.includeTemplates(getConfigParamHook, ast)} -->
            return _param;   
        }