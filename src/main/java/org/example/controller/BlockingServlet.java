package org.example.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.example.model.Audio;

public class BlockingServlet extends HttpServlet {
    
    private List<Audio> audioList;
    private Audio audio;

    public BlockingServlet(List<Audio> audioList, Audio audio) {
        this.audioList = audioList;
        this.audio = audio;
    }

    public BlockingServlet() {
    }

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        simulateLongRunningTask();
        
        Audio audio = new Audio(
            "Artist Name",
            "Track Title",
            "Album Title",
            1,
            2022,
            0, 
            0);
        
        response.setContentType("application/json");
        try (PrintWriter out = response.getWriter()) {
            out.print("{");
            out.print("\"artistName\":\"" + audio.getArtistName() + "\",");
            out.print("\"trackTitle\":\"" + audio.getTrackTitle() + "\",");
            out.print("\"albumTitle\":\"" + audio.getAlbumTitle() + "\",");
            out.print("\"trackNumber\":" + audio.getTrackNumber() + ",");
            out.print("\"year\":" + audio.getYear() + ",");
            out.print("\"numReviews\":" + audio.getNumReviews() + ",");
            out.print("\"numCopiesSold\":" + audio.getNumCopiesSold());
            out.print("}");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void simulateLongRunningTask() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
