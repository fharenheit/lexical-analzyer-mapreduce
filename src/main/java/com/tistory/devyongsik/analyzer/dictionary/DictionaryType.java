package com.tistory.devyongsik.analyzer.dictionary;

public enum DictionaryType {
    COMPOUND("복합명사") {
        @Override
        public String getPropertiesKey() {
            return "compounds.txt";
        }
    }, CUSTOM("사용자정의") {
        @Override
        public String getPropertiesKey() {
            return "custom.txt";
        }
    }, SYNONYM("동의어") {
        @Override
        public String getPropertiesKey() {
            return "synonym.txt";
        }
    }, STOP("불용어") {
        @Override
        public String getPropertiesKey() {
            return "stop.txt";
        }
    };

    private String description;

    DictionaryType(String desc) {
        this.description = desc;
    }

    public String getDescription() {
        return description;
    }

    public abstract String getPropertiesKey();
}
