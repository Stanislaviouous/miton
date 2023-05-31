package com.example.demo;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.models.Chat;
import com.example.demo.models.ID;
import com.example.demo.models.Message;
import com.example.demo.models.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

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

    // requests.post("http://localhost:8080/authorization",data={"userId": "test1","password": "1"}).text
    @PostMapping(value = "/authorization", params = { "userId", "password" })
    public ResponseEntity<String> authorization(@RequestParam("userId") String userId,
            @RequestParam("password") String password) {
        User currentUser = usersArrayList.stream()
                .filter(user -> user.getId().equals(userId) && user.getPassword().equals(password)).findFirst()
                .orElse(null);
        return (currentUser == null) ? new ResponseEntity<String>("No user found", HttpStatus.UNAUTHORIZED)
                : new ResponseEntity<String>(gson.toJson(currentUser), HttpStatus.OK);
    }

    // requests.post("http://localhost:8080/registration", data={"name": "test","password": "0"}).text
    @PostMapping(value = "/registration", params = { "name", "password" })
    public ResponseEntity<String> registration(@RequestParam("name") String name,
            @RequestParam("password") String password) {
        String id = "U" + UUID.randomUUID();
        User newUser = new User(id, false, name, password);
        usersArrayList.add(newUser);
        updateDB("users");
        return new ResponseEntity<String>(gson.toJson(newUser), HttpStatus.OK);
    }

    // requests.post("http://localhost:8080/createNewChat", data={"userId1":"test1","userId2": "test2"}).text 
    @PostMapping(value = "/createNewChat", params = { "userId1", "userId2" })
    public ResponseEntity<String> createNewChat(@RequestParam("userId1") String userId1,
            @RequestParam("userId2") String userId2) {
        if (userId1.equals(userId2)) {
            return new ResponseEntity<String>("You can't create chat with yourself", HttpStatus.BAD_REQUEST);
        }
        User user1, user2 = null;
        try {
            user1 = findInListById(usersArrayList, userId1);
            user2 = findInListById(usersArrayList, userId2);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<String>("No user(s) found", HttpStatus.BAD_REQUEST);
        }
        Chat newChat = new Chat(("C" + UUID.randomUUID()), new ArrayList<String>(List.of(userId1, userId2)));
        chatsArrayList.add(newChat);
        user1.addChat(newChat);
        user2.addChat(newChat);
        updateDB("chats");
        updateDB("users");
        return new ResponseEntity<String>(newChat.id, HttpStatus.OK);
    }

    // requests.post("http://localhost:8080/createNewMessage", data={"userId1":"test1","userId2": "test2","text":"TEXT","chatId":"test"}).text 
    @PostMapping(value = "/createNewMessage", params = { "userId1", "userId2",
            "text", "chatId" })
    public ResponseEntity<String> createNewMessage(@RequestParam("userId1") String userId1,
            @RequestParam("userId2") String userId2,
            @RequestParam("text") String text, @RequestParam("chatId") String chatId) {
        Chat chat = null;
        try {
            findInListById(usersArrayList, userId1);
            findInListById(usersArrayList, userId2);
            chat = findInListById(chatsArrayList, chatId);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<String>("No user(s) found, or chat does not exist", HttpStatus.BAD_REQUEST);
        }
        Message newMessage = new Message(("M" + UUID.randomUUID()), false, text, new Date().getTime(), chatId,
                new ArrayList<String>(List.of(userId1,
                        userId2)));
        messagesArrayList.add(newMessage);
        chat.addMessage(newMessage);
        updateDB("messages");
        updateDB("chats");
        return new ResponseEntity<String>("PLACEHOLDER", HttpStatus.OK);
    }

    /* Admin-only methods */
    // requests.get("http://localhost:8080/getAllUsers").text
    @GetMapping(value = "/getAllUsers")
    public ArrayList<User> getAllUsers() {
        return usersArrayList;
    }

    // requests.get("http://localhost:8080/getAllChats").text
    @GetMapping(value = "/getAllChats")
    public ArrayList<Chat> getAllChats() {
        return chatsArrayList;
    }

    // requests.get("http://localhost:8080/getMessageByText",params={"text":"TE"}).text
    @GetMapping(value = "/getMessageByText", params = { "text" })
    public ArrayList<Message> getMessageByText(@RequestParam("text") String text) {
        return (ArrayList<Message>) messagesArrayList.stream().filter(message -> message.getText().contains(text))
                .collect(Collectors.toList());
    }

    /* Admin and user methods, although User is handicapped at frontend */

    // requests.get("http://localhost:8080/getAllMessagesByChat",params={"chatId":"test1"}).text
    @GetMapping(value = "/getAllMessagesByChat", params = { "chatId" })
    public ArrayList<Message> getAllMessagesByChat(@RequestParam("chatId") String chatId) {
        ArrayList<String> messagesIds = findInListById(chatsArrayList, chatId).getMessagesIds();
        // if(messagesIds.isEmpty()){ return new ArrayList<Message>();}
        return (ArrayList<Message>) messagesArrayList.stream().filter(message -> messagesIds.contains(message.id))
                .collect(Collectors.toList());
    }

    // requests.get("http://localhost:8080/getAllChatsByUser",params={"userId":"test1"}).text
    @GetMapping(value = "/getAllChatsByUser", params = { "userId" })
    public ArrayList<Chat> getAllChatsByUser(@RequestParam("userId") String userId) {
        ArrayList<String> chatsIds = findInListById(usersArrayList, userId).getChatsIds();
        // if(chatsIds.isEmpty()){ return new ArrayList<Chat>();}
        return (ArrayList<Chat>) chatsArrayList.stream().filter(chat -> chatsIds.contains(chat.id))
                .collect(Collectors.toList());
    }

    // requests.delete("http://localhost:8080/deleteUserData", params={"userId":"test1"}).text
    @DeleteMapping(value = "/deleteUserData", params = { "userId" })
    public ResponseEntity<String> deleteUserData(@RequestParam("userId") String userId) {
        // this basically means that if nothing in usersArrayList is deleted, then
        // there's no such user
        if (!(usersArrayList.removeIf(user -> user.getId().equals(userId)))) {
            return new ResponseEntity<String>("No user found", HttpStatus.BAD_REQUEST);
        }
        chatsArrayList.removeIf(chat -> chat.getUsersIds().contains(userId));
        messagesArrayList.removeIf(message -> message.getUsersIds().contains(userId));
        updateDB("users");
        updateDB("chats");
        updateDB("messages");

        return new ResponseEntity<String>("Successfully deleted data for user " + userId, HttpStatus.OK);
    }

    // requests.delete("http://localhost:8080/deleteMessage",params={"messageId":"M655e9c66-c622-4b08-99b0-d409a4ae0e53"}).text
    @DeleteMapping(value = "/deleteMessage", params = { "messageId" })
    public ResponseEntity<String> deleteMessage(@RequestParam("messageId") String messageId) {
        Message message = null;
        try {
            message = findInListById(messagesArrayList, messageId);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<String>("No message found", HttpStatus.BAD_REQUEST);
        }
        messagesArrayList.remove(message);
        updateDB("messages");
        Chat chat = findInListById(chatsArrayList, message.getChatId());
        chat.deleteMessage(messageId);
        // if, after deletion of the message, parent chat is empty, we should delete it
        // completely - who needs empty chat after all?
        if (chat.getMessagesIds().isEmpty()) {
            chatsArrayList.remove(chat);
            // this ugly mess is written so that we can update every user's chatlist (for
            // those who had aforementioned chat in it)
            usersArrayList.stream().filter(user -> chat.getUsersIds().contains(user.getId()))
                    .forEach(user -> user.deleteChat(chat.getId()));
            updateDB("users");
        }
        updateDB("chats");

        return new ResponseEntity<String>("Successfully deleted message " + messageId, HttpStatus.OK);
    }

    // requests.post("http://localhost:8080/hideMessage",data={"messageId":"M655e9c66-c622-4b08-99b0-d409a4ae0e53"}).text
    @PostMapping(value = "/hideMessage", params = { "messageId" })
    public ResponseEntity<String> hideMessage(@RequestParam("messageId") String messageId) {
        Message message = null;
        try {
            message = findInListById(messagesArrayList, messageId);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<String>("No message found", HttpStatus.BAD_REQUEST);
        }
        message.setDeleted(true);
        updateDB("messages");
        return new ResponseEntity<String>("Successfully hidden message " + messageId, HttpStatus.OK);
    }

    // requests.patch("http://localhost:8080/patchPassword",params={"userId":"test1","oldPassword":"1","newPassword":"new1"}).text
    @PatchMapping(value = "/patchPassword", params = { "userId", "oldPassword", "newPassword"
    })
    public ResponseEntity<String> patchPassword(@RequestParam("userId") String userId,
            @RequestParam("oldPassword") String oldPassword, @RequestParam("newPassword") String newPassword) {
        User user = null;
        try {
            user = findInListById(usersArrayList, userId);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<String>("No user found", HttpStatus.BAD_REQUEST);
        }
        if (!(user.getPassword().equals(oldPassword))) {
            return new ResponseEntity<String>("Old password does not match the input", HttpStatus.UNAUTHORIZED);
        }
        user.setPassword(newPassword);
        updateDB("users");
        return new ResponseEntity<String>("The new password has been set", HttpStatus.OK);
    }

    // Helper function for finding User/Chat/Message object in
    // user-/chat-/message-ArrayList
    private static <T extends ID> T findInListById(ArrayList<T> list, String objId) throws NoSuchElementException {
        return list.stream().filter(listObj -> listObj.id.equals(objId)).findFirst().orElseThrow();
    }
}