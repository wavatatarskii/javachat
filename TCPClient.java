package Downloads;
import java.io.*;
import java.net.*;
import java.util.Scanner;

public class TCPClient implements Runnable
{
    private String name = null;
    private FileWriter logger;

    public static final int READ_BUFFER_SIZE = 10;

    public static String HOST = "localhost";
    public static int PORT = 2500;

    public TCPClient(String s, String host, int port, String path)
    {
        name = s;
        HOST = host;
        PORT = port;

        File file = new File(path);
        try
        {
            file.delete();
            logger = new FileWriter(file, true);
        }
        catch (IOException e)
        {
            System.err.println("Не удалось создать файл логов клиента");
        }
    }

    public void run()
    {
        String readed;
        StringBuffer strBuff = new StringBuffer();

        try
        {
            Socket socket = new Socket(HOST, PORT);

            Scanner br = new Scanner(System.in);
            System.out.print("Введите выражение(*число**операция*)(ввод завершится при операции '='):\n");
            String expression;

            DataOutputStream out= new DataOutputStream(socket.getOutputStream());
            do {
                expression = br.nextLine();
                out.writeUTF(expression);
            }
            while (!expression.substring(expression.length() - 1).equals("="));

            DataInputStream reader = new DataInputStream(
                    socket.getInputStream());
            readed = reader.readUTF();

            try
            {
                this.logger.write("Клиент " + name + " прочёл: " + readed);
                this.logger.close();
            }
            catch (IOException e)
            {
                System.err.println("Не удалось произветси запись в  файл логов клиента");
            }
        }
        catch (UnknownHostException e)
        {
            System.err.println("Исключение: " + e.toString());
        }
        catch (IOException e)
        {
            System.err.println("Исключение: " + e.toString());
        }
    }

    // Параметры командной строки -  Run->Edit Configurations->Program arguments
    public static void main(String[] args)
    {
        new Thread(new TCPClient("qwe", args[0], Integer.parseInt(args[1]), args[2])).start();
    }
}