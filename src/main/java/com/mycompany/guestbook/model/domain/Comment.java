package com.mycompany.guestbook.model.domain;

public class Comment {
    private Long id;
    private String header;
    private String body;


    public Long getId() {
        return id;
    }

    public Comment setId(Long id) {
        this.id = id;
        return this;
    }

    public String getHeader() {
        return header;
    }

    public Comment setHeader(String header) {
        this.header = header;
        return this;
    }

    public String getBody() {
        return body;
    }

    public Comment setBody(String body) {
        this.body = body;
        return this;
    }
}
