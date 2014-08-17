import java.io.*;
import java.nio.ByteBuffer;

public class byteCasting {
    private static ByteBuffer buffer;    

    // Convert a long to byte array
    public static byte[] longToBytes(long x) {
    	buffer = ByteBuffer.allocate(Long.SIZE);
        buffer.putLong(0, x);
        return buffer.array();
    }

    // Convert a byte array to a long value
    public static long bytesToLong(byte[] bytes) {
    	buffer = ByteBuffer.allocate(Long.SIZE);
        buffer.put(bytes, 0, bytes.length);
        buffer.flip();//need flip 
        return buffer.getLong();
    }
    
    // Convert objects to byte array
    public static byte[] objectToBytes(Object obj) throws IOException {
        byte[] bytes = null;
        ByteArrayOutputStream bos = null;
        ObjectOutputStream oos = null;
        try {
            bos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(bos);
            oos.writeObject(obj);
            oos.flush();
            bytes = bos.toByteArray();
        } finally {
            if (oos != null) {
                oos.close();
            }
            if (bos != null) {
                bos.close();
            }
        }
        return bytes;
    }

    // Convert byte array back to original object
    public static Object bytesToObject(byte[] bytes) throws IOException, ClassNotFoundException {
        Object obj = null;
        ByteArrayInputStream bis = null;
        ObjectInputStream ois = null;
        try {
            bis = new ByteArrayInputStream(bytes);
            ois = new ObjectInputStream(bis);
            obj = ois.readObject();
        } finally {
            if (bis != null) {
                bis.close();
            }
            if (ois != null) {
                ois.close();
            }
        }
        return obj;
    }
}
