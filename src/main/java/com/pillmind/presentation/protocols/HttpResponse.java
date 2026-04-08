package com.pillmind.presentation.protocols;

/**
 * Interface para respostas HTTP
 */
public interface HttpResponse {
    int getStatusCode();
    Object getBody();
}
