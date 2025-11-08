# 💬 Chat System Documentation - Workify Job Portal

## 📋 Mục lục

1. [Tổng quan hệ thống](#1-tổng-quan-hệ-thống)
2. [Kiến trúc & Thiết kế](#2-kiến-trúc--thiết-kế)
3. [Database Schema](#3-database-schema)
4. [Backend Implementation](#4-backend-implementation)
5. [API Documentation](#5-api-documentation)
6. [WebSocket Integration](#6-websocket-integration)
7. [Frontend React Implementation](#7-frontend-react-implementation)
8. [Error Handling](#8-error-handling)
9. [Testing Guide](#9-testing-guide)

---

## 1. Tổng quan hệ thống

### 1.1. Mục đích

Hệ thống chat cho phép **Nhà tuyển dụng (Employer)** và **Ứng viên (Job Seeker)** nhắn tin với nhau về các đơn ứng tuyển cụ thể.

### 1.2. Quy tắc nghiệp vụ

- ✅ **Employer** có quyền gửi tin nhắn đầu tiên để khởi tạo conversation
- ✅ **Job Seeker** chỉ được phép trả lời sau khi Employer đã gửi ít nhất 1 tin nhắn
- ✅ Mỗi conversation liên kết với 1 Job và 1 Application cụ thể
- ✅ Hỗ trợ realtime messaging qua WebSocket
- ✅ Đánh dấu tin nhắn đã đọc (seen status)
- ✅ Hiển thị tin nhắn cuối cùng trong danh sách conversation

### 1.3. Công nghệ sử dụng

- **Backend**: Spring Boot 3 + JPA + WebSocket (STOMP)
- **Database**: MySQL/PostgreSQL
- **Authentication**: JWT Token
- **Frontend**: React + SockJS + STOMP Client
- **Real-time**: WebSocket với endpoint `/ws`

---

## 2. Kiến trúc & Thiết kế

### 2.1. Polymorphic Design Pattern

Hệ thống xử lý 2 loại người dùng khác nhau:

- **User** entity (role: JOB_SEEKER, ADMIN)
- **Employer** entity (role: EMPLOYER)

Để xử lý polymorphic relationships, sử dụng pattern:

```
Message {
  senderId: Long          // ID của người gửi
  senderType: String      // "USER" hoặc "EMPLOYER"
  content: String
  seen: Boolean
}
```

### 2.2. Service Layer Architecture

```
┌─────────────────────────────────────┐
│      MessageController              │
│  (REST + WebSocket endpoints)       │
└────────────┬────────────────────────┘
             │
    ┌────────┴─────────┐
    │                  │
┌───▼──────────┐   ┌──▼─────────────────┐
│ MessageService│   │ConversationService │
│  (Interface)  │   │    (Interface)     │
└───┬──────────┘   └──┬─────────────────┘
    │                  │
┌───▼──────────────┐  ┌▼──────────────────┐
│MessageServiceImpl│  │ConversationService│
│                  │  │       Impl        │
└───┬──────────────┘  └┬──────────────────┘
    │                  │
    └─────┬────────────┘
          │
┌─────────▼──────────────────────────┐
│    Repositories + Entities         │
│  (Message, Conversation, User...)  │
└────────────────────────────────────┘
```

---

## 3. Database Schema

### 3.1. Entity: Conversation

```java
@Entity
@Table(name = "conversations")
public class Conversation extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", nullable = false)
    private Application application;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_seeker_id", nullable = false)
    private User jobSeeker;           // User entity

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employer_id", nullable = false)
    private Employer employer;        // Employer entity

    @Column(length = 500)
    private String lastMessage;

    @Column(name = "last_message_sender_id")
    private Long lastMessageSenderId;

    @Column(name = "last_message_sender_type", length = 10)
    private String lastMessageSenderType; // "USER" or "EMPLOYER"

    @Column(name = "has_employer_message", nullable = false)
    private Boolean hasEmployerMessage = false;  // Flag cho quy tắc nghiệp vụ
}
```

**Quan hệ:**

- `job_id` → `jobs.id`
- `application_id` → `applications.id`
- `job_seeker_id` → `users.id`
- `employer_id` → `employers.id`

### 3.2. Entity: Message

```java
@Entity
@Table(name = "messages")
public class Message extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conversation_id", nullable = false)
    private Conversation conversation;

    @Column(name = "sender_id", nullable = false)
    private Long senderId;           // Polymorphic: User.id hoặc Employer.id

    @Column(name = "sender_type", nullable = false, length = 10)
    private String senderType;       // "USER" hoặc "EMPLOYER"

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private Boolean seen = false;
}
```

**Index recommendations:**

```sql
CREATE INDEX idx_conversation_id ON messages(conversation_id);
CREATE INDEX idx_sender ON messages(sender_id, sender_type);
CREATE INDEX idx_seen ON messages(seen);
```

### 3.3. SQL Migration

```sql
-- Create conversations table
CREATE TABLE conversations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    job_id BIGINT NOT NULL,
    application_id BIGINT NOT NULL,
    job_seeker_id BIGINT NOT NULL,
    employer_id BIGINT NOT NULL,
    last_message VARCHAR(500),
    last_message_sender_id BIGINT,
    last_message_sender_type VARCHAR(10),
    has_employer_message BOOLEAN DEFAULT FALSE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (job_id) REFERENCES jobs(id) ON DELETE CASCADE,
    FOREIGN KEY (application_id) REFERENCES applications(id) ON DELETE CASCADE,
    FOREIGN KEY (job_seeker_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (employer_id) REFERENCES employers(id) ON DELETE CASCADE,
    UNIQUE KEY unique_job_application (job_id, application_id)
);

-- Create messages table
CREATE TABLE messages (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    conversation_id BIGINT NOT NULL,
    sender_id BIGINT NOT NULL,
    sender_type VARCHAR(10) NOT NULL,
    content TEXT NOT NULL,
    seen BOOLEAN DEFAULT FALSE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (conversation_id) REFERENCES conversations(id) ON DELETE CASCADE,
    INDEX idx_conversation_id (conversation_id),
    INDEX idx_sender (sender_id, sender_type),
    INDEX idx_seen (seen)
);
```

---

## 4. Backend Implementation

### 4.1. Service Layer

#### ConversationService Interface

```java
public interface ConversationService {
    // Tạo hoặc lấy conversation hiện có
    Conversation createOrGetConversation(Long jobId, Long applicationId, Long employerId);

    // Lấy conversation theo ID
    Conversation getConversationById(Long conversationId);

    // Lấy danh sách conversations của user hiện tại
    List<ConversationResponse> getConversationsForCurrentUser();

    // Cập nhật tin nhắn cuối cùng
    void updateLastMessage(Long conversationId, String content, Long senderId, String senderType);

    // Đánh dấu có tin nhắn từ employer
    void markHasEmployerMessage(Long conversationId);

    // Kiểm tra user có thuộc conversation không
    boolean isUserInConversation(Long conversationId, Long userId, String userType);
}
```

#### MessageService Interface

```java
public interface MessageService {
    // Gửi tin nhắn
    MessageResponse sendMessage(SendMessageRequest request);

    // Lấy danh sách tin nhắn
    List<MessageResponse> getMessagesByConversationId(Long conversationId);

    // Đánh dấu đã đọc
    void markMessagesAsSeen(Long conversationId);
}
```

### 4.2. Quy trình xử lý tin nhắn

#### Flow: Employer gửi tin nhắn đầu tiên

```
1. Employer gọi API POST /api/messages
   Request: {
     "jobId": 1,
     "applicationId": 5,
     "content": "Chào bạn, tôi muốn phỏng vấn..."
   }

2. Backend kiểm tra:
   - Principal instanceof Employer? → OK
   - conversationId null? → Tạo mới

3. Tạo Conversation:
   - Lấy Application (id=5) → Lấy User từ application.user
   - Tạo Conversation(job, application, jobSeeker, employer)
   - hasEmployerMessage = false

4. Tạo Message:
   - senderId = employer.id
   - senderType = "EMPLOYER"
   - content = "Chào bạn..."

5. Cập nhật Conversation:
   - lastMessage = content
   - lastMessageSenderId = employer.id
   - lastMessageSenderType = "EMPLOYER"
   - hasEmployerMessage = true (quan trọng!)

6. Gửi WebSocket notification:
   - Topic: /user/{jobSeekerId}/queue/messages
   - Payload: MessageResponse
```

#### Flow: Job Seeker trả lời

```
1. Job Seeker gọi API POST /api/messages
   Request: {
     "conversationId": 1,
     "content": "Cảm ơn anh, em sẵn sàng..."
   }

2. Backend kiểm tra:
   - Principal instanceof User? → OK
   - conversation.hasEmployerMessage == true? → OK (cho phép gửi)
   - isUserInConversation? → OK

3. Tạo Message:
   - senderId = user.id
   - senderType = "USER"

4. Gửi WebSocket notification:
   - Topic: /user/{employerId}/queue/messages
```

#### Flow: Job Seeker cố gửi tin nhắn đầu tiên (REJECTED)

```
1. Job Seeker gọi API POST /api/messages
   Request: {
     "conversationId": 1,
     "content": "Hello..."
   }

2. Backend kiểm tra:
   - Principal instanceof User? → OK
   - conversation.hasEmployerMessage == false? → ❌ REJECT

3. Throw AppException(ErrorCode.APPLICANT_MUST_WAIT_RECRUITER)
   → HTTP 500 với error code
```

### 4.3. Repository Custom Queries

```java
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByConversationIdOrderByCreatedAtAsc(Long conversationId);

    @Modifying
    @Query("UPDATE Message m SET m.seen = true " +
           "WHERE m.conversation.id = :conversationId " +
           "AND m.senderId != :userId " +
           "AND m.senderType = 'EMPLOYER'")
    void markAsSeenForJobSeeker(@Param("conversationId") Long conversationId,
                                 @Param("userId") Long userId);

    @Modifying
    @Query("UPDATE Message m SET m.seen = true " +
           "WHERE m.conversation.id = :conversationId " +
           "AND m.senderId != :employerId " +
           "AND m.senderType = 'USER'")
    void markAsSeenForEmployer(@Param("conversationId") Long conversationId,
                                @Param("employerId") Long employerId);
}
```

---

## 5. API Documentation

### 5.1. REST Endpoints

#### **POST** `/api/messages` - Gửi tin nhắn

**Authentication:** Required (JWT)

**Request Body:**

```json
{
  "conversationId": 1, // Optional: null nếu tạo mới (chỉ EMPLOYER)
  "jobId": 5, // Required nếu conversationId null
  "applicationId": 10, // Required nếu conversationId null
  "content": "Hello, how are you?"
}
```

**Response Success (200):**

```json
{
  "code": 200,
  "message": "Success",
  "data": {
    "id": 123,
    "conversationId": 1,
    "senderId": 5,
    "senderType": "EMPLOYER",
    "senderName": "ABC Company",
    "senderAvatar": "https://...",
    "content": "Hello, how are you?",
    "seen": false,
    "createdAt": "2025-11-09T14:30:00"
  }
}
```

**Error Responses:**

```json
// Job Seeker cố gửi tin nhắn đầu tiên
{
  "code": 7003,
  "message": "Applicant must wait for recruiter to send the first message"
}

// Job Seeker cố tạo conversation mới
{
  "code": 7004,
  "message": "Applicant cannot initiate conversation"
}

// User không thuộc conversation
{
  "code": 7005,
  "message": "User is not a participant of this conversation"
}
```

#### **GET** `/api/conversations` - Lấy danh sách conversations

**Authentication:** Required (JWT)

**Response:**

```json
{
  "code": 200,
  "message": "Success",
  "data": [
    {
      "id": 1,
      "jobId": 5,
      "jobTitle": "Senior Java Developer",
      "applicationId": 10,
      "jobSeekerId": 3,
      "jobSeekerName": "Nguyen Van A",
      "jobSeekerAvatar": "https://...",
      "employerId": 2,
      "employerName": "ABC Company",
      "employerAvatar": "https://...",
      "lastMessage": "Thank you for your interest",
      "lastMessageSenderId": 2,
      "lastMessageSenderType": "EMPLOYER",
      "hasEmployerMessage": true,
      "createdAt": "2025-11-09T14:00:00",
      "updatedAt": "2025-11-09T14:30:00"
    }
  ]
}
```

#### **GET** `/api/conversations/{conversationId}/messages` - Lấy tin nhắn

**Authentication:** Required (JWT)

**Response:**

```json
{
  "code": 200,
  "message": "Success",
  "data": [
    {
      "id": 1,
      "conversationId": 1,
      "senderId": 2,
      "senderType": "EMPLOYER",
      "senderName": "ABC Company",
      "senderAvatar": "https://...",
      "content": "Hello, I would like to interview you",
      "seen": true,
      "createdAt": "2025-11-09T14:00:00"
    },
    {
      "id": 2,
      "conversationId": 1,
      "senderId": 3,
      "senderType": "USER",
      "senderName": "Nguyen Van A",
      "senderAvatar": "https://...",
      "content": "Thank you, I am available anytime",
      "seen": false,
      "createdAt": "2025-11-09T14:15:00"
    }
  ]
}
```

#### **PUT** `/api/conversations/{conversationId}/mark-seen` - Đánh dấu đã đọc

**Authentication:** Required (JWT)

**Response:**

```json
{
  "code": 200,
  "message": "Messages marked as seen successfully"
}
```

---

## 6. WebSocket Integration

### 6.1. WebSocket Configuration

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
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }
}
```

### 6.2. WebSocket Endpoints

#### Send Message (Client → Server)

```
DESTINATION: /app/chat.sendMessage
PAYLOAD: {
  "conversationId": 1,
  "content": "Hello"
}
```

#### Receive Message (Server → Client)

```
SUBSCRIBE: /user/queue/messages
PAYLOAD: MessageResponse (same as REST API)
```

### 6.3. Authentication với WebSocket

```java
@Component
public class WebSocketAuthInterceptor implements ChannelInterceptor {

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String token = accessor.getFirstNativeHeader("Authorization");
            // Validate JWT token và set Authentication
        }

        return message;
    }
}
```

---

## 7. Frontend React Implementation

### 7.1. Cài đặt Dependencies

```bash
npm install sockjs-client @stomp/stompjs axios
```

### 7.2. WebSocket Service (React)

```javascript
// services/websocket.service.js
import SockJS from "sockjs-client";
import { Client } from "@stomp/stompjs";

class WebSocketService {
  constructor() {
    this.client = null;
    this.connected = false;
  }

  connect(token, onMessageReceived) {
    const socket = new SockJS("http://localhost:8080/ws");

    this.client = new Client({
      webSocketFactory: () => socket,
      connectHeaders: {
        Authorization: `Bearer ${token}`,
      },
      debug: (str) => {
        console.log("STOMP Debug:", str);
      },
      onConnect: () => {
        console.log("WebSocket Connected");
        this.connected = true;

        // Subscribe to personal queue
        this.client.subscribe("/user/queue/messages", (message) => {
          const messageData = JSON.parse(message.body);
          onMessageReceived(messageData);
        });
      },
      onStompError: (frame) => {
        console.error("STOMP Error:", frame);
      },
      onWebSocketClose: () => {
        console.log("WebSocket Disconnected");
        this.connected = false;
      },
    });

    this.client.activate();
  }

  sendMessage(messageData) {
    if (this.connected && this.client) {
      this.client.publish({
        destination: "/app/chat.sendMessage",
        body: JSON.stringify(messageData),
      });
    } else {
      console.error("WebSocket not connected");
    }
  }

  disconnect() {
    if (this.client) {
      this.client.deactivate();
      this.connected = false;
    }
  }
}

export default new WebSocketService();
```

### 7.3. API Service (React)

```javascript
// services/chat.service.js
import axios from "axios";

const API_URL = "http://localhost:8080/api";

class ChatService {
  // Lấy danh sách conversations
  async getConversations() {
    const response = await axios.get(`${API_URL}/conversations`, {
      headers: { Authorization: `Bearer ${localStorage.getItem("token")}` },
    });
    return response.data.data;
  }

  // Lấy tin nhắn của conversation
  async getMessages(conversationId) {
    const response = await axios.get(
      `${API_URL}/conversations/${conversationId}/messages`,
      {
        headers: { Authorization: `Bearer ${localStorage.getItem("token")}` },
      }
    );
    return response.data.data;
  }

  // Gửi tin nhắn qua REST (fallback nếu WebSocket fail)
  async sendMessage(messageData) {
    const response = await axios.post(`${API_URL}/messages`, messageData, {
      headers: { Authorization: `Bearer ${localStorage.getItem("token")}` },
    });
    return response.data.data;
  }

  // Đánh dấu đã đọc
  async markAsSeen(conversationId) {
    await axios.put(
      `${API_URL}/conversations/${conversationId}/mark-seen`,
      {},
      {
        headers: { Authorization: `Bearer ${localStorage.getItem("token")}` },
      }
    );
  }
}

export default new ChatService();
```

### 7.4. Chat Component (React)

```jsx
// components/Chat/ChatBox.jsx
import React, { useState, useEffect, useRef } from "react";
import websocketService from "../../services/websocket.service";
import chatService from "../../services/chat.service";
import "./ChatBox.css";

const ChatBox = ({ conversationId, currentUserId, currentUserType }) => {
  const [messages, setMessages] = useState([]);
  const [newMessage, setNewMessage] = useState("");
  const [loading, setLoading] = useState(true);
  const messagesEndRef = useRef(null);

  // Load tin nhắn khi mở conversation
  useEffect(() => {
    loadMessages();

    // Mark as seen khi mở conversation
    chatService.markAsSeen(conversationId);
  }, [conversationId]);

  // Scroll to bottom khi có tin nhắn mới
  useEffect(() => {
    scrollToBottom();
  }, [messages]);

  // Connect WebSocket
  useEffect(() => {
    const token = localStorage.getItem("token");

    websocketService.connect(token, (message) => {
      // Chỉ thêm tin nhắn nếu thuộc conversation hiện tại
      if (message.conversationId === conversationId) {
        setMessages((prev) => [...prev, message]);

        // Auto mark as seen
        chatService.markAsSeen(conversationId);
      }
    });

    return () => {
      websocketService.disconnect();
    };
  }, [conversationId]);

  const loadMessages = async () => {
    try {
      setLoading(true);
      const data = await chatService.getMessages(conversationId);
      setMessages(data);
    } catch (error) {
      console.error("Error loading messages:", error);
    } finally {
      setLoading(false);
    }
  };

  const handleSendMessage = async () => {
    if (!newMessage.trim()) return;

    const messageData = {
      conversationId: conversationId,
      content: newMessage.trim(),
    };

    try {
      // Gửi qua WebSocket
      websocketService.sendMessage(messageData);

      // Thêm tin nhắn vào UI ngay lập tức (optimistic update)
      const tempMessage = {
        id: Date.now(), // temporary ID
        conversationId,
        senderId: currentUserId,
        senderType: currentUserType,
        content: newMessage.trim(),
        seen: false,
        createdAt: new Date().toISOString(),
      };

      setMessages((prev) => [...prev, tempMessage]);
      setNewMessage("");
    } catch (error) {
      console.error("Error sending message:", error);

      // Fallback: gửi qua REST API
      try {
        const sentMessage = await chatService.sendMessage(messageData);
        setMessages((prev) => [...prev, sentMessage]);
        setNewMessage("");
      } catch (restError) {
        alert("Không thể gửi tin nhắn. Vui lòng thử lại.");
      }
    }
  };

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
  };

  if (loading) return <div>Loading...</div>;

  return (
    <div className="chat-box">
      {/* Messages List */}
      <div className="messages-container">
        {messages.map((msg) => (
          <div
            key={msg.id}
            className={`message ${
              msg.senderId === currentUserId &&
              msg.senderType === currentUserType
                ? "message-sent"
                : "message-received"
            }`}
          >
            <div className="message-avatar">
              <img src={msg.senderAvatar || "/default-avatar.png"} alt="" />
            </div>
            <div className="message-content">
              <div className="message-header">
                <span className="sender-name">{msg.senderName}</span>
                <span className="message-time">
                  {new Date(msg.createdAt).toLocaleTimeString()}
                </span>
              </div>
              <div className="message-text">{msg.content}</div>
              {msg.senderId === currentUserId && (
                <div className="message-status">
                  {msg.seen ? "✓✓ Đã xem" : "✓ Đã gửi"}
                </div>
              )}
            </div>
          </div>
        ))}
        <div ref={messagesEndRef} />
      </div>

      {/* Input Box */}
      <div className="message-input-container">
        <input
          type="text"
          value={newMessage}
          onChange={(e) => setNewMessage(e.target.value)}
          onKeyPress={(e) => e.key === "Enter" && handleSendMessage()}
          placeholder="Nhập tin nhắn..."
          className="message-input"
        />
        <button onClick={handleSendMessage} className="send-button">
          Gửi
        </button>
      </div>
    </div>
  );
};

export default ChatBox;
```

### 7.5. Conversation List Component

```jsx
// components/Chat/ConversationList.jsx
import React, { useState, useEffect } from "react";
import chatService from "../../services/chat.service";
import "./ConversationList.css";

const ConversationList = ({ onSelectConversation, currentUserType }) => {
  const [conversations, setConversations] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadConversations();
  }, []);

  const loadConversations = async () => {
    try {
      const data = await chatService.getConversations();
      setConversations(data);
    } catch (error) {
      console.error("Error loading conversations:", error);
    } finally {
      setLoading(false);
    }
  };

  const getOtherPartyInfo = (conv) => {
    if (currentUserType === "USER") {
      return {
        name: conv.employerName,
        avatar: conv.employerAvatar,
        subtitle: conv.jobTitle,
      };
    } else {
      return {
        name: conv.jobSeekerName,
        avatar: conv.jobSeekerAvatar,
        subtitle: conv.jobTitle,
      };
    }
  };

  if (loading) return <div>Loading...</div>;

  return (
    <div className="conversation-list">
      <div className="conversation-list-header">
        <h2>Tin nhắn</h2>
      </div>
      <div className="conversations">
        {conversations.map((conv) => {
          const otherParty = getOtherPartyInfo(conv);

          return (
            <div
              key={conv.id}
              className="conversation-item"
              onClick={() => onSelectConversation(conv.id)}
            >
              <div className="conversation-avatar">
                <img src={otherParty.avatar || "/default-avatar.png"} alt="" />
              </div>
              <div className="conversation-info">
                <div className="conversation-name">{otherParty.name}</div>
                <div className="conversation-subtitle">
                  {otherParty.subtitle}
                </div>
                <div className="conversation-last-message">
                  {conv.lastMessage}
                </div>
              </div>
              <div className="conversation-meta">
                <div className="conversation-time">
                  {new Date(conv.updatedAt).toLocaleDateString()}
                </div>
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
};

export default ConversationList;
```

### 7.6. Main Chat Page

```jsx
// pages/ChatPage.jsx
import React, { useState, useEffect } from "react";
import ConversationList from "../components/Chat/ConversationList";
import ChatBox from "../components/Chat/ChatBox";
import "./ChatPage.css";

const ChatPage = () => {
  const [selectedConversationId, setSelectedConversationId] = useState(null);
  const [currentUser, setCurrentUser] = useState(null);

  useEffect(() => {
    // Load user info từ token hoặc API
    const userInfo = JSON.parse(localStorage.getItem("userInfo"));
    setCurrentUser(userInfo);
  }, []);

  if (!currentUser) return <div>Loading...</div>;

  return (
    <div className="chat-page">
      <div className="chat-container">
        {/* Left: Conversation List */}
        <div className="chat-sidebar">
          <ConversationList
            onSelectConversation={setSelectedConversationId}
            currentUserType={currentUser.type} // "USER" or "EMPLOYER"
          />
        </div>

        {/* Right: Chat Box */}
        <div className="chat-main">
          {selectedConversationId ? (
            <ChatBox
              conversationId={selectedConversationId}
              currentUserId={currentUser.id}
              currentUserType={currentUser.type}
            />
          ) : (
            <div className="chat-empty">
              <p>Chọn một cuộc trò chuyện để bắt đầu</p>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default ChatPage;
```

### 7.7. CSS Styling

```css
/* ChatPage.css */
.chat-page {
  height: 100vh;
  display: flex;
  flex-direction: column;
}

.chat-container {
  display: flex;
  flex: 1;
  overflow: hidden;
}

.chat-sidebar {
  width: 350px;
  border-right: 1px solid #e0e0e0;
  background: white;
}

.chat-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  background: #f5f5f5;
}

.chat-empty {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
  color: #999;
}

/* ChatBox.css */
.chat-box {
  display: flex;
  flex-direction: column;
  height: 100%;
}

.messages-container {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.message {
  display: flex;
  gap: 10px;
  max-width: 70%;
}

.message-sent {
  align-self: flex-end;
  flex-direction: row-reverse;
}

.message-received {
  align-self: flex-start;
}

.message-avatar img {
  width: 40px;
  height: 40px;
  border-radius: 50%;
}

.message-content {
  background: white;
  padding: 10px 15px;
  border-radius: 12px;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
}

.message-sent .message-content {
  background: #0084ff;
  color: white;
}

.message-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 5px;
  font-size: 12px;
}

.sender-name {
  font-weight: 600;
}

.message-time {
  color: #999;
  font-size: 11px;
}

.message-text {
  word-wrap: break-word;
}

.message-status {
  font-size: 11px;
  color: rgba(255, 255, 255, 0.7);
  text-align: right;
  margin-top: 4px;
}

.message-input-container {
  display: flex;
  gap: 10px;
  padding: 15px;
  background: white;
  border-top: 1px solid #e0e0e0;
}

.message-input {
  flex: 1;
  padding: 10px 15px;
  border: 1px solid #e0e0e0;
  border-radius: 20px;
  outline: none;
}

.send-button {
  padding: 10px 20px;
  background: #0084ff;
  color: white;
  border: none;
  border-radius: 20px;
  cursor: pointer;
}

.send-button:hover {
  background: #0073e6;
}
```

---

## 8. Error Handling

### 8.1. Error Codes

```java
public enum ErrorCode {
    // ... existing codes

    // Chat error codes (7xxx)
    CONVERSATION_NOT_FOUND(7001, "Conversation not found"),
    MESSAGE_NOT_FOUND(7002, "Message not found"),
    APPLICANT_MUST_WAIT_RECRUITER(7003, "Applicant must wait for recruiter to send the first message"),
    APPLICANT_CANNOT_INITIATE(7004, "Applicant cannot initiate conversation"),
    NOT_CONVERSATION_PARTICIPANT(7005, "User is not a participant of this conversation"),
    INVALID_MESSAGE_CONTENT(7006, "Message content cannot be empty");
}
```

### 8.2. Frontend Error Handling

```javascript
// services/error-handler.js
export const handleChatError = (error) => {
  if (error.response?.data?.code) {
    const errorCode = error.response.data.code;

    switch (errorCode) {
      case 7003:
        return "Bạn cần đợi nhà tuyển dụng gửi tin nhắn trước";
      case 7004:
        return "Chỉ nhà tuyển dụng mới có thể bắt đầu cuộc trò chuyện";
      case 7005:
        return "Bạn không có quyền truy cập cuộc trò chuyện này";
      default:
        return "Có lỗi xảy ra. Vui lòng thử lại.";
    }
  }

  return "Không thể kết nối đến server";
};
```

---

## 9. Testing Guide

### 9.1. Postman Testing

#### Test 1: Employer gửi tin nhắn đầu tiên

```
POST http://localhost:8080/api/messages
Authorization: Bearer {employer_token}

Body:
{
  "jobId": 1,
  "applicationId": 5,
  "content": "Hello, I want to interview you"
}

Expected: 200 OK + MessageResponse
```

#### Test 2: Job Seeker trả lời

```
POST http://localhost:8080/api/messages
Authorization: Bearer {jobseeker_token}

Body:
{
  "conversationId": 1,
  "content": "Thank you, I am available"
}

Expected: 200 OK + MessageResponse
```

#### Test 3: Job Seeker gửi tin nhắn đầu tiên (SHOULD FAIL)

```
POST http://localhost:8080/api/messages
Authorization: Bearer {jobseeker_token}

Body:
{
  "conversationId": 1,
  "content": "Hello"
}

Expected: 500 with error code 7003
```

### 9.2. WebSocket Testing (HTML)

```html
<!DOCTYPE html>
<html>
  <head>
    <title>WebSocket Test</title>
    <script src="https://cdn.jsdelivr.net/npm/sockjs-client@1/dist/sockjs.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/@stomp/stompjs@7/bundles/stomp.umd.min.js"></script>
  </head>
  <body>
    <h1>WebSocket Chat Test</h1>
    <div>
      <input id="token" placeholder="JWT Token" style="width: 400px" />
      <button onclick="connect()">Connect</button>
      <button onclick="disconnect()">Disconnect</button>
    </div>
    <div>
      <input id="conversationId" placeholder="Conversation ID" />
      <input id="message" placeholder="Message" />
      <button onclick="sendMessage()">Send</button>
    </div>
    <div
      id="messages"
      style="margin-top: 20px; border: 1px solid #ccc; padding: 10px; height: 300px; overflow-y: auto;"
    ></div>

    <script>
      let stompClient = null;

      function connect() {
        const token = document.getElementById("token").value;
        const socket = new SockJS("http://localhost:8080/ws");

        stompClient = new StompJs.Client({
          webSocketFactory: () => socket,
          connectHeaders: {
            Authorization: "Bearer " + token,
          },
          debug: console.log,
          onConnect: () => {
            console.log("Connected");

            stompClient.subscribe("/user/queue/messages", (message) => {
              showMessage(JSON.parse(message.body));
            });
          },
          onStompError: (frame) => {
            console.error("Error:", frame);
          },
        });

        stompClient.activate();
      }

      function disconnect() {
        if (stompClient) {
          stompClient.deactivate();
          console.log("Disconnected");
        }
      }

      function sendMessage() {
        const conversationId = document.getElementById("conversationId").value;
        const content = document.getElementById("message").value;

        stompClient.publish({
          destination: "/app/chat.sendMessage",
          body: JSON.stringify({
            conversationId: parseInt(conversationId),
            content: content,
          }),
        });

        document.getElementById("message").value = "";
      }

      function showMessage(message) {
        const messagesDiv = document.getElementById("messages");
        const messageElement = document.createElement("div");
        messageElement.innerHTML = `
                <strong>${message.senderName} (${message.senderType}):</strong> 
                ${message.content}
                <small style="color: gray">${new Date(
                  message.createdAt
                ).toLocaleTimeString()}</small>
            `;
        messagesDiv.appendChild(messageElement);
        messagesDiv.scrollTop = messagesDiv.scrollHeight;
      }
    </script>
  </body>
</html>
```

---

## 10. Best Practices & Tips

### 10.1. Performance Optimization

1. **Lazy Loading Messages**: Load tin nhắn theo batch (pagination)

```java
@Query("SELECT m FROM Message m WHERE m.conversation.id = :convId " +
       "ORDER BY m.createdAt DESC")
Page<Message> findByConversationId(@Param("convId") Long convId, Pageable pageable);
```

2. **Caching Conversations**: Sử dụng Redis cache

```java
@Cacheable(value = "conversations", key = "#userId")
public List<ConversationResponse> getConversationsForUser(Long userId) {
    // ...
}
```

3. **Database Indexing**: Đảm bảo có index

```sql
CREATE INDEX idx_messages_conversation_created
ON messages(conversation_id, created_at DESC);
```

### 10.2. Security Considerations

1. **Validate Permissions**: Luôn check user có quyền access conversation
2. **Rate Limiting**: Giới hạn số tin nhắn gửi trong 1 phút
3. **Content Filtering**: Validate content (XSS protection)
4. **Token Refresh**: Handle token expiry trong WebSocket

### 10.3. Monitoring & Logging

```java
@Slf4j
public class MessageServiceImpl {

    public MessageResponse sendMessage(SendMessageRequest request) {
        log.info("User {} attempting to send message to conversation {}",
                 senderId, request.getConversationId());

        // ... logic

        log.info("Message {} sent successfully", message.getId());
        return response;
    }
}
```

---

## 11. Troubleshooting

### 11.1. WebSocket không kết nối được

- ✅ Kiểm tra CORS configuration
- ✅ Verify JWT token format trong header
- ✅ Check browser console cho errors
- ✅ Test với SockJS test page: `/ws/info`

### 11.2. Tin nhắn không realtime

- ✅ Verify subscription topic đúng: `/user/queue/messages`
- ✅ Check server logs xem có gửi notification không
- ✅ Ensure user ID trong token match với database

### 11.3. Permission denied errors

- ✅ Verify `hasEmployerMessage` flag trong conversation
- ✅ Check `isUserInConversation()` logic
- ✅ Ensure correct userType ("USER" vs "EMPLOYER")

---

## 12. Future Enhancements

### Tính năng có thể thêm:

- 📎 **File attachments**: Upload ảnh, CV, documents
- 📢 **Typing indicators**: "User is typing..."
- ✅ **Read receipts**: Timestamp khi đã xem
- 🔔 **Push notifications**: Firebase Cloud Messaging
- 🔍 **Search messages**: Full-text search
- 📊 **Message analytics**: Track response time, engagement
- 🌐 **Multi-language**: i18n support
- 🎨 **Rich text formatting**: Markdown, emoji picker
- 📱 **Mobile app**: React Native implementation

---

**Tài liệu này được tạo bởi: GitHub Copilot**  
**Ngày cập nhật: November 9, 2025**  
**Version: 1.0**
