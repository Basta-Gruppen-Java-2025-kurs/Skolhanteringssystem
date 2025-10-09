import java.util.ArrayList;
import java.util.regex.Pattern;

public class Validator {
    public static String validatePersonalData(String name, String securityNumber, String email, int year) {
        ArrayList<String> errors = new ArrayList<>() {
            @Override
            public boolean add(String s) {
                return !s.isBlank() && super.add(s);
            }
        };
        // validate name
        if (name.isBlank()) {
            errors.add("blank name");
        }
        // validate security number
        errors.add(validateSecurityNumber(securityNumber));
        // validate email
        errors.add(validateEmail(email));
        // validate year
        if (year <= 0) {
            errors.add("year must be positive");
        }
        return Capitalize(String.join(", ", errors));
    }

    public static String validateEmail(String email) {
        if (email.isBlank()) {
            return "empty email";
        } else if(!email.contains("@") || !email.contains(".")) {
            return "email is not in the format <address>@<domain>.<ext>";
        }
        return "";
    }

    public static String Capitalize(String string) {
        return string.isBlank() ? string : string.substring(0, 1).toUpperCase() + string.substring(1);
    }

    public static String validateSecurityNumber(String securityNumber) {
        if (securityNumber.isBlank()) {
            return "empty security number";
        } else if (securityNumber.length() != 10 && securityNumber.length() != 12) {
            return "security number length must be 10 or 12 digits";
        } else if (!Pattern.compile("\\d+").matcher(securityNumber).matches()) {
            return "security number must have decimal digits only";
        }
        return "";
    }
}
