# PillMind Backend - CI/CD Configuration

## Structure

```
/home/mvmaycon/projetos/cicd/pillmind-backend/
├── docker/
│   ├── Dockerfile        # Multi-stage Docker build
│   └── .dockerignore     # Docker build exclusions
└── k8s/
    ├── base/             # Base Kubernetes manifests
    │   ├── namespace.yaml
    │   ├── configmap.yaml
    │   ├── secret.yaml
    │   ├── deployment.yaml
    │   ├── service.yaml
    │   ├── ingress.yaml
    │   └── kustomization.yaml
    └── overlays/         # Environment-specific overlays
        ├── dev/
        ├── staging/
        └── prod/
```

## Quick Start

### 1. Build Docker Image

```bash
cd /home/mvmaycon/Documents/projetos/mayconaraujosantos/repositories/pillmind-backend
docker build -t pillmind-backend:latest -f /home/mvmaycon/projetos/cicd/pillmind-backend/docker/Dockerfile .
```

### 2. Deploy to Kubernetes (dev)

```bash
kubectl apply -k /home/mvmaycon/projetos/cicd/pillmind-backend/k8s/overlays/dev
```

### 3. Access Application

- **API:** <https://pillmind.192.168.1.7.nip.io>
- **Argo CD:** <https://argocd.192.168.1.7.nip.io>

## Argo CD Integration

Create an Application in Argo CD pointing to this repository.

## Environment Variables

Managed via ConfigMap and Secret. Update values in:

- `k8s/base/configmap.yaml` (non-sensitive)
- `k8s/base/secret.yaml` (sensitive - use sealed-secrets or external secrets in production)
