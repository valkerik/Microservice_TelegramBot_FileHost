package ru.valkerik.exceptions;

public class UploadFileException extends RuntimeException{
    public UploadFileException(String message, Throwable e){
        super(message, e);
    }

    public UploadFileException(String message){
        super(message);
    }

    public UploadFileException(Throwable e){
        super(e);
    }
}
