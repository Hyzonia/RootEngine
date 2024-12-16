package xyz.hyzonia.rootengine.common.messaging;

/**
 * Abstract class representing a packet.
 */
public abstract class Packet {
    public final String id;
    public byte[] buf;

    public Packet(String id) {
        this.id = id;
        buf = new byte[32_768];
    }

    public abstract void encode();

    public abstract void decode();
}
