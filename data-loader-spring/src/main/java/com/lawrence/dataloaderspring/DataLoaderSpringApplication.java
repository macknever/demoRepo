package com.lawrence.dataloaderspring;

import com.lawrence.dataloaderspring.model.Author;
import com.lawrence.dataloaderspring.model.AuthorRepository;
import com.lawrence.dataloaderspring.model.Work;
import com.lawrence.dataloaderspring.model.WorkRepository;
import jakarta.annotation.PostConstruct;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

@SpringBootApplication
public class DataLoaderSpringApplication {

    private final static Logger LOG = LoggerFactory.getLogger(DataLoaderSpringApplication.class);

    @Autowired
    AuthorRepository authorRepository;

    @Autowired
    WorkRepository workRepository;

    @Value("${datastax.astra.author-data-path}")
    String authorPath;

    @Value("${datastax.astra.work-data-path}")
    String workPath;

    @Value("${datastax.astra.partial-work-path}")
    String partialPath;

    public static void main(String[] args) {
        SpringApplication.run(DataLoaderSpringApplication.class, args);
    }

    @PostConstruct
    public void save() {
        initAuthor();
    }

    private void initAuthor() {
        Path dataPath = Paths.get(authorPath);
        String fullPath = getClass().getClassLoader().getResource(partialPath).getPath();
        LOG.info("fullPath: {}", fullPath);
        try (Stream<String> data = Files.lines(dataPath)){
            BufferedWriter writer = new BufferedWriter(new FileWriter(fullPath, true));
            data.limit(10000).forEach(line -> {
                try {
                    writer.write(line + "\n");
                    LOG.info("saving to partial work: {}", line.substring(0,10));

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            writer.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
//        try (Stream<String> authorData = Files.lines(dataPath)) {
//            authorData.limit(10).forEach(lines -> {
//                String authorStr = lines.substring(lines.indexOf("{"));
//                try {
//                    JSONObject jsonObject = new JSONObject(authorStr);
//                    Author author = new Author();
//                    author.setName(jsonObject.optString("name"));
//                    author.setPersonalName(jsonObject.optString("personalName"));
//                    author.setId(jsonObject.optString("key").replace("/authors/", ""));
//                    LOG.info("Saving author: {}", author.getName());
//                    authorRepository.save(author);
//
//                } catch (JSONException e) {
//                    throw new RuntimeException(e);
//                }
//            });
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    private void initWork() {
        Path wPath = Paths.get(workPath);


        try (Stream<String> workData = Files.lines(wPath)) {
            workData.limit(1000).forEach(line -> {
                String jsonString = line.substring(line.indexOf("{"));
                JSONObject workJson = new JSONObject(jsonString);
                Work work = new Work();
                work.setWork(workJson.optString("key").replace("/works/", ""));
                work.setCover(workJson.optString("covers"));
                work.setAuthor(workJson.optJSONArray("authors").optJSONObject(0)
                        .optJSONObject("author")
                        .optString("key").replace("/authors/", ""));
                work.setTitle(workJson.optString("title"));
                LOG.info("saving work: {}", work.getTitle());
                workRepository.save(work);

            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
