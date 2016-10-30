package com.library.app.commontests.util;

import org.junit.Ignore;

@Ignore
public interface DBCommand<T> {
	T execute();
}
