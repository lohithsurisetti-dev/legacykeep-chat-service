# LegacyKeep Chat Service - Technical Documentation

## 📋 Table of Contents
1. [Service Architecture](#service-architecture)
2. [Database Design](#database-design)
3. [API Specifications](#api-specifications)
4. [WebSocket Implementation](#websocket-implementation)
5. [Security Implementation](#security-implementation)
6. [Performance Considerations](#performance-considerations)
7. [Error Handling](#error-handling)
8. [Configuration Guide](#configuration-guide)

## 🏗️ Service Architecture

### Core Components

#### 1. Controllers Layer
- **ChatRoomController**: Manages chat room operations
- **MessageController**: Handles message CRUD operations
- **WebSocketController**: Manages real-time connections
- **AnalyticsController**: Provides insights and statistics
- **TestController**: Database connectivity testing

#### 2. Service Layer
- **ChatRoomService**: Business logic for chat rooms
- **MessageService**: Message processing and management
- **WebSocketService**: Real-time messaging coordination

#### 3. Repository Layer
- **PostgreSQL Repositories**: Metadata and relational data
- **MongoDB Repositories**: Message content and media
- **Redis Repositories**: Caching and session management

#### 4. Configuration Layer
- **WebSocketConfig**: STOMP protocol configuration
- **SecurityConfig**: Authentication and authorization
- **Application Properties**: Database and service configuration

### Data Flow Architecture

```
Client Request → Controller → Service → Repository → Database
                     ↓
              WebSocket → Real-time Updates → Client
```

## 🗄️ Database Design

### PostgreSQL Schema

#### Chat Rooms Table
```sql
CREATE TABLE chat_rooms (
    id BIGSERIAL PRIMARY KEY,
    room_uuid UUID UNIQUE NOT NULL,
    created_by_user_id BIGINT NOT NULL,
    room_name VARCHAR(255) NOT NULL,
    room_description TEXT,
    room_type VARCHAR(50) NOT NULL,
    family_id BIGINT,
    story_id BIGINT,
    event_id BIGINT,
    room_photo_url VARCHAR(500),
    status VARCHAR(20) DEFAULT 'ACTIVE',
    is_encrypted BOOLEAN DEFAULT FALSE,
    is_archived BOOLEAN DEFAULT FALSE,
    is_muted BOOLEAN DEFAULT FALSE,
    message_count BIGINT DEFAULT 0,
    participant_count INTEGER DEFAULT 0,
    room_settings JSONB,
    privacy_settings JSONB,
    notification_settings JSONB,
    metadata JSONB,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

#### Chat Participants Table
```sql
CREATE TABLE chat_participants (
    id BIGSERIAL PRIMARY KEY,
    chat_room_id BIGINT REFERENCES chat_rooms(id),
    user_id BIGINT NOT NULL,
    role VARCHAR(20) DEFAULT 'MEMBER',
    joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_read_at TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    UNIQUE(chat_room_id, user_id)
);
```

#### Message Reactions Table
```sql
CREATE TABLE message_reactions (
    id BIGSERIAL PRIMARY KEY,
    message_id VARCHAR(255) NOT NULL,
    user_id BIGINT NOT NULL,
    emoji VARCHAR(10) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(message_id, user_id)
);
```

### MongoDB Collections

#### Messages Collection
```javascript
{
  _id: ObjectId,
  messageId: String,
  chatRoomId: Long,
  senderId: Long,
  messageType: String, // TEXT, IMAGE, VIDEO, AUDIO, DOCUMENT, LOCATION
  content: String,
  mediaUrl: String,
  mediaMetadata: Object,
  replyToMessageId: String,
  forwardedFrom: Object,
  reactions: [{
    userId: Long,
    emoji: String,
    timestamp: Date
  }],
  isEdited: Boolean,
  editedAt: Date,
  isDeleted: Boolean,
  deletedAt: Date,
  metadata: Object,
  createdAt: Date,
  updatedAt: Date
}
```

#### Media Files Collection
```javascript
{
  _id: ObjectId,
  fileId: String,
  messageId: String,
  fileName: String,
  fileType: String,
  fileSize: Number,
  mimeType: String,
  storageUrl: String,
  thumbnailUrl: String,
  metadata: Object,
  uploadedBy: Long,
  createdAt: Date
}
```

## 📡 API Specifications

### Request/Response Format

All API responses follow the `ApiResponse<T>` wrapper format:

```java
{
  "status": "success|error",
  "message": "Human readable message",
  "data": T,
  "timestamp": "ISO 8601 timestamp",
  "error": null|ErrorDetails,
  "statusCode": 200,
  "path": "/api/v1/endpoint"
}
```

### Pagination Format

```java
{
  "content": T[],
  "totalElements": 100,
  "totalPages": 5,
  "currentPage": 0,
  "size": 20,
  "first": true,
  "last": false
}
```

### Error Response Format

```java
{
  "status": "error",
  "message": "Error description",
  "data": null,
  "timestamp": "2024-01-01T00:00:00Z",
  "error": {
    "code": "CHAT_ROOM_NOT_FOUND",
    "details": "Additional error details",
    "field": "chatRoomId"
  },
  "statusCode": 404,
  "path": "/api/v1/chat-rooms/123"
}
```

## 🔌 WebSocket Implementation

### STOMP Configuration

```java
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic", "/queue");
        config.setApplicationDestinationPrefixes("/app");
        config.setUserDestinationPrefix("/user");
    }
    
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws").withSockJS();
        registry.addEndpoint("/ws-direct");
    }
}
```

### Message Types

#### Chat Message
```javascript
{
  type: "CHAT_MESSAGE",
  chatRoomId: 123,
  messageId: "msg_456",
  senderId: 789,
  content: "Hello world!",
  messageType: "TEXT",
  timestamp: "2024-01-01T00:00:00Z"
}
```

#### Typing Indicator
```javascript
{
  type: "TYPING_INDICATOR",
  chatRoomId: 123,
  userId: 789,
  isTyping: true,
  timestamp: "2024-01-01T00:00:00Z"
}
```

#### User Status
```javascript
{
  type: "USER_STATUS",
  userId: 789,
  status: "ONLINE|OFFLINE|AWAY",
  lastSeen: "2024-01-01T00:00:00Z"
}
```

### Subscription Patterns

- `/topic/chat.{roomId}` - Chat room messages
- `/topic/family.{familyId}` - Family notifications
- `/topic/user.{userId}` - User-specific messages
- `/queue/notifications.{userId}` - User notifications

## 🔒 Security Implementation

### Authentication Flow

1. **JWT Token Validation**: All API requests require valid JWT tokens
2. **WebSocket Authentication**: STOMP connections authenticated via JWT
3. **Role-based Access**: Family roles determine permissions
4. **Rate Limiting**: API endpoints protected against abuse

### Security Headers

```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    return http
        .csrf(csrf -> csrf.disable())
        .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
        .authorizeHttpRequests(authz -> authz
            .requestMatchers("/api/**").authenticated()
            .requestMatchers("/ws/**").authenticated()
            .anyRequest().permitAll()
        )
        .build();
}
```

### Encryption

- **Transport**: HTTPS/WSS for all communications
- **Storage**: AES-256 encryption for sensitive data
- **End-to-End**: Optional E2E encryption for private messages

## ⚡ Performance Considerations

### Database Optimization

#### PostgreSQL
- **Indexes**: Optimized indexes on frequently queried columns
- **Connection Pooling**: HikariCP with optimized settings
- **Query Optimization**: Efficient queries with proper joins

#### MongoDB
- **Indexes**: Compound indexes for message queries
- **Sharding**: Horizontal scaling for large datasets
- **Aggregation**: Optimized aggregation pipelines

#### Redis
- **Caching Strategy**: Multi-level caching for frequently accessed data
- **Session Management**: Efficient session storage and retrieval
- **Pub/Sub**: Real-time message broadcasting

### Caching Strategy

```java
@Cacheable(value = "chatRooms", key = "#roomId")
public ChatRoom getChatRoomById(Long roomId) {
    return chatRoomRepository.findById(roomId);
}

@CacheEvict(value = "chatRooms", key = "#roomId")
public void updateChatRoom(Long roomId, UpdateChatRoomRequest request) {
    // Update logic
}
```

### Connection Pooling

```properties
# HikariCP Configuration
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.connection-timeout=30000
spring.datasource.hikari.idle-timeout=600000
spring.datasource.hikari.max-lifetime=1800000
```

## 🚨 Error Handling

### Exception Hierarchy

```java
public class ChatServiceException extends RuntimeException {
    private final String errorCode;
    private final HttpStatus httpStatus;
}

public class ChatRoomNotFoundException extends ChatServiceException {
    public ChatRoomNotFoundException(Long roomId) {
        super("CHAT_ROOM_NOT_FOUND", "Chat room not found: " + roomId, HttpStatus.NOT_FOUND);
    }
}
```

### Global Exception Handler

```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(ChatRoomNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleChatRoomNotFound(ChatRoomNotFoundException ex) {
        return ResponseEntity.status(ex.getHttpStatus())
            .body(ApiResponse.<Void>builder()
                .status("error")
                .message(ex.getMessage())
                .error(ErrorDetails.builder()
                    .code(ex.getErrorCode())
                    .details(ex.getDetails())
                    .build())
                .build());
    }
}
```

### Error Codes

| Code | Description | HTTP Status |
|------|-------------|-------------|
| `CHAT_ROOM_NOT_FOUND` | Chat room does not exist | 404 |
| `MESSAGE_NOT_FOUND` | Message does not exist | 404 |
| `UNAUTHORIZED_ACCESS` | Insufficient permissions | 403 |
| `INVALID_MESSAGE_TYPE` | Invalid message type | 400 |
| `ROOM_ARCHIVED` | Chat room is archived | 409 |
| `USER_NOT_PARTICIPANT` | User not in chat room | 403 |

## ⚙️ Configuration Guide

### Application Properties

```properties
# Server Configuration
server.port=8083
server.servlet.context-path=/chat

# Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/chat_db
spring.datasource.username=${DB_USERNAME:lohithsurisetti}
spring.datasource.password=${DB_PASSWORD:}
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=${SHOW_SQL:false}
spring.datasource.hikari.connectionTimeout=20000
spring.datasource.hikari.maximumPoolSize=5

# MongoDB Configuration
spring.data.mongodb.uri=${MONGODB_URI:mongodb://localhost:27017/chat_messages}
spring.data.mongodb.auto-index-creation=true

# Redis Configuration
spring.data.redis.host=${REDIS_HOST:localhost}
spring.data.redis.port=${REDIS_PORT:6379}
spring.data.redis.password=${REDIS_PASSWORD:}

# WebSocket Configuration
websocket.stomp.endpoint=/ws
websocket.stomp.destination-prefix=/topic
websocket.stomp.application-destination-prefix=/app

# Security Configuration
jwt.secret=${JWT_SECRET:your-secret-key}
jwt.expiration=${JWT_EXPIRATION:86400000}

# Logging Configuration
logging.level.com.legacykeep.chat=INFO
logging.level.org.springframework.web.socket=DEBUG
```

### Environment-Specific Configuration

#### Development
```properties
spring.profiles.active=dev
spring.jpa.show-sql=true
logging.level.com.legacykeep.chat=DEBUG
```

#### Production
```properties
spring.profiles.active=prod
spring.jpa.show-sql=false
logging.level.com.legacykeep.chat=WARN
```

### Docker Configuration

```dockerfile
FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/chat-service-1.0.0-SNAPSHOT.jar app.jar

EXPOSE 8083

ENV SPRING_PROFILES_ACTIVE=prod
ENV DB_USERNAME=chat_user
ENV DB_PASSWORD=secure_password
ENV MONGODB_URI=mongodb://mongo:27017/chat_messages
ENV REDIS_HOST=redis
ENV JWT_SECRET=production-secret-key

ENTRYPOINT ["java", "-jar", "app.jar"]
```

## 📊 Monitoring and Metrics

### Health Checks

- **Database Health**: PostgreSQL, MongoDB, Redis connectivity
- **WebSocket Health**: Active connections and message throughput
- **Service Health**: Memory usage, response times, error rates

### Metrics Collection

```java
@Component
public class ChatServiceMetrics {
    
    private final MeterRegistry meterRegistry;
    private final Counter messagesSent;
    private final Timer messageProcessingTime;
    
    public ChatServiceMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        this.messagesSent = Counter.builder("chat.messages.sent")
            .description("Total messages sent")
            .register(meterRegistry);
        this.messageProcessingTime = Timer.builder("chat.messages.processing.time")
            .description("Message processing time")
            .register(meterRegistry);
    }
}
```

### Logging Strategy

```java
@Slf4j
@Service
public class ChatRoomServiceImpl implements ChatRoomService {
    
    public ChatRoom createChatRoom(CreateChatRoomRequest request) {
        log.debug("Creating chat room: {}", request.getRoomName());
        
        try {
            ChatRoom chatRoom = // ... creation logic
            log.info("Created chat room with ID: {} and UUID: {}", 
                chatRoom.getId(), chatRoom.getRoomUuid());
            return chatRoom;
        } catch (Exception e) {
            log.error("Failed to create chat room: {}", e.getMessage(), e);
            throw new ChatServiceException("CHAT_ROOM_CREATION_FAILED", 
                "Failed to create chat room", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
```

---

This technical documentation provides comprehensive information about the LegacyKeep Chat Service implementation, architecture, and configuration. For additional details or questions, please refer to the source code or contact the development team.
