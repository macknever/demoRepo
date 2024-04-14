package com.lawrence.corejava.inheritance;

public class PatternMatchingExample {

    static class Chinese {
        private String language;

        public Chinese(String language) {
            this.language = language;
        }

        public void setLanguage(String language) {
            this.language = language;
        }

        public String getLanguage() {
            return this.language;
        }

        public String cook() {
            return "congee";
        }
    }

    static class NorthChinese extends Chinese {
        NorthChinese(String language) {
            super(language);
        }

        @Override
        public String cook() {
            return "baozi";
        }
    }

    static class SouthChinese extends Chinese {

        SouthChinese(String language) {
            super(language);
        }

        @Override
        public String cook() {
            return "hotpot";
        }
    }

    String showLanguage(Object o) {
        //Pattern matching is a preview feature in java 17. need to set it to 21 to run
        String language = switch (o) {
            case SouthChinese southChinese -> southChinese.getLanguage();
            case NorthChinese northChinese -> northChinese.getLanguage();
            default -> "No cook";
        };
        return language;
    }

}
