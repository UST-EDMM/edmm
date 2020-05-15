package io.github.edmm.model.parameters.validators;

public class StringValidator implements ValueValidator {

    private final String regex;

    public StringValidator() {
        this(null);
    }

    public StringValidator(String regex) {
        this.regex = regex;
    }

    @Override
    public boolean isValid(String input) {
        return input != null && (regex == null || input.matches(regex));
    }
}
