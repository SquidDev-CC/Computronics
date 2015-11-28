package pl.asie.computronics.api.audio;

import java.io.IOException;

import pl.asie.lib.network.Packet;

/**
 * NOTE: Using this packet type requires Computronics to be present!
 * The client-side implementation of the packet is left to be internal.
 */
public class AudioPacketDFPWM extends AudioPacket {
	private final int frequency;
	private final byte[] data;

	public AudioPacketDFPWM(IAudioSource source, byte volume, int frequency, byte[] data) {
		super(source, volume);
		this.frequency = frequency;
		this.data = data;
	}

	@Override
	protected void writeData(Packet p) throws IOException {
		p.writeInt(frequency).writeShort((short) data.length).writeByteArrayData(data);
	}
}
