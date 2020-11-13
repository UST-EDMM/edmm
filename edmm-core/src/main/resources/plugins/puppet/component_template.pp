class ${component} {
<#list artifacts as artifact>
    file { '${artifact}':
        ensure => 'file',
        source => 'puppet:///modules/${component}/${artifact}',
        path => '/usr/local/etc/${artifact}',
        owner => 'root',
        group => 'root',
        mode  => '0744', # Use 0700 if it is sensitive
    }
</#list>
<#list tasks as task>
    include ${component}::${task.name}
</#list>
}
