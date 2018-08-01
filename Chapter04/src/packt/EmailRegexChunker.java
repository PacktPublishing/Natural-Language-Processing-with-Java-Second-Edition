package packt;

import com.aliasi.chunk.RegExChunker;

public class EmailRegexChunker extends RegExChunker {
    // From: http://alias-i.com/lingpipe/demos/tutorial/ne/read-me.html
    public EmailRegexChunker() {
        super(EMAIL_REGEX,CHUNK_TYPE,CHUNK_SCORE);
    }

    private final static String EMAIL_REGEX
        = "[A-Za-z0-9](([_\\.\\-]?[a-zA-Z0-9]+)*)@([A-Za-z0-9]+)(([\\.\\-]?[a-zA-Z0-9]+)*)\\.([A-Za-z]{2,})";

    private final static String CHUNK_TYPE = "email";

    private final static double CHUNK_SCORE = 0.0;

}
