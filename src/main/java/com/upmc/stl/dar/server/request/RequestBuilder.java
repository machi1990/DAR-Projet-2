package com.upmc.stl.dar.server.request;

import com.upmc.stl.dar.server.HttpServer;
import com.upmc.stl.dar.server.exceptions.ExceptionCreator;
import com.upmc.stl.dar.server.exceptions.ServerException;
import com.upmc.stl.dar.server.request.Headers;
import com.upmc.stl.dar.server.request.Request;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class RequestBuilder {
    private static final String headerEnds = HttpServer.separator()+HttpServer.separator();

    public static Request newRequest(final SocketChannel channel) throws ServerException {
        final StringBuilder builder = new StringBuilder();
        final ByteBuffer buffer = ByteBuffer.allocate(32);

        int bytesRead;

        try {
            while ((bytesRead = channel.read(buffer)) > 0) {
                builder.append(new String(buffer.array()), 0, bytesRead);
                if (builder.toString().contains(headerEnds)) {
                    break;
                }
                buffer.clear();
            }
        }  catch (IOException e) {
            e.printStackTrace();
        }

        return readBody(channel, createRequest(builder.toString().replace(headerEnds,"")));
    }

    private static Request createRequest(String input) throws ServerException {
        final Request request = new Request();
        final String[] inputs = input.split("\r\n");
        final Headers headers = new Headers();

        if (input.isEmpty()) {
            throw ExceptionCreator.creator().create(ExceptionCreator.ExceptionKind.BAD_INPUT);
        }

        final String[] methodUrlContainer = inputs[0].split(" ");
        request.setMethod(methodUrlContainer[0]);
        request.setUrl(methodUrlContainer[1]);

        for (int i = 1; i < inputs.length; ++i) {
            parse(inputs[i], request, headers);
        }
        request.setHeaders(headers);

        return request;
    }

    private static void parse(String value, final Request request, final Headers headers) {
        if (value == null || value.isEmpty()) {
            return;
        }

        int index = value.indexOf(":");
        if (index < 0) {
            return;
        }

        String key = value.substring(0, index);
        value = value.substring(index + 1).trim();

        switch (key) {
            case "cookie":
            case "Cookie":
                request.setCookies(value);
                break;
            default:
                headers.put(key.toLowerCase(), value);
        }
    }

    protected static Request readBody(final SocketChannel channel, final Request request) {
        final String header = "Content-Length";
        if (!request.containsHeader(header)) {
            return request;
        }

        StringBuilder body = new StringBuilder();
        try {
            final ByteBuffer buffer = ByteBuffer.allocate(32);
            int bytesRead;
            final Long contentLength = Long.valueOf(request.getHeader(header).trim());
            while (body.length() < contentLength) {
                bytesRead = channel.read(buffer);

                if (bytesRead < 0) {
                    continue;
                }

                body.append(new String(buffer.array()), 0, bytesRead);
                buffer.clear();
            }
        } catch (IOException e) {
        }

        request.setBody(body.toString());

        return request;
    }
}
