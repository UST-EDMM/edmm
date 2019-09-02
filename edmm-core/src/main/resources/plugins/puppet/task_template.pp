class ${component}::${task.name}{
  file { '${task.scriptFileName}':
    ensure => 'file',
    source => 'puppet:///modules/${component}/${task.scriptFileName}',
    path => '/usr/local/bin/${task.scriptFileName}',
    owner => 'root'
    group => 'root'
    mode  => '0744', # Use 0700 if it is sensitive
    notify => Exec['run_${task.scriptFileName}'],
  }
  exec { 'run_${task.scriptFileName}':
    environment => ["${task.varString}"],
    command => '/usr/local/bin/${task.scriptFileName}',
    refreshonly => true
  }
}
