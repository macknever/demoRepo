package com.lawrence.corejava.inheritance;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class PatternMatchingExampleTest {

    @Test
    void patternMatchingOfInstanceOfShouldWork() {
        final String DONGBEIHUA = "dongBeiHua";
        final String MANDARIN = "mandarin";
        final String CANTONESE = "Cantonese";

        PatternMatchingExample.NorthChinese harbinese = new PatternMatchingExample.NorthChinese(DONGBEIHUA);
        PatternMatchingExample.Chinese chinese = new PatternMatchingExample.Chinese(MANDARIN);
        PatternMatchingExample.SouthChinese cantonese = new PatternMatchingExample.SouthChinese(CANTONESE);

        PatternMatchingExample.Chinese[] chinese1 = new PatternMatchingExample.Chinese[]{ chinese, harbinese,
                cantonese };

        if (chinese1[1] instanceof PatternMatchingExample.NorthChinese northChinese) {
            assertEquals("baozi", northChinese.cook());
        }

        if (chinese1[2] instanceof PatternMatchingExample.SouthChinese southChinese) {
            assertEquals("hotpot", southChinese.cook());
        }

        PatternMatchingExample patternMatchingExample = new PatternMatchingExample();

        assertThat(patternMatchingExample.showLanguage(chinese1[1])).describedAs("north").isEqualTo(DONGBEIHUA);
        assertThat(patternMatchingExample.showLanguage(chinese1[2])).describedAs("south").isEqualTo(CANTONESE);
    }

}
