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
/*
public class Controller {

    final static String fileNameProducts = "src/main/resources/templates/theme/assets/goods.json";
    final static String fileNameOrders = "src/main/resources/database/orders.json";
    final static String fileNameDelivery = "src/main/resources/database/deliverys.json";

    public static ArrayList<Product> productArrayList = new ArrayList<>();
    public static ArrayList<Order> orderArrayList = new ArrayList<>();
    public static ArrayList<Delivery> deliveryArrayList = new ArrayList<>();
    public static ArrayList<User> userArrayList= new ArrayList<>();

    public static TreeMap<String, Delivery> couriers = new TreeMap<>();
    public static TreeMap<String, String> statusOfDelivery = new TreeMap<>();

    public static TreeMap<String, String> linkMap = new TreeMap<>();

    public Controller() throws IOException {
        String s = "", s1 = "", s2 = "", s3 = "";
        Gson gson = new Gson();
        try {
            s1 = new String(Files.readAllBytes(Paths.get(fileNameProducts)));
            s2 = new String(Files.readAllBytes(Paths.get(fileNameOrders)));
            s3 = new String(Files.readAllBytes(Paths.get(fileNameDelivery)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        productArrayList = gson.fromJson(s1, new TypeToken<ArrayList<Product>>(){}.getType());
        orderArrayList = gson.fromJson(s2, new TypeToken<ArrayList<Order>>(){}.getType());
        deliveryArrayList = gson.fromJson(s3, new TypeToken<ArrayList<Delivery>>(){}.getType());
        userArrayList.add(new User("Оля Тиньков", "a", "a", "admin"));
        userArrayList.add(new User("Герда Греф", "s", "s", "storeman"));
        userArrayList.add(new User("Олеся Тиньков", "d", "d", "dispatcher"));
        userArrayList.add(new User("Aleksandr_Ivanovich", "c1", "c1", "courier"));
        userArrayList.add(new User("Nara", "c2", "c2", "courier"));

        linkMap.put("", "");
    }

    @GetMapping("/home")
    public String greeting(@RequestParam(name="name", required=false, defaultValue="World") String name, Model model) {
        model.addAttribute("name", name);
        return "index";
    }

    public static void update(){
        Gson gson = new Gson();
        FileWriter fw;
        try {
            fw = new FileWriter("src/main/resources/templates/theme/assets/goods.json");
            String l = "[";
            for (int i = 0; i < productArrayList.size(); i++) {
                if (i == productArrayList.size() - 1) {
                    l += gson.toJson(productArrayList.get(i));
                }
                else {
                    l += gson.toJson(productArrayList.get(i)) + ",";
                }
            }
            fw.write(l + "]");
            fw.close(); //// Поговорить со стасиком
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static void update1(){
        Gson gson = new Gson();
        FileWriter fw;
        try {
            fw = new FileWriter( "src/main/resources/database/orders.json");
            String l = "[";
            for (int i = 0; i < orderArrayList.size(); i++) {
                if (i == orderArrayList.size() - 1) {
                    l += gson.toJson(orderArrayList.get(i));
                }
                else {
                    l += gson.toJson(orderArrayList.get(i)) + ",";
                }
            }
            fw.write(l + "]");
            fw.close(); //// Поговорить со стасиком
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static void update2(){
        Gson gson = new Gson();
        FileWriter fw;
        try {
            fw = new FileWriter( "src/main/resources/database/deliverys.json");
            String l = "[";
            for (int i = 0; i < deliveryArrayList.size(); i++) {
                if (i == deliveryArrayList.size() - 1) {
                    l += gson.toJson(deliveryArrayList.get(i));
                }
                else {
                    l += gson.toJson(deliveryArrayList.get(i)) + ",";
                }
            }
            fw.write(l + "]");
            fw.close(); //// Поговорить со стасиком
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
/*
    @GetMapping("/joinOrders")
    @ResponseBody
    public String joinOrders(@RequestParam String idDelivery) {
        int index = -1;
        for (int i = 0; i < deliveryArrayList.size(); i++) {
            if (idDelivery.equals(deliveryArrayList.get(i).getId())){
                index = i;
            }
        }
        if (index != -1 && statusOfDelivery.keySet().contains(deliveryArrayList.get(index))) {
            return statusOfDelivery.get(deliveryArrayList.get(index));
        }
        return "false";
    }*/
