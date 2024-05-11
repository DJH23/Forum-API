# Forum Application

## Setup

1. Ensure you have Java and Maven installed on your machine.
2. Clone the repository and navigate to the `backend/java` directory.
3. Run `mvn install` to install the dependencies.

## Running the Application

You can run the application using the `ForumApplication` class in [ForumApplication.java](backend/java/forum/src/main/java/com/LessonLab/forum/ForumApplication.java). This can be done either through your IDE or from the command line with `mvn spring-boot:run`.

## Key Classes and Methods

- [`ForumApplication`](backend/java/forum/src/main/java/com/LessonLab/forum/ForumApplication.java): This is the main class that starts the application.
- [`CommentRepository`](backend/java/forum/src/main/java/com/LessonLab/forum/Repositories/CommentRepository.java): This interface provides methods for interacting with the `Comment` table in the database.
- [`ContentRepository`](backend/java/forum/src/main/java/com/LessonLab/forum/Repositories/ContentRepository.java): This interface provides methods for interacting with the `Content` table in the database.
- [`ThreadService`](backend/java/forum/src/main/java/com/LessonLab/forum/Services/ThreadService.java): This service class provides methods for managing threads.

## Running Tests

Tests are located in the `src/test` directory. They can be run from the IDE or from the command line with `mvn test`.