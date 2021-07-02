package edu.qc.seclass.replace;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.*;

public class MyMainTest {

    private ByteArrayOutputStream outStream;
    private ByteArrayOutputStream errStream;
    private PrintStream outOrig;
    private PrintStream errOrig;
    private Charset charset = StandardCharsets.UTF_8;

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Before
    public void setUp() throws Exception {
        outStream = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(outStream);
        errStream = new ByteArrayOutputStream();
        PrintStream err = new PrintStream(errStream);
        outOrig = System.out;
        errOrig = System.err;
        System.setOut(out);
        System.setErr(err);
    }

    @After
    public void tearDown() throws Exception {
        System.setOut(outOrig);
        System.setErr(errOrig);
    }

    // Some utilities

    private File createTmpFile() throws IOException {
        File tmpfile = temporaryFolder.newFile();
        tmpfile.deleteOnExit();
        return tmpfile;
    }

    private File createInputFile1() throws Exception {
        File file1 =  createTmpFile();
        FileWriter fileWriter = new FileWriter(file1);

        fileWriter.write("Hi Bill,\n" +
                "This is a test file for the replace utility\n" +
                "Let's make sure it has at least a few lines\n" +
                "so that we can create some interesting test cases...\n" +
                "And let's say \"hi bill\" again!");

        fileWriter.close();
        return file1;
    }

    private File createInputFile2() throws Exception {
        File file1 =  createTmpFile();
        FileWriter fileWriter = new FileWriter(file1);

        fileWriter.write("What's up Bill,\n" +
                "This is a test file for the replace utility\n" +
                "Let's make sure it has at least a few lines\n" +
                "so that we can create some interesting test cases...\n" +
                "And let's say \"what's up bill\" again!");

        fileWriter.close();
        return file1;
    }

    private File createInputFile3() throws Exception {
        File file1 =  createTmpFile();
        FileWriter fileWriter = new FileWriter(file1);

        fileWriter.write("What's up Bill, says Tom\n" +
                "what's up Tom, says Bill\n" +
                "What's up everyone, says John");

        fileWriter.close();
        return file1;
    }


