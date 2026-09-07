# new-api-stat

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
[![CI](https://github.com/CodeMonkeyJust/new-api-stat/actions/workflows/ci.yml/badge.svg)](https://github.com/CodeMonkeyJust/new-api-stat/actions/workflows/ci.yml)

<p align="center">
  <a href="./README.md">简体中文</a> |
  <a href="./README.en.md">English</a> |
  <a href="./README.fr.md">Français</a> |
  <a href="./README.ru.md">Русский</a> |
  <a href="./README.ja.md">日本語</a> |
  <strong>Tiếng Việt</strong> |
  <a href="./README.zh-TW.md">繁體中文</a>
</p>

Công cụ phân tích thống kê mức tiêu thụ Token và số lần gọi cho [new-api](https://github.com/Calcium-Ion/new-api), cung cấp các chế độ xem bảng điều khiển, xu hướng, mô hình, khung giờ, người dùng và số dư; backend cung cấp API xuất Excel, tuy nhiên frontend hiện chưa tích hợp nút xuất.

> Dự án đang ở giai đoạn đầu. Trước khi dùng trong môi trường production, hãy kiểm tra tính tương thích cơ sở dữ liệu và độ chính xác của phương pháp tính thống kê trong môi trường thử nghiệm.

## Tính năng

- Đăng nhập người dùng (tái sử dụng bảng `users` của new-api)
- Bảng điều khiển tổng quan: tổng số, hôm nay, hôm qua và người dùng quan trọng trong 7 ngày gần nhất
- Thống kê theo người dùng, mô hình và nhóm về mức tiêu thụ Token, số lần gọi và chi phí
- Xu hướng theo ngày, phân bố 24 giờ, chi tiết hằng ngày theo mô hình và theo người dùng
- Tra cứu số dư người dùng
- API xuất kết quả thống kê ra Excel (`.xlsx`) ở backend (lối vào trên frontend sẽ được hoàn thiện ở phiên bản sau)
- Hỗ trợ cơ sở dữ liệu PostgreSQL và MySQL (chuyển đổi qua `DB_URL`, xem phần cấu hình)

## Công nghệ sử dụng

- Backend: Java 17, Spring Boot 3, Spring Data JPA; hỗ trợ cơ sở dữ liệu PostgreSQL và MySQL
- Frontend: Vue 3, TypeScript, Vite, Element Plus, ECharts

## Cấu trúc dự án

```text
backend/   Dịch vụ API Spring Boot
db/        SQL tham khảo cấu trúc cơ sở dữ liệu
frontend/  Frontend Vue
```

## Bắt đầu nhanh

### 1. Chuẩn bị cơ sở dữ liệu

Dự án đọc trực tiếp các bảng `logs` và `users` của new-api để thống kê chỉ-đọc. Hỗ trợ cả PostgreSQL và MySQL; chuyển đổi qua `DB_URL`. Tất cả các ví dụ bên dưới đều dùng MySQL. Trước tiên hãy đảm bảo tài khoản cơ sở dữ liệu có quyền truy vấn chỉ-đọc và kiểm tra tính tương thích của SQL cùng phiên bản các trường trong môi trường thử nghiệm.

`db/MySQL.sql` là tệp tham khảo về cấu trúc bảng của instance MySQL mục tiêu của new-api, dùng để mô tả cấu trúc cơ sở dữ liệu của new-api (dự án này chủ yếu đọc hai bảng `logs` và `users`). Hiện tệp được xác minh theo cấu trúc bảng của new-api **v1.0.0-rc.32**. Tệp này chỉ dành cho việc đọc/tham khảo, không phải tập lệnh migration; không thực thi tệp trên bất kỳ cơ sở dữ liệu nào và không ghi đè lên bảng hiện có.

### 2. Khởi động backend

Yêu cầu: Java 17, Maven 3.9+. Backend dựa trên Spring Boot 3.5 (phiên bản chính xác xem `backend/pom.xml`).

```bash
cd backend
mvn clean package -DskipTests
java -jar target/new-api-stat-api-1.0.0.jar
```

Backend mặc định lắng nghe tại `http://localhost:8082` với đường dẫn ngữ cảnh `/new-api-stat-api`. Bắt buộc đặt `DB_USERNAME` và `DB_PASSWORD` trước lần khởi động đầu tiên; ứng dụng không bao giờ tạo hoặc sửa đổi cấu trúc bảng của new-api. Swagger UI và OpenAPI JSON mặc định bị tắt; để phát triển và gỡ lỗi hãy đặt `SWAGGER_ENABLED=true`. Tài liệu API:

- Swagger UI: `http://localhost:8082/new-api-stat-api/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8082/new-api-stat-api/v3/api-docs`

### 3. Khởi động frontend

Yêu cầu: Node.js 20.19+, npm 10+.

```bash
cd frontend
npm ci
npm run dev
```

Máy chủ phát triển mặc định tại `http://localhost:3001` và đường dẫn trang mặc định là `/new-api-stat/` (truy cập `http://localhost:3001` sẽ tự động chuyển hướng đến đường dẫn này). Proxy phát triển mặc định chuyển tiếp tới `http://localhost:8082`; nếu backend dùng địa chỉ khác, hãy cấu hình qua biến môi trường:

```powershell
$env:VITE_API_PROXY_TARGET = "http://localhost:8082"
npm run dev
```

Nếu thay đổi `SERVER_CONTEXT_PATH`, hãy điều chỉnh đồng bộ `VITE_API_BASE_URL`, đường dẫn proxy Vite và quy tắc reverse proxy.

Bản dựng production:

```bash
npm run build
```

Sản phẩm dựng nằm trong `frontend/new-api-stat/` và đường dẫn trang mặc định là `/new-api-stat/` (ví dụ: `https://<tên-miền>/new-api-stat/`). Hãy ánh xạ `/new-api-stat/` tới `frontend/new-api-stat/` để lưu trữ và cấu hình reverse proxy `/new-api-stat-api` trỏ về backend. Để triển khai ở đường dẫn gốc hoặc đường dẫn khác, hãy đặt `VITE_BASE_PATH` trước khi dựng (ví dụ: `VITE_BASE_PATH=/` hoặc `VITE_BASE_PATH=/stat/`).

## Cấu hình

Backend ưu tiên sử dụng biến môi trường để tránh đưa thông tin xác thực cơ sở dữ liệu vào Git:

> Lưu ý: khi triển khai hãy đặt tường minh các biến môi trường cơ sở dữ liệu; nếu không đặt `DB_URL`, ứng dụng sẽ dùng kết nối PostgreSQL cục bộ. Mọi ví dụ trong tài liệu này (kể cả các giá trị mẫu MySQL trong bảng dưới) đều dùng MySQL.

| Biến môi trường | Giá trị mặc định | Mô tả |
| --- | --- | --- |
| `DB_URL` | `jdbc:mysql://db.example.com:3306/new_api?...` | Địa chỉ JDBC; dùng được cả PostgreSQL và MySQL. Driver và dialect được tự động nhận diện theo URL |
| `DB_USERNAME` | không có | Tên người dùng cơ sở dữ liệu; bắt buộc cấu hình tường minh. Nên dùng tài khoản chỉ-đọc |
| `DB_PASSWORD` | không có | Mật khẩu cơ sở dữ liệu; bắt buộc cấu hình tường minh |
| `DB_CONNECTION_INIT_SQL` | `SET time_zone = '+08:00'` | SQL khởi tạo chạy sau khi mỗi kết nối được thiết lập, dùng để thống nhất múi giờ khi thống kê `created_at` (giây epoch). MySQL yêu cầu độ lệch cố định tương ứng `APP_TIME_ZONE`; PostgreSQL dùng cú pháp khác (`SET TIME ZONE '<APP_TIME_ZONE>'`). Hãy đặt theo cơ sở dữ liệu đang dùng |
| `SERVER_PORT` | `8082` | Cổng dịch vụ |
| `SERVER_CONTEXT_PATH` | `/new-api-stat-api` | Đường dẫn ngữ cảnh dịch vụ |
| `APP_TIME_ZONE` | `Asia/Shanghai` | Múi giờ dùng cho ngày và giờ thống kê |
| `APP_ANALYTICS_ROLES` | `10,100` | Giá trị vai trò new-api được phép truy cập API thống kê; mặc định là quản trị viên/người dùng root |
| `CORS_ALLOWED_ORIGINS` | `http://localhost:3001,http://localhost:5173` | Các nguồn frontend được phép, phân tách bằng dấu phẩy |
| `SESSION_COOKIE_SECURE` | `false` | Khi triển khai HTTPS nên đặt `true` |
| `SESSION_COOKIE_SAME_SITE` | `lax` | Thuộc tính SameSite của Session Cookie |
| `SWAGGER_ENABLED` | `false` | Có bật Swagger/OpenAPI hay không |

Ví dụ (MySQL):

```bash
export DB_URL='jdbc:mysql://db.example.com:3306/new_api?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&useSSL=false&allowPublicKeyRetrieval=true'
export DB_USERNAME='newapi_stat_readonly'
export DB_PASSWORD='replace-with-a-secret'
export DB_CONNECTION_INIT_SQL="SET time_zone = '+08:00'"
export APP_TIME_ZONE='Asia/Shanghai'
export CORS_ALLOWED_ORIGINS='https://stat.example.com'
export SESSION_COOKIE_SECURE='true'
export SWAGGER_ENABLED='false'
```

### Quản lý thông tin xác thực và khóa

Các giá trị nhạy cảm như mật khẩu cơ sở dữ liệu chỉ được đưa vào qua biến môi trường hoặc Secret của nền tảng; kho lưu trữ này không chứa thông tin xác thực thật. Các tệp `.env*`, khóa và chứng chỉ đã bị loại trừ bởi `.gitignore`; chỉ giữ lại các tệp mẫu `*.example`. CI có tích hợp quét bí mật gitleaks; bất kỳ bí mật nào viết cứng trong mã đều làm hỏng kiểm tra. Nếu từng lỡ commit thông tin nhạy cảm, hãy ngay lập tức xoay vòng thông tin xác thực và làm sạch lịch sử Git.

### Vai trò và kiểm soát truy cập

Theo mặc định, chỉ các vai trò new-api nằm trong `APP_ANALYTICS_ROLES` mới truy cập được API thống kê, số dư người dùng, danh sách người dùng, xuất dữ liệu và các endpoint trạng thái cơ sở dữ liệu; giá trị mặc định là `10,100`. Giá trị vai trò có thể khác nhau giữa các phiên bản new-api; hãy tham chiếu mã nguồn/quy ước cơ sở dữ liệu của phiên bản mục tiêu và điều chỉnh qua biến môi trường. Người dùng đã đăng nhập thông thường không truy cập được các endpoint này.

## Docker

Dockerfile hiện tại là image thời gian chạy, không bao gồm quá trình dựng Maven; hãy dựng JAR của backend trước, sau đó mới dựng image:

```bash
cd backend
mvn clean package -DskipTests
copy target\new-api-stat-api-1.0.0.jar app.jar  # Windows PowerShell có thể dùng Copy-Item
# Linux/macOS: cp target/new-api-stat-api-1.0.0.jar app.jar
docker build -t new-api-stat-api .
docker run -d --restart unless-stopped -p 8082:8082 \
  -e DB_URL='jdbc:mysql://host.docker.internal:3306/new_api?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&useSSL=false&allowPublicKeyRetrieval=true' \
  -e DB_USERNAME='newapi_stat_readonly' \
  -e DB_PASSWORD='replace-with-a-secret' \
  -e DB_CONNECTION_INIT_SQL="SET time_zone = '+08:00'" \
  --name new-api-stat-api \
  new-api-stat-api
```

Đừng commit mật khẩu thật, IP nội bộ hoặc `app.jar` vào kho lưu trữ. Trong môi trường production, hãy đưa thông tin xác thực qua Secret, biến môi trường hoặc cấu hình nền tảng.

### Triển khai bằng Docker Compose

Compose khởi động frontend nginx và backend bằng một lệnh duy nhất, đồng thời tự động dựng cả hai image qua Dockerfile đa giai đoạn: backend dùng `backend/Dockerfile.compose` (đóng gói Maven → chạy JRE), frontend dùng `frontend/Dockerfile` (dựng Node → phục vụ bằng nginx). Máy dựng không cần cài JDK, Maven hay Node.js.

1. Sao chép `.env.example` thành `.env` và điền thông tin kết nối tới cơ sở dữ liệu new-api hiện có (công cụ này chỉ đọc bảng `logs`/`users` của new-api, không tạo hay sửa bảng; nên dùng tài khoản chỉ đọc):

```powershell
Copy-Item .env.example .env
```

Trên Linux/macOS: `cp .env.example .env`

2. Dựng và khởi động:

```bash
docker compose up -d --build
```

3. Truy cập `http://<máy-chủ>:8080/new-api-stat/` (đổi cổng host qua `WEB_PORT` trong `.env`). nginx reverse proxy `/new-api-stat-api` tới container backend nên trang và API cùng nguồn gốc, không cần cấu hình CORS thêm; dừng bằng `docker compose down`.

- Các ví dụ dùng MySQL và kết nối tới cơ sở dữ liệu new-api trên máy host qua `host.docker.internal` (compose tự thêm ánh xạ `host-gateway` trên Linux). Nếu cơ sở dữ liệu chạy trong một Docker network khác, hãy nối stack này vào network đó theo chú thích cuối `compose.yaml` và đổi host trong `DB_URL` thành tên service tương ứng.
- `compose.yaml` mặc định cung cấp SQL khởi tạo kết nối cho MySQL (`SET time_zone = '+08:00'`, tương ứng `APP_TIME_ZONE=Asia/Shanghai` mặc định); để đổi múi giờ hoặc chuyển sang PostgreSQL, hãy ghi đè `DB_CONNECTION_INIT_SQL` trong `.env` (ví dụ cú pháp PostgreSQL: `SET TIME ZONE 'Asia/Shanghai'`).
- Frontend mặc định dùng `VITE_BASE_PATH=/new-api-stat/` và `VITE_API_BASE_URL=/new-api-stat-api/api`, tương ứng với tiền tố `location` trong `frontend/nginx.conf`; nếu triển khai ở sub-path khác, hãy sửa đồng bộ `VITE_BASE_PATH` trong compose và `frontend/nginx.conf`.
- Chỉ đặt thông tin xác thực thật trong `.env` (bị .gitignore bỏ qua) hoặc Secret của nền tảng — đừng commit chúng.

## Ghi chú bảo mật

- Nên dùng tài khoản cơ sở dữ liệu chỉ-đọc.
- CORS mặc định chỉ cho phép địa chỉ phát triển cục bộ; trong production phải đặt tên miền frontend thực tế.
- Các endpoint thống kê phụ thuộc vào đăng nhập Session; theo mặc định chỉ vai trò quản trị viên/root của new-api truy cập được thống kê, số dư, danh sách người dùng, xuất dữ liệu và trạng thái cơ sở dữ liệu. Backend bật CSRF Token dựa trên Cookie; hãy dùng HTTPS và giới hạn nguồn truy cập ở lớp reverse proxy.
- Phạm vi ngày truy vấn tối đa 366 ngày, mỗi lần tối đa 100 người dùng; xuất dữ liệu cũng chịu cùng giới hạn truy vấn.
- Ứng dụng không bao giờ nhận hoặc kiểm tra địa chỉ kết nối cơ sở dữ liệu tùy ý do người dùng gửi; chỉ kết nối tới nguồn dữ liệu được cấu hình khi khởi động.
- Trong production nên tắt Swagger, đồng thời cân nhắc thêm giới hạn tốc độ đăng nhập, nhật ký kiểm toán và phân quyền vai trò chặt chẽ hơn.
- Dự án này không đảm nhận việc sao lưu, migration hoặc nâng cấp cơ sở dữ liệu new-api; trước khi nâng cấp hãy sao lưu và chạy kiểm thử hồi quy.
- Ứng dụng hiện dùng Session trong bộ nhớ trên một nút; khi triển khai production nhiều instance cần cấu hình Session dùng chung (ví dụ: Spring Session + Redis) hoặc duy trì một instance duy nhất.
- Bản dựng production hiện tại của frontend tạo ra bundle JavaScript khá lớn; chấp nhận được cho bản Alpha đầu tiên, về sau nên lazy-load theo route.

## Giấy phép

Dự án này được phát hành theo [Giấy phép MIT](LICENSE).

## Tài liệu liên quan

Các tài liệu sau hiện chỉ có bằng tiếng Trung:

- [Hướng dẫn đóng góp](CONTRIBUTING.md)
- [Nhật ký thay đổi](CHANGELOG.md)
- [Chính sách bảo mật](SECURITY.md)
