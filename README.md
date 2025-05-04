# 🔔 couponmoa-notification

## 📌 개요

이 서버는 Couponmoa 프로젝트의 **알림 전담 서비스**로,  
**쿠폰 발급, 만료, 구독 알림 등을 SSE 또는 이메일로 사용자에게 전달**합니다.  

---

## 📡 주요 기능

- 실시간 SSE(Server-Sent Events)를 통한 클라이언트 알림 전송
- 이메일을 통한 쿠폰 만료, 구독 알림
- Amazon SQS 기반 비동기 메시지 처리

---

## 🔗 외부 API

### SSE 구독

| 메서드 | URI | 설명 |
|--------|-----|------|
| `GET` | `/api/v1/sse/subscribe` | 사용자 SSE 구독 |

---

## ⚙️ 내부 처리 흐름

- Store 서버 또는 Coupon 서버에서 SQS로 알림 메시지 전송
- Notification 서버는 SQS에서 메시지를 수신
- 알림 종류에 따라 이메일 전송 또는 SSE 푸시 처리
- 전송 상태 및 실패 이력 저장
- redis의 setIfAbsent를 활용한 멱등성 보장 처리

---

## 🧰 기술 스택

- Java 17
- Spring Boot 3.x
- Amazon SQS
- JavaMailSender (SMTP)
- Redis
- Gradle

---


