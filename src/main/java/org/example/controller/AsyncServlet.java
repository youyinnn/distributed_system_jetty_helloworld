package org.example.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.example.model.Audio;

import jakarta.servlet.AsyncContext;
import jakarta.servlet.annotation.WebServlet;

import javax.servlet.ServletRequest;


@WebServlet(urlPatterns = "/async", asyncSupported = true)
public class AsyncServlet extends HttpServlet implements Runnable {

    private static final long serialVersionUID = 1L;


    public AsyncServlet(List<Audio> audioList, int audioId, String propertyName, AsyncContext asyncContext) {
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // start the async context
        final AsyncContext asyncContext = request.startAsync();

        // simulate a long-running task
        CompletableFuture<Audio> audioFuture = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return new Audio(
                "Artist Name",
                "Track Title",
                "Album Title",
                1,
                2022,
                0,
                0);
        });

        // when the task is complete, write the response
        audioFuture.thenAccept(audio -> {
            try {
                PrintWriter out = asyncContext.getResponse().getWriter();
                out.println("Async Servlet Response:");
                out.println("Artist Name: " + audio.getArtistName());
                out.println("Track Title: " + audio.getTrackTitle());
                out.println("Album Title: " + audio.getAlbumTitle());
                out.println("Track Number: " + audio.getTrackNumber());
                out.println("Year: " + audio.getYear());
                out.println("Number of Reviews: " + audio.getNumReviews());
                out.println("Number of Copies Sold: " + audio.getNumCopiesSold());
                asyncContext.complete();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

}
