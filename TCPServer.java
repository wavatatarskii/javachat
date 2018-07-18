package Downloads;
import java.io.*;
import java.net.*;

public class TCPServer
{
    private static final int TIME_SEND_SLEEP = 100;
    private static final int COUNT_TO_SEND = 10;
    public static final int READ_BUFFER_SIZE = 50;

    private ServerSocket servSocket;
    private FileWriter logger;

    public static final int PORT = 2500;

    public static void main(String[] args)
    {
        TCPServer tcpServer = new TCPServer(args[0]);
        tcpServer.go();
    }

    public TCPServer(String path)
    {
        try
        {
            File file = new File("D:\\Dropbox\\КАИ\\Java\\Lab4\\src\\ServerSettings.txt");
            FileReader fr = new FileReader(file);
            char[] input = new char[(int)file.length()];
            fr.read(input);

            servSocket = new ServerSocket(Integer.parseInt(new String(input)));
            fr.close();
        }
        catch(IOException e)
        {
            System.err.println("Не удаётся открыть сокет для сервера: " + e.toString());
        }

        try
        {
            File file = new File(path);
            file.delete();
            logger = new FileWriter(file, true);
        }
        catch (IOException e)
        {
            System.err.println("Не удалось создать файл логов сервера");
        }
    }

    public void go()
    {
        class Listener implements Runnable
        {
            Socket socket;

            public Listener(Socket aSocket)
            {
                socket = aSocket;
            }

            public void run()
            {
                try
                {
                    System.out.println("Слушатель запущен");

                    // Приём выражения от клиента
                    double result = 0;
                    boolean isOk = true, isReaded = false;
                    String message, operation;
                    DataInputStream input = new DataInputStream(socket.getInputStream());

                    message         = input.readUTF();
                    logger.write(message);
                    operation       = message.substring(message.length() - 1);
                    try
                    {
                        result = Double.parseDouble(message.substring(0,message.length()-1));
                    }
                    catch (NumberFormatException e)
                    {
                        isOk        = false;
                        isReaded    = true;
                    }


                    while (!isReaded)
                    {
                        message = input.readUTF();
                        logger.write(message);

                        switch (operation)
                        {
                            case "+":
                                try {
                                    result += Double.parseDouble(message.substring(0,message.length()-1));
                                }
                                catch (NumberFormatException e)
                                {
                                    isOk = false;
                                    isReaded = true;
                                }
                                break;
                            case "-":
                                try
                                {
                                    result -= Double.parseDouble(message.substring(0,message.length()-1));
                                }
                                catch (NumberFormatException e)
                                {
                                    isOk = false;
                                    isReaded = true;
                                }
                                break;
                            case "=":
                                isReaded = true;
                                break;
                            default:
                                isOk = false;
                                isReaded = true;
                                break;
                        }

                        operation = message.substring(message.length() - 1);
                        if(operation.equals("="))
                            isReaded = true;
                    }

                    // Ответ клиенту
                    DataOutputStream writer = new DataOutputStream(
                            socket.getOutputStream());

                    if(!isOk)
                    {
                        writer.writeUTF("Некорректный ввод");
                        writer.close();
                        logger.close();
                        return;
                    }

                    writer.writeUTF(Double.toString(result));
                    logger.close();
                }
                catch(IOException e)
                {
                    System.err.println("Исключение: " + e.toString());
                }
            }
        }

        System.out.println("Сервер запущен...");

        try
        {
            while(true)
            {
                new Thread(
                        new Listener(
                                servSocket.accept()))
                        .start();
            }
        }
        catch(IOException e)
        {
            System.err.println("Исключение: " + e.toString());
        }
    }
}