# PetCare Backend (Spring Boot REST API)

Cài đặt REST API bằng Java (Spring Boot) dựa trên toàn bộ **User Story** trong file
`User Story - Pet_Care.docx`, cho 3 actor: **Pet Owner**, **Veterinarian**, **Manager**.

## Công nghệ

- Java 17, Spring Boot 3.2.5
- Spring Data JPA + H2 (in-memory database, không cần cài đặt gì thêm)
- Spring Validation (jakarta.validation)
- BCrypt (spring-security-crypto) để hash mật khẩu
- Lombok

## Cách chạy

Yêu cầu: JDK 17+ và Maven (hoặc dùng `./mvnw` nếu có mạng tới Maven Central).

```bash
cd petcare-backend
mvn spring-boot:run
```

Ứng dụng chạy tại `http://localhost:8080`. H2 console (xem dữ liệu trực tiếp):
`http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:petcare`, user: `sa`, không có mật khẩu).

Khi khởi động lần đầu, `DataSeeder` tự tạo 3 tài khoản mẫu (mật khẩu chung: `Password123`):

| Số điện thoại | Vai trò       | Tên                          |
|---------------|---------------|-------------------------------|
| 0900000001    | PET_OWNER     | Nguyễn Văn Chủ (Pet Owner)     |
| 0900000002    | VETERINARIAN  | Bs. Trần Thị Thú Y             |
| 0900000003    | MANAGER       | Lê Văn Quản Lý                 |

## Cấu trúc project

```
src/main/java/com/petcare/
├── entity/       # 12 JPA entity: User, Pet, Device, Appointment, Facility,
│                 #   Notification, Rating, Comment, Conversation, ChatMessage,
│                 #   CallSession, Announcement
├── repository/   # Spring Data JPA repository cho từng entity
├── dto/          # Request/Response DTO (tách biệt khỏi entity)
├── service/      # Toàn bộ business logic + acceptance criteria
├── controller/   # REST endpoint (@RestController)
├── exception/    # ApiException + GlobalExceptionHandler (trả JSON lỗi gọn)
├── config/       # PasswordEncoder, CORS, DataSeeder (dữ liệu mẫu)
└── util/         # ValidationUtils (quy tắc SĐT / mật khẩu)
```

Kiến trúc theo mô hình **layered / N-tier** kinh điển
(Controller → Service → Repository → Entity) — phù hợp minh họa OOP,
encapsulation và dependency injection cho mục đích giảng dạy.

## Bảng ánh xạ User Story → REST endpoint

### Pet Owner

| User Story | Endpoint |
|---|---|
| Login | `POST /api/auth/login` |
| Register | `POST /api/auth/register` → `POST /api/auth/verify-otp` |
| View / Edit Pet Owner Profile | `GET /api/users/{id}` · `PUT /api/users/{id}` |
| View list pets | `GET /api/pets?ownerId=` |
| Create / Edit / Delete pet profile | `POST /api/pets` · `PUT /api/pets/{id}` · `DELETE /api/pets/{id}` |
| Add Device | `POST /api/pets/{id}/device` |
| Locate pet | `GET /api/pets/{id}/location` (cập nhật GPS: `PUT /api/pets/{id}/location`) |
| View list Veterinarian | `GET /api/users/veterinarians` |
| Rate / Comment on Veterinarian | `POST /api/veterinarians/{vetId}/ratings` · `POST /api/veterinarians/{vetId}/comments` |
| Search Veterinarian or pets | `GET /api/users/veterinarians/search?q=` · `GET /api/pets/search?q=` |
| View / Delete notifications | `GET /api/notifications?userId=` · `DELETE /api/notifications/{id}?userId=` |
| View appointments | `GET /api/appointments/pet-owner/{petOwnerId}` |
| Request an appointment | `POST /api/appointments/request` |
| Cancel an appointment (tối đa 3 lần/tháng) | `PUT /api/appointments/{id}/cancel?petOwnerId=` |
| View list facility | `GET /api/facilities` |
| Chat with Veterinarian | `POST /api/chats/conversations` · `POST /api/chats/conversations/{id}/messages` |
| Video call with Veterinarian | `POST /api/calls` · `PUT /api/calls/{id}/accept` · `PUT /api/calls/{id}/end` |
| Add Device (GPS locator) | `POST /api/pets/{id}/device` |

### Veterinarian

| User Story | Endpoint |
|---|---|
| Accept / Refuse an appointment | `PUT /api/appointments/{id}/accept` · `PUT /api/appointments/{id}/refuse` |
| Create an appointment | `POST /api/appointments/create-by-vet` |
| View schedule | `GET /api/appointments/veterinarian/{vetId}/schedule` |
| View list pet owners / pets / veterinarians | `GET /api/users/pet-owners` · `GET /api/pets?ownerId=` · `GET /api/users/veterinarians` |
| View / Edit profile | `GET /api/users/{id}` · `PUT /api/users/{id}` |
| Chat / Video call | giống Pet Owner (`/api/chats/**`, `/api/calls/**`) |

### Manager

| User Story | Endpoint |
|---|---|
| Manage / Create / Edit / Delete account | `GET,POST /api/admin/accounts` · `PUT,DELETE /api/admin/accounts/{id}` |
| Manage / Create / Edit / Delete facility | `/api/facilities/**` (dùng chung với Pet Owner cho phần xem) |
| View / Export statistical | `GET /api/admin/statistics` · `GET /api/admin/statistics/export` (CSV) |
| Posts announcements | `POST /api/admin/announcements` · `GET /api/admin/announcements` |
| Admin profile | `GET /api/admin/profile/{id}` |

## Quy tắc nghiệp vụ đã cài đặt (Acceptance Criteria)

- **Số điện thoại**: đúng 10 hoặc 11 số, không chứa ký tự khác (`ValidationUtils`).
- **Mật khẩu**: ký tự đầu không phải số, dài hơn 6 ký tự, không chứa ký tự đặc biệt.
- **Đăng ký**: phải xác thực OTP mới đăng nhập được (Pet Owner).
- **Hủy lịch hẹn**: mỗi Pet Owner chỉ được hủy tối đa 3 lần/tháng.
- **Đặt lịch hẹn**: kiểm tra trùng giờ với bác sĩ (khoảng ±30 phút).
- **Từ chối lịch hẹn**: buộc phải nhập lý do.
- **Bình luận Veterinarian**: chỉ được bình luận sau khi đã đánh giá (rate).
- Mọi thay đổi trạng thái lịch hẹn / tin nhắn mới / cuộc gọi đến đều tự sinh
  **Notification** cho người liên quan.

## Giới hạn & hướng nâng cấp

- **Chat / Video call** hiện là REST đơn giản (gửi/nhận qua HTTP, không real-time).
  Có thể nâng cấp bằng Spring WebSocket (STOMP) để đẩy tin nhắn tức thời, và
  tích hợp WebRTC ở phía client để truyền âm/hình thực tế cho video call — phần
  `CallSession` ở đây chỉ đóng vai trò "signaling" (quản lý trạng thái cuộc gọi).
- Chưa có xác thực bằng JWT/Session — API hiện dùng `userId` truyền qua tham số
  cho đơn giản, phù hợp mục đích minh họa nghiệp vụ. Có thể bổ sung Spring Security
  + JWT nếu cần bảo mật đầy đủ.
- Database dùng H2 in-memory (mất dữ liệu khi restart); đổi sang MySQL/PostgreSQL
  chỉ cần sửa `application.yml`.
