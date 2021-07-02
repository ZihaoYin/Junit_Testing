package edu.qc.seclass.replace;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

import static java.nio.file.StandardCopyOption.*;

public class Main {

    public static void main(String[] args)  {
        // TODO: Empty skeleton method

        int fileIndex = findLastIndex(args,"--");
        if (fileIndex < 2){ //  "--" not found or missing operand from str, to str
            usage();
            return;
        }

        int fromStrIndex = fileIndex-2;
        int toStrIndex = fileIndex-1;
        String fromStr = args[fromStrIndex];
        String toStr = args[toStrIndex];
        if(fromStr.isEmpty()){
            usage();
            return;
        }
        if(fromStr.charAt(0) =='-'){

            if(fromStrIndex-1>=0 && args[fromStrIndex-1].equals("--")){

            }else{
                usage();
                return;
            }
        }

        Boolean optionB = false;
        Boolean optionF = false;
        Boolean optionL = false;
        Boolean optionI = false;

        //before fromStrIndex check -f -i -l -b option
        for(int i=0;i<fromStrIndex;i++){
            if(args[i].equals("-b")){ //back up file
                optionB = true;
            }else if(args[i].equals("-f")){ // replace first from str
                optionF = true;
            }else if(args[i].equals("-l")){ // replace last from str
                optionL = true;
            }else if(args[i].equals("-i")){ // replace from str case insensitively
                optionI = true;
            }else if(i == fromStrIndex-1 && args[i].equals("--")){ //nothing

            }else{ // error
                usage();
                return;
            }
        }

        //after "fileIndex check file exists or not,
            //replace
                //read file if exists
                //replace string
        ReplaceStr(args, fileIndex, fromStr, toStr, optionB, optionF, optionL, optionI);

    }

    private static void ReplaceStr(String[] args, int fileIndex, String fromStr, String toStr, Boolean optionB, Boolean optionF, Boolean optionL, Boolean optionI) {
        for(int i = fileIndex +1; i< args.length; i++){
            File tempFile = new File(args[i]);
            if(tempFile.exists()){
                backupFileFunc(args, optionB, i);

                //replace insensitively
                if(optionI){

                    if(optionF || optionL){
                        //replace first one insensitively
                        if(optionF){
                            try {
                                replaceFirstFileStrInsensitively(tempFile, args,i, fromStr, toStr);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        //replace last one insensitively
                        if(optionL){
                            try {
                                replaceLastFileStrInsensitively(tempFile, args,i, fromStr, toStr);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                    }else{
                        //replace all insensitively
                        try {
                            replaceFileStrInsensitively(tempFile, args,i, fromStr, toStr);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                }else{ // replace
                    if(optionF || optionL){
                        //replace first one
                        if(optionF){
                            try {
                                replaceFirstFileStr(tempFile, args,i, fromStr, toStr);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        //replace last one
                        if(optionL){
                            try {
                                replaceLastFileStr(tempFile, args,i, fromStr, toStr);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                    }else{
                        //replace all
                        try {
                            replaceFileStr(tempFile, args,i, fromStr, toStr);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                }

            }else{
                System.err.println("File "+ tempFile.getName()+ " not found" );
            }


        }
    }

    private static void backupFileFunc(String[] args, Boolean optionB, int i) {
        if(optionB){
            String backupFileName = args[i]+".bck";
            File backupFile = new File(backupFileName);
            try {
                backupFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Path source = Paths.get(args[i]);
            OutputStream target = null;
            try {
                target = new FileOutputStream(backupFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            try {
                Files.copy(source,target);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                target.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // replace all from str
    private static void replaceFileStr(File tempFile,String args[],int i,String from, String to) throws IOException {
        Path filePath = Paths.get(args[i]);
        String content = Files.readString(filePath);

        //content = content.replaceAll(from,to);
        int index = content.indexOf(from);
        while(index>=0){
            content = content.substring(0,index) + to + content.substring(index+from.length());
            index = content.indexOf(from,index+from.length());
        }

        FileWriter Writer = new FileWriter(tempFile);
        Writer.write(content);
        Writer.close();
    }

    // replace all from str insensitively
    private static void replaceFileStrInsensitively(File tempFile,String args[],int i,String from, String to) throws IOException {
        Path filePath = Paths.get(args[i]);
        String content = Files.readString(filePath);

        //content = content.replaceAll(from,to);
        int index = content.toLowerCase().indexOf(from.toLowerCase());
        while(index>=0){
            content = content.substring(0,index) + to + content.substring(index+from.length());
            index = content.toLowerCase().indexOf(from.toLowerCase(),index+from.length());
        }

        FileWriter Writer = new FileWriter(tempFile);
        Writer.write(content);
        Writer.close();
    }

    // replace first from str
    private static void replaceFirstFileStr(File tempFile,String args[],int i,String from, String to) throws IOException {
        Path filePath = Paths.get(args[i]);
        String content = Files.readString(filePath);

        int firstIndex = content.indexOf(from);
        if (firstIndex == -1){
            return;
        }
        content = content.substring(0,firstIndex) + to + content.substring(firstIndex+from.length());
        FileWriter Writer = new FileWriter(tempFile);
        Writer.write(content);
        Writer.close();
    }

    // replace first from str insensitively
    private static void replaceFirstFileStrInsensitively(File tempFile,String args[],int i,String from, String to) throws IOException {
        Path filePath = Paths.get(args[i]);
        String content = Files.readString(filePath);

        int firstIndex = content.toLowerCase().indexOf(from.toLowerCase());
        if (firstIndex == -1){
            return;
        }
        content = content.substring(0,firstIndex) + to + content.substring(firstIndex+from.length());
        FileWriter Writer = new FileWriter(tempFile);
        Writer.write(content);
        Writer.close();
    }

    // replace last from str
    private static void replaceLastFileStr(File tempFile,String args[],int i,String from, String to) throws IOException {
        Path filePath = Paths.get(args[i]);
        String content = Files.readString(filePath);
        int lastIndex = content.lastIndexOf(from);
        if (lastIndex == -1){
            return;
        }
        content = content.substring(0,lastIndex) + to + content.substring(lastIndex+from.length());
        FileWriter Writer = new FileWriter(tempFile);
        Writer.write(content);
        Writer.close();
    }

    // replace last from str insensitively
    private static void replaceLastFileStrInsensitively(File tempFile,String args[],int i,String from, String to) throws IOException {
        Path filePath = Paths.get(args[i]);
        String content = Files.readString(filePath);
        int lastIndex = content.toLowerCase().lastIndexOf(from.toLowerCase());
        if (lastIndex == -1){
            return;
        }
        content = content.substring(0,lastIndex) + to + content.substring(lastIndex+from.length());
        FileWriter Writer = new FileWriter(tempFile);
        Writer.write(content);
        Writer.close();
    }

    //find index of last "--"
    private static int findLastIndex(String[] arr,String str){
        int len = arr.length;
        for(int i=len-1;i>=0;i--){
            if(arr[i].equals(str)){
                return i;
            }
        }
        return -1;
    }
    private static void usage() {
        System.err.println("Usage: Replace [-b] [-f] [-l] [-i] <from> <to> -- " + "<filename> [<filename>]*" );
    }

}