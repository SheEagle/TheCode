# Ultraviolet

Welcome to Ultraviolet Forum! This forum serves as a platform for discussions and interactions on various topics.


## Technologies Used

**Frontend:** Vue3, Element-Plus

**Backend:** Spring Boot3, MyBatis-Plus, Redis, RabbitMQ, Spring Security

## Features

### Backend Features
- Utilizes MyBatis-Plus as the persistence framework for easier development.
- Stores registration/reset operation verification codes in Redis with expiration time control.
- Uses RabbitMQ for queuing SMS sending tasks, then handled by listeners.
- Integrates Spring Security for permission verification and manually integrates JWT authentication.
- Implements IP address rate limiting using Redis to prevent interface abuse.
- Separates view layer objects from data layer objects and provides utility methods for quick conversion using reflection.
- Uniformly returns JSON-formatted error and exception pages for more consistent frontend response handling.
- Manually handles cross-origin requests using filters.
- Utilizes Swagger for automatic API documentation generation, with login-related APIs already configured.
- Automatically generates snowflake IDs for all requests using filters to facilitate online problem localization.
- Handles multiple environments with different configurations for development and production.
- Logs include complete information for each request along with corresponding snowflake IDs, with support for file recording.
- Overall project structure is clear, responsibilities are clear, and comments are comprehensive, ready to use out of the box.

### Frontend Features
- Utilizes Vue Router for routing.
- Uses Axios for asynchronous request framework.
- Employs Element-Plus as the UI component library.
- Adapts to dark mode switching using VueUse.
- Implements on-demand import with unplugin-auto-import to reduce package size after bundling.




## Running Locally

1. Clone this project.

```bash
  git clone https://github.com/SheEagle/Ultraviolet.git
```

2. Navigate to the frontend directory.

```bash
  cd my-project-frontend
```

3. Install dependencies.

```bash
  npm install
```

4. Start the frontend server.

```bash
  npm dev
```

5. Navigate to the backend directory.

```bash
  cd my-project-backend
```

6. Edit application-dev.yml.

7. Start the Spring Boot project.

## Links

[![linkedin](https://img.shields.io/badge/linkedin-0A66C2?style=for-the-badge&logo=linkedin&logoColor=white)](https://www.linkedin.com/in/xiru-wang-551103248/)