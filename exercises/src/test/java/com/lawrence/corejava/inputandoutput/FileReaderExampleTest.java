package com.lawrence.corejava.inputandoutput;

import java.io.IOException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class FileReaderExampleTest {

    @Test
    void fileReaderByCharShouldWork() {
        FileReaderExample example = new FileReaderExample(
                "FileReaderExample.txt");
        example.readInCharacter();
    }

    @Test
    void fileReaderByLineShouldWork() {
        FileReaderExample example = new FileReaderExample(
                "FileReaderExample.txt");
        example.readInLine();
    }

    @Test
    void ObjectReadShouldWork() throws IOException, ClassNotFoundException {

        // Three Objects have a common dependency
        ToBeReadObj obj1 = new ToBeReadObj("John", "1");
        ToBeReadObj obj2 = new ToBeReadObj("Peter", "2");
        ToBeReadObj obj3 = new ToBeReadObj("Jay", "3");
        DependencyObject commonObj = new DependencyObject("Susan");

        obj1.setObject(commonObj);
        obj2.setObject(commonObj);
        obj3.setObject(commonObj);

        // Write them to file as an array.
        ObjectReaderExample example = new ObjectReaderExample();
        ToBeReadObj[] TBRs = new ToBeReadObj[]{ obj1, obj2, obj3 };

        final String cachePath1 = "obj1.ser";
        example.writeObjectToFile(TBRs, cachePath1);

        // Read them from the file
        var TBRsFromFile = example.readObjectFromFile(TBRs.getClass(), cachePath1);

        // Change the name field of the common dependency from obj1
        final String changedName = "changedName";
        TBRsFromFile[0].getObject().setName(changedName);

        // common dependency from obj2 also has the changed name field
        Assertions.assertEquals(changedName, TBRsFromFile[1].getObject().getName());
    }

}
