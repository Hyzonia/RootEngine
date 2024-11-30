package io.github.spigotrce.common.messaging;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Factory for decoding packets.
 */
public class PacketFactory {
    private final Map<String, PacketEntry<?>> packetMap = new HashMap<>();
    private final Consumer<PacketSender> sender;

    /**
     * Creates a new packet factory with the given sender.
     *
     * @param sender the sender to use for sending packets
     */
    public PacketFactory(Consumer<PacketSender> sender) {
        this.sender = sender;
    }

    /**
     * Registers a new packet in the packet map with automatic casting.
     *
     * @param id          the id of the packet
     * @param supplier    the packet supplier
     * @param initializer the initializer function
     * @param <T>         the type of the packet
     */
    public <T extends Packet> void registerPacket(String id, Supplier<T> supplier, Consumer<T> initializer) {
        packetMap.put(id, new PacketEntry<>(supplier, packet -> initializer.accept(safeCast(packet))));
    }

    /**
     * Decodes and applies a packet from the given data.
     *
     * @param data the data to decode
     */
    public void decodeAndApply(byte[] data) {
        ByteArrayDataInput in = ByteStreams.newDataInput(data);
        String id = in.readUTF();
        PacketEntry<?> packetEntry = packetMap.get(id);
        if (packetEntry == null) {
            throw new IllegalArgumentException("Unknown packet ID: " + id);
        }
        Packet packet = packetEntry.supplier.get();
        byte[] exactData = new byte[data.length - id.length() - 2];
        in.readFully(exactData);
        packet.buf = exactData;
        packet.decode();
        packetEntry.initializer.accept(packet);
    }

    /**
     * Encodes a packet and sends it over the connection.
     *
     * @param packet     the packet to encode
     * @param connection the connection
     */
    public void encodeAndSend(Packet packet, Object connection) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF(packet.id);
        packet.encode();
        out.write(packet.buf);
        PacketEntry<?> packetEntry = packetMap.get(packet.id);
        if (packetEntry == null) {
            throw new IllegalArgumentException("Unknown packet ID: " + packet.id);
        }
        sender.accept(new PacketSender(connection, out.toByteArray()));
    }

    @SuppressWarnings("unchecked")
    private <T extends Packet> T safeCast(Packet packet) {
        return (T) packet;
    }

    private record PacketEntry<T extends Packet>(Supplier<T> supplier, Consumer<Packet> initializer) {
    }

    public static class PacketSender {
        public Object connection;
        public byte[] data;

        public PacketSender(Object connection, byte[] data) {
            this.connection = connection;
            this.data = data;
        }
    }
}
