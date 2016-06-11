package com.badoo.hprof.library.model;

import com.badoo.hprof.library.util.StreamUtil;

import java.nio.ByteBuffer;
import java.util.Arrays;

import static com.badoo.hprof.library.util.StreamUtil.ID_SIZE;

/**
 * Created by mfarouk on 5/26/16.
 */
public class ID {

    byte[] idBytes;


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

    public void setIdBytes(byte[] idBytes) {
        this.idBytes = idBytes;
    }

    @Override
    public boolean equals(Object obj) {

        if(obj==null) return false;

        ID otherId = (ID)obj;
        return Arrays.equals(idBytes,otherId.idBytes);


    }

    @Override
    public int hashCode() {
        int hash=(int)toLong();
////        for(int i=0;i<StreamUtil.ID_SIZE;i++)
////        {
////            hash |= idBytes[i] << (8 * (StreamUtil.ID_SIZE-i-1));
////        }
//
//        if(StreamUtil.ID_SIZE==StreamUtil.U8_SIZE)
//        hash = (int)ByteBuffer.wrap(idBytes).getLong();
//        else
//            hash= ByteBuffer.wrap(idBytes).getInt();

        return hash;
    }

    @Override
    public String toString() {

//        long hash = 0L;
//        String s = "";

//        for(int i=0;i<StreamUtil.ID_SIZE;i++)
//        {
//            hash |= idBytes[StreamUtil.ID_SIZE-i-1] << (8 * i);
//
//
//        }

//        if(StreamUtil.ID_SIZE == StreamUtil.U8_SIZE)
//            hash = ByteBuffer.wrap(idBytes).getLong();
//        else if(StreamUtil.ID_SIZE == StreamUtil.U4_SIZE)
//            hash = ByteBuffer.wrap(idBytes).getInt();

        return Long.toHexString(toLong());
//                + ",ID{" +
//                "idBytes=" + Arrays.toString(idBytes)+
//                '}';
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


//
//    @Override
//    public String toString() {
//        long hash = 0L;
//        for(int i=0;i<4;i++)
//        {
//            hash |= ((long)idBytes[i]) << (8 * (StreamUtil.ID_SIZE-i-1));
//        }
//
//        return Long.toHexString(hash).toUpperCase();
//    }





//    @Override
//    public String toString() {
//        return "ID{" +
//                "idBytes=" + Arrays.toString(idBytes) +
//                '}';
//    }
}
