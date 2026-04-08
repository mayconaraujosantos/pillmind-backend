package com.pillmind.presentation.protocols;

import java.util.Map;

/**
 * Interface para requisições HTTP
 */
public interface HttpRequest {
    Object getBody();
    Map<String, String> getHeaders();
    Map<String, String> getParams();
    Map<String, String> getQueryParams();
}
