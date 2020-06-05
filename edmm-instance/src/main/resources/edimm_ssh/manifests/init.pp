class edimm_ssh {

file { "/home/ubuntu/.ssh/authorized_keys":
        mode => "0644",
        source => 'puppet:///modules/edimm_ssh/puppet.pub',
     }
}
