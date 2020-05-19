package logic.tool;

import java.io.*;

import static logic.tool.MyCompiler.outDir;

public class MyClassLoader extends ClassLoader {

    private final String classpath;

    public MyClassLoader() {
        this.classpath = outDir;
    }

    public MyClassLoader(String classpath) {
        this.classpath = classpath;
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        try {
            byte[] classDate = getDate(name);
            //defineClass方法将字节码转化为类
            if (classDate != null) return defineClass(name, classDate, 0, classDate.length);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return super.findClass(name);
    }

    //返回类的字节码
    private byte[] getDate(String className) throws IOException {
        String path = classpath + File.separatorChar + className.replace('.', File.separatorChar) + ".class";
        try (InputStream in = new FileInputStream(path); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[2048];
            int len;
            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
            return out.toByteArray();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

}
