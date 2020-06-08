class edimm_ssh {

file { "/home/ubuntu/authorized_key_append":
        mode => "0644",
        source => 'puppet:///modules/edimm_ssh/puppet.pub',
     }

exec { "append_key":
       command => 'cat /home/ubuntu/authorized_key_append >> /home/ubuntu/.ssh/authorized_keys',
       path =>  [ '/bin/', '/sbin/' , '/usr/bin/', '/usr/sbin/' ]
}
}
