package com.tistory.devyongsik.analyzer;

import org.apache.lucene.util.AttributeSource;

import java.util.List;
import java.util.Map;

public interface Engine {
    void collectNounState(AttributeSource attributeSource, List<ComparableState> comparableStateList, Map<String, String> returnedTokens) throws Exception;
}
