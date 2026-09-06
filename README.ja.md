# new-api-stat

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
[![CI](https://github.com/CodeMonkeyJust/new-api-stat/actions/workflows/ci.yml/badge.svg)](https://github.com/CodeMonkeyJust/new-api-stat/actions/workflows/ci.yml)

<p align="center">
  <a href="./README.md">简体中文</a> |
  <a href="./README.en.md">English</a> |
  <a href="./README.fr.md">Français</a> |
  <a href="./README.ru.md">Русский</a> |
  <strong>日本語</strong> |
  <a href="./README.vi.md">Tiếng Việt</a> |
  <a href="./README.zh-TW.md">繁體中文</a>
</p>

[new-api](https://github.com/Calcium-Ion/new-api) 向けの Token 消費量・呼び出し統計分析ツールです。ダッシュボード、トレンド、モデル、時間帯、ユーザー、残高などのビューを提供し、バックエンドは Excel エクスポート API を提供します（フロントエンドには現時点ではエクスポートボタンは未統合です）。

> 本プロジェクトは初期段階にあります。本番環境で使用する前に、テスト環境でデータベース互換性と統計の算出方法を検証してください。

## 機能

- ユーザーログイン（new-api の `users` テーブルを再利用）
- 概要ダッシュボード：合計・今日・昨日・直近 7 日間の主要ユーザー
- ユーザー・モデル・グループ別の Token 消費量、呼び出し回数、費用の統計
- 日次トレンド、24 時間分布、モデル別日次明細、ユーザー別日次明細
- ユーザー残高の照会
- バックエンドによる統計結果の `.xlsx` エクスポート API（フロントエンドの導線は後続バージョンで整備予定）
- PostgreSQL と MySQL に対応（`DB_URL` で切り替え。設定の説明を参照）

## 技術スタック

- バックエンド：Java 17、Spring Boot 3、Spring Data JPA（PostgreSQL / MySQL 対応）
- フロントエンド：Vue 3、TypeScript、Vite、Element Plus、ECharts

## プロジェクト構成

```text
backend/   Spring Boot API サービス
db/        データベース構造の参考 SQL
frontend/  Vue フロントエンド
```

## クイックスタート

### 1. データベースの準備

本プロジェクトは new-api の `logs` テーブルと `users` テーブルを読み取り専用で直接参照し、統計を取得します。PostgreSQL と MySQL の両方に対応しており、`DB_URL` で切り替えます（デフォルトは PostgreSQL）。まずデータベースアカウントに読み取り専用のクエリ権限があることを確認し、テスト環境で SQL とフィールドのバージョン互換性を検証してください。

`db/MySQL.sql` は、対象となる new-api MySQL インスタンスのテーブル構造を説明するための参考ファイルです（本プロジェクトは主に `logs` と `users` の 2 テーブルを読み取ります）。現在は new-api **v1.0.0-rc.32** のテーブル構造で検証しています。このファイルは読み取り・参考専用であり、マイグレーションスクリプトではありません。いかなるデータベースに対しても実行せず、既存のテーブルを上書きしないでください。

### 2. バックエンドの起動

要件：Java 17、Maven 3.9+。バックエンドは Spring Boot 3.5 ベースです（正確なバージョンは `backend/pom.xml` を参照）。

```bash
cd backend
mvn clean package -DskipTests
java -jar target/newapi-stat-api-1.0.0.jar
```

バックエンドはデフォルトで `http://localhost:8082` をリッスンし、コンテキストパスは `/newapi-stat-api` です。初回起動前に `DB_USERNAME` と `DB_PASSWORD` の設定が必須です。アプリケーションは new-api のテーブル構造を作成・変更しません。Swagger UI と OpenAPI JSON はデフォルトで無効です。開発・デバッグ時は `SWAGGER_ENABLED=true` を設定してください。API ドキュメント：

- Swagger UI：`http://localhost:8082/newapi-stat-api/swagger-ui.html`
- OpenAPI JSON：`http://localhost:8082/newapi-stat-api/v3/api-docs`

### 3. フロントエンドの起動

要件：Node.js 20.19+、npm 10+。

```bash
cd frontend
npm ci
npm run dev
```

開発サーバーのデフォルトアドレスは `http://localhost:3001` で、ページのパスはデフォルトで `/new-api-stat/` です（`http://localhost:3001` にアクセスすると自動的にこのパスへリダイレクトされます）。開発プロキシはデフォルトで `http://localhost:8082` に転送します。バックエンドが別のアドレスの場合は、環境変数で設定してください：

```powershell
$env:VITE_API_PROXY_TARGET = "http://localhost:8082"
npm run dev
```

`SERVER_CONTEXT_PATH` を変更する場合は、`VITE_API_BASE_URL`、Vite プロキシパス、リバースプロキシのルールも合わせて調整してください。

本番ビルド：

```bash
npm run build
```

ビルド成果物は `frontend/dist/` に出力され、ページのパスはデフォルトで `/new-api-stat/` です（例：`https://<ドメイン>/new-api-stat/`）。`/new-api-stat/` を `frontend/dist/` にマッピングしてホストし、`/newapi-stat-api` をバックエンドへリバースプロキシしてください。ルートや別のパスにデプロイする場合は、ビルド前に `VITE_BASE_PATH` を設定してください（例：`VITE_BASE_PATH=/` または `VITE_BASE_PATH=/stat/`）。

## 設定

バックエンドは、データベース認証情報を Git にコミットしないよう、環境変数を優先して設定を読み込みます：

| 環境変数 | デフォルト値 | 説明 |
| --- | --- | --- |
| `DB_URL` | `jdbc:postgresql://localhost:5432/new-api` | JDBC アドレス。PostgreSQL / MySQL のどちらでも可（例：`jdbc:mysql://host:3306/new_api?...`）。ドライバと方言は URL から自動判定 |
| `DB_USERNAME` | なし | データベースのユーザー名。明示的な設定が必須。読み取り専用アカウントを推奨 |
| `DB_PASSWORD` | なし | データベースのパスワード。明示的な設定が必須 |
| `DB_CONNECTION_INIT_SQL` | PostgreSQL のタイムゾーン文 | 各接続確立後に実行する初期化 SQL。`created_at`（epoch 秒）統計のタイムゾーンを統一するために使用。MySQL の場合は `APP_TIME_ZONE` に対応する固定オフセット（例：`SET time_zone = '+08:00'`）に変更 |
| `SERVER_PORT` | `8082` | サーバーポート |
| `SERVER_CONTEXT_PATH` | `/newapi-stat-api` | サーバーのコンテキストパス |
| `APP_TIME_ZONE` | `Asia/Shanghai` | 統計の日付と時間に使用するタイムゾーン |
| `APP_ANALYTICS_ROLES` | `10,100` | 統計 API へのアクセスを許可する new-api のロール値。デフォルトは管理者 / ルートユーザー |
| `CORS_ALLOWED_ORIGINS` | `http://localhost:3001,http://localhost:5173` | 許可するフロントエンドのオリジン（カンマ区切り） |
| `SESSION_COOKIE_SECURE` | `false` | HTTPS デプロイ時は `true` に設定 |
| `SESSION_COOKIE_SAME_SITE` | `lax` | セッション Cookie の SameSite 属性 |
| `SWAGGER_ENABLED` | `false` | Swagger/OpenAPI を有効にするかどうか |

例：

```bash
export DB_URL='jdbc:postgresql://db.example.com:5432/new-api'
export DB_USERNAME='newapi_stat_readonly'
export DB_PASSWORD='replace-with-a-secret'
export APP_TIME_ZONE='Asia/Shanghai'
export CORS_ALLOWED_ORIGINS='https://stat.example.com'
export SESSION_COOKIE_SECURE='true'
export SWAGGER_ENABLED='false'
```

MySQL の例（`DB_CONNECTION_INIT_SQL` の構文は PostgreSQL と異なる点に注意）：

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

### 認証情報とキー管理

データベースのパスワードなどの機密情報は、環境変数またはプラットフォームの Secret からのみ注入します。このリポジトリには実際の認証情報は存在しません。`.env*`、キー、証明書ファイルは `.gitignore` で除外され、`*.example` のサンプルファイルのみ保持されます。CI では gitleaks によるシークレットスキャンが実行され、ハードコードされたシークレットがあるとチェックが失敗します。誤って機密情報をコミットした場合は、直ちに認証情報をローテーションし、Git 履歴を掃除してください。

### ロールとアクセス制御

統計 API、ユーザー残高、ユーザー一覧、データエクスポート、データベース状態の各エンドポイントは、デフォルトでは `APP_ANALYTICS_ROLES` に含まれる new-api ロールのみアクセスできます（デフォルト値は `10,100`）。new-api のバージョンによってロール値が異なる場合があります。対象バージョンのソースコード / データベースの規約を確認し、環境変数で調整してください。一般のログインユーザーはこれらのエンドポイントにアクセスできません。

## Docker

現在の Dockerfile はランタイムイメージであり、Maven ビルドの工程は含みません。先にバックエンドの JAR をビルドしてからイメージをビルドしてください：

```bash
cd backend
mvn clean package -DskipTests
copy target\newapi-stat-api-1.0.0.jar app.jar  # Windows PowerShell では Copy-Item を使用可
# Linux/macOS: cp target/newapi-stat-api-1.0.0.jar app.jar
docker build -t newapi-stat-api .
docker run --rm -p 8082:8082 \
  -e DB_URL='jdbc:postgresql://host.docker.internal:5432/new-api' \
  -e DB_USERNAME='newapi_stat_readonly' \
  -e DB_PASSWORD='replace-with-a-secret' \
  --name newapi-stat-api \
  newapi-stat-api
```

MySQL の例：

```bash
docker run --rm -p 8082:8082 \
  -e DB_URL='jdbc:mysql://host.docker.internal:3306/new_api?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&useSSL=false&allowPublicKeyRetrieval=true' \
  -e DB_USERNAME='newapi_stat_readonly' \
  -e DB_PASSWORD='replace-with-a-secret' \
  -e DB_CONNECTION_INIT_SQL="SET time_zone = '+08:00'" \
  --name newapi-stat-api \
  newapi-stat-api
```

実際のパスワード、社内 IP、`app.jar` をリポジトリにコミットしないでください。本番環境では Secret、環境変数、プラットフォーム設定経由で認証情報を注入してください。

### Docker Compose でのデプロイ

Compose を使うと nginx フロントエンドとバックエンドを 1 コマンドで起動でき、マルチステージ Dockerfile で両イメージも自動ビルドされます：バックエンドは `backend/Dockerfile.compose`（Maven パッケージ → JRE 実行）、フロントエンドは `frontend/Dockerfile`（Node ビルド → nginx 配信）です。ビルド機に JDK・Maven・Node.js は不要です。

1. `.env.example` を `.env` にコピーし、既存の new-api データベースへの接続情報を記入します（本ツールは new-api の `logs`/`users` テーブルを読み取り専用で利用し、テーブルの作成・変更は行いません。読み取り専用アカウントを推奨）：

```powershell
Copy-Item .env.example .env
```

Linux/macOS の場合：`cp .env.example .env`

2. ビルドして起動：

```bash
docker compose up -d --build
```

3. `http://<ホスト>:8080/new-api-stat/` を開きます（ホスト側ポートは `.env` の `WEB_PORT` で変更）。nginx が `/newapi-stat-api` をバックエンドコンテナへリバースプロキシするため、ページと API は同一オリジンとなり追加の CORS 設定は不要です。停止は `docker compose down`。

- デフォルト例では `host.docker.internal` 経由でホスト上の new-api データベースへ接続します（Linux では compose が自動的に `host-gateway` マッピングを追加）。データベースが別の Docker ネットワークにある場合は、`compose.yaml` 末尾のコメントに従ってこのスタックをそのネットワークへ接続し、`DB_URL` のホスト名を対応するサービス名に変更してください。
- MySQL を使う場合は `compose.yaml` の `DB_CONNECTION_INIT_SQL` 行のコメントを外してください（タイムゾーンは `APP_TIME_ZONE` と一致させること）。
- フロントエンドは既定で `VITE_BASE_PATH=/new-api-stat/`、`VITE_API_BASE_URL=/newapi-stat-api/api` で、`frontend/nginx.conf` の location プレフィックスと対応しています。別のサブパスへデプロイする場合は compose の `VITE_BASE_PATH` と `frontend/nginx.conf` を合わせて変更してください。
- 実際の認証情報は `.env`（.gitignore で無視）またはプラットフォームの Secret にのみ置き、コミットしないでください。

## セキュリティ上の注意

- データベースアカウントは読み取り専用権限を推奨します。
- デフォルトの CORS はローカル開発アドレスのみ許可します。本番環境では実際のフロントエンドドメインを必ず設定してください。
- 統計エンドポイントはセッションログインに依存します。デフォルトでは new-api の管理者 / ルートユーザーロールのみが統計・残高・ユーザー一覧・エクスポート・データベース状態のエンドポイントにアクセスできます。バックエンドは Cookie ベースの CSRF トークンを有効化しています。HTTPS を使用し、リバースプロキシ層でアクセス元を制限してください。
- クエリの日付範囲は最大 366 日、1 回のクエリで最大 100 ユーザーまでです。エクスポートも同じクエリ制限の対象です。
- アプリケーションはユーザーが送信した任意のデータベース接続アドレスを受け取らず、テストもしません。起動時に設定されたデータソースにのみ接続します。
- 本番環境では Swagger を無効にし、ログインのレート制限、監査ログ、より厳格なロール認可の追加を推奨します。
- 本プロジェクトは new-api データベースのバックアップ・移行・アップグレードを担当しません。アップグレード前には必ずバックアップと回帰検証を行ってください。
- 現状のアプリケーションは単一ノードのインメモリセッションを使用します。本番の複数インスタンス構成では、共有セッション（例：Spring Session + Redis）を設定するか、単一インスタンスを維持してください。
- 現在のフロントエンドの本番ビルドは比較的大きな JavaScript バンドルを生成します。初期 Alpha リリースでは許容範囲ですが、今後はルート単位の遅延ロードを推奨します。

## ライセンス

本プロジェクトは [MIT License](LICENSE) の下で提供されます。

## 関連ドキュメント

以下のドキュメントは現在、中国語のみで提供されています：

- [コントリビューションガイド](CONTRIBUTING.md)
- [変更履歴](CHANGELOG.md)
- [セキュリティポリシー](SECURITY.md)
