package io.github.miwurster.edm.utils;

public abstract class Consts {

    public static final boolean PASSES = true;
    public static final boolean FAILS = false;

    public static final boolean SUCCESS = true;
    public static final boolean FAILURE = false;

    public static final boolean TRUE = true;
    public static final boolean FALSE = false;

    public static final String EMPTY = "";
    public static final String SPACE = " ";
    public static final String TAB = "\t";
    public static final String SINGLE_QUOTE = "'";
    public static final String PERIOD = ".";
    public static final String DOUBLE_QUOTE = "\"";

    /**
     * Useful for {@link String} operations, which return an index of <tt>-1</tt> when an item is not found.
     */
    public static final int NOT_FOUND = -1;

    /**
     * System property - <tt>line.separator</tt>
     */
    public static final String NL = System.getProperty("line.separator");

    /**
     * System property - <tt>file.separator</tt>
     */
    public static final String FS = System.getProperty("file.separator");

    /**
     * System property - <tt>path.separator</tt>
     */
    public static final String PS = System.getProperty("path.separator");

    /**
     * System property - <tt>java.io.tmpdir</tt>
     */
    public static final String TMPDIR = System.getProperty("java.io.tmpdir");

    /**
     * System property - <tt>user.home</tt>
     */
    public static final String USER_HOME = System.getProperty("user.home");
}
