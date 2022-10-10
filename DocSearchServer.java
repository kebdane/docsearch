import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

class FileHelpers {
    static List<File> getFiles(Path start) throws IOException {
        File f = start.toFile();
        List<File> result = new ArrayList<>();
        result.add(start.toFile());
        if(f.isDirectory()) {
            File[] paths = f.listFiles();
            for(File subFile: paths) {
                result.addAll(getFiles(subFile.toPath()));
            }
        }
        return result;
    }
    static String readFile(File f) throws IOException {
        return new String(Files.readAllBytes(f.toPath()));
    }
}

class Handler implements URLHandler {

    // The one bit of state on the server: a number that will be manipulated by
    // various requests.
    int num = 0;

    public String handleRequest(URI url) throws IOException {
        long start = System.currentTimeMillis();
        List<File> paths = FileHelpers.getFiles(Paths.get("./technical"));
        if (url.getPath().equals("/")) {
            return String.format("There are %d total files to search.", paths.size());
        } else if (url.getPath().equals("/search")) {
            String[] parameters = url.getQuery().split("=");
            if (parameters[0].equals("q")) {
                String result = "";
                List<String> foundPaths = new ArrayList<>();
                for(File f: paths) {
                    if(FileHelpers.readFile(f).contains(parameters[1])) {
                        foundPaths.add(f.toString());
                    }
                }
                result = String.join("\n", foundPaths);
                System.out.println(System.currentTimeMillis() - start);
                return String.format("Found %d paths:\n%s", foundPaths.size(), result);
            }
            else {
                return "Couldn't find query parameter q";
            }
        }
        else {
            return "Don't know how to handle that path!";
        }
    }
}

class DocSearchServer {
    public static void main(String[] args) throws IOException {
        if(args.length == 0){
            System.out.println("Missing port number! Try any number between 1024 to 49151");
            return;
        }

        int port = Integer.parseInt(args[0]);

        Server.start(port, new Handler());
    }
}
