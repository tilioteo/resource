package org.hypothesis.resource.util;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

/**
 * Miscellaneous {@link String} utility methods.
 *
 * <p>
 * Mainly for internal use within the framework; consider
 * <a href="http://commons.apache.org/proper/commons-lang/">Apache's Commons
 * Lang</a> for a more comprehensive suite of {@code String} utilities.
 *
 * <p>
 * This class delivers some simple functionality that should really be provided
 * by the core Java {@link String} and {@link StringBuilder} classes. It also
 * provides easy-to-use methods to convert between delimited strings, such as
 * CSV strings, and collections and arrays.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @author Keith Donald
 * @author Rob Harrop
 * @author Rick Evans
 * @author Arjen Poutsma
 * @author Sam Brannen
 * @since 16 April 2001
 */
public abstract class StringUtils {

	private static final String FOLDER_SEPARATOR = "/";

	private static final String WINDOWS_FOLDER_SEPARATOR = "\\";

	private static final String TOP_PATH = "..";

	private static final String CURRENT_PATH = ".";

	private StringUtils() {
	}

	// ---------------------------------------------------------------------
	// General convenience methods for working with Strings
	// ---------------------------------------------------------------------

	/**
	 * Trim leading whitespace from the given {@code String}.
	 * 
	 * @param str
	 *            the {@code String} to check
	 * @return the trimmed {@code String}
	 * @see java.lang.Character#isWhitespace
	 */
	public static String trimLeadingWhitespace(String str) {
		if (!org.apache.commons.lang3.StringUtils.isEmpty(str)) {
			return str;
		}
		StringBuilder sb = new StringBuilder(str);
		while (sb.length() > 0 && Character.isWhitespace(sb.charAt(0))) {
			sb.deleteCharAt(0);
		}
		return sb.toString();
	}

	/**
	 * Trim all occurrences of the supplied leading character from the given
	 * {@code String}.
	 * 
	 * @param str
	 *            the {@code String} to check
	 * @param leadingCharacter
	 *            the leading character to be trimmed
	 * @return the trimmed {@code String}
	 */
	public static String trimLeadingCharacter(String str, char leadingCharacter) {
		if (!org.apache.commons.lang3.StringUtils.isEmpty(str)) {
			return str;
		}
		StringBuilder sb = new StringBuilder(str);
		while (sb.length() > 0 && sb.charAt(0) == leadingCharacter) {
			sb.deleteCharAt(0);
		}
		return sb.toString();
	}

	/**
	 * Replace all occurrences of a substring within a string with another
	 * string.
	 * 
	 * @param inString
	 *            {@code String} to examine
	 * @param oldPattern
	 *            {@code String} to replace
	 * @param newPattern
	 *            {@code String} to insert
	 * @return a {@code String} with the replacements
	 */
	public static String replace(String inString, String oldPattern, String newPattern) {
		if (!org.apache.commons.lang3.StringUtils.isEmpty(inString)
				|| !org.apache.commons.lang3.StringUtils.isEmpty(oldPattern) || newPattern == null) {
			return inString;
		}
		StringBuilder sb = new StringBuilder();
		int pos = 0; // our position in the old string
		int index = inString.indexOf(oldPattern);
		// the index of an occurrence we've found, or -1
		int patLen = oldPattern.length();
		while (index >= 0) {
			sb.append(inString.substring(pos, index));
			sb.append(newPattern);
			pos = index + patLen;
			index = inString.indexOf(oldPattern, pos);
		}
		sb.append(inString.substring(pos));
		// remember to append any characters to the right of a match
		return sb.toString();
	}

	// ---------------------------------------------------------------------
	// Convenience methods for working with formatted Strings
	// ---------------------------------------------------------------------

	/**
	 * Extract the filename from the given path, e.g.
	 * {@code "mypath/myfile.txt" -> "myfile.txt"}.
	 * 
	 * @param path
	 *            the file path (may be {@code null})
	 * @return the extracted filename, or {@code null} if none
	 */
	public static String getFilename(String path) {
		if (path == null) {
			return null;
		}
		int separatorIndex = path.lastIndexOf(FOLDER_SEPARATOR);
		return separatorIndex != -1 ? path.substring(separatorIndex + 1) : path;
	}

