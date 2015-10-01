/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yourcompany.hadoop.mapreduce.lexical;

import com.google.common.collect.Lists;
import com.tistory.devyongsik.analyzer.KoreanAnalyzer;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

/**
 * Lexical Analyzer Mapper
 *
 * @author Edward KIM
 * @version 0.1
 */
public class LexicalAnalyzerMapper extends Mapper<LongWritable, Text, NullWritable, Text> {

    private boolean isIndexMode;

    private Analyzer analyzer;

    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
        Configuration configuration = context.getConfiguration();
        this.isIndexMode = configuration.getBoolean("indexmode", false);
        this.analyzer = new KoreanAnalyzer(this.isIndexMode);
    }

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        String row = value.toString();
        TokenStream tokenStream = analyzer.tokenStream("dummy", new StringReader(row));
        tokenStream.reset();
        List<String> tokens = collectExtractedNouns(tokenStream);
        for (String token : tokens) {
            context.write(NullWritable.get(), new Text(token));
        }
    }

    @Override
    protected void cleanup(Context context) throws IOException, InterruptedException {
    }

    protected static List<String> collectExtractedNouns(TokenStream stream) throws IOException {
        CharTermAttribute charTermAtt = stream.addAttribute(CharTermAttribute.class);
        List<String> extractedTokens = Lists.newArrayList();
        while (stream.incrementToken()) {
            extractedTokens.add(charTermAtt.toString());
        }
        return extractedTokens;
    }
}
