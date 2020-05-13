package io.github.edmm.model.parameters.validators;

public class FloatValidator implements ValueValidator {

    @Override
    public boolean isValid(String input) {
        try {
            Double.parseDouble(input);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
