package com.example.demo;

import com.example.demo.models.Chat;
import com.example.demo.models.ID;
import com.example.demo.models.Message;
import com.example.demo.models.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.TreeSet;
import java.util.NoSuchElementException;

@RestController
public class RestFullController {

    static Gson gson = new Gson();

    // Пути к файлам сохранения
    final static String fileNameUsers = "src/main/java/com/example/demo/files/users.json";
    final static String fileNameChats = "src/main/java/com/example/demo/files/chats.json";
    final static String fileNameMessages = "src/main/java/com/example/demo/files/messages.json";

    // Листы где обьекты будут храниться
    public static ArrayList<User> usersArrayList = new ArrayList<>();
    public static ArrayList<Chat> chatsArrayList = new ArrayList<>();
    public static ArrayList<Message> messagesArrayList = new ArrayList<>();
    // public static ArrayList<User> userArrayList= new ArrayList<>();

    // Конструктор конроллера - деиствия при запуске программы
    public RestFullController() {
        String s1, s2, s3;
        Gson gson = new Gson();
        try {
            s1 = new String(Files.readAllBytes(Paths.get(fileNameUsers)));
            s2 = new String(Files.readAllBytes(Paths.get(fileNameChats)));
            s3 = new String(Files.readAllBytes(Paths.get(fileNameMessages)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        usersArrayList = gson.fromJson(s1, new TypeToken<ArrayList<User>>() {
        }.getType());
        chatsArrayList = gson.fromJson(s2, new TypeToken<ArrayList<Chat>>() {
        }.getType());
        messagesArrayList = gson.fromJson(s3, new TypeToken<ArrayList<Message>>() {
        }.getType());

    }

    private static void updateDB(String nameFile) {
        Gson gson = new Gson();
        FileWriter fw;
        ArrayList<?> ent = new ArrayList<>();
        switch (nameFile) {
            case "users":
                ent = usersArrayList;
                break;
            case "chats":
                ent = chatsArrayList;
                break;
            case "messages":
                ent = messagesArrayList;
                break;
            default:
                System.out.println(
                        "Please, take notice of the fact that you're a retard since you're not using function as intended.");
        }
        try {
            fw = new FileWriter("src/main/java/com/example/demo/files/" + nameFile + ".json");
            StringBuilder l = new StringBuilder("[");
            for (int i = 0; i < ent.size(); i++) {
                if (i == ent.size() - 1) {
                    l.append(gson.toJson(ent.get(i)));
                } else {
                    l.append(gson.toJson(ent.get(i))).append(",");
                }
            }
            fw.write(l + "]");
            fw.close();
        } catch (IOException e) {
            System.out.println("WARNING, BIG SAD HAPPENED");
        }
    }

    // http://localhost:8080/authorization?login=loginExample&password=220512281
    @GetMapping(value = "/authorization", params = {"login", "password"})
    public String authorization(
            @RequestParam("login") String login,
            @RequestParam("password") String password) {
        for (User user : usersArrayList) {
            if (user.getLogin().equals(login) &&
                    user.getPassword().equals(password)) {
                return user.getName() + "-";
            }
        }
        return "false";
    }

    // http://localhost:8080/registration?name=Stas&login=loginExample1&password=passExample1
    // http://localhost:8080/registration?name=Yuri&login=loginExample2&password=passExample2
    @GetMapping(value = "/registration", params = {"name", "login", "password"})
    public String registration(
            @RequestParam("name") String name,
            @RequestParam("login") String login,
            @RequestParam("password") String password) {
        int n = usersArrayList.size();
        usersArrayList.add(new User(("U" + n), false, name, login, password, new ArrayList<>()));
        update("users");
        return gson.toJson(usersArrayList.get(n));
    }

    // http://localhost:8080/createNewChatWithTwoUsers?user1=Yuri&user2=Stas
    @GetMapping(value = "/createNewChatWithTwoUsers", params = {"user1", "user2"})
    public String createNewChatWithTwoUsers(
            @RequestParam("user1") String user1,
            @RequestParam("user2") String user2) {
        int n = usersArrayList.size();

        chatsArrayList
                .add(new Chat(("C" + n), false, new ArrayList<String>(), new TreeSet<>(List.of(user1, user2))));


        update("chats");
        return "true";
    }

    // http://localhost:8080/createNewMessage?user1=Yuri&user2=Stas
    @GetMapping(value = "/createNewMessage", params = {"user1", "user2", "text", "chatId"})
    public String createNewMessage(
            @RequestParam("user1") String user1,
            @RequestParam("user2") String user2,
            @RequestParam("text") String text,
            @RequestParam("chatId") String chatId) {
        int n = messagesArrayList.size();

        messagesArrayList
                .add(new Message(("M" + n), false, text, new Date().getTime(), user1, user2, new TreeSet<>()));
        chatsArrayList.get(n).messages.add(("M" + n));

        update("messages");
        return "true";
    }

    // Методы админа
    @GetMapping(value = "/getAllUsers")
    public Iterable<User> getAllUsers() {
        return usersArrayList;
    }

    @GetMapping(value = "/getAllChats")
    public Iterable<Chat> getAllChats() {
        return chatsArrayList;
    }
    //

    // Методы Админа и Юзера
    @GetMapping(value = "/getAllChatsByUser", params = {"userId"})
    public Iterable<Chat> getAllChatsByUser(
            @RequestParam("userId") String userId) {
        ArrayList<String> f = usersArrayList.get(getIdFromId(userId)).chatArrayList;
        return (Iterable<Chat>) chatsArrayList
                .stream()
                .filter(it -> f.contains(it.id));
    }

    @GetMapping(value = "/getAllCMessagesByChat", params = {"chatId"})
    public Iterable<Message> getAllCMessagesByChat(
            @RequestParam("chatId") String chatId) {
        ArrayList<String> f = Objects.requireNonNull(chatsArrayList.stream()
                .filter(it -> chatId.equals(it.id))
                .findAny()
                .orElse(null)).messages;
        return (Iterable<Message>) messagesArrayList
                .stream()
                .filter(it -> f.contains(it.id));
    }
    //

    // Helper function for finding User/Chat/Message object in
    // user-/chat-/message-ArrayList
    private static <T extends ID> T findInListById(ArrayList<T> list, String objId) throws NoSuchElementException {
        return list.stream().filter(listObj -> listObj.id.equals(objId)).findFirst().orElseThrow();
    }
}