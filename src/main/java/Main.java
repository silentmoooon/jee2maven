import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.regex.Pattern;

public class Main {
    static Pattern groupIdPattern = Pattern.compile("<groupId>([\\s\\S]*?)</groupId>");
    static Pattern artifactIdPattern = Pattern.compile("<artifactId>([\\s\\S]*?)</artifactId>");
    static Pattern versionPattern = Pattern.compile("<version>([\\s\\S]*?)</version>");
    static String depsTemplate = """
            <dependency>
                <groupId>${groupId}</groupId>
                <artifactId>${artifactId}</artifactId>
                <version>${version}</version>
            </dependency>
                     """;
    static String sysDepsTemplate = """
                     <dependency>
                         <groupId>${jarName}</groupId>
                         <artifactId>${jarName}</artifactId>
                         <version>0.0.1</version>
                         <scope>system</scope>
            <systemPath>${basedir}/${path}/${jarName}.jar</systemPath>
                     </dependency>
                              """;

    public static void main(String[] args) throws IOException, URISyntaxException {


        String groupId="com.fun";
        //最后一个文件夹为项目名
        String path = "C:/Users/xiecan/dev/xingjian/Pro/management";


        String lib="src/main/webapp/WEB-INF/lib";

        String libPath = path + "/"+lib;
        String name = Paths.get(path).getFileName().toString();
        byte[] bytes = Files.readAllBytes(Paths.get(Main.class.getResource("/pom.xml").toURI()));
        String s = new String(bytes, StandardCharsets.UTF_8);
        s = s.replaceAll("\\$\\{name\\}", name);
        s = s.replaceAll("\\$\\{groupId\\}", groupId);

        StringBuilder deps = new StringBuilder();
        Files.list(Paths.get(libPath)).forEach(jar -> {
            try {
                deps.append(getDep(jar,lib)).append("\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        s = s.replace("${dependencies}", deps.toString());
        Files.writeString(Paths.get(path+"/pom.xml"), s, StandardOpenOption.CREATE,StandardOpenOption.WRITE);
        //System.out.println(s);
    }

    public static String getDep(Path path,String p) throws IOException {
        String name = path.toFile().getName();
        if(!name.endsWith(".jar"))
        {
            return "";
        }
        name = name.replace(".jar", "");

        return sysDepsTemplate.replaceAll("\\$\\{jarName\\}", name).replaceAll("\\$\\{path\\}",p);


    }
}
