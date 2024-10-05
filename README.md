# User Management API

Welcome to the User management API repository! This API allows you to easily register a user, edit/read/(soft) delete single or multiple user(s).

## Getting Started

### Prerequisites

Make sure you have the following installed on your machine:

- [Git](https://git-scm.com/)
- [Docker](https://www.docker.com/)
- [Docker Compose](https://docs.docker.com/compose/)

### Cloning the Repository

 Clone the repository to your local machine:

```bash
git clone https://github.com/farzaneh-haeri/demo_userservice.git
```

### Running the Services

1. Navigate to the project root folder.
2. To start the services, run the following command:

    ```bash
    ./run_services.sh
    ```

    or alternatively run the following commands:

     ```bash
    docker-compose build
    docker-compose up
    ```
    
4. Once the services are up and running, you can access the Swagger UI at:

    ```bash
    http://localhost:8081/swagger-ui/index.html
    ```
    You do not need credentials to access the Swagger UI. However, when you want to try the endpoints, use the following credentials:
   
    Username: demo
   
    Password: pass
   

