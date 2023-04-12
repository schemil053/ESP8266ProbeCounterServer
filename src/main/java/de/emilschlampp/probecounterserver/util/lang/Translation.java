package de.emilschlampp.probecounterserver.util.lang;

public class Translation {
    private final String key;
    public Translation(String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        String s = LangManager.getCurrentLanguage().getTranslation(key);
        return s == null ? key : s;
    }

    public String format(Object... val) {
        return LangManager.format(this, val);
    }
}
