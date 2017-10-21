package com.mycompany.guestbook.controller;

import com.mycompany.guestbook.dao.CommentDao;
import com.mycompany.guestbook.model.domain.Comment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Objects;

@RestController
public class CommentController {

    private final CommentDao commentDao;

    public CommentController(CommentDao commentDao) {
        this.commentDao = commentDao;
    }

    @GetMapping(value = "/guestbook")
    public Flux<Comment> findAll() {
        return this.commentDao.findAll();
    }

    @GetMapping("/guestbook/{id}")
    public Mono<ResponseEntity<Comment>> findOne(@PathVariable Long id) {
        return this.commentDao.findAll()
                .filter(c -> Objects.equals(id, c.getId()))
                .map(ResponseEntity::ok)
                .single(ResponseEntity.notFound().build());
    }

    @PostMapping("/guestbook")
    public Mono<String> save(@RequestBody Mono<Comment> commentMono) {
        return this.commentDao.save(Flux.from(commentMono)).single();
    }

    @DeleteMapping("/guestbook/{id}")
    public Mono<Long> delete(@PathVariable Long id) {
        return commentDao.delete(id);
    }
}
