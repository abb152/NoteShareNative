package com.tilak.noteshare;

import com.tilak.db.Config;

import java.util.Random;

/**
 * Created by Jay on 20-11-2015.
 */
public class FileNameGenerator {

    public String getFileName(String fileType){
        String type = null, format = null;

        switch (fileType) {
            case "IMAGE":
                type = "IMG_";
                format = ".jpg";
                break;

            case "AUDIO":
                type = "AUD_";
                format = ".m4a";
                break;

            case "SCRIBBLE":
                type = "SCR_";
                format = ".png";
                break;
        }

        Config config = Config.findById(Config.class,1l);
        String userId = config.getServerid()+"_";

        String timestamp = String.valueOf(System.currentTimeMillis())+"_";

        Random randomGenerator = new Random();
        String randomNumber = String.valueOf(randomGenerator.nextInt(10000));

        String fileName = type+ userId+ timestamp+ randomNumber+ format;

        return fileName;
    }
}
