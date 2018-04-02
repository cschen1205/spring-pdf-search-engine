package com.github.chen0040.pdf_search.utils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class HttpResponseHelper {


    public static void sendImageBytes(HttpServletResponse response, byte[] bytes) throws IOException {
        if (bytes != null) {
            response.setContentType("image/jpeg, image/jpg, image/png, image/gif");
            response.getOutputStream().write(bytes);
            response.getOutputStream().close();
        }
    }

    public static void sendAudioBytes(HttpServletResponse response, byte[] bytes) throws IOException {
        if (bytes != null) {
            response.setContentType("audio/wav, audio/ogg, audio/webm, audio/*");
            response.getOutputStream().write(bytes);
            response.getOutputStream().close();
        }
    }


    public static void sendBinaryData(HttpServletResponse response, byte[] bytes) throws IOException {
        if (bytes != null) {
            response.setContentType("application/octet-stream");
            response.getOutputStream().write(bytes);
            response.getOutputStream().close();
        }
    }

    public static void sendDefaultPdf(HttpServletResponse response) {
        try {
            sendBinaryFileInResource(response, "static/default.pdf");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void sendBinaryFileInResource(HttpServletResponse response, String filePath) throws IOException {
        byte[] bytes = ResourceFileUtils.getBytes(filePath);
        if (bytes != null) {
            response.setContentType("application/octet-stream");
            response.getOutputStream().write(bytes);
            response.getOutputStream().close();
        }
    }
}
