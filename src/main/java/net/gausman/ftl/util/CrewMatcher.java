package net.gausman.ftl.util;

import net.gausman.ftl.model.Crew;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

public class CrewMatcher {

    public static Crew findMatchingCrew(
            List<Crew> crewList,
            Crew test,
            List<String> fieldsToCompare,
            Map<String, Object> overrideValues
    ) {
        for (Crew crew : crewList) {
            boolean match = true;

            for (String fieldName : fieldsToCompare) {
                try {
                    Field field = Crew.class.getDeclaredField(fieldName);
                    field.setAccessible(true);
                    Object value1 = field.get(crew);
                    Object value2 = field.get(test);

                    if (!value1.equals(value2)) {
                        match = false;
                        break;
                    }

                } catch (NoSuchFieldException | IllegalAccessException e) {
                    throw new RuntimeException("Invalid field: " + fieldName, e);
                }
            }

            for (Map.Entry<String, Object> entry : overrideValues.entrySet()) {
                try {
                    Field field = Crew.class.getDeclaredField(entry.getKey());
                    field.setAccessible(true);
                    Object value = field.get(crew);
                    if (!value.equals(entry.getValue())) {
                        match = false;
                        break;
                    }

                } catch (NoSuchFieldException | IllegalAccessException e) {
                    throw new RuntimeException("Invalid field: " + entry.getKey(), e);
                }
            }

            if (match) {
                return crew;
            }
        }

        return null;
    }
}

