import java.nio.file.*;
import java.sql.*;
import javax.xml.parsers.*;

/** Creates/removes only an explicitly named, isolated smoke-test schema. Never uses public. */
public class PrepareWebTestDatabase {
    public static void main(String[] args) throws Exception {
        String schema=System.getenv("DB_TEST_SCHEMA");
        if(schema==null || !schema.matches("hk_web_check_[a-z0-9_]+")) throw new IllegalArgumentException("Set DB_TEST_SCHEMA=hk_web_check_<unique suffix>");
        var document=DocumentBuilderFactory.newInstance().newDocumentBuilder().parse("src/main/resources/META-INF/persistence.xml");
        String url="",user="";
        var properties=document.getElementsByTagName("property");
        for(int i=0;i<properties.getLength();i++) {
            var e=(org.w3c.dom.Element)properties.item(i);
            if(e.getAttribute("name").equals("jakarta.persistence.jdbc.url")) url=e.getAttribute("value");
            if(e.getAttribute("name").equals("jakarta.persistence.jdbc.user")) user=e.getAttribute("value");
        }
        if(System.getenv("DB_URL")!=null) url=System.getenv("DB_URL");
        if(System.getenv("DB_USER")!=null) user=System.getenv("DB_USER");
        try(var c=DriverManager.getConnection(url,user,System.getenv("DB_PASSWORD"));var statement=c.createStatement()) {
            c.setAutoCommit(false);
            try {
                if(args.length==1 && args[0].equals("drop")) {
                    statement.execute("DROP SCHEMA "+schema+" CASCADE");
                } else {
                    statement.execute("CREATE SCHEMA "+schema);
                    statement.execute("SET LOCAL search_path TO "+schema);
                    for(String file:new String[]{"02_create_tables.sql","03_insert_sample_data.sql","04_update_monan_images.sql"})
                        statement.execute(Files.readString(Path.of("database/postgresql",file)));
                }
                c.commit();System.out.println("Test schema ready: "+schema);
            } catch(Exception e) {c.rollback();throw e;}
        }
    }
}
