${state.id}:
  ${state.state}.${state.fun}:
  <#list state.vars as key, value>
  - ${key}: ${value}
  </#list>
  <#if state.require == true>
  - require:
      - ${state.requireState.state}: ${state.requireState.id}
  <#else>
  </#if>