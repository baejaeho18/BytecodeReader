package edu.handong.csee.isel.bytecodeparser;

import org.junit.jupiter.api.Test;

import java.util.Set;

public class ListClassFilesTest {
    @Test
    void someLibraryMethodReturnsTrue() {
        Set<String> fileLists = Utils.listClassFiles("./../../OSPdata/");
        for(String f : fileLists) {
            System.out.println(f);
        }
    }
}
