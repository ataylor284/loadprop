package ca.redtoad.loadprop;

import java.io.*;
import java.net.*;
import java.util.*;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class Example implements HttpHandler {

    private Config config;

    static class Config {
        public Optional<Integer> port;
        public String context;
        public int status;
        public String message;
    }

    public static void main(String[] args) throws Exception {
        Example example = new Example();
        example.config = new PropertiesLoader().load(Config.class, Example.class.getResourceAsStream("/config.properties"));
        HttpServer server = HttpServer.create(new InetSocketAddress(example.config.port.orElse(8080)), 0);
        server.createContext(example.config.context, example);
        server.start();
    }

    public void handle(HttpExchange http) throws IOException {
        http.sendResponseHeaders(config.status, config.message.getBytes().length);
        try (OutputStream os = http.getResponseBody()) {
            os.write(config.message.getBytes());
        }
    }
}
