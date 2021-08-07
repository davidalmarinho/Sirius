package jade.renderer;

import jade.utils.OperatingSystem;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glGetProgramInfoLog;

public class Shader {
    private String vertexShaderSource;
    private String fragmentShaderSource;
    private int shaderProgramID; // // Responsável pelo linking do vertexID e do fragmentID
    private final String filePath;

    public Shader(String filePath) {
        this.filePath = filePath;

        String content = "";
        try {
            // Criar uma String com todos os conteúdos do ficheiro
            content = new String(Files.readAllBytes(Paths.get(filePath)));
        } catch (IOException e) {
            e.printStackTrace();
            assert false : "Error: Couldn't open file for shader '" + filePath + "'.";
        }

        if (content.equals("")) {
            System.exit(-1);
        }

        String[] navigator = content.split("(#type)( )+([a-zA-Z]+)");

        // Verificar qual navigator é o vertex ou fragment
        // Primeiro pattern
        int startIndex = content.indexOf("#type") + 6;
        int endOfLineIndex = getEndLineIndex(content);

        // Para depois copiarmos letras
        char[] contentToChars = content.toCharArray();

        StringBuilder type1 = new StringBuilder();
        for (int index = startIndex; index < endOfLineIndex; index++) {
            type1.append(contentToChars[index]);
        }

        // Segundo pattern
        startIndex = content.indexOf("#type", endOfLineIndex) + 6;
        endOfLineIndex = getEndLineIndex(content) + startIndex - 6;

        StringBuilder type2 = new StringBuilder();
        for (int index = startIndex; index < endOfLineIndex; index++) {
            type2.append(contentToChars[index]);
        }

        // Colocar o conteúdo correto na fragment e na vertex String
        switch (type1.toString().trim()) {
            case "vertex":
                this.vertexShaderSource = navigator[1];
                break;
            case "fragment":
                this.fragmentShaderSource = navigator[1];
                break;
            default:
                System.out.println("Error: Couldn't recognise the types in '" + filePath + "' shader.");
                System.out.println("Instead found '" + type1.toString().trim() + "'.");
                System.exit(-1);
                break;
        }

        switch (type2.toString().trim()) {
            case "vertex":
                this.vertexShaderSource = navigator[2];
                break;
            case "fragment":
                this.fragmentShaderSource = navigator[2];
                break;
            default:
                System.out.println("Error: Couldn't recognise the types in '" + filePath + "' shader.");
                System.out.println("Instead found '" + type2.toString().trim() + "'.");
                System.exit(-1);
                break;
        }
    }

    private int getEndLineIndex(String str) {
        if (OperatingSystem.isWindows()) {
            return str.indexOf("\r\n");
        }

        return str.indexOf("\n");
    }

    public void compile() {
        int vertexID, fragmentID;

        // Compilar e ligar
        // Primeiro: carregar e compilar o vertex shader
        vertexID = glCreateShader(GL_VERTEX_SHADER);

        // Colocar o shader vertex ao nível do GPU
        glShaderSource(vertexID, vertexShaderSource);
        glCompileShader(vertexID);

        // Verificar se existiram erros na compilação dos glsl
        int success = glGetShaderi(vertexID, GL_COMPILE_STATUS); // Buscar informações à cerca da compilação do vertex shader
        if (success == GL_FALSE) {
            int len = glGetShaderi(vertexID, GL_INFO_LOG_LENGTH);
            System.out.println("Error: '" + "default.glsl" + "', couldn't be compiled.\n\tFailed vertex shader " +
                    "compilation");
            System.out.println(glGetShaderInfoLog(vertexID, len));
            assert false : "";
        }

        // Mesma coisa para o fragment shader
        fragmentID = glCreateShader(GL_FRAGMENT_SHADER);

        glShaderSource(fragmentID, fragmentShaderSource);
        glCompileShader(fragmentID);

        success = glGetShaderi(fragmentID, GL_COMPILE_STATUS);
        if (success == GL_FALSE) {
            int len = glGetShaderi(fragmentID, GL_INFO_LOG_LENGTH);
            System.out.println("Error: '" + "default.glsl" + "', couldn't be compiled.\n\tFailed " +
                    "fragment shader compilation.");
            System.out.println(glGetShaderInfoLog(fragmentID, len));
            assert false : "";
        }

        // Linking
        shaderProgramID = glCreateProgram();
        glAttachShader(shaderProgramID, vertexID);
        glAttachShader(shaderProgramID, fragmentID);
        glLinkProgram(shaderProgramID);

        success = glGetProgrami(shaderProgramID, GL_LINK_STATUS);
        if (success == GL_FALSE) {
            int len = glGetProgrami(shaderProgramID, GL_INFO_LOG_LENGTH);
            System.out.println("Error: '" + "default.glsl" + "' couldn't be linked.");
            System.out.println(glGetProgramInfoLog(shaderProgramID, len));
            assert false : "";
        }
    }

    public void use() {
        // Bind shader program
        glUseProgram(shaderProgramID);
    }

    public void detach() {
        glUseProgram(0);
    }
}
