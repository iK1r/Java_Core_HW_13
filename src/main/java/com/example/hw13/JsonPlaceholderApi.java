package com.example.hw13;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JsonPlaceholderApi {

  private static final String BASE_URL =
      "https://jsonplaceholder.typicode.com";

  private static final int SUCCESS_STATUS_START = 200;
  private static final int SUCCESS_STATUS_END = 300;

  private final Gson gson = new GsonBuilder()
      .setPrettyPrinting()
      .create();

  public User createUser(User user) throws IOException {
    String json = gson.toJson(user);

    String response = sendRequest(
        BASE_URL + "/users",
        "POST",
        json
    );

    return gson.fromJson(response, User.class);
  }

  public User updateUser(int id, User user) throws IOException {
    String json = gson.toJson(user);

    String response = sendRequest(
        BASE_URL + "/users/" + id,
        "PUT",
        json
    );

    return gson.fromJson(response, User.class);
  }

  public boolean deleteUser(int id) throws IOException {
    URL url = new URL(BASE_URL + "/users/" + id);

    HttpURLConnection connection =
        (HttpURLConnection) url.openConnection();

    connection.setRequestMethod("DELETE");

    int responseCode = connection.getResponseCode();

    connection.disconnect();

    return responseCode >= SUCCESS_STATUS_START
        && responseCode < SUCCESS_STATUS_END;
  }

  public List<User> getAllUsers() throws IOException {
    String response = sendGet(BASE_URL + "/users");

    User[] users = gson.fromJson(response, User[].class);

    return Arrays.asList(users);
  }

  public User getUserById(int id) throws IOException {
    String response = sendGet(BASE_URL + "/users/" + id);

    return gson.fromJson(response, User.class);
  }

  public List<User> getUsersByUsername(String username)
      throws IOException {

    String encodedUsername = URLEncoder.encode(
        username,
        StandardCharsets.UTF_8
    );

    String response = sendGet(
        BASE_URL + "/users?username=" + encodedUsername
    );

    User[] users = gson.fromJson(response, User[].class);

    return Arrays.asList(users);
  }

  public void saveCommentsForLastPost(int userId)
      throws IOException {

    String postsResponse = sendGet(
        BASE_URL + "/users/" + userId + "/posts"
    );

    Post[] posts = gson.fromJson(postsResponse, Post[].class);

    if (posts.length == 0) {
      System.out.println("У користувача немає постів");
      return;
    }

    Post lastPost = posts[0];

    for (Post post : posts) {
      if (post.getId() > lastPost.getId()) {
        lastPost = post;
      }
    }

    String commentsResponse = sendGet(
        BASE_URL + "/posts/" + lastPost.getId() + "/comments"
    );

    Comment[] comments =
        gson.fromJson(commentsResponse, Comment[].class);

    String commentsJson = gson.toJson(comments);

    System.out.println(commentsJson);

    String fileName =
        "user-" + userId
            + "-post-" + lastPost.getId()
            + "-comments.json";

    try (FileWriter writer = new FileWriter(fileName)) {
      writer.write(commentsJson);
    }

    System.out.println("Файл створено: " + fileName);
  }

  public List<Todo> getOpenTodos(int userId) throws IOException {
    String response = sendGet(
        BASE_URL + "/users/" + userId + "/todos"
    );

    Todo[] todos = gson.fromJson(response, Todo[].class);

    List<Todo> openTodos = new ArrayList<>();

    for (Todo todo : todos) {
      if (!todo.isCompleted()) {
        openTodos.add(todo);
      }
    }

    return openTodos;
  }

  public String toJson(Object object) {
    return gson.toJson(object);
  }

  private String sendGet(String urlString) throws IOException {
    URL url = new URL(urlString);

    HttpURLConnection connection =
        (HttpURLConnection) url.openConnection();

    connection.setRequestMethod("GET");

    String response = readResponse(connection);

    connection.disconnect();

    return response;
  }

  private String sendRequest(
      String urlString,
      String method,
      String json
  ) throws IOException {

    URL url = new URL(urlString);

    HttpURLConnection connection =
        (HttpURLConnection) url.openConnection();

    connection.setRequestMethod(method);
    connection.setRequestProperty(
        "Content-Type",
        "application/json"
    );
    connection.setDoOutput(true);

    try (OutputStream outputStream =
             connection.getOutputStream()) {

      byte[] input =
          json.getBytes(StandardCharsets.UTF_8);

      outputStream.write(input);
    }

    String response = readResponse(connection);

    connection.disconnect();

    return response;
  }

  private String readResponse(HttpURLConnection connection)
      throws IOException {

    int responseCode = connection.getResponseCode();

    InputStream inputStream;

    if (responseCode >= SUCCESS_STATUS_START
        && responseCode < SUCCESS_STATUS_END) {

      inputStream = connection.getInputStream();
    } else {
      inputStream = connection.getErrorStream();
    }

    if (inputStream == null) {
      return "";
    }

    StringBuilder response = new StringBuilder();

    try (BufferedReader reader =
             new BufferedReader(
                 new InputStreamReader(inputStream))) {

      String line;

      while ((line = reader.readLine()) != null) {
        response.append(line);
      }
    }

    return response.toString();
  }
}