/*
    @RequestMapping(
            value = "/joinOrdersInOrder",
            method = GET)
    @ResponseBody
    public String joinOrdersInOrder(@RequestParam String list) {
        return list;
    }

    @GetMapping("/setOrder")
    @ResponseBody
    public String setOrder(@RequestParam String id) {
        return id;
    }

    @RequestMapping(
            value = "/joinOrdersInDelivery",
            method = GET)
    @ResponseBody
    public String joinOrdersInDelivery(@RequestParam List<String> listIdOfOrders) {
        String nameOfDelivery = listIdOfOrders.get(0);
        ArrayList<String> stringList = new ArrayList<>();
        stringList.addAll(listIdOfOrders.subList(1, listIdOfOrders.size()));
        try {
            deliveryArrayList.add(new Delivery(nameOfDelivery, (ArrayList<String>) stringList));
        }
        catch (Error e){
            return "false";
        }
        catch (Exception p){
            return "false";
        }
        update2();
        return "true";
    }

    @GetMapping("/getStatus")
    @ResponseBody
    public String getStatus(@RequestParam String idDelivery) {
        if (statusOfDelivery.keySet().contains(idDelivery)) {
            return statusOfDelivery.get(idDelivery);
        }
        return "false";
    }

    @GetMapping("/getDelivery")
    @ResponseBody
    public  String getData(@RequestParam String name) {
        if (couriers.containsKey(name)) {
            return couriers.get(name).getId();
        }
        return "false";
    }

    @RequestMapping(
            value = "/setStatus",
            params = { "idDelivery", "status"},
            method = GET)
    @ResponseBody
    public String setStatus(
            @RequestParam("idDelivery") String idDelivery,
            @RequestParam("status") String status) {
        statusOfDelivery.put(idDelivery, status);
        return status;
    }

    @RequestMapping(
            value = "/setCourier",
            params = { "name", "idDelivery"},
            method = GET)
    @ResponseBody
    public String setCourier(
            @RequestParam("name") String name,
            @RequestParam("idDelivery") String idDelivery) {
        int index = -1;
        for (int i = 0; i < deliveryArrayList.size(); i++) {
            if (idDelivery.equals(deliveryArrayList.get(i).getId())){
                index = i;
            }
        }
        if (index != -1) {
            couriers.put(name, deliveryArrayList.get(index));
            //statusOfDelivery.put(deliveryArrayList.get(index).getId(), "");
            return "true";
        }
        return "false";
    }

    @RequestMapping(
            value = "/authorization",
            params = { "email", "password"},
            method = GET)
    @ResponseBody
    public String authorization(
            @RequestParam("email") String email,
            @RequestParam("password") String password) {
        for (int i = 0; i < userArrayList.size(); i++) {
            if(userArrayList.get(i).getEmail().equals(email) && userArrayList.get(i).getPassword().equals(password)){
                return "" + userArrayList.get(i).nameAll + "-" + userArrayList.get(i).getRole();
            }
        }
        return "false";
    }

    @RequestMapping(
            value = "/registration",
            params = { "displayName", "email", "password" },
            method = GET)
    @ResponseBody
    public String registration(
            @RequestParam("displayName") String displayName,
            @RequestParam("email") String email,
            @RequestParam("password") String password) {
        return "Done";
    }

    @RequestMapping(
            value = "/addProduct",
            method = GET)
    @ResponseBody
    public String addProduct(@RequestParam List<String> listArgs) {
        try {
            productArrayList.add(new Product(listArgs.get(0), listArgs.get(1), ("https://www.bestvinyl.ru/components/com_virtuemart/shop_image/product/resized/") + listArgs.get(2), Double.valueOf(listArgs.get(3))));
        }
        catch (Error e){
            return "false";
        }
        catch (Exception p){
            return "false";
        }
        update();
        return "true";
    }

    @RequestMapping(
            value = "/products",
            method = GET)
    @ResponseBody
    public Iterable<Product> returnProducts() {
        return productArrayList;
    }

    @RequestMapping(
            value = "/orders",
            method = GET)
    @ResponseBody
    public Iterable<Order> returnOrders() {
        return orderArrayList;
    }

    @RequestMapping(
            value = "/deliveries",
            method = GET)
    @ResponseBody
    public Iterable<Delivery> returnDeliveries() {
        return deliveryArrayList;
    }

    @GetMapping("/")
    public String home(@RequestParam(name="name", required=false, defaultValue="World") String name, Model model) {
        model.addAttribute("name", name);
        return "home";
    }

    @GetMapping("/productDelete")
    @ResponseBody
    public String deleteProduct(@RequestParam String id) {;
        for (int i = 0; i < productArrayList.size(); i++) {
            System.out.println(productArrayList.get(i).getId());
            if(productArrayList.get(i).getId().equals(String.valueOf(id.trim()))){
                productArrayList.remove(i);
                update();
                return "true";
            }
        }
        return "false";
    }
    @GetMapping("/deliveryDelete")
    @ResponseBody
    public String deleteDelivery(@RequestParam String id) {
        for (int i = 0; i < deliveryArrayList.size(); i++) {
            if(deliveryArrayList.get(i).getId().equals(String.valueOf(id))){
                deliveryArrayList.remove(i);
                return "true";
            }
        }
        return "false";
    }
    @GetMapping("/orderDelete")
    @ResponseBody
    public String deleteOrder(@RequestParam String id) {
        for (int i = 0; i < orderArrayList.size(); i++) {
            if(orderArrayList.get(i).getId().equals(String.valueOf(id))){
                orderArrayList.remove(i);
                update1();
                return "true";
            }
        }
        return "false";
    }

}
*/