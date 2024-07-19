package com.lawrence.corejava.inputandoutput;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ObjectReaderExample {

    public void writeObjectToFile(final Object obj, final String fileName) throws IOException {
        try (var out = new ObjectOutputStream(new FileOutputStream(fileName))) {
            out.writeObject(obj);
        }
    }

    public <T> T readObjectFromFile(final Class<T> clazz, final String fileName)
            throws ClassNotFoundException, IOException {
        try (var in = new ObjectInputStream(new FileInputStream(fileName))) {
            var readObj = in.readObject();
            if (clazz.isInstance(readObj)) {
                return clazz.cast(readObj);
            } else {
                throw new ClassNotFoundException("Object read from file is not of type " + clazz.getName());
            }
        }
    }

}
