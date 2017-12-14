package br.ufpe.cin.if710.podcast.ui;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by jpvic on 13/12/2017.
 */
public class EpisodeDetailActivityTest {
    @Test
    public void convertPubDate() throws Exception {
        String input = "Mon, 20 Nov 2017 12:30:46";
        String expected = "Mon Nov 20 09:30:46 GFT 2017";
        EpisodeDetailActivity test = new EpisodeDetailActivity();
        String output = test.convertPubDate(input);
        assertEquals(expected, output);
    }

}