    private String getFileContent(String filename) {
        String content = null;
        try {
            content = new String(Files.readAllBytes(Paths.get(filename)), charset);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content;
    }

    // Actual test cases
    @Test            //Implementation of test frame #1
    public void EmptyFromStrTest() throws Exception {
        File inputFile1 = createInputFile1();
        String args[] = {"","Hi","--", inputFile1.getPath()};
        Main.main(args);
        assertEquals("Usage: Replace [-b] [-f] [-l] [-i] <from> <to> -- <filename> [<filename>]*", errStream.toString().trim());
    }

    @Test            //Implementation of test frame #2
    public void FromStrSameAsToStrTest() throws Exception {
        File inputFile = createInputFile1();
        String args[] = {"Hi","Hi","--", inputFile.getPath()};
        Main.main(args);

        String expected = "Hi Bill,\n" +
                "This is a test file for the replace utility\n" +
                "Let's make sure it has at least a few lines\n" +
                "so that we can create some interesting test cases...\n" +
                "And let's say \"hi bill\" again!";

        String actual = getFileContent(inputFile.getPath());

        assertEquals("The files differ!", expected, actual);
        assertFalse(Files.exists(Paths.get(inputFile.getPath() + ".bck")));
    }

    @Test            //Implementation of test frame #3
    public void EmptyFileTest() throws Exception {
        File inputFile = createTmpFile();
        String args[] = {"Sup","Hi","--", inputFile.getPath()};
        Main.main(args);

        String expected = "";

        String actual = getFileContent(inputFile.getPath());

        assertEquals("The files differ!", expected, actual);
        assertFalse(Files.exists(Paths.get(inputFile.getPath() + ".bck")));
    }

    @Test            //Implementation of test frame #4
    public void FromStrNotExistTest() throws Exception {
        File inputFile = createInputFile1();
        String args[] = {"Sup","Hello","--", inputFile.getPath()};
        Main.main(args);

        String expected = "Hi Bill,\n" +
                "This is a test file for the replace utility\n" +
                "Let's make sure it has at least a few lines\n" +
                "so that we can create some interesting test cases...\n" +
                "And let's say \"hi bill\" again!";

        String actual = getFileContent(inputFile.getPath());

        assertEquals("The files differ!", expected, actual);
        assertFalse(Files.exists(Paths.get(inputFile.getPath() + ".bck")));
    }

    @Test            //Implementation of test frame #5
    public void FileNotExistTest(){
        String args[] = {"Sup","Hello","--","inputFile1000001.txt" };
        Main.main(args);

        assertEquals("Usage: Replace [-b] [-f] [-l] [-i] <from> <to> -- <filename> [<filename>]*", errStream.toString().trim());
    }

    @Test            //Implementation of test frame #6
    public void EmptyToStrTest() throws Exception {
        File inputFile = createInputFile2();
        String args[] = {"What's up","","--",inputFile.getPath() };
        Main.main(args);

        String expected = " Bill,\n" +
                "This is a test file for the replace utility\n" +
                "Let's make sure it has at least a few lines\n" +
                "so that we can create some interesting test cases...\n" +
                "And let's say \"what's up bill\" again!";

        String actual = getFileContent(inputFile.getPath());

        assertEquals("The files differ!", expected, actual);
        assertFalse(Files.exists(Paths.get(inputFile.getPath() + ".bck")));
    }

    @Test            //Implementation of test frame #7
    public void EmptyToStrBTest() throws Exception {
        File inputFile = createInputFile2();
        String args[] = {"-b","What's up","","--",inputFile.getPath() };
        Main.main(args);

        String expected = " Bill,\n" +
                "This is a test file for the replace utility\n" +
                "Let's make sure it has at least a few lines\n" +
                "so that we can create some interesting test cases...\n" +
                "And let's say \"what's up bill\" again!";

        String actual = getFileContent(inputFile.getPath());

        assertEquals("The files differ!", expected, actual);
        assertTrue(Files.exists(Paths.get(inputFile.getPath() + ".bck")));
    }

    @Test            //Implementation of test frame #8
    public void EmptyToStrFTest() throws Exception {
        File inputFile = createInputFile3();
        String args[] = {"-f","What's up","","--",inputFile.getPath() };
        Main.main(args);

        String expected =" Bill, says Tom\n" +
                "what's up Tom, says Bill\n" +
                "What's up everyone, says John";

        String actual = getFileContent(inputFile.getPath());

        assertEquals("The files differ!", expected, actual);
        assertFalse(Files.exists(Paths.get(inputFile.getPath() + ".bck")));
    }

    @Test            //Implementation of test frame #9
    public void EmptyToStrLTest() throws Exception {
        File inputFile = createInputFile3();
        String args[] = {"-l","What's up","","--",inputFile.getPath() };
        Main.main(args);

        String expected ="What's up Bill, says Tom\n" +
                "what's up Tom, says Bill\n" +
                " everyone, says John";

        String actual = getFileContent(inputFile.getPath());

        assertEquals("The files differ!", expected, actual);
        assertFalse(Files.exists(Paths.get(inputFile.getPath() + ".bck")));
    }

    @Test            //Implementation of test frame #10
    public void EmptyToStrITest() throws Exception {
        File inputFile = createInputFile3();
        String args[] = {"-i","What's up","","--",inputFile.getPath() };
        Main.main(args);

        String expected =" Bill, says Tom\n" +
                " Tom, says Bill\n" +
                " everyone, says John";

        String actual = getFileContent(inputFile.getPath());

        assertEquals("The files differ!", expected, actual);
        assertFalse(Files.exists(Paths.get(inputFile.getPath() + ".bck")));
    }

    @Test            //Implementation of test frame #11
    public void SpaceAndSpaceTest11() throws Exception {

        File inputFile = createInputFile2();
        String args[] = {"What's up","Nice to meet you","--",inputFile.getPath() };
        Main.main(args);

        String expected = "Nice to meet you Bill,\n" +
                "This is a test file for the replace utility\n" +
                "Let's make sure it has at least a few lines\n" +
                "so that we can create some interesting test cases...\n" +
                "And let's say \"what's up bill\" again!";

        String actual = getFileContent(inputFile.getPath());

        assertEquals("The files differ!", expected, actual);
        assertFalse(Files.exists(Paths.get(inputFile.getPath() + ".bck")));
    }

    @Test            //Implementation of test frame #12
    public void StrIncludeSpaceBTest12() throws Exception {

        File inputFile = createInputFile2();
        String args[] = {"-b","What's up","Nice to meet you","--",inputFile.getPath() };
        Main.main(args);

        String expected = "Nice to meet you Bill,\n" +
                "This is a test file for the replace utility\n" +
                "Let's make sure it has at least a few lines\n" +
                "so that we can create some interesting test cases...\n" +
                "And let's say \"what's up bill\" again!";

        String actual = getFileContent(inputFile.getPath());

        assertEquals("The files differ!", expected, actual);
        assertTrue(Files.exists(Paths.get(inputFile.getPath() + ".bck")));
    }

    @Test            //Implementation of test frame #13
    public void StrIncludeSpaceFTest13() throws Exception {
        File inputFile = createInputFile3();
        String args[] = {"-f","What's up","Nice to meet you","--",inputFile.getPath() };
        Main.main(args);

        String expected ="Nice to meet you Bill, says Tom\n" +
                "what's up Tom, says Bill\n" +
                "What's up everyone, says John";

        String actual = getFileContent(inputFile.getPath());

        assertEquals("The files differ!", expected, actual);
        assertFalse(Files.exists(Paths.get(inputFile.getPath() + ".bck")));
    }


    @Test            //Implementation of test frame #14
    public void StrIncludeSpaceLTest14() throws Exception {
        File inputFile = createInputFile3();
        String args[] = {"-l","What's up","Nice to meet you","--",inputFile.getPath() };
        Main.main(args);

        String expected ="What's up Bill, says Tom\n" +
                "what's up Tom, says Bill\n" +
                "Nice to meet you everyone, says John";

        String actual = getFileContent(inputFile.getPath());

        assertEquals("The files differ!", expected, actual);
        assertFalse(Files.exists(Paths.get(inputFile.getPath() + ".bck")));
    }

    @Test            //Implementation of test frame #15
    public void StrIncludeSpaceITest15() throws Exception {
        File inputFile = createInputFile3();
        String args[] = {"-i","What's up","Nice to meet you","--",inputFile.getPath() };
        Main.main(args);

        String expected ="Nice to meet you Bill, says Tom\n" +
                "Nice to meet you Tom, says Bill\n" +
                "Nice to meet you everyone, says John";

        String actual = getFileContent(inputFile.getPath());

        assertEquals("The files differ!", expected, actual);
        assertFalse(Files.exists(Paths.get(inputFile.getPath() + ".bck")));
    }

    @Test            //Implementation of test frame #16
    public void SpaceAndSpecialCharTest16() throws Exception {
        File inputFile = createInputFile3();
        String args[] = {"What's up","'\\/`\"","--",inputFile.getPath() };
        Main.main(args);

        String expected ="'\\/`\" Bill, says Tom\n" +
                "what's up Tom, says Bill\n" +
                "'\\/`\" everyone, says John";

        String actual = getFileContent(inputFile.getPath());

        assertEquals("The files differ!", expected, actual);
        assertFalse(Files.exists(Paths.get(inputFile.getPath() + ".bck")));
    }

    @Test            //Implementation of test frame #17
    public void SpaceAndSpecialCharBTest17() throws Exception {
        File inputFile = createInputFile3();
        String args[] = {"-b","What's up","'\\/`\"","--",inputFile.getPath() };
        Main.main(args);

        String expected ="'\\/`\" Bill, says Tom\n" +
                "what's up Tom, says Bill\n" +
                "'\\/`\" everyone, says John";

        String actual = getFileContent(inputFile.getPath());

        assertEquals("The files differ!", expected, actual);
        assertTrue(Files.exists(Paths.get(inputFile.getPath() + ".bck")));
    }

    @Test            //Implementation of test frame #18
    public void SpaceAndSpecialCharFTest18() throws Exception {
        File inputFile = createInputFile3();
        String args[] = {"-f","What's up","'\\/`\"","--",inputFile.getPath() };
        Main.main(args);

        String expected ="'\\/`\" Bill, says Tom\n" +
                "what's up Tom, says Bill\n" +
                "What's up everyone, says John";

        String actual = getFileContent(inputFile.getPath());

        assertEquals("The files differ!", expected, actual);
        assertFalse(Files.exists(Paths.get(inputFile.getPath() + ".bck")));
    }

    @Test            //Implementation of test frame #19
    public void SpaceAndSpecialCharLTest19() throws Exception {
        File inputFile = createInputFile3();
        String args[] = {"-l","What's up","'\\/`\"","--",inputFile.getPath() };
        Main.main(args);

        String expected ="What's up Bill, says Tom\n" +
                "what's up Tom, says Bill\n" +
                "'\\/`\" everyone, says John";

        String actual = getFileContent(inputFile.getPath());

        assertEquals("The files differ!", expected, actual);
        assertFalse(Files.exists(Paths.get(inputFile.getPath() + ".bck")));
    }

    @Test            //Implementation of test frame #20
    public void SpaceAndSpecialCharITest20() throws Exception {
        File inputFile = createInputFile3();
        String args[] = {"-i","What's up","'\\/`\"","--",inputFile.getPath() };
        Main.main(args);

        String expected ="'\\/`\"  Bill, says Tom\n" +
                "'\\/`\"  Tom, says Bill\n" +
                "'\\/`\" everyone, says John";

        String actual = getFileContent(inputFile.getPath());

        assertEquals("The files differ!", expected, actual);
        assertFalse(Files.exists(Paths.get(inputFile.getPath() + ".bck")));
    }

    @Test            //Implementation of test frame #21
    public void SpaceAndAlphaNumericTest21() throws Exception {
        File inputFile = createInputFile3();
        String args[] = {"What's up","abc123","--",inputFile.getPath() };
        Main.main(args);

        String expected ="abc123 Bill, says Tom\n" +
                "what's up Tom, says Bill\n" +
                "abc123 everyone, says John";

        String actual = getFileContent(inputFile.getPath());

        assertEquals("The files differ!", expected, actual);
        assertFalse(Files.exists(Paths.get(inputFile.getPath() + ".bck")));
    }

    @Test            //Implementation of test frame #22
    public void SpaceAndAlphaNumericBTest22() throws Exception {
        File inputFile = createInputFile3();
        String args[] = {"-b","What's up","abc123","--",inputFile.getPath() };
        Main.main(args);

        String expected ="abc123 Bill, says Tom\n" +
                "what's up Tom, says Bill\n" +
                "abc123 everyone, says John";

        String actual = getFileContent(inputFile.getPath());

        assertEquals("The files differ!", expected, actual);
        assertTrue(Files.exists(Paths.get(inputFile.getPath() + ".bck")));
    }

    @Test            //Implementation of test frame #23
    public void SpaceAndAlphaNumericFTest23() throws Exception {
        File inputFile = createInputFile3();
        String args[] = {"-f","What's up","abc123","--",inputFile.getPath() };
        Main.main(args);

        String expected ="abc123 Bill, says Tom\n" +
                "what's up Tom, says Bill\n" +
                "What's up everyone, says John";

        String actual = getFileContent(inputFile.getPath());

        assertEquals("The files differ!", expected, actual);
        assertFalse(Files.exists(Paths.get(inputFile.getPath() + ".bck")));
    }

    @Test            //Implementation of test frame #24
    public void SpaceAndAlphaNumericLTest24() throws Exception {
        File inputFile = createInputFile3();
        String args[] = {"-l","What's up","abc123","--",inputFile.getPath() };
        Main.main(args);

        String expected ="What's up Bill, says Tom\n" +
                "what's up Tom, says Bill\n" +
                "abc123 everyone, says John";

        String actual = getFileContent(inputFile.getPath());

        assertEquals("The files differ!", expected, actual);
        assertFalse(Files.exists(Paths.get(inputFile.getPath() + ".bck")));
    }

    @Test            //Implementation of test frame #25
    public void SpaceAndAlphaNumericITest25() throws Exception {
        File inputFile = createInputFile3();
        String args[] = {"-i","What's up","abc123","--",inputFile.getPath() };
        Main.main(args);

        String expected ="abc123 Bill, says Tom\n" +
                "abc123 Tom, says Bill\n" +
                "abc123 everyone, says John";

        String actual = getFileContent(inputFile.getPath());

        assertEquals("The files differ!", expected, actual);
        assertFalse(Files.exists(Paths.get(inputFile.getPath() + ".bck")));
    }

    @Test            //Implementation of test frame #26
    public void SpecialCharAndEmptyToStrTest26() throws Exception {
        File inputFile = createInputFile3();
        String args[] = {"'","","--",inputFile.getPath() };
        Main.main(args);

        String expected ="Whats up Bill, says Tom\n" +
                "whats up Tom, says Bill\n" +
                "Whats up everyone, says John";

        String actual = getFileContent(inputFile.getPath());

        assertEquals("The files differ!", expected, actual);
        assertFalse(Files.exists(Paths.get(inputFile.getPath() + ".bck")));
    }

    @Test            //Implementation of test frame #27
    public void SpecialCharAndEmptyToStrBTest27() throws Exception {
        File inputFile = createInputFile3();
        String args[] = {"-b","'","","--",inputFile.getPath() };
        Main.main(args);

        String expected ="Whats up Bill, says Tom\n" +
                "whats up Tom, says Bill\n" +
                "Whats up everyone, says John";

        String actual = getFileContent(inputFile.getPath());

        assertEquals("The files differ!", expected, actual);
        assertTrue(Files.exists(Paths.get(inputFile.getPath() + ".bck")));
    }

    @Test            //Implementation of test frame #28
    public void SpecialCharAndEmptyToStrFTest28() throws Exception {
        File inputFile = createInputFile3();
        String args[] = {"-f","'","","--",inputFile.getPath() };
        Main.main(args);

        String expected ="Whats up Bill, says Tom\n" +
                "what's up Tom, says Bill\n" +
                "What's up everyone, says John";

        String actual = getFileContent(inputFile.getPath());

        assertEquals("The files differ!", expected, actual);
        assertFalse(Files.exists(Paths.get(inputFile.getPath() + ".bck")));
    }

    @Test            //Implementation of test frame #29
    public void SpecialCharAndEmptyToStrLTest29() throws Exception {
        File inputFile = createInputFile3();
        String args[] = {"-l","'","","--",inputFile.getPath() };
        Main.main(args);

        String expected ="What's up Bill, says Tom\n" +
                "what's up Tom, says Bill\n" +
                "Whats up everyone, says John";

        String actual = getFileContent(inputFile.getPath());

        assertEquals("The files differ!", expected, actual);
        assertFalse(Files.exists(Paths.get(inputFile.getPath() + ".bck")));
    }

    @Test            //Implementation of test frame #30
    public void SpecialCharAndEmptyToStrITest30() throws Exception {
        File inputFile = createInputFile3();
        String args[] = {"-i","'","","--",inputFile.getPath() };
        Main.main(args);

        String expected ="Whats up Bill, says Tom\n" +
                "whats up Tom, says Bill\n" +
                "Whats up everyone, says John";

        String actual = getFileContent(inputFile.getPath());

        assertEquals("The files differ!", expected, actual);
        assertFalse(Files.exists(Paths.get(inputFile.getPath() + ".bck")));
    }

    @Test            //Implementation of test frame #31
    public void SpecialCharAndSpaceTest31() throws Exception {
        File inputFile = createInputFile3();
        String args[] = {"'"," ","--",inputFile.getPath() };
        Main.main(args);

        String expected ="What s up Bill, says Tom\n" +
                "what s up Tom, says Bill\n" +
                "What s up everyone, says John";

        String actual = getFileContent(inputFile.getPath());

        assertEquals("The files differ!", expected, actual);
        assertFalse(Files.exists(Paths.get(inputFile.getPath() + ".bck")));
    }

    @Test            //Implementation of test frame #32
    public void SpecialCharAndSpaceBTest32() throws Exception {
        File inputFile = createInputFile3();
        String args[] = {"-b","'"," ","--",inputFile.getPath() };
        Main.main(args);

        String expected ="What s up Bill, says Tom\n" +
                "what s up Tom, says Bill\n" +
                "What s up everyone, says John";

        String actual = getFileContent(inputFile.getPath());

        assertEquals("The files differ!", expected, actual);
        assertTrue(Files.exists(Paths.get(inputFile.getPath() + ".bck")));
    }

    @Test            //Implementation of test frame #33
    public void SpecialCharAndSpaceFTest33() throws Exception {
        File inputFile = createInputFile3();
        String args[] = {"-f","'"," ","--",inputFile.getPath() };
        Main.main(args);

        String expected ="What s up Bill, says Tom\n" +
                "what's up Tom, says Bill\n" +
                "What's up everyone, says John";

        String actual = getFileContent(inputFile.getPath());

        assertEquals("The files differ!", expected, actual);
        assertFalse(Files.exists(Paths.get(inputFile.getPath() + ".bck")));
    }

    @Test            //Implementation of test frame #34
    public void SpecialCharAndSpaceLTest34() throws Exception {
        File inputFile = createInputFile3();
        String args[] = {"-l","'"," ","--",inputFile.getPath() };
        Main.main(args);

        String expected ="What's up Bill, says Tom\n" +
                "what's up Tom, says Bill\n" +
                "What s up everyone, says John";

        String actual = getFileContent(inputFile.getPath());

        assertEquals("The files differ!", expected, actual);
        assertFalse(Files.exists(Paths.get(inputFile.getPath() + ".bck")));
    }

    @Test            //Implementation of test frame #35
    public void SpecialCharAndSpaceITest35() throws Exception {
        File inputFile = createInputFile3();
        String args[] = {"-i","'"," ","--",inputFile.getPath() };
        Main.main(args);

        String expected ="What s up Bill, says Tom\n" +
                "what s up Tom, says Bill\n" +
                "What s up everyone, says John";

        String actual = getFileContent(inputFile.getPath());

        assertEquals("The files differ!", expected, actual);
        assertFalse(Files.exists(Paths.get(inputFile.getPath() + ".bck")));
    }

    @Test            //Implementation of test frame #36
    public void SpecialCharAndSpecialCharTest36() throws Exception {
        File inputFile = createInputFile3();
        String args[] = {"'",",","--",inputFile.getPath() };
        Main.main(args);

        String expected ="What,s up Bill, says Tom\n" +
                "what,s up Tom, says Bill\n" +
                "What,s up everyone, says John";

        String actual = getFileContent(inputFile.getPath());

        assertEquals("The files differ!", expected, actual);
        assertFalse(Files.exists(Paths.get(inputFile.getPath() + ".bck")));
    }

    @Test            //Implementation of test frame #37
    public void SpecialCharAndSpecialCharBTest37() throws Exception {
        File inputFile = createInputFile3();
        String args[] = {"-b","'",",","--",inputFile.getPath() };
        Main.main(args);

        String expected ="What,s up Bill, says Tom\n" +
                "what,s up Tom, says Bill\n" +
                "What,s up everyone, says John";

        String actual = getFileContent(inputFile.getPath());

        assertEquals("The files differ!", expected, actual);
        assertTrue(Files.exists(Paths.get(inputFile.getPath() + ".bck")));
    }

    @Test            //Implementation of test frame #38
    public void SpecialCharAndSpecialCharFTest38() throws Exception {
        File inputFile = createInputFile3();
        String args[] = {"-f","'",",","--",inputFile.getPath() };
        Main.main(args);

        String expected ="What,s up Bill, says Tom\n" +
                "what's up Tom, says Bill\n" +
                "What's up everyone, says John";

        String actual = getFileContent(inputFile.getPath());

        assertEquals("The files differ!", expected, actual);
        assertFalse(Files.exists(Paths.get(inputFile.getPath() + ".bck")));
    }

    @Test            //Implementation of test frame #39
    public void SpecialCharAndSpecialCharLTest39() throws Exception {
        File inputFile = createInputFile3();
        String args[] = {"-l","'",",","--",inputFile.getPath() };
        Main.main(args);

        String expected ="What's up Bill, says Tom\n" +
                "what's up Tom, says Bill\n" +
                "What,s up everyone, says John";

        String actual = getFileContent(inputFile.getPath());

        assertEquals("The files differ!", expected, actual);
        assertFalse(Files.exists(Paths.get(inputFile.getPath() + ".bck")));
    }

    @Test            //Implementation of test frame #40
    public void SpecialCharAndSpecialCharLTest40() throws Exception {
        File inputFile = createInputFile3();
        String args[] = {"-i","'",",","--",inputFile.getPath() };
        Main.main(args);

        String expected ="What,s up Bill, says Tom\n" +
                "what,s up Tom, says Bill\n" +
                "What,s up everyone, says John";

        String actual = getFileContent(inputFile.getPath());

        assertEquals("The files differ!", expected, actual);
        assertFalse(Files.exists(Paths.get(inputFile.getPath() + ".bck")));
    }

}
