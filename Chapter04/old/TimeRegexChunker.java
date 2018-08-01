package packt;

import com.aliasi.chunk.RegExChunker;

public class TimeRegexChunker extends RegExChunker {
    private final static String TIME_RE = 
        "(([0-1]?[0-9])|([2][0-3])):([0-5]?[0-9])(:([0-5]?[0-9]))?";
    private final static String CHUNK_TYPE = "time";
    private final static double CHUNK_SCORE = 1.0;
    
    public TimeRegexChunker() {
        super(TIME_RE,CHUNK_TYPE,CHUNK_SCORE);
    }
}
