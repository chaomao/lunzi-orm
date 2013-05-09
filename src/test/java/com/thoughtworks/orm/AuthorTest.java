package com.thoughtworks.orm;

import com.google.common.collect.Lists;
import com.thoughtworks.orm.finder.ModelFinder;
import com.thoughtworks.orm.model.Author;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class AuthorTest extends DBTest {

    @Test
    public void should_save_author_into_db() {
        Author author = new Author("Mao Chao");
        author.save();

        Author result = ModelFinder.findById(Author.class, author.getId());

        assertThat(result, is(author));
        assertThat(result.getId(), is(author.getId()));
    }

    @Test
    public void should_return_all_authors() {
        List<Author> arrayList = Lists.newArrayList(new Author("1"), new Author("2"));
        for (Author author : arrayList) {
            author.save();
        }

        List<Author> all = ModelFinder.findAll(Author.class);
        assertThat(all, is(arrayList));
    }
}
