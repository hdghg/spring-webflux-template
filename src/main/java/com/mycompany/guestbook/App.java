package com.mycompany.guestbook;

import com.mycompany.guestbook.configuration.Constants;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.http.server.reactive.ReactorHttpHandlerAdapter;
import org.springframework.web.server.adapter.WebHttpHandlerBuilder;
import reactor.ipc.netty.NettyContext;
import reactor.ipc.netty.http.server.HttpServer;

import java.util.concurrent.ExecutionException;

public class App {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.scan("com.mycompany.guestbook");
        ctx.refresh();
        Environment env = ctx.getBean(Environment.class);
        String bind = env.getProperty(Constants.BIND, "0.0.0.0");
        int port = env.getProperty(Constants.PORT, Integer.class, 8080);
        HttpHandler handler = WebHttpHandlerBuilder.applicationContext(ctx).build();
        ReactorHttpHandlerAdapter handlerAdapter = new ReactorHttpHandlerAdapter(handler);
        NettyContext server = HttpServer.create(bind, port).newHandler(handlerAdapter).block();
        server.channel().closeFuture().sync();
    }
}
