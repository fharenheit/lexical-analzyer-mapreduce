package com.yourcompany.hadoop.mapreduce.lexical;

import com.google.common.collect.Lists;
import com.tistory.devyongsik.analyzer.KoreanAnalyzer;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

public class LexicalAnalyzer {

    public static void main(String[] args) throws IOException {
        Analyzer analyzer = getKoreanAnalyzer();
        TokenStream tokenStream = analyzer.tokenStream("dummy", new StringReader("2015-03-01 경 신고인께서 유기농마루 쇼핑몰서 유기농마루 양배추환을 구매. 배우자 분이 하루 2,3회 이십알정도 취식함. 그리고 간수치가 올라가서 병원에 입원함. 해당병원에서는 치료가 안되니 더 큰병원으로 가서 검사를 받아보라고 했다고 함. 100%양배추만 들어갔다고 하는데 다른 첨가물이 들어간 것 같다고 함. 담당 전공의사 역시 기타가공품이기 때문에 양배추 외에 기타 첨가물이 들어간 것 같다는 얘기를 했다고 함. 해당제품은 보관중임."));
        tokenStream.reset();

        List<String> tokens = collectExtractedNouns(tokenStream);
        for (String next : tokens) {
            System.out.println(next);
        }
    }

    private static Analyzer getKoreanAnalyzer() {
        KoreanAnalyzer analyzer = new KoreanAnalyzer(false);
        return analyzer;
    }

    protected static List<String> collectExtractedNouns(TokenStream stream) throws IOException {
        CharTermAttribute charTermAtt = stream.addAttribute(CharTermAttribute.class);
        OffsetAttribute offSetAtt = stream.addAttribute(OffsetAttribute.class);
        TypeAttribute typeAttr = stream.addAttribute(TypeAttribute.class);
        List<String> extractedTokens = Lists.newArrayList();
        while (stream.incrementToken()) {
            extractedTokens.add(charTermAtt.toString());
        }
        return extractedTokens;
    }
}
