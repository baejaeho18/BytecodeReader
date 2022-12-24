package edu.handong.csee.isel.bytecodeparser;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Utils {

    public static ArrayList<String> getMatchedClassFiles(String prjName, String classPath, String sourcePath){
        return findMatched(prjName, readFileList(classPath), readFileList(sourcePath));
    }

    private static ArrayList<String> readFileList(String path) {
        ArrayList<String> fileList = new ArrayList<>();
        //read file list
        try {
        BufferedReader reader = new BufferedReader(new FileReader(path));
            String str;
            while ((str = reader.readLine()) != null) {
                if(str.startsWith("./"))
                    fileList.add(str);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileList;
    }

    private static ArrayList<String> findMatched(String prjName, ArrayList<String> classFiles, ArrayList<String> sourceFiles){
        ArrayList<String> matchedClasses = new ArrayList<>();
        ArrayList<String> matchedSrc = new ArrayList<>();
        // split before $ and .class for class files
        // split before . for source files
        // split proceeding a parent directory
        // ==> get file name and a parent directory
        System.out.println(classFiles.size());
        int cnt = 0;
        for (String cla : classFiles){
            if(cnt%1000 == 0)
                System.out.println(cnt);
            String tmpCla;
            int len = cla.split("/").length;
            String parentDir = cla.split("/")[len - 2];
            String fileName =  cla.split("/")[len - 1];
            if(fileName.contains("$")){
                fileName = fileName.split("\\$")[0];
            } else {
                fileName = fileName.split("\\.")[0];
            }
            tmpCla = parentDir + fileName;

            for(String src : sourceFiles){
                len = src.split("/").length;
                parentDir = src.split("/")[len - 2];
                fileName =  src.split("/")[len - 1].split("\\.")[0];
                String tmpSrc = parentDir + fileName;
                if(tmpCla.equals(tmpSrc)){
                    matchedClasses.add(cla);
                    if(!matchedSrc.contains(src)){
                        matchedSrc.add(src);
                    }
                    break;
                }
            }
            cnt++;
        }
        writeMatchedList(prjName, matchedSrc);
        return matchedClasses;
    }

    private static void writeMatchedList(String prjName, ArrayList<String> list){
        String fileName = "./matched_list/" + prjName+"_matched_source_list.txt";
        try {
            BufferedWriter bw = new BufferedWriter(new java.io.FileWriter(fileName, false));
            for(String f : list) {
                bw.write(f + "\n");
                bw.flush();
            }
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Set<String> listClassFiles(String dir) {
        Set<String> fileList = new HashSet<>();
        try {
            Files.walkFileTree(Paths.get(dir), new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    if (!Files.isDirectory(file) && file.getFileName().toString().endsWith(".class")) {
                        fileList.add(file.toAbsolutePath().toString());
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileList;
    }
}
