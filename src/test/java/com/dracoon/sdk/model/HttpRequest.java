package com.dracoon.sdk.model;

import java.util.List;

public class HttpRequest {

    public HttpMethod method;
    public String url;
    public List<HttpHeader> headers;
    public HttpBody body;

}
