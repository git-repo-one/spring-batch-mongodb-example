package com.example.batch.infra.reader;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.batch.item.data.AbstractPaginatedDataItemReader;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

public class MongoDbCursorItemReader<T> extends AbstractPaginatedDataItemReader<T> implements InitializingBean {

	private Stream<T> cursorStream;
	private Iterator<T> cursorIterator;

	private FetchCursorStreamForReader<T> fetchCursorStreamForReader;

	public MongoDbCursorItemReader() {
		super();
		setName(ClassUtils.getShortName(MongoDbCursorItemReader.class));
	}

	public void setFetchCursorStreamForReader(FetchCursorStreamForReader<T> fetchCursorStreamForReader) {
		this.fetchCursorStreamForReader = fetchCursorStreamForReader;
	}

	@Override
	protected void doOpen() throws Exception {

		cursorStream = fetchCursorStreamForReader.fetch();
		cursorIterator = cursorStream.iterator();
	}

	@Override
	protected void doClose() throws Exception {
		cursorStream.close();
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.notNull(fetchCursorStreamForReader,"fetchCursorStreamForReader must be specified");
	}

	@Override
	protected Iterator<T> doPageRead() {

		List<T> pageOfItems = new ArrayList<>();

		int i = 0;

		while (cursorIterator.hasNext()) {

			++i;

			T t = cursorIterator.next();

			pageOfItems.add(t);

			if (i == pageSize) {

				return pageOfItems.iterator();
			}
		}

		return pageOfItems.iterator();
	}
}
