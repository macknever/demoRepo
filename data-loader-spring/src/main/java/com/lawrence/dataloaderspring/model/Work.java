package com.lawrence.dataloaderspring.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.CassandraType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

@Table(value = "work_by_id")
@Getter
@Setter
public class Work {

    @Column(value = "work_title")
    @CassandraType(type= CassandraType.Name.TEXT)
    private String title;
    @Column(value = "work_cover")
    @CassandraType(type= CassandraType.Name.TEXT)
    private String cover;
    @Id
    @PrimaryKeyColumn(value = "work_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    private String work;
    @Column(value = "author_id")
    @CassandraType(type= CassandraType.Name.TEXT)
    private String author;
}
