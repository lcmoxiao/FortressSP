package logic.tool;

import javax.tools.JavaCompiler;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Java6及以上才可用。
 * 如果ToolProvider.getSystemJavaCompiler();返回的是null，说明jre的lib里面
 * 没有tools.jar，要将jdk的lib中的tools.jar复制到jre的lib中。
 * 设计作者: teasp
 * 信息描述：
 */
public class MyCompiler {
    public static String outDir = System.getProperty("user.dir") + "\\src\\main\\java";

    public static Class<?> compile(String name, String content) {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
        StrSrcJavaObject srcObject = new StrSrcJavaObject(name, content);
        List<StrSrcJavaObject> fileObjects = Collections.singletonList(srcObject);
        String flag = "-d";
        System.out.println("输出目录为" + outDir);
        Iterable<String> options = Arrays.asList(flag, outDir);
        JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, null, options, null, fileObjects);
        boolean result = task.call();
        if (result) {
            System.out.println("Compile it successfully.");
//            try {
//                return Class.forName(name);
//            } catch (ClassNotFoundException e) {
//                e.printStackTrace();
//            }
            /*ClassLoader loader = CompileTest.class.getClassLoader();
            Class<?> cls;
            try
            {
                cls = loader.loadClass(name);
                return cls;
            }
            catch (ClassNotFoundException e)
            {
                e.printStackTrace();
            }*/
        }
        return null;
    }

    public static void main(String[] args) throws ClassNotFoundException {
        String content = "package play; public class Test{ public static void main(String[] args){System.out.println(\"base.compile test.\");} }";
        Class<?> cls = compile("play.Test", content);


        MyClassLoader myClassLoader = new MyClassLoader(outDir);
        Class c = myClassLoader.loadClass("playcache.Test");
        try {
            Method method = c.getMethod("main", String[].class);
            method.invoke(null, new Object[]{new String[]{}});
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static class StrSrcJavaObject extends SimpleJavaFileObject {
        private final String content;

        public StrSrcJavaObject(String name, String content) {
            super(URI.create("string:///" + name.replace('.', '/') + Kind.SOURCE.extension), Kind.SOURCE);
            this.content = content;
        }

        public CharSequence getCharContent(boolean ignoreEncodingErrors) {
            return content;
        }
    }
}