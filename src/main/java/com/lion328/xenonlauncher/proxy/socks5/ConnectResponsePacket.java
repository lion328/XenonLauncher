package com.lion328.xenonlauncher.proxy.socks5;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ConnectResponsePacket implements Packet
{

    private int version;
    private State state;
    private Address address;
    private int port;

    public ConnectResponsePacket()
    {

    }

    public ConnectResponsePacket(int version, State state, Address address, int port) throws IOException
    {
        this.version = version;
        this.state = state;
        this.address = address;
    }

    public int getVersion()
    {
        return version;
    }

    public void setVersion(int version)
    {
        this.version = version;
    }

    public State getState()
    {
        return state;
    }

    public void setState(State state)
    {
        this.state = state;
    }

    public Address getAddress()
    {
        return address;
    }

    public void setAddress(Address address)
    {
        this.address = address;
    }

    public int getPort()
    {
        return port;
    }

    public void setPort(int port)
    {
        this.port = port;
    }

    @Override
    public void read(InputStream in) throws IOException
    {
        version = in.read() & 0xFF;
        state = State.getByByte(in.read() & 0xFF);
        in.read();
        address = Address.fromInputStream(in);
        port = ((in.read() & 0xFF) << 8) | (in.read() & 0xFF);
    }

    @Override
    public void write(OutputStream out) throws IOException
    {
        out.write(version & 0xFF);
        out.write(state.getByte());
        out.write(0x00);
        out.write(address.toByteArray());
        out.write((port >> 8) & 0xFF);
        out.write(port & 0xFF);
    }

    public enum State
    {

        SUCCEEDED(0x00),
        GENERAL_FAILURE(0x01),
        CONNECTION_NOT_ALLOWED(0x02),
        NETWORK_UNREACHABLE(0x03),
        HOST_UNREACHABLE(0x04),
        CONNECTION_REFUSED(0x05),
        TTL_EXPIRED(0x06),
        COMMAND_NOT_SUPPORTED(0x07),
        ADDRESS_TYPE_NOT_SUPPORTED(0x08);

        private int b;

        State(int b)
        {
            this.b = b;
        }

        public static State getByByte(int b)
        {
            for (State s : values())
            {
                if (s.b == (b & 0xFF))
                {
                    return s;
                }
            }
            return null;
        }

        public int getByte()
        {
            return b & 0xFF;
        }
    }
}
