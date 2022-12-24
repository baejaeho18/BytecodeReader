package edu.handong.csee.isel.bytecodeparser.asm;

import edu.handong.csee.isel.bytecodeparser.asm.jdis.Jdis;
import edu.handong.csee.isel.bytecodeparser.asm.jdis.uEscWriter;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;

public class BytecodeReader {
    Iterator<String> fileList;

    public BytecodeReader(String fileListPath){
        ArrayList<String> list = new ArrayList<>();
        BufferedReader r = null;
        try {
            String l;
            r = new BufferedReader(new FileReader(fileListPath));
            while ((l = r.readLine()) != null) {
                if(l.equals("")) continue;
                list.add(l);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (r != null)
                    r.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        System.out.println("Number of files: " + list.size());
        this.fileList = list.iterator();
    }

    public void readClass(String outputPath) {
        if(outputPath.endsWith("/")){
            outputPath = outputPath.substring(0,outputPath.length() - 1);
        }
        int cnt = 0;
        StringBuilder paths = new StringBuilder();
        while(fileList.hasNext()){
            cnt ++;
            try {
                File f = new File("./assembler_version.txt");
                FileOutputStream fos = new FileOutputStream(f, false);
                Jdis jdis = new Jdis(new PrintWriter(new uEscWriter(fos)), new PrintWriter(System.err), "jdis");
                String p = jdis.disasm(outputPath, fileList.next());
                if(p.equals("ERROR")){
                    System.out.println("ERROR");
                } else{
                    paths.append(p).append("\n");
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            if(cnt % 1000 == 0) {
                writeFileList(paths, outputPath + "_file_list.txt");
                paths.setLength(0);
            }
            if(cnt % 5000 ==0){
                System.out.println("current progress: " + cnt);
            }
        }
        if(cnt % 1000 != 0) {
            writeFileList(paths, outputPath + "_file_list.txt");
            paths.setLength(0);
        }
    }

    private void writeFileList(StringBuilder paths, String outputPath) {
        BufferedWriter bw = null;
        try {
            File out = new File(outputPath);
            if (!out.exists()) {
                out.createNewFile();
            }
            FileWriter fw = new FileWriter(out, true);
            bw = new BufferedWriter(fw);
            bw.write(paths.toString());

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        finally {
            try {
                if(bw!=null)
                    bw.close();
            } catch(Exception ex){
                System.out.println("Error in closing the BufferedWriter"+ex);
            }
        }
    }
}
