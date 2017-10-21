package com.mycompany.guestbook.dao;

import com.mycompany.guestbook.model.domain.Comment;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CommentDao {
    Flux<Comment> findAll();

    Flux<String> save(Flux<Comment> commentFlux);

    Mono<Long> delete(long id);
}
