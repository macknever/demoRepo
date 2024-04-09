package com.lawrence;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.CqlSessionBuilder;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import com.google.inject.Inject;
import com.google.inject.Key;
import com.google.inject.name.Names;
import com.lawrence.cassandra.Author;
import com.lawrence.cassandra.AuthorRepository;
import com.lawrence.guice.injector.MainInjector;
import com.typesafe.config.ConfigException;
import org.checkerframework.checker.units.qual.A;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.stream.Stream;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.*;

import static com.lawrence.cassandra.Author.*;


public class Main {
    private static String AUTHOR_PATH_PROP = "author.path";
    private static String WORK_PATH_PROP = "work.path";

    public static void main(String[] args) {

        final AuthorRepository repository = MainInjector.getInstance().getInstance(AuthorRepository.class);
        final String authorDataPath = MainInjector.getInstance()
                .getInstance(Key.get(String.class, Names.named(AUTHOR_PATH_PROP)));
        final String workDataPath = MainInjector.getInstance()
                .getInstance(Key.get(String.class, Names.named(WORK_PATH_PROP)));

        String authorFullPath = Objects.requireNonNull(Main.class.getClassLoader()
                .getResource(authorDataPath)).getPath();








    }

    private static void loadAuthor(final String dataPath, final AuthorRepository repository) {
        String authorFullPath = Objects.requireNonNull(Main.class.getClassLoader()
                .getResource(dataPath)).getPath();
        Path authorDataPath = Paths.get(authorFullPath);

        try(Stream<String> lines = Files.lines(authorDataPath)) {
            lines.parallel().map(line -> line.substring(line.indexOf("{"))).map(jsonString -> {
                try {
                    JSONObject jsonAuthor = new JSONObject(jsonString);
                    return new Author(jsonAuthor.optString("key").replace("/authors/",""),
                            jsonAuthor.optString("name"), jsonAuthor.optString("personal_name"));
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }).forEach(repository::insert);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}