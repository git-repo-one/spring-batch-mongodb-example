package com.example.batch.infra.reader;

import java.util.stream.Stream;

public interface FetchCursorStreamForReader<T> {

	Stream<T> fetch();
}
