#!/bin/bash
PREFIX="node default {"
SUFFIX="}"
STRING_TO_BE_INCLUDED="include edimm_ssh"
LINE_TO_FIND="node default"
PATH_TO_FILE="/etc/puppetlabs/code/environments/production/manifests/site.pp"

if [ -f $PATH_TO_FILE ]; then
   echo "site.pp exists"
else
   touch $PATH_TO_FILE
fi

if grep -Fq $LINE_TO_FIND $PATH_TO_FILE; then
    if grep -Fq $STRING_TO_BE_INCLUDED $PATH_TO_FILE; then
        echo "NO REPLACEMENT NECESSARY"
        exit
    fi
    sed -i '/node default {/a  include edimm_ssh' $PATH_TO_FILE
else
    echo $PREFIX >> $PATH_TO_FILE
    echo $STRING_TO_BE_INCLUDED >> $PATH_TO_FILE
    echo $SUFFIX >> $PATH_TO_FILE
fi
