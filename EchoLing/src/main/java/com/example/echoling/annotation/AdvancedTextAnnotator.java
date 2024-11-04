package com.example.echoling.annotation;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import org.apache.xerces.parsers.SAXParser;
import org.springframework.stereotype.Component;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

import com.atilika.kuromoji.ipadic.Token;
import com.atilika.kuromoji.ipadic.Tokenizer;


@Component
public class AdvancedTextAnnotator {

    private Map<String, String> wordReadingMap;
    private static final Tokenizer tokenizer = new Tokenizer();

    public AdvancedTextAnnotator() {
        wordReadingMap = new HashMap<>();
        try {
            File jmdictFile = new File("D:\\Prog\\rust\\EchoLing\\src\\JMdict.xml");
            parseJMDict(jmdictFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void parseJMDict(File jmdictFile) {
        try {
            SAXParser saxParser = new SAXParser();

            saxParser.setFeature("http://apache.org/xml/features/disallow-doctype-decl", false);
            saxParser.setFeature("http://xml.org/sax/features/external-general-entities", false);
            saxParser.setFeature("http://xml.org/sax/features/external-parameter-entities", false);

            saxParser.setContentHandler(new DefaultHandler() {
                private String currentElement = "";
                private String currentKanji = "";
                private List<String> currentReadings = new ArrayList<>();
                private boolean inKEle = false;
                private boolean inREle = false;

                @Override
                public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
                    currentElement = qName;
                    if (qName.equals("k_ele")) {
                        inKEle = true;
                        currentKanji = "";
                    } else if (qName.equals("r_ele")) {
                        inREle = true;
                        currentReadings = new ArrayList<>();
                    }
                }

                @Override
                public void characters(char[] ch, int start, int length) throws SAXException {
                    String content = new String(ch, start, length).trim();
                    if (inKEle && currentElement.equals("keb")) {
                        currentKanji += content;
                    } else if (inREle && currentElement.equals("reb")) {
                        if (!content.isEmpty()) {
                            currentReadings.add(content);
                        }
                    }
                }

                @Override
                public void endElement(String uri, String localName, String qName) throws SAXException {
                    if (qName.equals("k_ele")) {
                        inKEle = false;
                    } else if (qName.equals("r_ele")) {
                        inREle = false;
                    } else if (qName.equals("entry")) {
                        if (!currentKanji.isEmpty() && !currentReadings.isEmpty()) {
                            // For word-level mapping, we'll map the word to its readings
                            for (String reading : currentReadings) {
                                wordReadingMap.putIfAbsent(currentKanji, reading);
                            }
                        }
                        currentKanji = "";
                        currentReadings = new ArrayList<>();
                    }
                    currentElement = "";
                }
            });

            InputSource inputSource = new InputSource(new FileInputStream(jmdictFile));
            saxParser.parse(inputSource);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String annotateJapanese(String text) {
        StringBuilder annotatedText = new StringBuilder();
        List<Token> tokens = tokenizeJapaneseText(text);

        for (Token token : tokens) {
            String word = token.getSurface();
            if (containsKanji(word)) {
                String reading = getReadingForWord(token);

                if (reading != null && !reading.isEmpty()) {
                    annotatedText.append("<ruby>").append(word)
                            .append("<rt>").append(reading)
                            .append("</rt></ruby>");
                } else {
                    annotatedText.append(word);
                }
            } else {
                annotatedText.append(word);
            }
        }

        return annotatedText.toString();
    }

    private List<Token> tokenizeJapaneseText(String text) {
        return tokenizer.tokenize(text);
    }

    private String getReadingForWord(Token token) {
        String word = token.getSurface();

        String reading = wordReadingMap.get(word);
        if (reading != null) {
            return reading;
        }

        String katakanaReading = token.getReading();
        if (katakanaReading != null && !katakanaReading.equals("*")) {
            return katakanaToHiragana(katakanaReading);
        }

        return null;
    }

    private String katakanaToHiragana(String katakana) {
        StringBuilder hiragana = new StringBuilder();
        for (char c : katakana.toCharArray()) {
            if (c >= 'ァ' && c <= 'ン') {
                hiragana.append((char) (c - 'ァ' + 'ぁ'));
            } else if (c == 'ヴ') {
                hiragana.append('ゔ');
            } else {
                hiragana.append(c);
            }
        }
        return hiragana.toString();
    }

    private boolean containsKanji(String text) {
        for (char c : text.toCharArray()) {
            if ((c >= '\u4E00' && c <= '\u9FFF') || (c >= '\u3400' && c <= '\u4DBF')) {
                return true;
            }
        }
        return false;
    }
    private static HanyuPinyinOutputFormat getDefaultPinyinFormat() {
        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
        format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        format.setToneType(HanyuPinyinToneType.WITH_TONE_MARK);
        format.setVCharType(HanyuPinyinVCharType.WITH_U_UNICODE);
        return format;
    }

    public String annotateChinese(String text) {
        StringBuilder annotated = new StringBuilder();
        HanyuPinyinOutputFormat format = getDefaultPinyinFormat();

        for (char ch : text.toCharArray()) {
            if (isChineseCharacter(ch)) {
                try {
                    String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(ch, format);
                    if (pinyinArray != null && pinyinArray.length > 0) {
                        String pinyin = pinyinArray[0];
                        annotated.append("<ruby>").append(ch).append("<rt>").append(pinyin).append("</rt></ruby>");
                    } else {
                        annotated.append(ch);
                    }
                } catch (BadHanyuPinyinOutputFormatCombination e) {
                    annotated.append(ch);
                }
            } else {
                annotated.append(ch);
            }
        }

        return annotated.toString();
    }


    private boolean isChineseCharacter(char ch) {
        Character.UnicodeBlock block = Character.UnicodeBlock.of(ch);
        return block == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS ||
               block == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A ||
               block == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B ||
               block == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS ||
               block == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS_SUPPLEMENT;
    }

}
