package Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static Utils.Utils.READ_ACTION;

public class Logger {
    FileOutputStream fileOutputStream;
    File file;
    public Logger(String name){

        try {
            file = new File(name);
            if (file.createNewFile()) {
                System.out.println("File created: " + file.getName());

            } else {
                System.out.println("File already exists.");
            }
            this.fileOutputStream = new FileOutputStream(file);

        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public void writeLog(Message message, int port){
        String line ;
        if (message.getAction().equals(READ_ACTION)){
           line = port + ": READ received from node "+ message.getPort() +" with key : " + message.getLine() + "\n";
        }else{
            line = port + ": WRITE received from node "+ message.getPort() +" with key : " + message.getLine() + " and value " + message.getValue()+ "\n";
        }
        try {
            this.fileOutputStream.write(line.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public void closeLog(){
        try {
            this.fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
