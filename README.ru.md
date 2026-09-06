# new-api-stat

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
[![CI](https://github.com/CodeMonkeyJust/new-api-stat/actions/workflows/ci.yml/badge.svg)](https://github.com/CodeMonkeyJust/new-api-stat/actions/workflows/ci.yml)

<p align="center">
  <a href="./README.md">简体中文</a> |
  <a href="./README.en.md">English</a> |
  <a href="./README.fr.md">Français</a> |
  <strong>Русский</strong> |
  <a href="./README.ja.md">日本語</a> |
  <a href="./README.vi.md">Tiếng Việt</a> |
  <a href="./README.zh-TW.md">繁體中文</a>
</p>

Инструмент статистического анализа расхода токенов и вызовов для [new-api](https://github.com/Calcium-Ion/new-api). Предоставляет панель мониторинга, тренды, модели, почасовые данные, пользователей и баланс; бэкенд предоставляет API экспорта в Excel, однако во фронтенде кнопка экспорта пока не интегрирована.

> Проект находится на ранней стадии. Перед использованием в production проверьте совместимость базы данных и корректность методики подсчёта статистики в тестовой среде.

## Возможности

- Вход пользователя (используется таблица `users` из new-api)
- Обзорная панель: итоги, сегодня, вчера и ключевые пользователи за последние 7 дней
- Статистика по пользователям, моделям и группам: расход токенов, количество вызовов и стоимость
- Ежедневные тренды, распределение за 24 часа, детализация по моделям и по пользователям за день
- Запрос баланса пользователей
- API экспорта результатов статистики в Excel (`.xlsx`) на бэкенде (вход во фронтенде будет доработан в одном из следующих релизов)
- Поддержка PostgreSQL и MySQL (переключение через `DB_URL`, см. раздел о конфигурации)

## Технологический стек

- Бэкенд: Java 17, Spring Boot 3, Spring Data JPA; поддержка баз данных PostgreSQL и MySQL
- Фронтенд: Vue 3, TypeScript, Vite, Element Plus, ECharts

## Структура проекта

```text
backend/   Сервис API на Spring Boot
db/        Справочный SQL по структуре базы данных
frontend/  Фронтенд на Vue
```

## Быстрый старт

### 1. Подготовка базы данных

Проект напрямую читает таблицы `logs` и `users` из new-api для статистики только на чтение. Поддерживаются PostgreSQL и MySQL; переключение выполняется через `DB_URL` (по умолчанию PostgreSQL). Сначала убедитесь, что учётная запись базы данных имеет права только на чтение, и проверьте совместимость SQL и версий полей в тестовой среде.

`db/MySQL.sql` — справочный файл со структурой таблиц целевого экземпляра MySQL для new-api, поясняющий структуру базы данных new-api (проект в основном читает таблицы `logs` и `users`). Актуальность проверена для new-api **v1.0.0-rc.32**. Файл предназначен только для чтения/справки и не является скриптом миграции; не выполняйте его на любой базе данных и не перезаписывайте существующие таблицы.

### 2. Запуск бэкенда

Требования: Java 17, Maven 3.9+. Бэкенд основан на Spring Boot 3.5 (точная версия указана в `backend/pom.xml`).

```bash
cd backend
mvn clean package -DskipTests
java -jar target/newapi-stat-api-1.0.0.jar
```

По умолчанию бэкенд слушает `http://localhost:8082` с путём контекста `/newapi-stat-api`. Перед первым запуском необходимо задать `DB_USERNAME` и `DB_PASSWORD`; приложение никогда не создаёт и не изменяет таблицы new-api. Swagger UI и OpenAPI JSON по умолчанию отключены; для разработки и отладки установите `SWAGGER_ENABLED=true`. Документация API:

- Swagger UI: `http://localhost:8082/newapi-stat-api/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8082/newapi-stat-api/v3/api-docs`

### 3. Запуск фронтенда

Требования: Node.js 20.19+, npm 10+.

```bash
cd frontend
npm ci
npm run dev
```

По умолчанию dev-сервер слушает `http://localhost:3001`. Dev-прокси по умолчанию перенаправляет запросы на `http://localhost:8082`; если бэкенд работает по другому адресу, настройте его через переменную окружения:

```powershell
$env:VITE_API_PROXY_TARGET = "http://localhost:8082"
npm run dev
```

При изменении `SERVER_CONTEXT_PATH` синхронно скорректируйте `VITE_API_BASE_URL`, путь прокси Vite и правила обратного прокси.

Сборка для production:

```bash
npm run build
```

Результаты сборки находятся в `frontend/dist/`. Разместите их на веб-сервере и настройте обратный прокси с `/newapi-stat-api` на бэкенд.

## Конфигурация

Бэкенд отдаёт приоритет переменным окружения, чтобы не попадать учётными данными базы данных в Git:

| Переменная окружения | Значение по умолчанию | Описание |
| --- | --- | --- |
| `DB_URL` | `jdbc:postgresql://localhost:5432/new-api` | JDBC-адрес; подходят и PostgreSQL, и MySQL (например, `jdbc:mysql://host:3306/new_api?...`). Драйвер и диалект определяются автоматически по URL |
| `DB_USERNAME` | нет | Имя пользователя базы данных; должно быть задано явно. Рекомендуется учётная запись только для чтения |
| `DB_PASSWORD` | нет | Пароль базы данных; должен быть задан явно |
| `DB_CONNECTION_INIT_SQL` | выражение часового пояса PostgreSQL | Инициализирующий SQL, выполняемый после установления каждого соединения; используется для единообразного часового пояса при подсчёте статистики по `created_at` (секунды epoch). Для MySQL укажите фиксированное смещение, соответствующее `APP_TIME_ZONE`, например `SET time_zone = '+08:00'` |
| `SERVER_PORT` | `8082` | Порт сервера |
| `SERVER_CONTEXT_PATH` | `/newapi-stat-api` | Путь контекста сервера |
| `APP_TIME_ZONE` | `Asia/Shanghai` | Часовой пояс для дат и часов статистики |
| `APP_ANALYTICS_ROLES` | `10,100` | Значения ролей new-api, которым разрешён доступ к API статистики; по умолчанию администратор/корневой пользователь |
| `CORS_ALLOWED_ORIGINS` | `http://localhost:3001,http://localhost:5173` | Разрешённые источники фронтенда через запятую |
| `SESSION_COOKIE_SECURE` | `false` | Для HTTPS-развёртывания установите `true` |
| `SESSION_COOKIE_SAME_SITE` | `lax` | Атрибут SameSite для cookie сессии |
| `SWAGGER_ENABLED` | `false` | Включать ли Swagger/OpenAPI |

Пример:

```bash
export DB_URL='jdbc:postgresql://db.example.com:5432/new-api'
export DB_USERNAME='newapi_stat_readonly'
export DB_PASSWORD='replace-with-a-secret'
export APP_TIME_ZONE='Asia/Shanghai'
export CORS_ALLOWED_ORIGINS='https://stat.example.com'
export SESSION_COOKIE_SECURE='true'
export SWAGGER_ENABLED='false'
```

Пример для MySQL (обратите внимание: синтаксис `DB_CONNECTION_INIT_SQL` отличается от PostgreSQL):

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

### Управление учётными данными и ключами

Конфиденциальные значения, такие как пароли базы данных, передаются только через переменные окружения или Secrets платформы; в репозитории нет реальных учётных данных. Файлы `.env*`, ключи и сертификаты исключены через `.gitignore`; сохраняются только файлы-примеры `*.example`. В CI выполняется сканирование секретов gitleaks, и любой захардкоженный секрет приведёт к ошибке проверки. Если конфиденциальная информация была случайно закоммичена, немедленно смените соответствующие учётные данные и очистите историю Git.

### Роли и контроль доступа

По умолчанию только роли new-api из `APP_ANALYTICS_ROLES` могут обращаться к API статистики, балансу пользователей, списку пользователей, экспорту данных и конечным точкам состояния базы данных; значение по умолчанию — `10,100`. Значения ролей могут различаться в разных версиях new-api; ориентируйтесь на исходный код/соглашения о базе данных целевой версии и настраивайте их через переменную окружения. Обычные вошедшие пользователи не могут обращаться к этим конечным точкам.

## Docker

Текущий Dockerfile — это образ времени выполнения, не включающий сборку Maven. Сначала соберите JAR бэкенда, затем создайте образ:

```bash
cd backend
mvn clean package -DskipTests
copy target\newapi-stat-api-1.0.0.jar app.jar  # в Windows PowerShell можно использовать Copy-Item
# Linux/macOS: cp target/newapi-stat-api-1.0.0.jar app.jar
docker build -t newapi-stat-api .
docker run --rm -p 8082:8082 \
  -e DB_URL='jdbc:postgresql://host.docker.internal:5432/new-api' \
  -e DB_USERNAME='newapi_stat_readonly' \
  -e DB_PASSWORD='replace-with-a-secret' \
  --name newapi-stat-api \
  newapi-stat-api
```

Пример для MySQL:

```bash
docker run --rm -p 8082:8082 \
  -e DB_URL='jdbc:mysql://host.docker.internal:3306/new_api?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&useSSL=false&allowPublicKeyRetrieval=true' \
  -e DB_USERNAME='newapi_stat_readonly' \
  -e DB_PASSWORD='replace-with-a-secret' \
  -e DB_CONNECTION_INIT_SQL="SET time_zone = '+08:00'" \
  --name newapi-stat-api \
  newapi-stat-api
```

Не коммитьте реальные пароли, внутренние IP-адреса или `app.jar`. В production передавайте учётные данные через Secrets, переменные окружения или конфигурацию платформы.

## Примечания по безопасности

- Рекомендуется использовать учётную запись базы данных только для чтения.
- По умолчанию CORS разрешает только адреса локальной разработки; в production обязательно укажите реальный домен фронтенда.
- Конечные точки статистики зависят от входа по сессии; по умолчанию только роли администратора/корневого пользователя new-api имеют доступ к статистике, балансу, списку пользователей, экспорту и состоянию базы данных. Бэкенд включает CSRF-токен на основе cookie; используйте HTTPS и ограничивайте источники доступа на уровне обратного прокси.
- Диапазон дат запроса ограничен 366 днями, не более 100 пользователей за один запрос; экспорт подчиняется тем же ограничениям.
- Приложение никогда не принимает и не проверяет произвольные URL подключения к базе данных от пользователей; оно подключается только к источнику данных, настроенному при запуске.
- В production рекомендуется отключить Swagger, а также добавить ограничение частоты входа, журналы аудита и более строгую авторизацию по ролям.
- Этот проект не выполняет резервное копирование, миграцию или обновление базы данных new-api; перед обновлением сделайте резервную копию и проведите регрессионное тестирование.
- Приложение использует сессии в памяти на одном узле; для многопоточных production-развёртываний настройте общие сессии (например, Spring Session + Redis) или поддерживайте один экземпляр.
- Текущая production-сборка фронтенда создаёт довольно крупный JavaScript-бандл; для первого альфа-релиза это допустимо, в дальнейшем рекомендуется ленивая загрузка по маршрутам.

## Лицензия

Проект распространяется под [лицензией MIT](LICENSE).

## Связанные документы

Следующие документы пока доступны только на китайском языке:

- [Руководство по участию](CONTRIBUTING.md)
- [Журнал изменений](CHANGELOG.md)
- [Политика безопасности](SECURITY.md)
