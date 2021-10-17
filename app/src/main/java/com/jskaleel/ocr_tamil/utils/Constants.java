package com.jskaleel.ocr_tamil.utils;

public class Constants {

    public static final String TESS_DATA_PATH = "best/tessdata";
    public static final String TESS_DATA_NAME = "%s.traineddata";
    public static final String TESSERACT_DATA_DOWNLOAD_URL_BEST = "https://github.com/tesseract-ocr/tessdata_best/raw/4.0.0/%s.traineddata";



    /***
     *Paths to tesseract directory( only used to create to directory)
     */
    public static final String PATH_OF_TESSERACT_DATA_BEST = "/tesseract4/best/tessdata/";
    public static final String PATH_OF_TESSERACT_DATA_FAST = "/tesseract4/fast/tessdata/";
    public static final String PATH_OF_TESSERACT_DATA_STANDARD = "/tesseract4/standard/tessdata/";

    /***
     * folder path used for training Tesseract Api
     */

    public static final String TESSERACT_PATH_BEST = "/tesseract4/best/";
    public static final String TESSERACT_PATH_FAST = "/tesseract4/fast/";
    public static final String TESSERACT_PATH_STANDARD = "/tesseract4/standard/";

    /***
     *TRAINING DATA URL TEMPLATES for downloading
     */
    public static final String TESSERACT_DATA_DOWNLOAD_URL_STANDARD = "https://github.com/tesseract-ocr/tessdata/raw/4.0.0/%s.traineddata";
    public static final String TESSERACT_DATA_DOWNLOAD_URL_FAST = "https://github.com/tesseract-ocr/tessdata_fast/raw/4.0.0/%s.traineddata";

    /**
     * TRAINING DATA FILEs to save
     */
    public static final String TESSERACT_DATA_FILE_NAME_BEST = "/tesseract4/best/tessdata/%s.traineddata";
    public static final String TESSERACT_DATA_FILE_NAME_FAST = "/tesseract4/fast/tessdata/%s.traineddata";
    public static final String TESSERACT_DATA_FILE_NAME_STANDARD = "/tesseract4/standard/tessdata/%s.traineddata";


}