# LegacyKeep Chat Service

A comprehensive, family-centric chat service designed to compete with WhatsApp, built with Spring Boot, PostgreSQL, MongoDB, and Redis.

## 🚀 Overview

The LegacyKeep Chat Service is a microservice that provides real-time messaging capabilities with advanced family communication features. It's designed to be the centerpiece of the LegacyKeep application, focusing on bringing users from WhatsApp to our platform.

## 🏗️ Architecture

### Technology Stack
- **Backend**: Spring Boot 3.2.0 with Java 17
- **Databases**: 
  - PostgreSQL (chat metadata, user data, room settings)
  - MongoDB (messages, media files, chat history)
  - Redis (caching, sessions, real-time data)
- **Real-time**: WebSocket with STOMP protocol
- **Security**: Spring Security with JWT
- **Build Tool**: Maven

### Service Architecture
```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   PostgreSQL    │    │    MongoDB      │    │     Redis       │
│  (Metadata)     │    │   (Messages)    │    │   (Caching)     │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                       │                       │
         └───────────────────────┼───────────────────────┘
                                 │
                    ┌─────────────────┐
                    │  Chat Service   │
                    │  (Spring Boot)  │
                    └─────────────────┘
                                 │
                    ┌─────────────────┐
                    │   WebSocket     │
                    │  (Real-time)    │
                    └─────────────────┘
```

## 🎯 Key Features

### Core Messaging
- **Real-time messaging** with WebSocket support
- **Message types**: Text, images, videos, audio, documents, location
- **Message reactions** with emoji support
- **Message editing and deletion**
- **Message forwarding**
- **Typing indicators**
- **Message status tracking** (sent, delivered, read)

### Family-Centric Features
- **Family chat rooms** with hierarchical structure
- **Story-based conversations** for family memories
- **Event-based chat rooms** for family gatherings
- **Individual and group messaging**
- **Family member management**
- **Privacy controls** and settings

### Advanced Features
- **End-to-end encryption** for sensitive conversations
- **Message archiving** and search
- **Media sharing** with cloud storage
- **Voice messages** with transcription
- **Location sharing** with privacy controls
- **Message scheduling** for future delivery
- **Chat room customization** (themes, backgrounds)

### Analytics & Insights
- **Family communication analytics**
- **Message statistics** and trends
- **Engagement metrics**
- **AI-powered insights** for family communication
- **Usage patterns** and recommendations

## 🚀 Quick Start

### Prerequisites
- Java 17+
- Maven 3.6+
- PostgreSQL 13+
- MongoDB 5.0+
- Redis 6.0+

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/lohithsurisetti-dev/legacykeep-chat-service.git
   cd legacykeep-chat-service
   ```

2. **Configure databases**
   
   Create PostgreSQL database:
   ```sql
   CREATE DATABASE chat_db;
   ```
   
   MongoDB will create collections automatically.

3. **Update configuration**
   
   Edit `src/main/resources/application.properties`:
   ```properties
   # Database Configuration
   spring.datasource.url=jdbc:postgresql://localhost:5432/chat_db
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   
   # MongoDB Configuration
   spring.data.mongodb.uri=mongodb://localhost:27017/chat_messages
   
   # Redis Configuration
   spring.data.redis.host=localhost
   spring.data.redis.port=6379
   ```

4. **Run the application**
```bash
mvn spring-boot:run
```

The service will start on `http://localhost:8083/chat`

## 📡 API Endpoints

### Health & Testing
- `GET /actuator/health` - Service health check
- `GET /api/v1/test/db` - Database connectivity test

### Chat Rooms
- `GET /api/v1/chat-rooms` - List all chat rooms (paginated)
- `POST /api/v1/chat-rooms` - Create new chat room
- `GET /api/v1/chat-rooms/{id}` - Get chat room details
- `PUT /api/v1/chat-rooms/{id}` - Update chat room
- `DELETE /api/v1/chat-rooms/{id}` - Delete chat room
- `GET /api/v1/chat-rooms/uuid/{uuid}` - Get chat room by UUID
- `GET /api/v1/chat-rooms/family/{familyId}` - Get family chat rooms
- `GET /api/v1/chat-rooms/user/{userId}` - Get user's chat rooms
- `GET /api/v1/chat-rooms/stats` - Get chat room statistics
- `POST /api/v1/chat-rooms/{id}/participants` - Add participant
- `DELETE /api/v1/chat-rooms/{id}/participants/{userId}` - Remove participant
- `POST /api/v1/chat-rooms/{id}/archive` - Archive chat room
- `POST /api/v1/chat-rooms/{id}/mute` - Mute chat room
- `GET /api/v1/chat-rooms/individual/{userId1}/{userId2}` - Check individual chat

