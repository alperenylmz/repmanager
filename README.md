# RepManager Package Server

This project is a modular Spring Boot application designed to upload and download `.rep` packages along with their metadata.

## 🧱 Features

- ✅ Upload & Download packages
- ✅ File System and Object Storage (MinIO) support
- ✅ PostgreSQL metadata storage
- ✅ Strategy Pattern for pluggable storage
- ✅ Dockerized deployment

## 🚀 Quick Start (Docker)

### 1. Build the JAR and Docker image

```bash
mvn clean install
cd repmanager-core
docker build -t repmanager-app .
```

### 2. Run PostgreSQL & MinIO

```bash
docker run -d -p 5432:5432 --name postgres \
  -e POSTGRES_DB=repmanager -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=postgres \
  postgres:16

docker run -d -p 9000:9000 -p 9001:9001 --name minio \
  -e MINIO_ROOT_USER=minioadmin -e MINIO_ROOT_PASSWORD=minioadmin \
  quay.io/minio/minio server /data --console-address ":9001"
```

### 3. Run the application

```bash
docker run --rm -p 8080:8080 repmanager-app
```

## 📦 API Usage

### Upload `.rep` Package

```bash
curl -X POST http://localhost:8080/mypackage/1.0.0 \
  -F "package=@test-package.rep" \
  -F "meta=@meta.json"
```

### Download `.rep` or `meta.json`

```bash
curl -O http://localhost:8080/mypackage/1.0.0/package.rep
curl -O http://localhost:8080/mypackage/1.0.0/meta.json
```

## ⚙️ Configuration

Update `application.yml` to switch between file-system and object-storage:

```yaml
storage:
  strategy: object-storage # or file-system
  minio:
    endpoint: http://host.docker.internal:9000
    accessKey: minioadmin
    secretKey: minioadmin
    bucket: repmanager
```


## 📦 Maven Repository

The following libraries have been published to a public Maven repository on Repsy:

- `rep-storage-filesystem`
- `rep-storage-object`

Repository: [https://repo.repsy.io/mvn/alperenylmz/repmanager/](https://repo.repsy.io/mvn/alperenylmz/repmanager/)

To use:

```xml
<repository>
  <id>repsy</id>
  <url>https://repo.repsy.io/mvn/alperenylmz/repmanager/</url>
</repository>
```

## 🐳 Docker Image

The Docker image is published publicly at:

👉 [https://hub.docker.com/r/vigoureux/repmanager](https://hub.docker.com/r/vigoureux/repmanager)

To pull:

```bash
docker pull vigoureux/repmanager
```

## 📝 Author

Created by **Alperen Yılmaz**  
As part of the **Junior FullStack Developer Assignment** @ Repsy.
