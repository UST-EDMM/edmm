class ${component}::${task.name} {
  file { '${task.scriptFileName}':
    ensure => 'file',
    source => 'puppet:///modules/${component}/${task.scriptFileName}',
    path => '/usr/local/bin/${task.scriptFileName}',
    owner => 'root',
    group => 'root',
    mode  => '0744', # Use 0700 if it is sensitive
    notify => Exec['run_${task.scriptFileName}'],
  }
  exec { 'run_${task.scriptFileName}':
    <#if task.envVars?? && task.envVars?size != 0>
    environment => [
        <#list task.envVars as var>
        '${var}'<#sep>,</#sep>
        </#list>
    ],
    </#if>
    command => '/usr/local/bin/${task.scriptFileName}',
    refreshonly => true
  }
}
