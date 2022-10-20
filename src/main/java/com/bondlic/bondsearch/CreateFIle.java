package com.bondlic.bondsearch;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class CreateFIle {

    public static void main(String[] args) throws IOException {
        final int mbSize = 20;
        final String fileFormat = ".pdf";
        File file = new File("./target/foo" + mbSize + fileFormat);
        RandomAccessFile f = new RandomAccessFile(file, "rw");
        f.setLength(mbSize * 1000 * 1000 + 300000);

    }
}
