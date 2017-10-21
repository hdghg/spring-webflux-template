package com.mycompany.guestbook.dao;

import com.mycompany.guestbook.configuration.Constants;
import com.mycompany.guestbook.model.domain.Comment;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.reactive.RedisReactiveCommands;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.PreDestroy;
import java.util.HashMap;
import java.util.Map;

@Component
public class RedisCommentDao implements CommentDao {

    public static final String PREFIX = "comment:";
    public static final String BODY = "body";
    public static final String HEADER = "header";
    public static final String ID = "id";

    private final RedisClient redisClient;
    private final StatefulRedisConnection<String, String> connect;
    private final RedisReactiveCommands<String, String> commands;


    public RedisCommentDao(Environment env) {
        String regisUrl = env.getProperty(Constants.REDIS_URL, "redis://localhost");
        this.redisClient = RedisClient.create(regisUrl);
        this.connect = redisClient.connect();
        this.commands = connect.reactive();

    }

    @PreDestroy
    public void destroy() {
        this.connect.close();
        this.redisClient.shutdown();
    }

    private Comment mapToComment(Map<String, String> values) {
        Comment comment = new Comment();
        comment.setHeader(values.get(HEADER));
        comment.setBody(values.get(BODY));
        String id = values.get(ID);
        comment.setId(null == id ? null : Long.valueOf(id));
        return comment;
    }

    private Map<String, String> unmapFromComment(Comment comment) {
        Map<String, String> result = new HashMap<>();
        result.put(ID, String.valueOf(comment.getId()));
        result.put(BODY, comment.getBody());
        result.put(HEADER, comment.getHeader());
        return result;
    }

    @Override
    public Flux<Comment> findAll() {
        return this.commands.keys(PREFIX + "*")
                .flatMap(this.commands::hgetall)
                .map(this::mapToComment);
    }

    @Override
    public Flux<String> save(Flux<Comment> comments) {
        return comments.flatMap(c ->
                null != c.getId() ? Mono.just(c) : commands.incr("comment_id").map(c::setId))
                .map(this::unmapFromComment)
                .flatMap(m -> this.commands.hmset(PREFIX + m.get(ID), m));
    }

    @Override
    public Mono<Long> delete(long id) {
        return commands.del(PREFIX + id);
    }
}
