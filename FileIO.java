import java.io.*;
import java.util.ArrayList;
import java.util.Base64;

public class FileIO {
    
    public static ArrayList<FunkoPop> readFromFile(String fileName) throws IOException, ClassNotFoundException {
        FileInputStream fileInputStream = new FileInputStream(fileName);
        ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
        ArrayList<FunkoPop> collection = (ArrayList<FunkoPop>) objectInputStream.readObject();
        objectInputStream.close();
        fileInputStream.close();
        return collection;
    }
    
    public static void writeToFile(String fileName, ArrayList<FunkoPop> collection) throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream(fileName);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
        objectOutputStream.writeObject(collection);
        objectOutputStream.close();
        fileOutputStream.close();
    }
    
    public static byte[] serialize(Object obj) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(out);
        os.writeObject(obj);
        return out.toByteArray();
    }

    public static Object deserialize(String previousStateStr) throws IOException, ClassNotFoundException {
        byte[] decodedBytes = Base64.getDecoder().decode(previousStateStr);
        ByteArrayInputStream in = new ByteArrayInputStream(decodedBytes);
        ObjectInputStream is = new ObjectInputStream(in);
        return is.readObject();
    }


}
