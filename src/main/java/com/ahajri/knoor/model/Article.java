package com.ahajri.knoor.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Article implements Serializable {
    private Long id;
    private String body;

    public String getBody() {
        return body;
    }

    @Override
    public String toString() {
        return "Article{" +
                "id=" + id +
                '}';
    }
}