### Messages
- `GET /api/v1/messages/room/{roomId}` - Get room messages (paginated)
- `POST /api/v1/messages` - Send message
- `PUT /api/v1/messages/{id}` - Edit message
- `DELETE /api/v1/messages/{id}` - Delete message
- `POST /api/v1/messages/{id}/reactions` - Add reaction
- `DELETE /api/v1/messages/{id}/reactions/{userId}` - Remove reaction
- `POST /api/v1/messages/forward` - Forward message
- `GET /api/v1/messages/search` - Search messages
- `GET /api/v1/messages/stats/user/{userId}` - User message stats
- `GET /api/v1/messages/stats/room/{roomId}` - Room message stats

### WebSocket
- `GET /api/v1/websocket/stats` - WebSocket statistics
- `POST /api/v1/websocket/subscribe` - Subscribe to topics
- `POST /api/v1/websocket/unsubscribe` - Unsubscribe from topics
- `GET /api/v1/websocket/connections` - Active connections

### Analytics
- `GET /api/v1/analytics/family/{familyId}` - Family analytics
- `GET /api/v1/analytics/user/{userId}` - User analytics
- `GET /api/v1/analytics/chat-room/{chatRoomId}` - Chat room analytics
- `GET /api/v1/analytics/trends` - Communication trends
- `GET /api/v1/analytics/ai-features` - AI feature usage
- `GET /api/v1/analytics/security` - Security metrics
- `GET /api/v1/analytics/media` - Media usage statistics
- `GET /api/v1/analytics/real-time` - Real-time metrics
- `GET /api/v1/analytics/engagement/family/{familyId}` - Family engagement
- `GET /api/v1/analytics/health` - Analytics health check

## 🔌 WebSocket Configuration

### STOMP Endpoints
- `/ws` - WebSocket endpoint with SockJS fallback
- `/ws-direct` - Direct WebSocket endpoint

### Message Destinations
- `/topic/chat.{roomId}` - Chat room messages
- `/topic/family.{familyId}` - Family-wide notifications
- `/topic/user.{userId}` - User-specific messages
- `/queue/notifications.{userId}` - User notifications

### Connection Management
- Automatic reconnection support
- Connection status tracking
- User session management
- Family subscription management

## 🗄️ Database Schema

### PostgreSQL Tables
- `chat_rooms` - Chat room metadata and settings
- `chat_participants` - Room participants and roles
- `message_reactions` - Message reactions and emojis
- `family_passwords` - Family security settings
- `chat_audit` - Audit trail for security

### MongoDB Collections
- `messages` - Message content and metadata
- `media_files` - Media attachments and metadata
- `chat_history` - Historical message data
- `user_sessions` - WebSocket session data

## 🔒 Security Features

- **JWT Authentication** for API access
- **WebSocket Security** with user authentication
- **End-to-end encryption** for sensitive messages
- **Role-based access control** for family management
- **Audit logging** for security monitoring
- **Rate limiting** for API protection

## 🧪 Testing

### Run Tests
```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=ChatRoomServiceTest

# Run with coverage
mvn test jacoco:report
```

### Test Endpoints
```bash
# Health check
curl http://localhost:8083/chat/actuator/health

# Database test
curl http://localhost:8083/chat/api/v1/test/db

# List chat rooms
curl http://localhost:8083/chat/api/v1/chat-rooms
```

## 📊 Monitoring

### Health Checks
- Database connectivity (PostgreSQL, MongoDB, Redis)
- WebSocket connection status
- Service performance metrics
- Memory and disk usage

### Metrics
- Message throughput
- WebSocket connections
- Database performance
- Error rates and response times

## 🚀 Deployment

### Docker
```bash
# Build image
docker build -t legacykeep-chat-service .

# Run container
docker run -p 8083:8083 legacykeep-chat-service
```

### Environment Variables
```bash
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/chat_db
export SPRING_DATASOURCE_USERNAME=your_username
export SPRING_DATASOURCE_PASSWORD=your_password
export SPRING_DATA_MONGODB_URI=mongodb://localhost:27017/chat_messages
export SPRING_DATA_REDIS_HOST=localhost
export SPRING_DATA_REDIS_PORT=6379
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🆘 Support

For support and questions:
- Create an issue in the repository
- Contact the LegacyKeep team
- Check the documentation wiki

## 🗺️ Roadmap

### Phase 1 (Current)
- ✅ Core messaging functionality
- ✅ WebSocket real-time communication
- ✅ Basic analytics and insights
- ✅ Database integration

### Phase 2 (Next)
- 🔄 Advanced AI features
- 🔄 Voice message transcription
- 🔄 Advanced encryption
- 🔄 Mobile app integration

### Phase 3 (Future)
- 📋 Video calling integration
- 📋 Advanced family features
- 📋 Third-party integrations
- 📋 Advanced analytics dashboard

---

**Built with ❤️ by the LegacyKeep Team**