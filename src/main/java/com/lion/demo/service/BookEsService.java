package com.lion.demo.service;

import com.lion.demo.entity.Book;
import com.lion.demo.entity.BookEs;
import com.lion.demo.entity.BookEsDto;
import com.lion.demo.repository.BookEsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.core.query.StringQuery;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookEsService {
    public static final int PAGE_SIZE = 10;
    @Autowired private BookEsRepository bookEsRepository;
    @Autowired private ElasticsearchTemplate elasticsearchTemplate;

    public BookEs findById(String bookId) {
        return bookEsRepository.findById(bookId).orElse(null);
    }

    // 자연어 처리?
    public Page<BookEsDto> getPagedBooks(int page, String field, String keyword) {
        Pageable pageable = PageRequest.of(page-1, PAGE_SIZE);

        Query query = NativeQuery.builder()
                .withQuery(buildMatchQuery(field,keyword))
                .withPageable(pageable)
                .build();

        SearchHits<BookEs> searchHits = elasticsearchTemplate.search(query, BookEs.class);
        List<BookEsDto> bookEsDtoList = searchHits
                .getSearchHits()
                .stream().map(hit -> new BookEsDto(hit.getContent(), hit.getScore()))
                .toList();

        // total hit count
        long totalHits = searchHits.getTotalHits();
        return new PageImpl<>(bookEsDtoList, pageable, totalHits);
    }

    public void insertBook(BookEs bookEs) {
        bookEsRepository.save(bookEs);
    }

    private Query buildMatchQuery(String field, String keyword) {

        if (keyword.isEmpty()) {
            return new StringQuery("{\"match_all\": {}}");
        }

        String queryString = String.format(
//                "{\"match\":{\"%s\": \"%s\"}}", // 이렇게 해도 되긴함, 아래꺼로 하면 오타알아서 감안해서 인식함
                "{\"match\": {\"%s\": {\"query\": \"%s\", \"fuzziness\": \"AUTO\"}}}",
                field, keyword);
        return new StringQuery(queryString);
    }

}
