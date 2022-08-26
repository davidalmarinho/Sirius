package compiling_tools;

import java.io.*;

public class CustomClassLoader extends ClassLoader {
    @Override
    public Class findClass(String name) throws ClassFormatError {
        byte[] b = loadClassFromFile(name);
        return defineClass(name, b, 0, b.length);
    }

    private byte[] loadClassFromFile(String fileName)  {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(
                fileName.replace('.', File.separatorChar) + ".class");

        // if (inputStream == null) {
        //     System.err.println("Error: Couldn't find '" + fileName + "' class.");
        //     return new byte[0];
        // }

        byte[] buffer;
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        int nextValue = 0;
        try {
            while ((nextValue = inputStream.read()) != -1) {
                byteStream.write(nextValue);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        buffer = byteStream.toByteArray();
        return buffer;
    }
}
