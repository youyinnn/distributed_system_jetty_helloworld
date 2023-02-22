package org.example.controller;

import jakarta.servlet.AsyncContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.example.model.Audio;

public class ResourceServlet extends HttpServlet {

    private List<Audio> audioList = new ArrayList<>();
    private ExecutorService executorService = Executors.newFixedThreadPool(10);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Get the ID of the Audio resource from the request URL
        String[] pathParts = request.getPathInfo().split("/");
        int audioId = Integer.parseInt(pathParts[1]);

        // Get the name of the property to retrieve from the request query parameters
        String propertyName = request.getParameter("property");

        if (propertyName == null || propertyName.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "The 'property' query parameter is missing or empty.");
            return;
        }

        // Create an AsyncContext to handle the long-running operation
        AsyncContext asyncContext = request.startAsync();
        asyncContext.setTimeout(0);

        // Use an AsyncServlet to retrieve the property value
        executorService.execute(new AsyncServlet(audioList, audioId, propertyName, asyncContext));
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Read the Audio resource from the request body
        Audio audio = readAudioFromRequestBody(request);

        // Check if the Audio resource is valid
        if (audio == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "The request body is missing or contains invalid data.");
            return;
        }

        // Create a BlockingServlet to store the Audio resource
        BlockingServlet blockingServlet = new BlockingServlet();

        // Use a separate thread to avoid blocking the request thread
        executorService.execute(new BlockingServlet(audioList, audio));

        // Set the Location header in the response to the URL of the newly created resource
        response.setStatus(HttpServletResponse.SC_CREATED);
        response.setHeader("Location", "/audio/" + audioList.size());
    }

    private Audio readAudioFromRequestBody(HttpServletRequest request) throws IOException {
        String artistName = request.getParameter("artistName");
        String trackTitle = request.getParameter("trackTitle");
        String albumTitle = request.getParameter("albumTitle");
        int trackNumber = Integer.parseInt(request.getParameter("trackNumber"));
        int year = Integer.parseInt(request.getParameter("year"));
        int numReviews = Integer.parseInt(request.getParameter("numReviews"));
        int numCopiesSold = Integer.parseInt(request.getParameter("numCopiesSold"));

        if (artistName == null || artistName.isEmpty() || trackTitle == null || trackTitle.isEmpty()
                || albumTitle == null || albumTitle.isEmpty()) {
            return null;
        }

        return new Audio(artistName, trackTitle, albumTitle, trackNumber, year, numReviews, numCopiesSold);
    }
}
