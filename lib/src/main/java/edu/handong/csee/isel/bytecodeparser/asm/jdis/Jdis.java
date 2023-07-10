/*
 * Copyright (c) 1996, 2020, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */
package edu.handong.csee.isel.bytecodeparser.asm.jdis;

import edu.handong.csee.isel.bytecodeparser.asm.common.Tool;
import edu.handong.csee.isel.bytecodeparser.asm.util.I18NResourceBundle;

import java.io.*;
import java.util.ArrayList;
import java.util.Set;

/**
 * Main program of the Java Disassembler :: class to jasm
 */
public class Jdis extends Tool {
    private Options options;

    public static final I18NResourceBundle i18n
            = I18NResourceBundle.getBundleForClass(Jdis.class);

    public Jdis(PrintWriter out, PrintWriter err, String programName) {
        super(out, err, programName);
        // tool specific initialization
        options = Options.OptionObject();
        DebugFlag = () -> options.contains(Options.PR.DEBUG);
        printCannotReadMsg = (fname) -> error( i18n.getString("jdis.error.cannot_read", fname));
    }

    public Jdis(PrintStream out, String program) {
        this(new PrintWriter(out), new PrintWriter(System.err), program);
    }

    @Override
    public void usage() {
        println(i18n.getString("jdis.usage"));
        println(i18n.getString("jdis.opt.g"));
        println(i18n.getString("jdis.opt.sl"));
        println(i18n.getString("jdis.opt.hx"));
        println(i18n.getString("jdis.opt.v"));
        println(i18n.getString("jdis.opt.version"));
    }

    /**
     * Run the disassembler
     */
    public synchronized String disasm(String outputPath, String classPath) {
        out.println(org.openjdk.asmtools.util.ProductInfo.FULL_VERSION);
        File p = generateOutputPath(outputPath, classPath);

        try {
            out = new PrintWriter(new uEscWriter(new FileOutputStream(p)));
            ClassData cc = new ClassData(out, this);
            cc.read(classPath);
            cc.printMemberDataList(cc.methods);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            out.close();
            System.exit(-1);
        } catch (Error ee) {
            if (DebugFlag.getAsBoolean())
                ee.printStackTrace();
            ee.printStackTrace();
            error(i18n.getString("jdis.error.fatal_error", classPath));
            return "ERROR";
        } catch (Exception ee) {
            if (DebugFlag.getAsBoolean())
                ee.printStackTrace();
            ee.printStackTrace();
            error(i18n.getString("jdis.error.fatal_exception", classPath));
            return "ERROR";
        } finally {
            try {
                if(out != null)
                    out.close();
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        return p.getPath();
    }

    private File generateOutputPath(String outputPath, String classPath){
//        outputPath = "./RxJava";
//        classPath = "../Data/fiveLangData/Java/RxJava/build/classes/java/test/io/reactivex/rxjava3/internal/operators/mixed/FlowableSwitchMapMaybeTest$34.class";
        File p = null;
        String prjName = outputPath.split("/")[1];
//System.out.println(outputPath);
        //String prjName = outputPath.split("\\")[1];
        //RXJava
        //String fileName = classPath.split("\\")[classPath.split("\\").length - 1];
        String fileName = classPath.split("/")[classPath.split("/").length - 1];
        //FlowableSwitchMapMaybeTest$34.class
        String insidePath = classPath.replace(fileName, "");
        //../Data/fiveLangData/Java/RxJava/build/classes/java/test/io/reactivex/rxjava3/internal/operators/mixed/
        String tmpDirPath = outputPath + insidePath.split(prjName,2)[1];
        File tmpDir = new File(tmpDirPath);
        //./RxJava/build/classes/java/test/io/reactivex/rxjava3/internal/operators/mixed/
        if(!tmpDir.exists()){
            tmpDir.mkdirs();
        }
        String textName = fileName.split("\\.")[0] + ".txt";
        p = new File(tmpDirPath + File.separator + textName);
        return p;
    }
}
