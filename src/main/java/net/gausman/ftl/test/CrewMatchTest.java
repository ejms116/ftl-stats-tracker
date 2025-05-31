package net.gausman.ftl.test;

import net.gausman.ftl.util.CrewMatcher;

import java.util.*;

public class CrewMatchTest {
    public static void main(String[] args) {
        Crew2 test = new Crew2(1, 2, 3, 4, 5);
        List<Crew2> list = List.of(
                new Crew2(1, 2, 3, 4, 99),
                new Crew2(1, 2, 3, 4, 5),
                new Crew2(1, 2, 3, 0, 99)
        );

        // Compare a, b, c, d from test, but require e = 99

        List<String> fieldsTocompare = new ArrayList<>();
        fieldsTocompare.add("a");
        fieldsTocompare.add("b");
        fieldsTocompare.add("c");

        Map<String, Object> overridesValues = new HashMap<>();
        overridesValues.put("e", 99);
        overridesValues.put("d", 0);
//        Crew2 match = CrewMatcher.findMatchingCrew(
//                list,
//                test,
//                fieldsTocompare,
//                overridesValues
//        );
//        System.out.println(match);  // Outputs: Crew(a=1, b=2, c=3, d=4, e=99)

    }

}
