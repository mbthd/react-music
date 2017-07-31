package App.configuration;

import App.controllers.AlbumController;
import App.domain.Albums;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
//@Profile("in-memory")
@Profile("!mysqlcloud")
public class InMemoryConfig implements DatasourceConfig {

    //@Bean
    @Override
    public DataSource dataSource() {
        ClassPathResource filename = new ClassPathResource("AlbumList.csv");
        String cvsSplitBy = ",";
        String data;
        try {
            byte[] albumData = FileCopyUtils.copyToByteArray(filename.getInputStream());
            data = new String(albumData, StandardCharsets.UTF_8);
            String[] rows = data.split("\r\n");
            int counter = 0;
            for(String a: rows) {
                if (counter != 0) {
                    String[] albums = a.split(cvsSplitBy);
                    Albums albumInstance = new Albums(albums[0], albums[1], albums[2], albums[3]);
                    AlbumController.repository.save(albumInstance);
                }
                counter++;
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("in-mem setting up");
        EmbeddedDatabaseBuilder builder = new EmbeddedDatabaseBuilder();
        return builder.setType(EmbeddedDatabaseType.H2).build();

        //return "Setting up in-memory datasource...";
    }

}