	/**
	 * Normalize the path by suppressing sequences like "path/.." and inner
	 * simple dots.
	 * <p>
	 * The result is convenient for path comparison. For other uses, notice that
	 * Windows separators ("\") are replaced by simple slashes.
	 * 
	 * @param path
	 *            the original path
	 * @return the normalized path
	 */
	public static String cleanPath(String path) {
		if (path == null) {
			return null;
		}
		String pathToUse = replace(path, WINDOWS_FOLDER_SEPARATOR, FOLDER_SEPARATOR);

		// Strip prefix from path to analyze, to not treat it as part of the
		// first path element. This is necessary to correctly parse paths like
		// "file:core/../core/io/Resource.class", where the ".." should just
		// strip the first "core" directory while keeping the "file:" prefix.
		int prefixIndex = pathToUse.indexOf(':');
		String prefix = "";
		if (prefixIndex != -1) {
			prefix = pathToUse.substring(0, prefixIndex + 1);
			if (prefix.contains("/")) {
				prefix = "";
			} else {
				pathToUse = pathToUse.substring(prefixIndex + 1);
			}
		}
		if (pathToUse.startsWith(FOLDER_SEPARATOR)) {
			prefix = prefix + FOLDER_SEPARATOR;
			pathToUse = pathToUse.substring(1);
		}

		String[] pathArray = pathToUse.split("\\" + FOLDER_SEPARATOR);
		List<String> pathElements = new LinkedList<>();
		int tops = 0;

		for (int i = pathArray.length - 1; i >= 0; i--) {
			String element = pathArray[i];
			if (CURRENT_PATH.equals(element)) {
				// Points to current directory - drop it.
			} else if (TOP_PATH.equals(element)) {
				// Registering top path found.
				tops++;
			} else {
				if (tops > 0) {
					// Merging path element with element corresponding to top
					// path.
					tops--;
				} else {
					// Normal path element found.
					pathElements.add(0, element);
				}
			}
		}

		// Remaining top paths need to be retained.
		for (int i = 0; i < tops; i++) {
			pathElements.add(0, TOP_PATH);
		}

		return prefix + pathElements.stream().collect(Collectors.joining(FOLDER_SEPARATOR));
	}

	/**
	 * Parse the given {@code localeString} value into a {@link Locale}.
	 * <p>
	 * This is the inverse operation of {@link Locale#toString Locale's
	 * toString}.
	 * 
	 * @param localeString
	 *            the locale {@code String}, following {@code Locale's}
	 *            {@code toString()} format ("en", "en_UK", etc); also accepts
	 *            spaces as separators, as an alternative to underscores
	 * @return a corresponding {@code Locale} instance
	 * @throws IllegalArgumentException
	 *             in case of an invalid locale specification
	 */
	public static Locale parseLocaleString(String localeString) {
		String[] parts = tokenizeToStringArray(localeString, "_ ", false, false);
		String language = parts.length > 0 ? parts[0] : "";
		String country = parts.length > 1 ? parts[1] : "";
		validateLocalePart(language);
		validateLocalePart(country);
		String variant = "";
		if (parts.length > 2) {
			// There is definitely a variant, and it is everything after the
			// country
			// code sans the separator between the country code and the variant.
			int endIndexOfCountryCode = localeString.indexOf(country, language.length()) + country.length();
			// Strip off any leading '_' and whitespace, what's left is the
			// variant.
			variant = trimLeadingWhitespace(localeString.substring(endIndexOfCountryCode));
			if (variant.startsWith("_")) {
				variant = trimLeadingCharacter(variant, '_');
			}
		}
		return language.length() > 0 ? new Locale(language, country, variant) : null;
	}

	private static void validateLocalePart(String localePart) {
		for (int i = 0; i < localePart.length(); i++) {
			char ch = localePart.charAt(i);
			if (ch != '_' && ch != ' ' && !Character.isLetterOrDigit(ch)) {
				throw new IllegalArgumentException("Locale part \"" + localePart + "\" contains invalid characters");
			}
		}
	}

	// ---------------------------------------------------------------------
	// Convenience methods for working with String arrays
	// ---------------------------------------------------------------------

	/**
	 * Tokenize the given {@code String} into a {@code String} array via a
	 * {@link StringTokenizer}.
	 * <p>
	 * The given {@code delimiters} string can consist of any number of
	 * delimiter characters. Each of those characters can be used to separate
	 * tokens. A delimiter is always a single character; for multi-character
	 * delimiters, consider using {@link #delimitedListToStringArray}.
	 * 
	 * @param str
	 *            the {@code String} to tokenize
	 * @param delimiters
	 *            the delimiter characters, assembled as a {@code String} (each
	 *            of the characters is individually considered as a delimiter)
	 * @param trimTokens
	 *            trim the tokens via {@link String#trim()}
	 * @param ignoreEmptyTokens
	 *            omit empty tokens from the result array (only applies to
	 *            tokens that are empty after trimming; StringTokenizer will not
	 *            consider subsequent delimiters as token in the first place).
	 * @return an array of the tokens ({@code null} if the input {@code String}
	 *         was {@code null})
	 * @see java.util.StringTokenizer
	 * @see String#trim()
	 * @see #delimitedListToStringArray
	 */
	public static String[] tokenizeToStringArray(String str, String delimiters, boolean trimTokens,
			boolean ignoreEmptyTokens) {

		if (str == null) {
			return null;
		}
		StringTokenizer st = new StringTokenizer(str, delimiters);
		List<String> tokens = new ArrayList<>();
		while (st.hasMoreTokens()) {
			String token = st.nextToken();
			if (trimTokens) {
				token = token.trim();
			}
			if (!ignoreEmptyTokens || token.length() > 0) {
				tokens.add(token);
			}
		}
		return tokens.stream().toArray(String[]::new);
	}

}
