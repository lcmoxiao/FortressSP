package logic.tool;

import java.io.*;

public class Cloner {
    public static <T>T clone(T o1){
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream(); ObjectOutputStream oos = new ObjectOutputStream(bos);) {
            // 写入对象
            oos.writeObject(o1);
            oos.flush();
            // 使用ByteArrayInputStream和ObjectInputStream反序列化
            try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray()));) {
                // 读取对象
                return (T)ois.readObject();
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
