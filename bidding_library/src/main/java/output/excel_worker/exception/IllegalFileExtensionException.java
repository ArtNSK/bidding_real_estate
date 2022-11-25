package output.excel_worker.exception;

public class IllegalFileExtensionException extends RuntimeException{
    private static final String DEFAULT_MESSAGE = "Неправильное расширение файла. Ожидается xlsx";

    public IllegalFileExtensionException(String message) {
        super(message);
    }

    public IllegalFileExtensionException(){
        super(DEFAULT_MESSAGE);
    }

}
