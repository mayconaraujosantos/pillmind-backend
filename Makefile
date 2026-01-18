# PillMind Backend - Makefile

SHELL := /bin/bash

# Variables
APP_NAME := pillmind-backend
NAMESPACE := pillmind-dev
KIND_CLUSTER := pillmind
IMAGE := pillmind-backend:latest
DOCKERFILE := docker/Dockerfile
DOMAIN := pillmind.192.168.1.7.nip.io

# Default target
.PHONY: help
help:
	@echo "PillMind Backend - Makefile Commands"
	@echo "-------------------------------------"
	@echo "make build             - Gradle build"
	@echo "make test              - Run tests"
	@echo "make run               - Run app (local)"
	@echo "make clean             - Clean build"
	@echo "make docker-build      - Build Docker image"
	@echo "make kind-load         - Load image into kind cluster"
	@echo "make k8s-restart       - Restart deployment in cluster"
	@echo "make k8s-logs          - Tail logs from backend pods"
	@echo "make k8s-delete-pods   - Delete backend pods (force restart)"
	@echo "make curl-health       - Check health endpoint via ingress"
	@echo "make curl-swagger      - Check Swagger UI via ingress"
	@echo "make hash PASSWORD=... - Generate BCrypt hash for a password"
	@echo "make redeploy          - Build, load to kind, and restart pods"

.PHONY: build
build:
	./gradlew build

.PHONY: test
test:
	./gradlew test

.PHONY: run
run:
	./gradlew run

.PHONY: clean
clean:
	./gradlew clean

.PHONY: docker-build
docker-build:
	docker build -t $(IMAGE) -f $(DOCKERFILE) .

.PHONY: kind-load
kind-load:
	kind load docker-image $(IMAGE) --name $(KIND_CLUSTER)

.PHONY: k8s-restart
k8s-restart:
	kubectl rollout restart deployment $(APP_NAME) -n $(NAMESPACE)

.PHONY: k8s-logs
k8s-logs:
	kubectl logs -n $(NAMESPACE) -l app=$(APP_NAME) --tail=100 -f

.PHONY: k8s-delete-pods
k8s-delete-pods:
	kubectl delete pods -n $(NAMESPACE) -l app=$(APP_NAME)

.PHONY: curl-health
curl-health:
	curl -k -i https://$(DOMAIN)/api/health

.PHONY: curl-swagger
curl-swagger:
	curl -k -I https://$(DOMAIN)/swagger-ui

.PHONY: hash
hash:
	@if [ -z "$(PASSWORD)" ]; then echo "Usage: make hash PASSWORD=yourPass"; exit 1; fi
	./gradlew generateHash -Ppassword="$(PASSWORD)"

.PHONY: redeploy
redeploy: docker-build kind-load k8s-delete-pods
	@echo "Redeploy triggered: image built, loaded to kind, pods restarted."