package pl.czyz;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

public final class AttributesAndUtils {
    private static AttributesAndUtils instance = new AttributesAndUtils();
    private boolean less;
    private boolean destinationFlag = false;
    private boolean copyFlag;
    private String destination="";
    private String board;
    private String threadnumber;


    private AttributesAndUtils(){

    }

    static AttributesAndUtils getInstance() {
        return instance;
    }

    void setFlags(boolean less, boolean copyFlag){
        this.less = less;
        this.copyFlag=copyFlag;
    }

    final boolean isSilent() {
        return less;
    }

    final boolean isDestinationFlag() {
        return destinationFlag;
    }

    final boolean isCopyFlag() {
        return copyFlag;
    }

    final String getDestination() {
        return destination;
    }

    final String getBoard() {
        return board;
    }

    final String getThreadnumber() {
        return threadnumber;
    }

    final void setDestination(String destination) {
        this.destination = destination;
        this.destinationFlag=true;
    }

    final void setBoard(String board) {
        this.board = board;
    }

    final void setThreadnumber(String threadnumber) {
        this.threadnumber = threadnumber;
    }

    final String readFile(String path, Charset encoding) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }
}
