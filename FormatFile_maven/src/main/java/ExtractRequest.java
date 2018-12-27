import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.io.*;
import java.util.Properties;

/**
 *
 */
public class ExtractRequest {
    // final String PATH_DIR = "c:\\WORK\\Java\\FormatFile\\";
    private String PATH_DIR = "./LOG/";
    private String RESULT_DIR = "./RESULT/";
    private String agree = "";
    private List<Integer> startPoint = new ArrayList<Integer>();
    private List<Integer> endPoint = new ArrayList<Integer>();

    /**
     *
     * @param agree
     */
    //сеттер
    public void setAgree(String agree) {
        this.agree = agree;
    }

    /**
     *
     * @return
     */
    //геттер
    public String getAgree() {
        return agree;
    }

    ExtractRequest() {

    }
    ExtractRequest(String config){
        try {
            File f = new File(config);
            if (f.exists()) {
                FileInputStream fis = new FileInputStream(config);
                Properties conf = new Properties();
                conf.load(fis);
                PATH_DIR = conf.getProperty("LOG_DIR");
                RESULT_DIR = conf.getProperty("RESULT_DIR");
            }
        }
        catch (IOException ioe) {
            System.out.println("Trouble reading from the file: " + ioe.getMessage());
        }

    };

    //чтение файлов директории
    public void findFile() {
        deleteResultFile(); //удалить результат предыдущей работы
        File dir = checkPATH();
        String[] _file = dir.list();
        for (int i = 0; i < _file.length; i++) {
            if (_file[i].endsWith(".txt") && _file[i].startsWith("dess")) {
                System.out.println(_file[i]);
                readFile(PATH_DIR + _file[i]);
            }
        }
    }

    //чтение содержимого фоайла
    private void readFile(String fl) {
        try {
            Scanner in = new Scanner(Paths.get(fl), "UTF-8");
            String str = new String();
            int s = 1;
            while (in.hasNextLine()) {
                str = in.nextLine();
                if (findAgreeInStr(str)){
                    System.out.println(str);
                    //String replace_str = str.replace("windows-1251","UTF-8");
                    String replace_str = str;
                    FileWriter resultFile  = createOutputFile(s);     //создать файл для вывода
                    fillArraySymbol(replace_str,"<",startPoint);
                    fillArraySymbol(replace_str,">",endPoint);
                    parseStr(startPoint,endPoint,replace_str,resultFile);    //вывести в файл данными договора
                    closeOutputFile(resultFile); //закрыть файл
                    s++;
                }
                ;
            }
        } catch (IOException ioe) {
            System.out.println("Trouble reading from the file: " + ioe.getMessage());
        }
    }

    //ф-ция провряет, есть ли искомый договор в строке
    private boolean findAgreeInStr(String str) {
        if (str.indexOf(agree) != -1 && str.indexOf("?xml version=") != -1) {
            return true;
        } else
            return false;
    }

    //заполнить массивы вхождения символов для дальнейшего разделения строки
    private void fillArraySymbol(String str, String symbol, List<Integer> list) {
        list.clear();
        int lastIndex = 0;
        while (lastIndex != -1) {
            lastIndex = str.indexOf(symbol, lastIndex);
            if (lastIndex != -1) {
                list.add(lastIndex);
                lastIndex += 1;
            }
        }
    }

    private void parseStr(List<Integer> start, List<Integer> end, String str, FileWriter outputFile){
        // str = str.replace("windows-1251","UTF-8");
        int n = 0;
        for(int i = 0; i < start.size(); i++){
            int _start = start.get(i);
            int _end = end.get(i);
            if(_start - n > 1 && n > 0) insertBlockInFile(outputFile,"\t" +str.substring(n+1,_start)); //вывести в файл кусок  (n+1,start[i]-1);
            insertBlockInFile(outputFile, str.substring(_start, _end + 1)); //вывести в файл кусок  (start[i],end[i]);
            n = end.get(i);
        }
    }

    private FileWriter createOutputFile(int seq){
        try {
            //deleteResultFile();
            File file = new File(RESULT_DIR,"result_" + agree + "_" + seq + ".xml");
            file.createNewFile();
            FileWriter output = new FileWriter(file, true);
            return output;
        }
        catch (IOException ioe) {
            System.out.println("Trouble reading from the file: " + ioe.getMessage());
            return null;
        }
    }

    private void closeOutputFile(FileWriter file){
        try {
            file.close();
        } catch (IOException ioe) {
            System.out.println("Trouble reading from the file: " + ioe.getMessage());
        }
    }

    private void insertBlockInFile(FileWriter file, String str){
        try{
            System.out.println(str);
            file.write(str);
            file.append('\n');
        } catch (IOException ioe) {
            System.out.println("Trouble reading from the file: " + ioe.getMessage());
        }
    }

    private void deleteResultFile(){
        File dir = new File(RESULT_DIR);
        if(!dir.exists()){
            //создать каталог
            dir.mkdir();
        }
        System.out.println(dir.getAbsolutePath());
        String[] _file = dir.list();
        for (int i = 0; i < _file.length; i++) {
            System.out.println(_file[i]);
            if (_file[i].endsWith(".xml") && _file[i].startsWith("result")) {
                new File(RESULT_DIR + _file[i]).delete();
            }
        }
    }

    private File checkPATH(){
        File folder = new File(PATH_DIR);
        if(!folder.exists()){
            //создать каталог
            folder.mkdir();
        }
        return folder;
    }
}
