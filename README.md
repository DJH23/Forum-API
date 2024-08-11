# Forum Application

## Overview

This is a forum application built with Spring Boot, designed to support user roles, threads, posts, and comments. The application uses JWT for authentication and MySQL for data persistence.

## Getting Started

### Prerequisites

- Java 22 or higher
- MySQL database
- Maven 

### Configuration

Before running the application, you need to set up the following configuration. Copy `application.example.properties` to `application.properties` and provide your own values.

- **Database Configuration:**

  - `spring.datasource.url`: URL of your MySQL database
  - `spring.datasource.username`: Database username
  - `spring.datasource.password`: Database password

- **JWT Configuration:**
  - `jwt.secret`: Secret key used for JWT token signing

### Running the Application

1. **Set up your MySQL database:**

   - Create a database named `lessonlab` or modify the URL in `application.properties` to match your database.

2. **Run the application:**

   - Use Maven: `mvn spring-boot:run`

3. **Access Swagger UI:**
   - Visit `http://localhost:8080/swagger-ui-lesson-lab.html` to explore the API endpoints.

## API Documentation

Refer to the Swagger UI for detailed API documentation, including endpoints for user roles, posts, comments, and more.

## Key Classes and Methods

### ForumApplication

This is the main class that starts the application. It contains the main method which is the entry point of the application.

### Repository classes

The repository classes contain the database interactions for the models. They are located in the `Repositories` directory and are named after the corresponding model class. For example, the repository class for the `Thread` model is `ThreadRepository`.

### Service classes

The service classes contain the business logic for creating, retrieving, updating, and deleting these objects. They are located in the `Services` directory and are named after the corresponding model class. For example, the service class for the `Thread` model is `ThreadService`.

### Controller classes

The controller classes contain the endpoints for the application. They are located in the `Controllers` directory and are named after the corresponding model class. For example, the controller class for the `Thread` model is `ThreadController`. The controllers are Spring MVC controllers that handle HTTP requests and return HTTP responses. They are annotated with `@RestController` and `@RequestMapping` to map the controller to specific URLs. The controllers use the service classes to perform the necessary operations on the models. For example, the `ThreadController` uses the `ThreadService` to create, retrieve, update, and delete threads. The controllers also use the repository classes to interact with the database.

### User

This class represents a user in the system. It contains information about the user's identity, such as their username, password, and other personal details.

### Content

This is an abstract class that represents some form of content in the system. It contains fields common to thread, post, comment, and the user who created the content and the creation timestamp.

### Thread

This class extends Content and represents a discussion thread in the forum. It contains a list of Post objects, representing the posts in the thread.

### Post

This class also extends Content and represents a post in a discussion thread. It contains a reference to the Thread it belongs to and a list of Comment objects, representing the comments on the post.

### Comment

This class also extends Content and represents a comment on a post. It contains a reference to the Post it belongs to.

The interactions between these classes form the structure of the forum:

- A User can create a Thread, which becomes a new discussion topic.
- Inside this Thread, the User or other users can create Post objects, contributing to the discussion.
- Users can also create Comment objects on these Post objects, further contributing to the discussion.

The ContentService, ThreadService, and other service classes in the Services directory contain the business logic for creating, retrieving, updating, and deleting these objects.

## Running Tests

Tests are located in the `src/test` directory. They can be run from the IDE or from the command line with `mvn test`.
