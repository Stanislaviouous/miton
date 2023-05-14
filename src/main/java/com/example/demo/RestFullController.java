package com.example.demo;

import com.example.demo.models.Chat;
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
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
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
//    public static ArrayList<User> userArrayList= new ArrayList<>();


    // Конструктор конроллера - деиствия при запуске программы
    public RestFullController() {
        String s = "", s1 = "", s2 = "", s3 = "";
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

    public static void update(String nameFile) {
        Gson gson = new Gson();
        FileWriter fw;
        ArrayList<?> ent = new ArrayList<>();
        switch (nameFile) {
            case "users":
                ent =  usersArrayList;
                break;
            case "chats":
                ent =  chatsArrayList;
                break;
            case "messages":
                ent =  messagesArrayList;
                break;
            default:
                ent =  new ArrayList<>();
        }
        try {
            fw = new FileWriter("src/main/java/com/example/demo/files/" + nameFile + ".json");
            String l = "[";
            for (int i = 0; i < ent.size(); i++) {
                if (i == ent.size() - 1) {
                    l += gson.toJson(ent.get(i));
                } else {
                    l += gson.toJson(ent.get(i)) + ",";
                }
            }
            fw.write(l + "]");
            fw.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // http://localhost:8080/authorization?login=loginExample&password=220512281
    @RequestMapping(
            value = "/authorization",
            params = {"login", "password"},
            method = GET)
    @ResponseBody
    public String authorization(
            @RequestParam("login") String login,
            @RequestParam("password") String password) {
        for (User user : usersArrayList) {
            if (user.getLogin().equals(login) &&
                    user.getPassword().hashCode() == Integer.parseInt(password)) {
                return "" + user.getName() + "-";
            }
        }
        return "false";
    }

    // http://localhost:8080/registration?name=Stas&login=loginExample1&password=passExample1
    // http://localhost:8080/registration?name=Yuri&login=loginExample2&password=passExample2
    @RequestMapping(
            value = "/registration",
            params = {"name", "login", "password"},
            method = GET)
    @ResponseBody
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
    @RequestMapping(
            value = "/createNewChatWithTwoUsers",
            params = {"user1", "user2"},
            method = GET)
    @ResponseBody
    public String createNewChatWithTwoUsers(
            @RequestParam("user1") String user1,
            @RequestParam("user2") String user2) {
        int n = usersArrayList.size();
        try {
            chatsArrayList.add(new Chat(("C" + n), false, new ArrayList<String>(), new TreeSet<>(List.of(user1, user2))));
        } catch (Error | Exception e) {
            return "false";
        }
        update("chats");
        return "true";
    }

    // http://localhost:8080/createNewMessage?user1=Yuri&user2=Stas
    @RequestMapping(
            value = "/createNewMessage",
            params = {"user1", "user2", "text", "chatId"},
            method = GET)
    @ResponseBody
    public String sendNewMessageInChat(
            @RequestParam("user1") String user1,
            @RequestParam("user2") String user2,
            @RequestParam("text") String text,
            @RequestParam("chatId") String chatId) {
        int n = messagesArrayList.size();
        try {
            messagesArrayList.add(new Message(("M" + n), false, text, new Date().getTime(), user1, user2, new TreeSet<>()));
            chatsArrayList.get(n).messages.add(("M" + n));
        } catch (Error | Exception e) {
            return "false";
        }
        update("messages");
        return "true";
    }

    // Методы админа
    @RequestMapping(
            value = "/getAllUsers",
            method = GET)
    @ResponseBody
    public Iterable<User> returnUsers() {
        return usersArrayList;
    }

    @RequestMapping(
            value = "/getAllChats",
            method = GET)
    @ResponseBody
    public Iterable<Chat> returnChats() {
        return chatsArrayList;
    }
    //

    // Методы Админа и Юзера
    @RequestMapping(
            value = "/getAllChatsByUser",
            params = {"userId"},
            method = GET)
    @ResponseBody
    public Iterable<Chat> getAllChatsByUser(
            @RequestParam("userId") String userId) {
        ArrayList<String> f = usersArrayList.get(getIdFromId(userId)).chatArrayList;
        return (Iterable<Chat>) chatsArrayList
                .stream()
                .filter(it -> f.contains(it.id));
    }

    @RequestMapping(
            value = "/getAllCMessagesByChat",
            params = {"chatId"},
            method = GET)
    @ResponseBody
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

    public int getIdFromId(String s) {
        return Integer.parseInt(s.substring(1));
    }

}