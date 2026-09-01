package com.example.hw13;

import java.io.IOException;
import java.util.List;

public class Main {

  private static final int TEST_USER_ID = 1;

  public static void main(String[] args) {

    JsonPlaceholderApi api = new JsonPlaceholderApi();

    try {

      User newUser = new User(
          0,
          "Test User",
          "testuser",
          "test@test.com"
      );

      User createdUser = api.createUser(newUser);

      System.out.println("Створення користувача:");
      System.out.println(api.toJson(createdUser));


      User updatedUser = new User(
          TEST_USER_ID,
          "Updated User",
          "updateduser",
          "updated@test.com"
      );

      System.out.println("\nОновлення користувача:");
      System.out.println(
          api.toJson(
              api.updateUser(TEST_USER_ID, updatedUser)
          )
      );


      System.out.println("\nВидалення користувача:");

      boolean deleted =
          api.deleteUser(TEST_USER_ID);

      System.out.println(
          "Статус видалення: " + deleted
      );


      System.out.println("\nВсі користувачі:");

      List<User> users =
          api.getAllUsers();

      System.out.println(api.toJson(users));


      System.out.println("\nКористувач за id:");

      User user =
          api.getUserById(TEST_USER_ID);

      System.out.println(api.toJson(user));


      System.out.println("\nКористувач за username:");

      System.out.println(
          api.toJson(
              api.getUsersByUsername("Bret")
          )
      );


      System.out.println(
          "\nКоментарі до останнього поста:"
      );

      api.saveCommentsForLastPost(TEST_USER_ID);


      System.out.println("\nВідкриті задачі:");

      List<Todo> openTodos =
          api.getOpenTodos(TEST_USER_ID);

      System.out.println(api.toJson(openTodos));

    } catch (IOException e) {
      System.out.println(
          "Помилка: " + e.getMessage()
      );
    }
  }
}
