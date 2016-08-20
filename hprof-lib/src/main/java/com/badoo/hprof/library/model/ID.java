package com.badoo.hprof.library.model;

import com.badoo.hprof.library.util.StreamUtil;

import java.nio.ByteBuffer;
import java.util.Arrays;

import static com.badoo.hprof.library.util.StreamUtil.ID_SIZE;

public final class ID {

    private final byte[] idBytes;


    public ID() {
        this.idBytes = new byte[StreamUtil.ID_SIZE];
    }
    public ID(byte[] idBytes)
    {
        this.idBytes = idBytes;
    }

    public ID(long idBytesLong)
    {
        this();

        for(int j =0;j< ID_SIZE;j++)
        {
            idBytes[ID_SIZE-j-1] = (byte) ((idBytesLong >>> (8*j))& 0xFFL);
        }

    }

    public byte[] getIdBytes() {
        return idBytes;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ID id = (ID) o;

        return Arrays.equals(idBytes, id.idBytes);

    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(idBytes);
    }

    @Override
    public String toString() {
        return Long.toHexString(toLong());
    }


    public long toLong()
    {
        long hash=0l;
        if(StreamUtil.ID_SIZE == StreamUtil.U8_SIZE)
            hash = ByteBuffer.wrap(idBytes).getLong();
        else if(StreamUtil.ID_SIZE == StreamUtil.U4_SIZE)
            hash = ByteBuffer.wrap(idBytes).getInt();
        return hash;

    }
}
