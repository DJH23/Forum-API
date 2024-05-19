# Forum Application

## Setup

1. Ensure you have Java and Maven installed on your machine.
2. Clone the repository and navigate to the `backend/java` directory.
3. Run `mvn install` to install the dependencies.

## Running the Application

You can run the application using the ForumApplication class in ForumApplication.java. This can be done either through your IDE or from the command line with mvn spring-boot:run. The application will start and you can access it at http://localhost:8080.

## Key Classes and Methods

### ForumApplication
This is the main class that starts the application. It contains the main method which is the entry point of the application.

### CommentRepository
This interface provides methods for interacting with the Comment table in the database. It extends JpaRepository which provides methods like save, findById, findAll, etc.

### ContentRepository
This interface provides methods for interacting with the Content table in the database. Like CommentRepository, it also extends JpaRepository.

### ThreadService
This service class provides methods for managing threads. It uses the repositories to interact with the database.

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