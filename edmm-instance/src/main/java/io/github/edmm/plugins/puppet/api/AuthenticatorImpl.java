package io.github.edmm.plugins.puppet.api;

import io.github.edmm.core.plugin.Authenticator;
import io.github.edmm.plugins.puppet.model.Master;

import lombok.Getter;

public class AuthenticatorImpl implements Authenticator {

    @Getter
    private Master master;

    public AuthenticatorImpl(Master master) {
        this.master = master;
    }

    @Override
    public void authenticate() {
        this.master.connectAndSetupMaster();
    }
}
