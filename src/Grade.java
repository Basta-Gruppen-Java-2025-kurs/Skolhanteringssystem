import Helpers.Named;

public enum Grade implements Named {
    A, B, C, D, E, F, NA, ABSENT, SPECIAL;

    @Override
    public String getName() {
        return this.toString();
    }
}
