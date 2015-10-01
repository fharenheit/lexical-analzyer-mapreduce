package com.tistory.devyongsik.analyzer.dictionary;

import com.tistory.devyongsik.analyzer.DictionaryProperties;
import com.tistory.devyongsik.analyzer.dictionaryindex.SynonymDictionaryIndex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

public class DictionaryFactory {
    private static DictionaryFactory factory = new DictionaryFactory();
    private Logger logger = LoggerFactory.getLogger(DictionaryFactory.class);
    private Map<String, List<String>> compoundDictionaryMap = new HashMap<String, List<String>>();
    private Map<String, String> customNounDictionaryMap = new HashMap<String, String>();
    private Map<String, String> stopWordDictionaryMap = new HashMap<String, String>();
    private List<String> synonymList = new ArrayList<String>();

    private Map<DictionaryType, List<String>> dictionaryMap = new HashMap<DictionaryType, List<String>>();

    private DictionaryFactory() {
        initDictionary();
    }

    public static DictionaryFactory getFactory() {
        return factory;
    }

    private void initDictionary() {
        DictionaryLoader dictionaryLoader = new DictionaryLoader();
        dictionaryLoader.loadDictionaries();
    }

    public List<String> get(DictionaryType dictionaryType) {
        return dictionaryMap.get(dictionaryType);
    }

    public List<String> getSynonymList() {
        return synonymList;
    }

    public void setSynonymList(List<String> synonymList) {
        this.synonymList = synonymList;
    }

    public Map<String, List<String>> getCompoundDictionaryMap() {
        return compoundDictionaryMap;
    }

    public void setCompoundDictionaryMap(
            Map<String, List<String>> compoundDictionaryMap) {
        this.compoundDictionaryMap = compoundDictionaryMap;
    }

    public Map<String, String> getCustomNounDictionaryMap() {
        return customNounDictionaryMap;
    }

    public void setCustomNounDictionaryMap(
            Map<String, String> customNounDictionaryMap) {
        this.customNounDictionaryMap = customNounDictionaryMap;
    }

    public Map<String, String> getStopWordDictionaryMap() {
        return stopWordDictionaryMap;
    }

    public void setStopWordDictionaryMap(Map<String, String> stopWordDictionaryMap) {
        this.stopWordDictionaryMap = stopWordDictionaryMap;
    }

    public void rebuildDictionary(DictionaryType dictionaryType) {

        if (DictionaryType.CUSTOM == dictionaryType) {
            List<String> customNouns = dictionaryMap.get(DictionaryType.CUSTOM);
            customNounDictionaryMap.clear();
            for (String noun : customNouns) {
                customNounDictionaryMap.put(noun, null);
            }

            return;
        }

        if (DictionaryType.COMPOUND == dictionaryType) {
            List<String> customNouns = dictionaryMap.get(DictionaryType.CUSTOM);
            customNounDictionaryMap.clear();
            for (String noun : customNouns) {
                customNounDictionaryMap.put(noun, null);
            }
        }

        if (DictionaryType.STOP == dictionaryType) {
            List<String> stopWords = dictionaryMap.get(DictionaryType.STOP);
            stopWordDictionaryMap.clear();
            for (String stopWord : stopWords) {
                stopWordDictionaryMap.put(stopWord, null);
            }
        }

        if (DictionaryType.SYNONYM == dictionaryType) {
            List<String> synonymWords = dictionaryMap.get(DictionaryType.SYNONYM);
            SynonymDictionaryIndex indexModule = SynonymDictionaryIndex.getIndexingModule();
            indexModule.indexingDictionary(synonymWords);
        }
    }

    class DictionaryLoader {

        public void loadDictionaries() {
            DictionaryType[] dictionaryTypes = DictionaryType.values();

            for (DictionaryType dictionaryType : dictionaryTypes) {
                if (logger.isInfoEnabled()) {
                    logger.info("[" + dictionaryType.getDescription() + "] " + "create wordset from file");
                }

                List<String> dictionary = loadDictionary(dictionaryType);
                dictionaryMap.put(dictionaryType, dictionary);
            }

            List<String> dictionaryData = dictionaryMap.get(DictionaryType.COMPOUND);
            String[] extractKey = null;
            String key = null;
            String[] nouns = null;

            for (String data : dictionaryData) {
                extractKey = data.split(":");
                key = extractKey[0];
                nouns = extractKey[1].split(",");

                compoundDictionaryMap.put(key, Arrays.asList(nouns));
            }

            List<String> customNouns = dictionaryMap.get(DictionaryType.CUSTOM);
            for (String noun : customNouns) {
                customNounDictionaryMap.put(noun, null);
            }

            synonymList = dictionaryMap.get(DictionaryType.SYNONYM);

            List<String> stopWords = dictionaryMap.get(DictionaryType.STOP);
            for (String stopWord : stopWords) {
                stopWordDictionaryMap.put(stopWord, null);
            }
        }

        private List<String> loadDictionary(DictionaryType name) {

            BufferedReader in = null;
            String dictionaryFile = DictionaryProperties.getInstance().getProperty(name.getPropertiesKey());
            InputStream inputStream = DictionaryFactory.class.getClassLoader().getResourceAsStream(dictionaryFile);

            if (inputStream == null) {
                logger.info("couldn't find dictionary : " + dictionaryFile);

                inputStream = DictionaryFactory.class.getResourceAsStream(dictionaryFile);

                logger.info(dictionaryFile + " file loaded.. from classloader.");
            }

            List<String> words = new ArrayList<String>();

            try {
                String readWord = "";
                in = new BufferedReader(new InputStreamReader(inputStream, "utf-8"));


                while ((readWord = in.readLine()) != null) {
                    words.add(readWord.trim());
                }

                if (logger.isInfoEnabled()) {
                    logger.info(name.getDescription() + " : " + words.size());
                }

                if (logger.isInfoEnabled()) {
                    logger.info("create wordset from file complete");
                }

            } catch (IOException e) {
                logger.error(e.toString());
            } finally {
                try {
                    in.close();
                } catch (IOException e) {
                    logger.error(e.toString());
                }
            }

            return words;
        }
    }
}
