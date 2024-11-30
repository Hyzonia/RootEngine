package io.github.spigotrce.common.messaging;

public enum MessagingConstants {
    ENGINE_CHANNEL("rootengine:main");

    public final String channelName;

    MessagingConstants(String channelName) {
        this.channelName = channelName;
    }
}
